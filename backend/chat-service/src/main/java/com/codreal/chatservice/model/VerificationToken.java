package com.codreal.chatservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document (collection = "verification_tokens")
public class VerificationToken {
    @Id
    private String id;
    private String token;
    private String userId;
    private LocalDateTime expiryDate;

    public VerificationToken() {
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

}
