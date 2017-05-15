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

@Configurable(value="view")
public abstract class View {
	private static Map<String, View> view_id_map = new ConcurrentHashMap<String, View>();
	private static AtomicInteger view_count = new AtomicInteger(0);

	private static String getNewViewID () {
		return String.valueOf(view_count.incrementAndGet());
	}
	
	public static View getViewByID(String view_id) {
		return view_id_map.get(view_id);
	}

	@Resource(name="GeneralService")
	protected GeneralService general_service;
	
	private ChatRoom parent;
	private final String view_id = getNewViewID();
	private String view_name;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private ObjectMapper mapper = new ObjectMapper();
	
	private Set<WebSocketSession> sessions = new ConcurrentHashSet<WebSocketSession>();
	
	protected View(ChatRoom parent, String view_name) {
		logger.info("Creating View {}, \"{}\"", view_id, view_name);
		this.parent = parent;
		this.view_name = view_name;
		view_id_map.put(this.view_id, this);
	}
	
	final protected void setParent(ChatRoom parent) {
		this.parent = parent;
	}
	
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
	
	final public String getID() {
		return view_id;
	}
	
	final public String getName() {
		return view_name;
	}
	
	public void sessionIn(WebSocketSession session) {
		if (sessions.isEmpty())
			parent.increaseLiveView();
		sessions.add(session);
		logger.info("View " + view_name + " - " + view_id + " Session In:" + session.getRemoteAddress() + " - " + session.getId());
	}
	
	public void sessionOut(WebSocketSession session) {
		sessions.remove(session);
		logger.info("View " + view_name + " - " + view_id + " Session Out:" + session.getRemoteAddress() + " - " + session.getId());
		if (sessions.isEmpty())
			parent.decreaseLiveView();
	}
	
	final protected void send(InteractInterface obj) {
		for (WebSocketSession session: this.sessions) {
			if (!session.isOpen())
				continue;
			
			try{
				session.sendMessage(new TextMessage(mapper.writeValueAsString(obj)));
			} catch (Exception ignored) {
				this.logger.error("fail to send message!", ignored);
			}
		}
	}

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
	
	final protected void sendTo(WebSocketSession session, InteractInterface obj) {
		try{
			session.sendMessage(new TextMessage(mapper.writeValueAsString(obj)));
		}
		catch (SocketTimeoutException e) {}
		catch (Exception ignored) {
			this.logger.error("fail to send message!", ignored);
		}
	}

	final protected void sendToAll(WebSocketSession session, List<InteractInterface> objs) {
		try{
			session.sendMessage(new TextMessage(mapper.writeValueAsString(objs)));
		} catch (Exception ignored) {
			this.logger.error("fail to send message!", ignored);
		}	
	}
	
	public abstract void interact(WebSocketSession session, InteractInterface obj);
}
