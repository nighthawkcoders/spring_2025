package com.nighthawk.spring_portfolio.mvc.Slack;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
// Registering JPA Repository and table contents
public interface SlackMessageRepository extends JpaRepository<SlackMessage, LocalDateTime> {}
