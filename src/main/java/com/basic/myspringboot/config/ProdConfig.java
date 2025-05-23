package com.basic.myspringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
public class ProdConfig {
    @Bean
    public CustomerVO customerVO(){
        return CustomerVO.builder()
                .mode("운영환경")
                .rate(0.8)
                .build();
    }
}
