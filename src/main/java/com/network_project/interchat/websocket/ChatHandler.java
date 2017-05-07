package com.network_project.interchat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network_project.interchat.VO.ChatObject;
import com.network_project.interchat.VO.InteractInterface;

public class ChatHandler extends ViewWebSocketHandler {	
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected InteractInterface translatePayload(String payload) throws Exception {
		ChatObject chatobj = mapper.readValue(payload, ChatObject.class);
		chatobj.setUser(general_service.getUserName(chatobj.getUser()));
		return chatobj;
	}
}