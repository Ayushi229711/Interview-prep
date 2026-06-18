package com.ayushi.interview_prep.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResultDto {

    private String question;
    private String answer;
    private Double score;
    private String feedback;
}