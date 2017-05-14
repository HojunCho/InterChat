package com.network_project.interchat.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.network_project.interchat.other.ChatRoom;
import com.network_project.interchat.other.View;
import com.network_project.interchat.other.ViewRunnableWork;

@Service ("GeneralService")
public final class GeneralServiceImpl implements GeneralService {
	private Map<String, String> user_code2id = new ConcurrentHashMap<String, String>();
	private Map<WebSocketSession, String> user_session2code = new ConcurrentHashMap<WebSocketSession, String>(); 
	
	@PreDestroy
	private void destroyer() {
		ViewRunnableWork.shutdown();
		ChatRoom.disableAllRoom();
	}

	@Override
	public String getUserCode(String user_name) {
		return String.valueOf(user_name.hashCode());
	}
	
	@Override
	public String getUserCode(WebSocketSession session) {
		return user_session2code.get(session);
	}
	
	@Override
	public String getUserName(String user_code) {
		return user_code2id.get(user_code);
	}

	@Override
	public String getUserName(WebSocketSession session) {
		return user_code2id.get(getUserCode(session));
	}

	
	@Override
	public boolean insertUserName(String user_name) {
		if (user_name.compareTo("Admin") == 0)
			return false;
		user_code2id.put(String.valueOf(user_name.hashCode()), user_name);
		return true;
	}
	

	@Override
	public ChatRoom roomFactory(String room_name) {
		return new ChatRoom(room_name);
	}

	@Override
	public Set<ChatRoom> getRoomList() {
		return ChatRoom.getRoomList();
	}
	
	
	private Map<WebSocketSession, String> session_map = new ConcurrentHashMap<WebSocketSession, String>();
	
	@Override
	public boolean sessionIn(WebSocketSession session, String view_id, String user_id) {
		if (View.getViewByID(view_id) == null)
			return false;
		user_session2code.put(session, user_id);
		session_map.put(session, view_id);
		View.getViewByID(view_id).sessionIn(session);
		return true;
	}

	@Override
	public void sessionOut(WebSocketSession session) {
		View.getViewByID(session_map.remove(session)).sessionOut(session);
	}

	@Override
	public View getView(WebSocketSession session) {
		return View.getViewByID(session_map.get(session));	
	}

	@Override
	public View getView(String view_id) {
		return View.getViewByID(view_id);
	}
}
