package com.ayushi.interview_prep.Controller;

import com.ayushi.interview_prep.Repository.UserRepository;
import com.ayushi.interview_prep.Service.GeminiService;
import com.ayushi.interview_prep.Service.InterviewService;
import com.ayushi.interview_prep.dto.*;
import com.ayushi.interview_prep.entity.InterviewSession;
import com.ayushi.interview_prep.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final GeminiService geminiService;
    private final UserRepository userRepository;

    @PostMapping("/start")
    public StartInterviewResponse startInterview(
            @RequestBody StartInterviewRequest request,
            Authentication authentication) {

        String email =
                authentication.getName();

        return interviewService.startInterview(
                request,
                email);
    }

    @GetMapping("/test")
    public String testGemini() {

        return geminiService.generateQuestions(
                "Java Backend",
                "Easy");
    }

    @PostMapping("/answer")
    public SubmitAnswerResponse submitAnswer(
            @RequestBody SubmitAnswerRequest request) {

        return interviewService.submitAnswer(request);
    }
    @GetMapping("/dashboard")
    public ResponseEntity<List<DashboardResponse>> dashboard(
            Authentication authentication) {

        User user = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(
                interviewService.getDashboard(user.getId())
        );
    }
    @GetMapping("/result/{sessionId}")
    public ResponseEntity<InterviewResultResponse>
    getResult(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                interviewService.getResult(
                        sessionId));
    }
}