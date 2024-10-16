package com.codreal.chatservice.services;

import com.codreal.chatservice.dto.ChatRoomDto;
import com.codreal.chatservice.dto.MemberDto;
import com.codreal.chatservice.model.ChatRoom;

import java.util.List;

public interface ChatRoomService {
  ChatRoomDto addRoom(ChatRoomDto chatRoom);
  List<ChatRoomDto> getAll();
  List<ChatRoomDto> getByRoomName(String roomName);
}
