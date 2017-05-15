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

/**
 * Web Socket Handler.
 * Web Socket을 초기화하고 View에 연결할 수 있게한다.
 * 또한 Web Socket에서 들어온 데이터를 가공하여 View가 쉽게 읽을 수 있는 형태로 변환하여 전달해 준다.
 * @see ChatHandler
 * @see DrawingHandler
 */
public abstract class ViewWebSocketHandler extends TextWebSocketHandler {
	/** 로그 기록기 */
	private static Logger logger = LoggerFactory.getLogger(ViewWebSocketHandler.class);
	/** 초기화 되지 못한 Web Socket의 목록 */
	private Set<WebSocketSession> uninitialized = new ConcurrentHashSet<WebSocketSession>(); 
	
	/** JSON parser */
	private ObjectMapper mapper = new ObjectMapper();

	/** 비즈니스 로직을 담당하는 {@link GeneralService GeneralService} Bean. */
	@Resource(name="GeneralService")
	protected GeneralService general_service;
	
	/** 
	 * 맨 처음 Web Socket이랑 연결되었을 시 호출되는 메소드.
	 * 초기화할 목록에 해당 Web Socket을 넣는다.
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		uninitialized.add(session);
		logger.info("Session Extablished: " + session.getRemoteAddress() + " - " + session.getId());
	}

	/**
	 * Web Socket과의 연결이 끊어 졌을 시 호출되는 메소드.
	 * 초기화 목록에서 삭제를 시도한 후, 초기화 목록에 없을 경우 (이미 초기화가 된 경우) 연결된 뷰에서 연결을 해제한다.
	 * @see GeneralService#sessionOut(WebSocketSession)
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		if (!uninitialized.remove(session))
			general_service.sessionOut(session);
		logger.info("Session Closed: " + session.getRemoteAddress() + " - " + session.getId());
	}

	/**
	 * Web Socket에서 데이터를 받았을 시 호출되는 메소드.
	 * 만약 초기화할 목록에 해당 Web Socket이 있을 경우, 받은 데이터를 {@link InitWebSocketObject 초기화 객체}로 변환한 후, Web Socket을 초기화 한다.
	 * 초기화에 실패할 경우 Web Socket을 종료한다.
	 * 초기화할 목록에 해당 Web Socket이 없다면, 데이터를 가공한 후 연결된 뷰에 전달한다.
	 * @see com.network_project.interchat.other.View#interact(WebSocketSession, InteractInterface)
	 */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		super.handleMessage(session, message);
		if (((String) message.getPayload()).compareTo("NULL") == 0)
			return;
		if (uninitialized.remove(session)) {
			logger.info("Initializing Session: " + session.getRemoteAddress() + " - " + session.getId());
			InitWebSocketObject init_obj = mapper.readValue((String) message.getPayload(), InitWebSocketObject.class);
			if (general_service.getUserName(init_obj.getUserid()) == null || !general_service.sessionIn(session, init_obj.getViewid(), init_obj.getUserid())) {
				uninitialized.add(session);
				session.close(CloseStatus.BAD_DATA);
			}
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
	
	/**
	 * 데이터를 가공하는 법이 정의될 메소드
	 * Web Socket에서 데이터는 String 형태로 날아오고, 이를 가공하여 적절한 데이터 형으로 바꾸어 줄 필요가 있다.
	 * @param payload 받은 String 데이터
	 * @return 가공된 데이터
	 * @throws Exception 형식에 알맞지 않은 JSON String이 전달되었을 경우.
	 */
	protected abstract InteractInterface translatePayload(String payload) throws Exception;
}
