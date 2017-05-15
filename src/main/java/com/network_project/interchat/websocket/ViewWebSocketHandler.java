package com.network_project.interchat.websocket;

import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network_project.interchat.VO.InitWebSocketObject;
import com.network_project.interchat.VO.InteractInterface;
import com.network_project.interchat.service.GeneralService;
import com.network_project.interchat.util.ConcurrentHashSet;

public abstract class ViewWebSocketHandler extends TextWebSocketHandler {
	private static Logger logger = LoggerFactory.getLogger(ViewWebSocketHandler.class);
	private Set<WebSocketSession> uninitialized = new ConcurrentHashSet<WebSocketSession>(); 
	
	private ObjectMapper mapper = new ObjectMapper();

	@Resource(name="GeneralService")
	protected GeneralService general_service;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		uninitialized.add(session);
		logger.info("Session Extablished: " + session.getRemoteAddress() + " - " + session.getId());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		if (!uninitialized.remove(session))
			general_service.sessionOut(session);
		logger.info("Uninitialized Session: " + session.getRemoteAddress() + " - " + session.getId());
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		super.handleMessage(session, message);
		if (((String) message.getPayload()).compareTo("NULL") == 0)
			return;
		if (uninitialized.remove(session)) {
			logger.info("Initializing Session: " + session.getRemoteAddress() + " - " + session.getId());
			InitWebSocketObject init_obj = mapper.readValue((String) message.getPayload(), InitWebSocketObject.class);
			if (general_service.getUserName(init_obj.getUserid()) == null || !general_service.sessionIn(session, init_obj.getViewid(), init_obj.getUserid()))
				session.close(CloseStatus.BAD_DATA);
			logger.info("Initialized Session: " + session.getRemoteAddress() + " - " + session.getId());
		}
		else
		{
			InteractInterface obj = translatePayload((String) message.getPayload());
			if (obj == null)
				return;
			general_service.getView(session).interact(session, obj);
		}
	}
	
	protected abstract InteractInterface translatePayload(String payload) throws Exception;
}
