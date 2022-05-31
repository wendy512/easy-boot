package io.github.wendy512.easyboot.sso.manage;

import io.github.wendy512.easyboot.sso.config.SSOConfiguration;

/**
 * @author taowenwu
 * @date 2021-04-16 13:10:13:10
 * @since 1.0.0
 */
public interface ResourceConfigManager {
    String getResourceId();

    String getLoginProcessingUrl();

    String getLogoutUrl();

    String[] getSecurityExcludeUrls();

    String[] getResourceExcludeUrls();

    boolean allowFormAuthenticationForClients();

    boolean enableVerifyCode();

    SSOConfiguration getConfiguration();
}
