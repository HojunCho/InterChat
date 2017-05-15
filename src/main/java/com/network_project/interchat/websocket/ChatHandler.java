package com.network_project.interchat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network_project.interchat.VO.ChatObject;
import com.network_project.interchat.VO.InteractInterface;

/**
 * 채팅 관련 Web Socket을 담당할 Web Socket Handler.
 * /websocket/chat.do의 주소와 연결된다. (servlet-context.xml 참조)
 */
public class ChatHandler extends ViewWebSocketHandler {	
	/** JSON parser */
	private ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * JSON String을 받아 이를 가공하여 {@link ChatObject 채팅 객체}를 반환한다.
	 */
	@Override
	protected InteractInterface translatePayload(String payload) throws Exception {
		ChatObject chatobj = mapper.readValue(payload, ChatObject.class);
		chatobj.setUser(general_service.getUserName(chatobj.getUser()));
		return chatobj;
	}
}
