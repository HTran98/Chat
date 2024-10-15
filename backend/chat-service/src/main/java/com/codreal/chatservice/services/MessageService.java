package com.codreal.chatservice.services;

import com.codreal.chatservice.dto.MessageDto;

import java.util.List;

public interface MessageService {
    MessageDto addMessge(MessageDto messageDto);
    List<MessageDto> getListMessage(String roomId);
    MessageDto getMessById(String id);
}
