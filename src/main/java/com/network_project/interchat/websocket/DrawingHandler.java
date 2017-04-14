package com.network_project.interchat.websocket;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network_project.interchat.VO.LineObject;
import com.network_project.interchat.service.DrawingService;

public class DrawingHandler extends TextWebSocketHandler {
	private final Logger logger = LogManager.getLogger(getClass());
	private ObjectMapper mapper = new ObjectMapper();
	private Set<WebSocketSession> sessionSet = new HashSet<WebSocketSession>();
	
	@Resource(name="drawingService")
	private DrawingService drawingService;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		sessionSet.add(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		sessionSet.remove(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		super.handleMessage(session, message);
		if (((String) message.getPayload()).compareTo("NULL") == 0)
			return;
		drawingService.drawLine(mapper.readValue((String) message.getPayload(), LineObject.class));
		sendMessage (message);
	}
	
	public void sendMessage (WebSocketMessage<?> message) {
		for (WebSocketSession session: this.sessionSet) {
			if (session.isOpen()) {
				try{
					synchronized(session) {
						session.sendMessage(message);
					}
				} catch (Exception ignored) {
					this.logger.error("fail to send message!", ignored);
				}
			}
		}
	}	
}
