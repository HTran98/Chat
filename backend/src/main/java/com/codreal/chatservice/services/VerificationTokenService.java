package com.codreal.chatservice.services;

import com.codreal.chatservice.model.VerificationToken;

public interface VerificationTokenService {

    VerificationToken saveToken(VerificationToken token);

    void deleteToken(VerificationToken token);
    VerificationToken findToken(String token);
}
