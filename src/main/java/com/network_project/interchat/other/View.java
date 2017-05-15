package com.network_project.interchat.other;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.network_project.interchat.VO.InteractInterface;
import com.network_project.interchat.service.GeneralService;
import com.network_project.interchat.util.ConcurrentHashSet;

/**
 * 뷰를 관리하는 클래스.
 * @see ChatRoom
 * @see DrawingView
 */
@Configurable(value="view")
public abstract class View {
	/** View id 해시 맵. id를 key로 하여 해당하는 뷰를 value로 가진다.*/
	private static Map<String, View> view_id_map = new ConcurrentHashMap<String, View>();	
	/** View id를 할당하기 위한 카운터. */
	private static AtomicInteger view_count = new AtomicInteger(0);

	/**
	 * 새로운 View id를 할당한다.
	 * @return 새로운 View id
	 */
	private static String getNewViewID () {
		return String.valueOf(view_count.incrementAndGet());
	}
	
	/**
	 * View id에 해당하는 뷰를 찾는다.
	 * 없을 경우 NULL 반환.
	 * @param view_id 찾고자 하는 뷰의 id
	 * @return 해당하는 뷰
	 */
	public static View getViewByID(String view_id) {
		return view_id_map.get(view_id);
	}

	/** 비즈니스 로직을 담당하는 {@link GeneralService GeneralService} Bean. */
	@Resource(name="GeneralService")
	protected GeneralService general_service;
	
	/** 소속된 방 */
	private ChatRoom parent;
	/** 고유의 View id. 처음 만들어질 때 할당. */
	private final String view_id = getNewViewID();
	/** 뷰의 이름 */
	private String view_name;
	/** 로그 편집기 */
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/** JSON 파서 */
	private ObjectMapper mapper = new ObjectMapper();
	
	/** 해당 뷰에 접속중인 Web Socket Session의 목록 */
	private Set<WebSocketSession> sessions = new ConcurrentHashSet<WebSocketSession>();
	
	/**
	 * 뷰 생성자
	 * @param parent 소속된 방
	 * @param view_name 뷰의 이름
	 */
	protected View(ChatRoom parent, String view_name) {
		logger.info("Creating View {}, \"{}\"", view_id, view_name);
		this.parent = parent;
		this.view_name = view_name;
		view_id_map.put(this.view_id, this);
	}
	
	/**
	 * 소속된 방 설정
	 * @param parent 소속된 방
	 */
	final protected void setParent(ChatRoom parent) {
		this.parent = parent;
	}
	
	/**
	 * 뷰를 비활성화
	 * 모든 연결된 Web Socket을 끊고 모든 의존성을 초기화한다.
	 */
	final protected void invalidate() {
		if(!sessions.isEmpty()) {
			sessions.forEach(session->{
				try {
					session.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			});
		}
		view_id_map.remove(view_id);
		parent = null;
		logger.info("Invalidated View {}", view_id);
	}
	
	/**
	 * 뷰의 id를 반환한다
	 * @return 뷰의 id
	 */
	final public String getID() {
		return view_id;
	}
	
	/**
	 * 뷰의 이름을 반환한다
	 * @return 뷰의 이름
	 */
	final public String getName() {
		return view_name;
	}
	
	/**
	 * 어떤 Web Socket을 이 뷰에 연결한다
	 * @param session 연결할 Web Socket
	 */
	public void sessionIn(WebSocketSession session) {
		if (sessions.isEmpty())
			parent.increaseLiveView();
		sessions.add(session);
		logger.info("View " + view_name + " - " + view_id + " Session In:" + session.getRemoteAddress() + " - " + session.getId());
	}
	
	/**
	 * 어떤 Web Socket을 이 뷰에서 연결 해제한다 
	 * @param session 연결  해제할 Web Socket
	 */
	public void sessionOut(WebSocketSession session) {
		sessions.remove(session);
		logger.info("View " + view_name + " - " + view_id + " Session Out:" + session.getRemoteAddress() + " - " + session.getId());
		if (sessions.isEmpty())
			parent.decreaseLiveView();
	}
	
	/**
	 * 연결된 모든 Web Socket에 데이터를 보낸다
	 * @param obj 보낼 데이터
	 */
	final protected void send(InteractInterface obj) {
		for (WebSocketSession session: this.sessions) {
			if (!session.isOpen())
				continue;
			
			try{
				session.sendMessage(new TextMessage(mapper.writeValueAsString(obj)));
			}
			catch (SocketTimeoutException e) {}
			catch (Exception ignored) {
				this.logger.error("fail to send message!", ignored);
			}
		}
	}

	/**
	 * 연결된 모든 Web Socket에 데이터 더미를 보낸다
	 * @param objs 보낼 데이터 더미
	 */
	final protected void sendAll(List<InteractInterface> objs) {
		for (WebSocketSession session: this.sessions) {
			if (!session.isOpen())
				continue;
			
			try{
				session.sendMessage(new TextMessage(mapper.writeValueAsString(objs)));
			}
			catch (SocketTimeoutException e) {} 
			catch (Exception ignored) {
				this.logger.error("fail to send message!", ignored);
			}
		}
	}
	
	/**
	 * 특정 Web Socket에 데이터를 보낸다
	 * @param session 데이터를 보낼 Web Socket
	 * @param obj 보낼 데이터
	 */
	final protected void sendTo(WebSocketSession session, InteractInterface obj) {
		try{
			session.sendMessage(new TextMessage(mapper.writeValueAsString(obj)));
		}
		catch (SocketTimeoutException e) {}
		catch (Exception ignored) {
			this.logger.error("fail to send message!", ignored);
		}
	}

	/**
	 * 특정 Web Socket에 데이터 더미를 보낸다
	 * @param session 데이터 더미를 보낼 Web Socket
	 * @param objs 보낼 데이터 더미
	 */
	final protected void sendToAll(WebSocketSession session, List<InteractInterface> objs) {
		try{
			session.sendMessage(new TextMessage(mapper.writeValueAsString(objs)));
		}
		catch (SocketTimeoutException e) {}
		catch (Exception ignored) {
			this.logger.error("fail to send message!", ignored);
		}	
	}
	
	/**
	 * 특정 Web Socket에서 데이터를 받았을 경우 실행되는 메소드
	 * @param session 데이터를 보낸 Web Socket
	 * @param obj 보낸 데이터
	 */
	public abstract void interact(WebSocketSession session, InteractInterface obj);
}
