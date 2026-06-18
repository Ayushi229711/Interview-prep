package com.ayushi.interview_prep.Repository;

import com.ayushi.interview_prep.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository
        extends JpaRepository<Answer, Long> {
    List<Answer> findByInterviewSessionId(Long sessionId);
}