package com.codreal.chatservice.controller;

import com.codreal.chatservice.model.User;
import com.codreal.chatservice.model.VerificationToken;
import com.codreal.chatservice.services.EmailService;
import com.codreal.chatservice.services.UserService;
import com.codreal.chatservice.services.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping ("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenService tokenRepository;
    @Autowired
    private EmailService emailService;

    @GetMapping ("/verify")
    public ResponseEntity<Object> verifyUser(@RequestParam String token) {
        VerificationToken verificationToken = tokenRepository.findToken(token);
        if(verificationToken != null){
            LocalDateTime time = LocalDateTime.now();
            if(time.isBefore(verificationToken.getExpiryDate())){
                User user = userService.getUserById(verificationToken.getUserId());
                user.setVerified(true);
                userService.saveUser(user);

                tokenRepository.deleteToken(verificationToken);
                URI loginUri = URI.create("http://localhost:4200/login");
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(loginUri);
                return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
            }
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid token");
        }
        return null;
    }
    @GetMapping ("/reset-password")
    public String resetPassword(@RequestParam String email) throws MessagingException {
        String mess = "";
        if(!StringUtils.isEmpty(email)){
//            User user = userService.findUserByUserName()
            emailService.resetPassword(email);
            mess = "success";
        }else {
            mess =  "Invalid email";
        }
        return mess;
    }
}
