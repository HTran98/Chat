package com.codreal.chatservice.repository;

import com.codreal.chatservice.model.ChatRoomResource;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRoomResourceRepository extends MongoRepository<ChatRoomResource, String> {
ChatRoomResource findChatRoomResourceByRoomIdAndUserId(String roomId, String userId);
List<ChatRoomResource> findChatRoomResourceByRoomIdAndStatus(String roomId, String status);
}
