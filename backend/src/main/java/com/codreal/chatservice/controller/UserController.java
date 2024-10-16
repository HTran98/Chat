package com.codreal.chatservice.controller;

import com.codreal.chatservice.dto.UserDto;
import com.codreal.chatservice.model.User;
import com.codreal.chatservice.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping ("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

//    @GetMapping("/getall")
//    public ResponseEntity<List<User>> getall() throws IOException {
//        try{
//            return new ResponseEntity<List<User>>(userService.getall(), HttpStatus.OK);
//        }catch (UserNotFoundException e){
//            return new ResponseEntity("User not Found", HttpStatus.NOT_FOUND);
//        }
//    }

    @PostMapping ("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) throws IOException {
        try {
            return new ResponseEntity<User>(
                    userService.registerUser(user.getEmail(), user.getUsername(), user.getPassword()), HttpStatus.OK);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping ("/find-username")
    public ResponseEntity<UserDto> getUserByUserName(@RequestParam (name = "username") String username,
            @RequestParam (name = "password") String password) throws IOException {
        User user = userService.findUserByUserName(username, password);
        UserDto userDto = new UserDto() ;
        if (user != null) {
            userDto = modelMapper.map(user, UserDto.class);
        }

        return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
    }

}
