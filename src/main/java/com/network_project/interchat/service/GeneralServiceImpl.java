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

/**
 * General Service의 실제 구현부 
 */
@Service ("GeneralService")
public final class GeneralServiceImpl implements GeneralService {
	/** 유저 코드를 key로 하고 유저 이름을 value로 한 해시 맵 */
	private Map<String, String> user_code2name = new ConcurrentHashMap<String, String>();
	/** Web Socket을 key로 하고 연결된 유저의 유저 코드를 value로 한 해시 맵*/
	private Map<WebSocketSession, String> user_session2code = new ConcurrentHashMap<WebSocketSession, String>(); 
	
	/**
	 * Service가 Bean에서 파괴 될시 호출되는 소멸자.
	 * 서버가 중지될 때 호출.
	 * 모든 일하기 에이전트를 중지시키고, 모든 방을 비활성화한다.
	 */
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
		return user_code2name.get(user_code);
	}

	@Override
	public String getUserName(WebSocketSession session) {
		return user_code2name.get(getUserCode(session));
	}

	
	@Override
	public boolean insertUserName(String user_name) {
		if (user_name.compareTo("Admin") == 0 || user_name.compareTo("") == 0)
			return false;
		user_code2name.put(String.valueOf(user_name.hashCode()), user_name);
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
	
	/** Web Socket을 key로 연결된 뷰의 id를 value로 하는 맵 */
	private Map<WebSocketSession, String> session_map = new ConcurrentHashMap<WebSocketSession, String>();
	
	@Override
	public boolean sessionIn(WebSocketSession session, String view_id, String user_code) {
		if (View.getViewByID(view_id) == null)
			return false;
		user_session2code.put(session, user_code);
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
