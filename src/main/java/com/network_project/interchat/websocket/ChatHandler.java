package com.network_project.interchat.websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network_project.interchat.VO.ChatObject;

public class ChatHandler extends TextWebSocketHandler {	
	private final Logger logger = LogManager.getLogger(getClass());
	private ObjectMapper mapper = new ObjectMapper();
	private Set<WebSocketSession> sessionSet = new HashSet<WebSocketSession>();
	private Map<WebSocketSession, String> user_names = new HashMap<WebSocketSession, String>();
	private int user_num = 0;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		logger.info("낯선 사람" + user_num + " 접속");
		ChatObject chat = new ChatObject();
		chat.setUser("Admin");
		chat.setContent("낯선 사람" + user_num + "님이 들어왔습니다.");
		sessionSet.add(session);
		user_names.put(session, "낯선 사람" + user_num++);
		sendMessage(chat);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		logger.info(user_names.get(session) + " 종료");
		ChatObject chat = new ChatObject();
		chat.setUser("Admin");
		chat.setContent(user_names.get(session) + "님이 나가셨습니다.");
		sessionSet.remove(session);
		user_names.remove(session);
		sendMessage(chat);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		super.handleMessage(session, message);
		ChatObject chat = new ChatObject();
		chat.setUser(user_names.get(session));
		chat.setContent((String) message.getPayload());
		if (chat.getContent().compareTo("NULL") == 0)
			return;
		sendMessage(chat);
	}
	
	public void sendMessage (ChatObject chat) {
		for (WebSocketSession session: this.sessionSet) {
			if (session.isOpen()) {
				try{
					session.sendMessage(new TextMessage(mapper.writeValueAsString(chat)));
				} catch (Exception ignored) {
					this.logger.error("fail to send message!", ignored);
				}
			}
		}
	}
}
