package com.codreal.chatservice.services;

import javax.mail.MessagingException;

public interface EmailService {
    void  sendVerificationEmail(String toEmail, String token) throws MessagingException;
    void  resetPassword(String toEmail) throws MessagingException;
}
