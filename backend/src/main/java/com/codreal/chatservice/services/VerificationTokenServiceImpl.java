package com.codreal.chatservice.services;

import com.codreal.chatservice.model.VerificationToken;
import com.codreal.chatservice.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public VerificationToken saveToken(VerificationToken token) {
        return tokenRepository.save(token);
    }

    @Override
    public void deleteToken(VerificationToken token) {
        tokenRepository.deleteById(token.getId());
    }

    @Override
    public VerificationToken findToken(String token) {
        return tokenRepository.findVerificationTokenByToken(token);
    }

}
