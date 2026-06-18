package com.ayushi.interview_prep.Service;

import com.ayushi.interview_prep.Repository.UserRepository;
import com.ayushi.interview_prep.Security.JwtService;
import com.ayushi.interview_prep.dto.LoginRequest;
import com.ayushi.interview_prep.dto.LoginResponse;
import com.ayushi.interview_prep.dto.RegisterRequest;
import com.ayushi.interview_prep.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {

        if(userRepository.findByEmail(
                request.getEmail()).isPresent()) {

            throw new RuntimeException(
                    "Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()))
                .build();

        userRepository.save(user);

        return "User Registered";
    }

    public LoginResponse login(LoginRequest request) {


        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow();

        boolean matches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword());

        if(!matches) {
            throw new RuntimeException(
                    "Invalid Credentials");
        }

        String token =
                jwtService.generateToken(
                        user.getEmail());

        return new LoginResponse(token);
    }
}
