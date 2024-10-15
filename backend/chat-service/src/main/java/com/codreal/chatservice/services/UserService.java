package com.codreal.chatservice.services;

import com.codreal.chatservice.exceptions.UserAlreadyExistException;
import com.codreal.chatservice.exceptions.UserNotFoundException;
import com.codreal.chatservice.model.User;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    User registerUser(String email, String username, String password) throws MessagingException;
    User getUserById(String id);
    void saveUser(User user);
    User findUserByUserName(String userName, String passWords);
}
