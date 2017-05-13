package com.network_project.interchat.other;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import com.network_project.interchat.VO.ChatObject;
import com.network_project.interchat.VO.InteractInterface;

public class ChatRoom extends View {
	private static Logger logger = LoggerFactory.getLogger(ChatRoom.class);
	
	private static Set<ChatRoom> room_list = new HashSet<ChatRoom>();
	
	public static Set<ChatRoom> getRoomList() {
		return room_list;
	}
	
	public static void disableAllRoom() {
		room_list.forEach(room->room.invalidateRoom());
	}
	
	private List<View> views = new ArrayList<View>();
	private AtomicInteger left_view = new AtomicInteger();
	private AtomicReference<Timer> timer = new AtomicReference<Timer>(null);
	
	public ChatRoom(String name) {
		super(null, name);
		this.setParent(this);
		addView(this);
		synchronized(room_list) {
			room_list.add(this);
		}
		logger.info("Room \"{}\" is initialized", getName());
		timer.set(new Timer());
		timer.get().schedule(new RoomChecker(), 10000);
	}
	
	public void addView(View view) {
		views.add(view);
		logger.info("Added view \"{}\" to Room \"{}\"", view.getName(), getName());
	}
	
	public List<View> getViewList() {
		return views;
	}
	
	public void increaseLiveView() {
		left_view.incrementAndGet();
	}
	
	public void decreaseLiveView() {
		if(left_view.decrementAndGet() > 0)
			return;
		Timer old = timer.getAndSet(new Timer());
		if (old != null)
			old.cancel();
		timer.get().schedule(new RoomChecker(), 10000);
	}
	
	private void invalidateRoom() {
		for (View view : views)
			view.invalidate();
		views.clear();
		synchronized(room_list) {
			room_list.remove(this);
		}
		Timer old = timer.getAndSet(null);
		if (old != null)
			old.cancel();
		logger.info("Invalidated Room \"{}\"", getName());	
	}
	
	public BufferedImage getThumbnail() {
		for (View view : views) {
			if (view instanceof DrawingView)
				return ((DrawingView) view).getImage();
		}
		return null;
	}
	
	@Override
	public void interact(WebSocketSession session, InteractInterface obj) {
		send(obj);
	}
	
	@Override
	public void sessionIn(WebSocketSession session) {
		super.sessionIn(session);
		ChatObject chat = new ChatObject();
		chat.setUser("Admin");
		if (general_service == null)
			System.out.println("BAD");
		chat.setContent(general_service.getUserName(session) + "님이 들어왔습니다.");
		send(chat);
	}
	
	@Override
	public void sessionOut(WebSocketSession session) {
		super.sessionOut(session);
		ChatObject chat = new ChatObject();
		chat.setUser("Admin");
		chat.setContent(general_service.getUserName(session) + "님이 나갔습니다.");
		send(chat);
	}
	
	private class RoomChecker extends TimerTask {
		public void run() {
			timer.set(null);
			if (left_view.get() == 0)
				invalidateRoom();
		}
	}
}