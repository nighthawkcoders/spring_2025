package com.nighthawk.spring_portfolio.mvc.Slack;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, LocalDateTime> {}
