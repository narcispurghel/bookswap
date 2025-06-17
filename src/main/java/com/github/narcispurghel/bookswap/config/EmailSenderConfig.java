package com.github.narcispurghel.bookswap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailSenderConfig {

    @Bean
    JavaMailSender mailSender(@Value("${security.mail.host}") String host,
            @Value("${security.mail.username}") String username,
            @Value("${security.mail.password}") String password,
            @Value("${security.mail.port}") int port,
            @Value("${security.mail.protocol}") String protocol,
            @Value("${security.mail.auth}") String auth,
            @Value("${security.mail.enabled}") String enabled) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setPort(port);
        Properties properties = mailSender.getJavaMailProperties();
        properties.put(enabled, true);
        properties.put(auth, true);
        properties.put("mail.smtp.connectionpoolsize", "5"); // Pool size
        properties.put("mail.smtp.connectionpooltimeout", "10000");
        return mailSender;
    }

}


