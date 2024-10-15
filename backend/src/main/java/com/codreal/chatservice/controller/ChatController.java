package com.codreal.chatservice.controller;


import com.codreal.chatservice.dto.MessageDto;
import com.codreal.chatservice.model.Message;
import com.codreal.chatservice.repository.MessageRepository;
import com.codreal.chatservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;
    @MessageMapping ("/chat.sendMessage")
    @SendTo ("/topic/public")
    public MessageDto sendMessage(@Payload MessageDto chatMessage) {
        chatMessage.setId(messageService.addMessge(chatMessage).getId()); ;
        return chatMessage;
    }

    @MessageMapping ("/chat.addUser")
    @SendTo ("/topic/public")
    public MessageDto addUser(@Payload MessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}

