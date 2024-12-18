package com.nighthawk.spring_portfolio.mvc.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

    @Autowired
    private MediaJpaRepository mediaJpaRepository;

    public Score initScore() {
        Score score = new Score("John Doe", 100);
        mediaJpaRepository.save(score);
        return score;
    }
}
