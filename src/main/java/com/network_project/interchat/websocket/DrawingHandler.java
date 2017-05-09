package com.network_project.interchat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network_project.interchat.VO.InteractInterface;
import com.network_project.interchat.VO.LineObject;

public class DrawingHandler extends ViewWebSocketHandler {
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected InteractInterface translatePayload(String payload) throws Exception {
		return mapper.readValue(payload, LineObject.class);
	}
}
