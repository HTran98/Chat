package com.codreal.chatservice.repository;

import com.codreal.chatservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    User findUserByUsernameAndPassword(String userName, String password);
}
