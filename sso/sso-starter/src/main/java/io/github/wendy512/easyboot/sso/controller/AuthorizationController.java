/**
 * Copyright wendy512@yeah.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.wendy512.easyboot.sso.controller;

import java.security.Principal;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.collect.ImmutableMap;

import io.github.wendy512.easyboot.sso.custom.tokenstore.RedisTokenStore;
import io.github.wendy512.easyboot.vo.VoResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 拓展authorize模式
 * @author wendy512
 * @date 2022-05-28 13:51:38
 * @since 1.0.0
 */
@Slf4j
@Controller
@SessionAttributes({AuthorizationController.AUTHORIZATION_REQUEST_ATTR_NAME, AuthorizationController.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME})
public class AuthorizationController {

    static final String AUTHORIZATION_REQUEST_ATTR_NAME = "authorizationRequest";

    static final String ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME = "org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST";
    
    @Autowired
    private AuthorizationEndpoint authorizationEndpoint;
    
    @Autowired
    private RedisTokenStore redisTokenStore;

    @RequestMapping(value = "/oauth/authorize")
    @ResponseBody
    public VoResponse authorize(Map<String, Object> model, @RequestParam Map<String, String> parameters, SessionStatus sessionStatus,
                                Principal principal, @RequestHeader("Authorization") String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (StringUtils.isNotBlank(token) && token.length() > 7 && null == authentication) {
            if (token.substring(0, 7).trim().equalsIgnoreCase(OAuth2AccessToken.BEARER_TYPE)) {
                authentication = redisTokenStore.readAuthentication(token.substring(7));
            }
        }

        ModelAndView modelAndView = authorizationEndpoint.authorize(model, parameters, sessionStatus, (Principal) authentication);
        String url = ((RedirectView) modelAndView.getView()).getUrl();
        ImmutableMap<Object, Object> data = ImmutableMap.builder().put("url", url).build();
        return VoResponse.builder().ok(data);
    }
}
