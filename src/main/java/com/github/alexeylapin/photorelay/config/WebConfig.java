package com.github.alexeylapin.photorelay.config;

import org.apache.catalina.servlets.WebdavServlet;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class WebConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {

    }

    @Bean
    public ServletRegistrationBean<WebdavServlet> webdavServlet() {
        Map<String, String> params = Map.of(
                "readonly", "false",
                "listings", "true",
                "debug", "0"
        );
        var registration = new ServletRegistrationBean<>(new WebdavServlet(), "/webdav/*");
        registration.setInitParameters(params);
        return registration;
    }

}
