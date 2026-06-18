package com.ayushi.interview_prep.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitAnswerResponse {

    private Double score;

    private String feedback;
}