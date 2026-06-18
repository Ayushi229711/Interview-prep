package com.ayushi.interview_prep.Controller;

import com.ayushi.interview_prep.Repository.UserRepository;
import com.ayushi.interview_prep.Service.InterviewService;
import com.ayushi.interview_prep.dto.DashboardResponse;
import com.ayushi.interview_prep.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final InterviewService interviewService;

    @GetMapping
    public ResponseEntity<List<DashboardResponse>> dashboard(
            Authentication authentication) {

        User user = userRepository.findByEmail(
                        authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return ResponseEntity.ok(
                interviewService.getDashboard(user.getId())
        );
    }
}