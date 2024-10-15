package com.codreal.chatservice.repository;

import com.codreal.chatservice.model.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken,String> {
   VerificationToken findVerificationTokenByToken(String token);
}
