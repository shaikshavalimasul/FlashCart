package com.flashcart.service;

import com.flashcart.model.User;
import com.flashcart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flashcart.dto.UserRegistrationRequest;
import com.flashcart.dto.UserResponse;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse registerUser(
            UserRegistrationRequest request) {

        if (userRepository.existsByEmail(
                request.getEmail())) {
            throw new RuntimeException(
                    "Email already registered: "
                            + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

}