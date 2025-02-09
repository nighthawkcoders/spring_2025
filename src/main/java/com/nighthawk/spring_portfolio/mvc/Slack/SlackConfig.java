package com.nighthawk.spring_portfolio.mvc.Slack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SlackConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
