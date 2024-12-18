package com.nighthawk.spring_portfolio.mvc.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MediaInitializer implements CommandLineRunner {

    @Autowired
    private MediaService mediaService;

    @Override
    public void run(String... args) throws Exception {
        mediaService.initScore();
    }
}