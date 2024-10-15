package com.codreal.chatservice.repository;

import com.codreal.chatservice.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findMessageByRoomIdOrderByTimestamp(String roomId);
    @Query (value = "{}", count = true)
    long countDistinctBySender();
}
