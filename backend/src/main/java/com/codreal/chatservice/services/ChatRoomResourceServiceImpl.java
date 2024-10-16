package com.codreal.chatservice.services;

import com.codreal.chatservice.dto.ChatRoomResourceDto;
import com.codreal.chatservice.dto.MemberDto;
import com.codreal.chatservice.model.ChatRoomResource;
import com.codreal.chatservice.repository.ChatRoomResourceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomResourceServiceImpl implements ChatRoomResourceService{

    @Autowired
    private ChatRoomResourceRepository chatRoomResourceRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    private static final String CHAT_ROOM_RESOURCE_SEQUENCE = "CHAT_ROOM_RESOURCE_SEQUENCE";
    private static final String JOIN ="JOIN";
    private static final String MOVE ="MOVE";
    private static final Long MAX_MEMBER = 30L;
    @Override
    public long changeMember(MemberDto memberDto) {
        long totalMember = chatRoomResourceRepository.findChatRoomResourceByRoomIdAndStatus(memberDto.getRoomId(),JOIN).size();
        ChatRoomResource chatRoomResource = chatRoomResourceRepository.findChatRoomResourceByRoomIdAndUserId(memberDto.getRoomId(), memberDto.getUserId());
        if(totalMember == MAX_MEMBER && memberDto.getStatus().equals(JOIN)){
            return -1;
        }
        if(chatRoomResource == null && totalMember < MAX_MEMBER){
            chatRoomResource = new ChatRoomResource();
            chatRoomResource.setId(String.valueOf(sequenceGeneratorService.generateSequence(CHAT_ROOM_RESOURCE_SEQUENCE)));
            chatRoomResource.setRoomId(memberDto.getRoomId());
            chatRoomResource.setUserId(memberDto.getUserId());
        }
        chatRoomResource.setStatus(memberDto.getStatus());

        chatRoomResourceRepository.save(chatRoomResource);
        return chatRoomResourceRepository.findChatRoomResourceByRoomIdAndStatus(memberDto.getRoomId(),JOIN).size();
    }

    @Override
    public long getListMember(String roomId) {
        return chatRoomResourceRepository.findChatRoomResourceByRoomIdAndStatus(roomId,JOIN).size();
    }

}
