package com.network_project.interchat.service;

import java.util.Set;

import org.springframework.web.socket.WebSocketSession;

import com.network_project.interchat.other.ChatRoom;
import com.network_project.interchat.other.View;

/**
 * 서비스 단의 비즈니스 로직을 수행하는 General Service.
 * @see GeneralServiceImpl
 */
public interface GeneralService {
	/**
	 * 유저 이름을 받으면 해당하는 유저 코드를 반환하는 메소드
	 * @see #getUserName(String)
	 * @param user_name 유저 이름
	 * @return 해당하는 유저 코드
	 */
	String getUserCode(String user_name);
	
	/**
	 * Web Socket을 받으면 그 소켓을 소유한 유저의 유저 코드를 반환하는 메소드
	 * @see #getUserName(WebSocketSession)
	 * @param session Web Socket
	 * @return 해당 Web Socket을 소유한 유저의 유저 코드
	 */
	String getUserCode(WebSocketSession session);
	
	/**
	 * 유저 코드를 받으면 해당하는 유저 이름을 반환하는 메소드
	 * @see #getUserCode(String)
	 * @param user_code 유저 코드
	 * @return 해당하는 유저 이름
	 */
	String getUserName(String user_code);
	
	/**
	 * Web Socket을 받으면 그 소켓을 소유한 유저의 이름을 반환하는 메소드
	 * @see #getUserCode(WebSocketSession)
	 * @param session Web Socket
	 * @return 해당 Web Socket을 소유한 유저의 이름
	 */
	String getUserName(WebSocketSession session);
	
	/**
	 * 유저 이름을 받아서 해당 유저 이름을 사용할 수 있는 지 확인하고 유저 이름을 등록.
	 * @param user_same 유저 이름
	 * @return 등록에 성공했을 시 true, 사용할 수 없는 이름일 시 false
	 */
	boolean insertUserName(String user_same);
	
	/**
	 * 새로운 방을 만드는 메소드.
	 * @param room_name 새로 만들 방의 이름
	 * @return 만들어지 방
	 */
	ChatRoom roomFactory(String room_name);

	/**
	 * 현재 존재하는 방의 목록을 반환하는 메소드
	 * @return 현재 존재하는 방의 목록
	 */
	Set<ChatRoom> getRoomList();

	/**
	 * 어떤 Web Socket을 특정한 뷰와 유저에 연결하는 메소드
	 * @see #sessionOut(WebSocketSession)
	 * @param session 연결할 Web Socket
	 * @param view_id 연결할 뷰의 id
	 * @param user_code 연결할 유저의 유저 코드
	 * @return 성공할 시 true, 실패할 시 false
	 */
	boolean sessionIn(WebSocketSession session, String view_id, String user_code);
	
	/**
	 * 어떤 Web Socket을 연결 해제하는 메소드
	 * @see #sessionIn(WebSocketSession, String, String)
	 * @param session 연결 해제할 Web Socket
	 */
	void sessionOut(WebSocketSession session);
	
	/**
	 * 특정 Web Socket과 연결된 뷰를 반환.
	 * @param session Web Socket
	 * @return Web Socket과 연결된 뷰
	 */
	View getView(WebSocketSession session);
	
	/**
	 * 뷰 id를 받아 해당하는 뷰를 반환.
	 * @param view_id 뷰 id
	 * @return 해당하는 뷰
	 */
	View getView(String view_id);
}