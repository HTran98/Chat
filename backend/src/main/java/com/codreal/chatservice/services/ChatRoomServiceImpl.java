package com.codreal.chatservice.services;

import com.codreal.chatservice.dto.ChatRoomDto;
import com.codreal.chatservice.dto.MemberDto;
import com.codreal.chatservice.model.ChatRoom;
import com.codreal.chatservice.repository.ChatRoomRepository;
import com.codreal.chatservice.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatRoomServiceImpl implements ChatRoomService{

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private MessageRepository messageRepository;
    private static final String CHAT_ROOM_SEQUENCE = "CHAT_ROOM_SEQUENCE";
    private static final String STATUS_ADD = "ADD";
    private static final String STATUS_REMOVE = "REMOVE";
    @Override
    public ChatRoomDto addRoom(ChatRoomDto chatRoomDto) {

        chatRoomDto.setRoomId(String.valueOf(sequenceGeneratorService.generateSequence(CHAT_ROOM_SEQUENCE)));
        ChatRoom chatRoom = modelMapper.map(chatRoomDto,ChatRoom.class);
        chatRoom = chatRoomRepository.save(chatRoom);
        return modelMapper.map(chatRoom,ChatRoomDto.class);
    }

    @Override
    public List<ChatRoomDto> getAll() {
        List<ChatRoom> lstChatRoom = chatRoomRepository.findAll();
        return lstChatRoom.stream().map(chatRoom -> modelMapper.map(chatRoom,ChatRoomDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomDto> getByRoomName(String roomName) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findByRoomNameContaining(roomName);
        List<ChatRoomDto> chatRoomListDto = chatRoomList.stream().map(chatRoom -> modelMapper.map(chatRoom,
                ChatRoomDto.class)).collect(Collectors.toList());
        return chatRoomListDto;
    }

}
