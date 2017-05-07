package com.network_project.interchat.service;

import java.util.Set;

import org.springframework.web.socket.WebSocketSession;

import com.network_project.interchat.other.ChatRoom;
import com.network_project.interchat.other.View;

public interface GeneralService {
	String getUserName(String user_code);
	String getUserCode(String user_name);
	boolean insertUserName(String user_same);
	
	ChatRoom roomFactory(String room_name);
	Set<ChatRoom> getRoomList();

	boolean sessionIn(WebSocketSession session, String view_id);
	void sessionOut(WebSocketSession session);
	View getView(WebSocketSession session);
	View getView(String view_id);
}