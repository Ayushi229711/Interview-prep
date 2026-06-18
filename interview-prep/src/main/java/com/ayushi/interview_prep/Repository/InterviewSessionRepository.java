package com.ayushi.interview_prep.Repository;

import com.ayushi.interview_prep.entity.InterviewSession;
import com.ayushi.interview_prep.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewSessionRepository
        extends JpaRepository<InterviewSession, Long> {
    List<InterviewSession> findByUser(User user);
}