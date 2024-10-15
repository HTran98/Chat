package com.codreal.chatservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String toEmail, String token) throws MessagingException {
        String url = "http://localhost:8080/api/auth/verify?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email");

            // Tạo nội dung HTML cho email
            String htmlContent = "<h3>Click the button below to verify your account:</h3>"
                    + "<a href='" + url + "' style='"
                    + "padding: 10px 20px; background-color: #007BFF; color: white; "
                    + "text-decoration: none; border-radius: 5px;'>"
                    + "Verify Account</a>";

            helper.setText(htmlContent, true); // true để gửi dưới dạng HTML
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resetPassword(String toEmail) throws MessagingException {
        String url = "http://localhost:4200/reset-password";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email");

            // Tạo nội dung HTML cho email
            String htmlContent = "<h3>Click the link below to reset password:</h3>"
                    + "<a href='" + url + "' style='"
                    + "Reset password</a>";

            helper.setText(htmlContent, true); // true để gửi dưới dạng HTML
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
