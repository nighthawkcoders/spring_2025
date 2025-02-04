package com.nighthawk.spring_portfolio.mvc.forum.forumBlogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nighthawk.spring_portfolio.mvc.forum.Forum;

@Repository
public interface forumBlogRepository extends JpaRepository<Forum, Long> {

}
