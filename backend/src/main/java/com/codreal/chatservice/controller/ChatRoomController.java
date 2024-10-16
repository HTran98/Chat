package com.codreal.chatservice.controller;

import com.codreal.chatservice.dto.ChatRoomDto;
import com.codreal.chatservice.dto.ChatRoomResourceDto;
import com.codreal.chatservice.dto.MemberDto;
import com.codreal.chatservice.services.ChatRoomResourceService;
import com.codreal.chatservice.services.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/room")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatRoomResourceService chatRoomResourceService;

    @PostMapping ("/add-room")
    private ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody ChatRoomDto chatRoom) {

        ChatRoomDto chatRoomDto = chatRoomService.addRoom(chatRoom);

        return new ResponseEntity<ChatRoomDto>(chatRoomDto, HttpStatus.CREATED);
    }

    @GetMapping ("/get-list-room")
    private ResponseEntity<List<ChatRoomDto>> createChatRoom() {

        List<ChatRoomDto> lstChatRoom = chatRoomService.getAll();
        return new ResponseEntity<List<ChatRoomDto>>(lstChatRoom, HttpStatus.CREATED);
    }

    @GetMapping ("/get-list-room-by-name")
    private ResponseEntity<List<ChatRoomDto>> getListRoomByName(@RequestParam ("roomName") String roomName) {

        List<ChatRoomDto> lstChatRoom = chatRoomService.getByRoomName(roomName);
        return new ResponseEntity<List<ChatRoomDto>>(lstChatRoom, HttpStatus.CREATED);
    }

    @PostMapping ("/change-member")
    private ResponseEntity<Long> changeMember(@RequestBody MemberDto memberDto) {
        return new ResponseEntity<Long>(chatRoomResourceService.changeMember(memberDto), HttpStatus.OK);
    }

    @GetMapping ("/count-member")
    private ResponseEntity<Long> changeMember(@RequestParam ("roomId") String roomId) {
        return new ResponseEntity<Long>(chatRoomResourceService.getListMember(roomId), HttpStatus.OK);
    }


}
