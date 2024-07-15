package com.kurtcan.sepsearchservice.shared.security;

import com.kurtcan.sepsearchservice.shared.constant.ProfileName;
import com.kurtcan.sepsearchservice.shared.jwt.JwtUtilsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("!" + ProfileName.TEST)
public class FilterConfig {

    private final JwtUtilsImpl jwtUtils;
    private final SecurityGlobalProperties securityGlobalProperties;
    private final CurrentUserService currentUserService;

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilter() {
        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthorizationFilter(jwtUtils, securityGlobalProperties, currentUserService));
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}