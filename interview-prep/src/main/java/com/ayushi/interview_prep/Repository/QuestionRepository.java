package com.ayushi.interview_prep.Repository;

import com.ayushi.interview_prep.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository
        extends JpaRepository<Question, Long> {
}