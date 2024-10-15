package com.codreal.chatservice.services;

import com.codreal.chatservice.dto.MessageDto;
import com.codreal.chatservice.model.Message;
import com.codreal.chatservice.repository.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    private static final String MESS_ID_SEQUENCE = "MESS_ID_SEQUENCE";
    @Override
    public MessageDto addMessge(MessageDto messageDto) {
        Message mess = modelMapper.map(messageDto,Message.class);
        long timestamp = System.currentTimeMillis();
        mess.setTimestamp(timestamp);
        mess.setId(String.valueOf(sequenceGeneratorService.generateSequence(MESS_ID_SEQUENCE)));
        mess = messageRepository.save(mess);
        return modelMapper.map(mess,MessageDto.class);
    }

    @Override
    public List<MessageDto> getListMessage(String roomId) {
        List<Message> messageList = messageRepository.findMessageByRoomIdOrderByTimestamp(roomId);
        List<MessageDto> messageDtoList = messageList.stream().map(message -> modelMapper.map(message,MessageDto.class)).collect(
                Collectors.toList());
        return messageDtoList;
    }

    @Override
    public MessageDto getMessById(String id) {
        Message message = messageRepository.findById(id).get();

        return modelMapper.map(message,MessageDto.class);
    }

}
