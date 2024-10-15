package com.codreal.chatservice.repository;

import com.codreal.chatservice.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom,String> {
    List<ChatRoom> findByRoomNameContaining(String roomName);
}
