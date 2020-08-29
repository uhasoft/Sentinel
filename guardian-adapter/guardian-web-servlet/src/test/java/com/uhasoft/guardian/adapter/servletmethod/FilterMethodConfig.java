package com.uhasoft.guardian.adapter.servletmethod;

import com.uhasoft.guardian.adapter.servlet.CommonFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Roger Law
 **/
@Configuration
public class FilterMethodConfig {

    @Bean
    public FilterRegistrationBean sentinelFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CommonFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter(CommonFilter.HTTP_METHOD_SPECIFY, "true");
        registration.setName("sentinelFilter");
        registration.setOrder(1);

        return registration;
    }
}
