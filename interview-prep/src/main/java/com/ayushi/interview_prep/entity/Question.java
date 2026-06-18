package com.ayushi.interview_prep.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private Integer questionNumber;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private InterviewSession interviewSession;
}