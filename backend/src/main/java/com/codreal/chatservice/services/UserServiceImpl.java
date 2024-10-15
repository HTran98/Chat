package com.codreal.chatservice.services;

import com.codreal.chatservice.exceptions.UserAlreadyExistException;
import com.codreal.chatservice.exceptions.UserNotFoundException;
import com.codreal.chatservice.model.User;
import com.codreal.chatservice.model.VerificationToken;
import com.codreal.chatservice.repository.UserRepository;
import com.codreal.chatservice.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;

    private static final String USER_ID_SEQUENCE = "USER_ID";
    private static final String TOKEN_SEQUENCE = "TOKEN_SEQUENCE";

    @Override
    public User registerUser(String email, String username, String password) throws MessagingException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already in use.");
        }

        //String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setId(String.valueOf(sequenceGeneratorService.generateSequence(USER_ID_SEQUENCE)));
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);  // Store encrypted password
        user.setVerified(false);

        userRepository.save(user);

        String verificationToken = UUID.randomUUID().toString();

        VerificationToken token = new VerificationToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(5);

        token.setId(String.valueOf(sequenceGeneratorService.generateSequence(TOKEN_SEQUENCE)));
        token.setUserId(user.getId());
        token.setExpiryDate(expiryDate);
        token.setToken(verificationToken);
        verificationTokenRepository.save(token);
        emailService.sendVerificationEmail(email, verificationToken);

        return user;
    }

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findUserByUserName(String userName, String passWords) {
        return userRepository.findUserByUsernameAndPassword(userName, passWords);
    }

}
