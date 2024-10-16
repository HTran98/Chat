package com.codreal.chatservice.services;

import com.codreal.chatservice.dto.ChatRoomResourceDto;
import com.codreal.chatservice.dto.MemberDto;

import java.util.List;

public interface ChatRoomResourceService {
long changeMember(MemberDto memberDto);
long getListMember(String roomId);
}
