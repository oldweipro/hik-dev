package com.oldwei.hikdev.config;

import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrProxyServletConfiguration {

    @Value("${proxy.rtsp_server.url}")
    private String url;
    @Value("${proxy.rtsp_server.target_url}")
    private String targetUrl;


    @Bean
    public ServletRegistrationBean<ProxyServlet> servletRegistrationBean() {
        ServletRegistrationBean<ProxyServlet> servletRegistrationBean = new ServletRegistrationBean<>(new ProxyServlet(), url);
        servletRegistrationBean.setName("rtsp_server");
        servletRegistrationBean.addInitParameter("targetUri", targetUrl);
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, String.valueOf(true));
        return servletRegistrationBean;
    }
}
