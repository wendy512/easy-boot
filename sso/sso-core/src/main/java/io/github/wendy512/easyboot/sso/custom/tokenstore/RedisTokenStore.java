package io.github.wendy512.easyboot.sso.custom.tokenstore;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.util.ClassUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * redis token 存储
 * @author taowenwu
 * @date 2021-08-14 10:26:10:26
 * @since 1.0.0
 */
@Slf4j
public class RedisTokenStore implements TokenStore {
    private static final String ACCESS = "access:";
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    private static final String AUTH = "auth:";
    private static final String REFRESH_AUTH = "refresh_auth:";
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    private static final String REFRESH = "refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    private static final String UNAME_TO_ACCESS = "uname_to_access:";

    private static final boolean springDataRedis_2_0 = ClassUtils.isPresent(
            "org.springframework.data.redis.connection.RedisStandaloneConfiguration",
            RedisTokenStore.class.getClassLoader());

    private final RedisTemplate redisTemplate;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    private String prefix = "";

    private Method redisConnectionSet_2_0;

    public RedisTokenStore(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private byte[] serializeKey(String object) {
        return serialize(prefix + object);
    }

    private OAuth2AccessToken deserializeAccessToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2AccessToken.class);
    }

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    private OAuth2RefreshToken deserializeRefreshToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2RefreshToken.class);
    }

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    private String deserializeString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        
        byte[] serializedKey = serializeKey(AUTH_TO_ACCESS + key);

        return (OAuth2AccessToken) redisTemplate.execute((RedisCallback) conn -> {
            OAuth2AccessToken accessToken = deserializeAccessToken(conn.get(serializedKey));
            if (accessToken != null) {
                OAuth2Authentication storedAuthentication = readAuthentication(accessToken.getValue());
                if ((storedAuthentication == null || !key.equals(authenticationKeyGenerator.extractKey(storedAuthentication)))) {
                    // Keep the stores consistent (maybe the same user is
                    // represented by this authentication but the details have
                    // changed)
                    storeAccessToken(accessToken, authentication);
                }

            }
            return accessToken;
        });
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return (OAuth2Authentication)redisTemplate.execute((RedisCallback) conn -> {
            return deserializeAuthentication(conn.get(serializeKey(AUTH + token)));
        });
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        return (OAuth2Authentication) redisTemplate.execute((RedisCallback) session -> {
            byte[] bytes = session.get(serializeKey(REFRESH_AUTH + token));
            OAuth2Authentication auth = deserializeAuthentication(bytes);
            return auth;
        });
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        byte[] serializedAccessToken = serialize(token);
        byte[] serializedAuth = serialize(authentication);
        byte[] accessKey = serializeKey(ACCESS + token.getValue());
        byte[] authKey = serializeKey(AUTH + token.getValue());
        byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication));
        byte[] approvalKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(authentication));
        byte[] clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());

        
        redisTemplate.executePipelined((RedisCallback) session -> {
            session.set(accessKey, serializedAccessToken);
            session.set(authKey, serializedAuth);
            session.set(authToAccessKey, serializedAccessToken);
            if (!authentication.isClientOnly()) {
                session.sAdd(approvalKey, serializedAccessToken);
            }
            session.hSet(clientId, token.getValue().getBytes(StandardCharsets.UTF_8),
                serializedAccessToken);

            if (token.getExpiration() != null) {
                int seconds = token.getExpiresIn();
                session.expire(accessKey, seconds);
                session.expire(authKey, seconds);
                session.expire(authToAccessKey, seconds);
                session.expire(clientId, seconds);
                session.expire(approvalKey, seconds);
            }

            OAuth2RefreshToken refreshToken = token.getRefreshToken();
            if (refreshToken != null && refreshToken.getValue() != null) {

                byte[] refresh = serialize(token.getRefreshToken().getValue());
                byte[] auth = serialize(token.getValue());
                byte[] refreshToAccessKey = serializeKey(REFRESH_TO_ACCESS + token.getRefreshToken().getValue());
                byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + token.getValue());

                session.set(refreshToAccessKey, auth);
                session.set(accessToRefreshKey, refresh);
                
                if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                    
                    ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
                    Date expiration = expiringRefreshToken.getExpiration();
                    if (expiration != null) {
                        int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                                .intValue();
                        session.expire(refreshToAccessKey, seconds);
                        session.expire(accessToRefreshKey, seconds);
                    }
                }
            }

            return null;
        });
    }

    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? ""
                : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken accessToken) {
        removeAccessToken(accessToken.getValue());
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        byte[] key = serializeKey(ACCESS + tokenValue);

        return (OAuth2AccessToken) redisTemplate.execute((RedisCallback) conn -> {
            return deserializeAccessToken(conn.get(key));
        });
    }

    public void removeAccessToken(String tokenValue) {
        log.info("remove redis access_token {}", tokenValue);
        byte[] accessKey = serializeKey(ACCESS + tokenValue);
        byte[] authKey = serializeKey(AUTH + tokenValue);
        byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);

        byte[] access = (byte[]) redisTemplate.execute((RedisCallback) conn -> {
            return conn.get(accessKey);
        });
        byte[] auth = (byte[]) redisTemplate.execute((RedisCallback) conn -> {
            return conn.get(authKey);
        });
       
        
        redisTemplate.executePipelined((RedisCallback) conn -> {
            conn.del(accessKey);
            conn.del(accessToRefreshKey);
            // Don't remove the refresh token - it's up to the caller to do that
            conn.del(authKey);
            return null;
        });

        OAuth2Authentication authentication = deserializeAuthentication(auth);
        if (authentication != null) {
            redisTemplate.executePipelined((RedisCallback) conn -> {
                String key = authenticationKeyGenerator.extractKey(authentication);
                byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + key);
                byte[] unameKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(authentication));
                byte[] clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
                conn.del(authToAccessKey);
                conn.del(unameKey);
                conn.hDel(clientId, tokenValue.getBytes(StandardCharsets.UTF_8));
                conn.del(serialize(ACCESS + key));
                return null;
            });
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        byte[] refreshKey = serializeKey(REFRESH + refreshToken.getValue());
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + refreshToken.getValue());
        byte[] serializedRefreshToken = serialize(refreshToken);

        redisTemplate.execute((RedisCallback) conn -> {
            conn.set(refreshKey, serializedRefreshToken);
            conn.set(refreshAuthKey, serialize(authentication));

            if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
                Date expiration = expiringRefreshToken.getExpiration();
                if (expiration != null) {
                    int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                            .intValue();
                    conn.expire(refreshKey, seconds);
                    conn.expire(refreshAuthKey, seconds);
                }
            }
            
            return null;
        });
        
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        byte[] key = serializeKey(REFRESH + tokenValue);

        return (OAuth2RefreshToken) redisTemplate.execute((RedisCallback) conn -> {
            return deserializeRefreshToken(conn.get(key));
        });
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getValue());
    }

    public void removeRefreshToken(String tokenValue) {
        log.info("remove redis refresh_token {}", tokenValue);
        byte[] refreshKey = serializeKey(REFRESH + tokenValue);
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + tokenValue);
        byte[] refresh2AccessKey = serializeKey(REFRESH_TO_ACCESS + tokenValue);
        byte[] access2RefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);

        redisTemplate.executePipelined((RedisCallback) conn -> {
            conn.del(refreshKey);
            conn.del(refreshAuthKey);
            conn.del(refresh2AccessKey);
            conn.del(access2RefreshKey);
            return null;
        });
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    private void removeAccessTokenUsingRefreshToken(String refreshToken) {
        byte[] key = serializeKey(REFRESH_TO_ACCESS + refreshToken);
        byte[] bytes = (byte[]) redisTemplate.execute((RedisCallback) conn -> {
            return conn.get(key);
        });

        redisTemplate.execute((RedisCallback) conn -> {
            return conn.del(key);
        });
        
        if (bytes == null) {
            return;
        }
        
        String accessToken = deserializeString(bytes);
        if (accessToken != null) {
            removeAccessToken(accessToken);
        }
    }

    private List<byte[]> getByteLists(byte[] approvalKey, RedisConnection conn) {
        Long size = conn.sCard(approvalKey);
        List<byte[]> byteList = new ArrayList<byte[]>(size.intValue());
        Cursor<byte[]> cursor = conn.sScan(approvalKey, ScanOptions.NONE);
        while(cursor.hasNext()) {
            byteList.add(cursor.next());
        }
        return byteList;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        byte[] approvalKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(clientId, userName));

        List<byte[]> byteList = (List<byte[]>) redisTemplate.execute((RedisCallback) conn -> {
            return getByteLists(approvalKey, conn);
        });
        
        if (byteList == null || byteList.size() == 0) {
            return Collections.<OAuth2AccessToken> emptySet();
        }
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>(byteList.size());
        for (byte[] bytes : byteList) {
            OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
            accessTokens.add(accessToken);
        }
        return Collections.<OAuth2AccessToken> unmodifiableCollection(accessTokens);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        byte[] key = serializeKey(CLIENT_ID_TO_ACCESS + clientId);
        
        List<byte[]> byteList = (List<byte[]>) redisTemplate.execute((RedisCallback) conn -> {
            return new ArrayList<>(conn.hGetAll(key).values());
        });
        
        if (byteList == null || byteList.size() == 0) {
            return Collections.<OAuth2AccessToken> emptySet();
        }
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>(byteList.size());
        for (byte[] bytes : byteList) {
            OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
            accessTokens.add(accessToken);
        }
        return Collections.<OAuth2AccessToken> unmodifiableCollection(accessTokens);
    }
}
