package com.ayushi.interview_prep.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {

    private Long sessionId;

    private Long questionId;

    private String answerText;
}