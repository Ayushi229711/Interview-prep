package com.ayushi.interview_prep.Service;

import com.ayushi.interview_prep.Repository.AnswerRepository;
import com.ayushi.interview_prep.Repository.InterviewSessionRepository;
import com.ayushi.interview_prep.Repository.QuestionRepository;
import com.ayushi.interview_prep.Repository.UserRepository;
import com.ayushi.interview_prep.dto.*;
import com.ayushi.interview_prep.entity.Answer;
import com.ayushi.interview_prep.entity.InterviewSession;
import com.ayushi.interview_prep.entity.Question;
import com.ayushi.interview_prep.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final GeminiService geminiService;
    private final AnswerRepository answerRepository;

    public StartInterviewResponse startInterview(
            StartInterviewRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        InterviewSession session = InterviewSession.builder()
                .role(request.getRole())
                .difficulty(request.getDifficulty())
                .overallScore(0.0)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        // Save session first
        session = interviewSessionRepository.save(session);

        System.out.println("Session saved with ID: " + session.getId());


        String generatedQuestions;

        try {
            generatedQuestions = geminiService.generateQuestions(
                    request.getRole(),
                    request.getDifficulty());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String[] lines = generatedQuestions.split("\n");

        List<Question> questionEntities = new ArrayList<>();

        int questionNumber = 1;

        for (String line : lines) {

            String text = line.trim();

            text = text.replaceFirst("^\\d+[.)]\\s*", "");

            if (text.isEmpty()) {
                continue;
            }

            Question question = Question.builder()
                    .questionText(text)
                    .questionNumber(questionNumber++)
                    .interviewSession(session)
                    .build();

            questionEntities.add(question);
        }

// Save questions and get IDs
        List<Question> savedQuestions =
                questionRepository.saveAll(questionEntities);

// Convert to DTO
        List<QuestionDto> questionDtos =
                savedQuestions.stream()
                        .map(q -> QuestionDto.builder()
                                .id(q.getId())
                                .questionText(q.getQuestionText())
                                .build())
                        .toList();

        return StartInterviewResponse.builder()
                .sessionId(session.getId())
                .questions(questionDtos)
                .build();
    }

    public SubmitAnswerResponse submitAnswer(
            SubmitAnswerRequest request) {

        Question question =
                questionRepository.findById(
                                request.getQuestionId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Question not found: " +
                                                request.getQuestionId()));

        InterviewSession session =
                interviewSessionRepository.findById(
                                request.getSessionId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Session not found: " +
                                                request.getSessionId()));

        String geminiResponse;

        try {

            geminiResponse =
                    geminiService.evaluateAnswer(
                            question.getQuestionText(),
                            request.getAnswerText());

        } catch (Exception e) {

            System.out.println("Gemini evaluation failed: "
                    + e.getMessage());

            geminiResponse =
                    """
                    Score: 7
        
                    Feedback:
                    Good attempt. Gemini quota exceeded so
                    automatic evaluation is temporarily unavailable.
                    """;
        }

        double score = 0.0;

        try {

            String[] parts =
                    geminiResponse.split("\n");

            String scoreLine =
                    parts[0];

            score = Double.parseDouble(
                    scoreLine.replace("Score:", "")
                            .trim());

        } catch (Exception e) {

            score = 0.0;
        }

        Answer answer =
                Answer.builder()
                        .answerText(
                                request.getAnswerText())
                        .score(score)
                        .feedback(geminiResponse)
                        .question(question)
                        .interviewSession(session)
                        .build();

        answerRepository.save(answer);

        return SubmitAnswerResponse.builder()
                .score(score)
                .feedback(geminiResponse)
                .build();
    }
    public List<DashboardResponse> getDashboard(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<InterviewSession> sessions =
                interviewSessionRepository.findByUser(user);

        return sessions.stream()
                .map(session ->
                        DashboardResponse.builder()
                                .sessionId(session.getId())
                                .role(session.getRole())
                                .difficulty(session.getDifficulty())
                                .score(session.getOverallScore())
                                .build())
                .toList();
    }

    public InterviewResultResponse getResult(
            Long sessionId) {

        InterviewSession session =
                interviewSessionRepository
                        .findById(sessionId)
                        .orElseThrow();

        List<Answer> answers =
                answerRepository
                        .findByInterviewSessionId(sessionId);

        double overallScore =
                answers.stream()
                        .mapToDouble(Answer::getScore)
                        .average()
                        .orElse(0.0);

        session.setOverallScore(overallScore);

        interviewSessionRepository.save(session);

        List<AnswerResultDto> answerDtos =
                answers.stream()
                        .map(answer ->
                                AnswerResultDto.builder()
                                        .question(
                                                answer.getQuestion()
                                                        .getQuestionText())
                                        .answer(
                                                answer.getAnswerText())
                                        .score(
                                                answer.getScore())
                                        .feedback(
                                                answer.getFeedback())
                                        .build())
                        .toList();

        return InterviewResultResponse.builder()
                .sessionId(session.getId())
                .role(session.getRole())
                .difficulty(session.getDifficulty())
                .overallScore(overallScore)
                .answers(answerDtos)
                .build();
    }
}