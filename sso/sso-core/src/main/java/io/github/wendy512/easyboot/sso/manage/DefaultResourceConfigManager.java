package io.github.wendy512.easyboot.sso.manage;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import io.github.wendy512.easyboot.sso.config.ExcludeUrl;
import io.github.wendy512.easyboot.sso.config.SSOConfiguration;

/**
 * 鉴权配置管理类
 * @author taowenwu
 * @date 2021-04-16 13:17:13:17
 * @since 1.0.0
 */
public class DefaultResourceConfigManager implements ResourceConfigManager, InitializingBean {
    public static final String DEFAULT_LOGIN_PROCESSING_URL = "/auth/authorize";
    public static final String DEFAULT_LOGIN_OUT_URL = "/auth/logout";
    public static final String[] DEFAULT_RESOURCE_EXCLUDE_URLS = { "/css/**/*", "/js/**/*", "/icons/**/*", "/login/**", "/*.ico", "/static/**/*" };
    public static final String[] DEFAULT_SECURITY_EXCLUDE_URLS = { "/verifyCode/**" };

    private String resourceId;
    private String loginProcessingUrl = DEFAULT_LOGIN_PROCESSING_URL;
    private String logoutUrl = DEFAULT_LOGIN_OUT_URL;
    private SSOConfiguration ssoConfiguration;
    private String[] securityExcludeUrls = {};
    private String[] resourceExcludeUrls = {};

    public DefaultResourceConfigManager(SSOConfiguration ssoConfiguration) {
        this.ssoConfiguration = ssoConfiguration;
        ExcludeUrl exclude = ssoConfiguration.getExclude();
        
        if (null != exclude) {
            if (StringUtils.isNotEmpty(exclude.getResourceExcludeUrl())) {
                Set<String> urls =
                    Stream.of(exclude.getResourceExcludeUrl().split(",")).map(String::trim).collect(Collectors.toSet());
                urls.addAll(Arrays.asList(DEFAULT_RESOURCE_EXCLUDE_URLS));
                this.resourceExcludeUrls = urls.toArray(new String[urls.size()]);
            }

            if (StringUtils.isNotEmpty(exclude.getSecurityExcludeUrl())) {
                Set<String> urls =
                        Stream.of(exclude.getSecurityExcludeUrl().split(",")).map(String::trim).collect(Collectors.toSet());
                urls.addAll(Arrays.asList(DEFAULT_SECURITY_EXCLUDE_URLS));
                this.securityExcludeUrls = urls.toArray(new String[urls.size()]);
            }
        } else {
            this.resourceExcludeUrls = DEFAULT_RESOURCE_EXCLUDE_URLS;
            this.securityExcludeUrls = DEFAULT_SECURITY_EXCLUDE_URLS;
        }
        this.resourceId = ssoConfiguration.getResourceId();
    }
    
    private void trim(String[] urls) {
        for (int i = 0; i < urls.length; i++) {
            urls[i] = urls[i].trim();
        }
    }

    @Override
    public String getResourceId() {
        return this.resourceId;
    }

    @Override
    public String getLoginProcessingUrl() {
        return this.loginProcessingUrl;
    }

    @Override
    public String getLogoutUrl() {
        return this.logoutUrl;
    }

    @Override
    public String[] getSecurityExcludeUrls() {
        return securityExcludeUrls;
    }

    @Override
    public String[] getResourceExcludeUrls() {
        return resourceExcludeUrls;
    }

    @Override
    public boolean allowFormAuthenticationForClients() {
        return false;
    }

    @Override
    public boolean enableVerifyCode() {
        return ssoConfiguration.getVerifyCode().isEnable();
    }

    @Override
    public SSOConfiguration getConfiguration() {
        return this.ssoConfiguration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(ssoConfiguration, "ssoConfiguration 不能为空");
    }
}
