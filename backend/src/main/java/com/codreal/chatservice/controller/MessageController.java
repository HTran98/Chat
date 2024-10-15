package com.codreal.chatservice.controller;

import com.codreal.chatservice.dto.MessageDto;
import com.codreal.chatservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @GetMapping("/get-mess-in-room")
    private ResponseEntity<List<MessageDto>>getListMessInRoom(@RequestParam ("roomId") String roomId){
        List<MessageDto> messageDtoList = messageService.getListMessage(roomId);
        return  new ResponseEntity<List<MessageDto>>(messageDtoList, HttpStatus.OK);
    }
}
