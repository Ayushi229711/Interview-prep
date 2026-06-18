package com.ayushi.interview_prep.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartInterviewResponse {

    private Long sessionId;

    private List<QuestionDto> questions;
}