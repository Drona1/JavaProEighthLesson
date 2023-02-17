package com.gmail.dimabah.javaproeighthlesson;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public CommandLineRunner start(DBService dbService){
        return args -> dbService.reset();
    }

}
