package com.ayushi.interview_prep.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewResultResponse {

    private Long sessionId;
    private String role;
    private String difficulty;
    private Double overallScore;

    private List<AnswerResultDto> answers;
}
