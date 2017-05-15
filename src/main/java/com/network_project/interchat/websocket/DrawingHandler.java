package com.network_project.interchat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network_project.interchat.VO.InteractInterface;
import com.network_project.interchat.VO.LineObject;

/**
 * 그림판 관련 Web Socket을 담당할 Web Socket Handler.
 * /websocket/drawing.do의 주소와 연결된다. (servlet-context.xml 참조)
 */
public class DrawingHandler extends ViewWebSocketHandler {
	/** JSON parser */
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * JSON String을 받아 이를 가공하여 {@link LineObject 라인 객체}를 반환한다.
	 */
	@Override
	protected InteractInterface translatePayload(String payload) throws Exception {
		return mapper.readValue(payload, LineObject.class);
	}
}
