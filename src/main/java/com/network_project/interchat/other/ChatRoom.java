package com.network_project.interchat.other;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
import com.network_project.interchat.util.ConcurrentHashSet;

/**
 * 채팅방을 관리하는 클래스.
 * 채팅방 역시 하나의 뷰이며, 채팅창을 보여주는 역할을 한다.
 * 단, 다른 뷰와 달리 ChatRoom은 다른 뷰를 포함할 수 있으며 다른 모든 뷰의 컨테이너 역할을 한다.
 * 만약 10초 이상 자식 뷰를 포함하여 방에 아무도 없을 경우 자동으로 비활성화 된다.
 */
public class ChatRoom extends View {
	/** 로그 기록기 */
	private static Logger logger = LoggerFactory.getLogger(ChatRoom.class);
	
	/** 채팅방 목록 */
	private static Set<ChatRoom> room_list = new ConcurrentHashSet<ChatRoom>();
	
	/**
	 * 채팅방 목록을 반환한다
	 * @return 채팅방 목록
	 */
	public static Set<ChatRoom> getRoomList() {
		return room_list;
	}
	
	/**
	 * 모든 채팅방을 강제로 비활성화 한다.
	 * 서버를 종료시키거나 할 때 사용.
	 */
	public static void disableAllRoom() {
		room_list.forEach(room->room.invalidateRoom());
	}
	
	/** 방에 포함된 뷰의 목록 */
	private List<View> views = new ArrayList<View>();
	/** 현재 사람이 있는 뷰의 갯수 */
	private AtomicInteger left_view = new AtomicInteger();
	/** 방 비활성 타이머 */
	private AtomicReference<Timer> timer = new AtomicReference<Timer>(null);
	
	/** 최근 채팅 기록 */
	private Queue<InteractInterface> recent_chats = new LinkedList<InteractInterface>();
	
	/**
	 * 채팅방 생성자
	 * 방을 생성한 뒤, 10초 타이머를 걸어 10초내로 사람이 안 들어올 경우 자동으로 비활성화한다. 
	 * @param name 채팅방 이름
	 */
	public ChatRoom(String name) {
		super(null, name);
		this.setParent(this);
		addView(this);
		room_list.add(this);
		logger.info("Room \"{}\" is initialized", getName());
		timer.set(new Timer());
		timer.get().schedule(new RoomChecker(), 10000);
	}
	
	/**
	 * 채팅방에 특정 뷰를 추가.
	 * @param view 추가할 뷰
	 */
	public void addView(View view) {
		views.add(view);
		logger.info("Added view \"{}\" to Room \"{}\"", view.getName(), getName());
	}
	
	/**
	 * 현재 채팅방에 소속된 뷰의 목록 반환.
	 * @return 소속된 뷰 목록
	 */
	public List<View> getViewList() {
		return views;
	}
	
	/**
	 * 현재 사람이 남아 있는 뷰의 갯수를 하나 늘임.
	 */
	public void increaseLiveView() {
		left_view.incrementAndGet();
	}
	
	/**
	 * 현재 사람이 남아 있는 뷰의 갯수를 하나 줄임.
	 * 만약 사람이 남아 있는 뷰의 갯수가 0이면 10초 후 방을 파괴하는 타이머 작동.
	 */
	public void decreaseLiveView() {
		if(left_view.decrementAndGet() > 0)
			return;
		Timer old = timer.getAndSet(new Timer());
		if (old != null)
			old.cancel();
		timer.get().schedule(new RoomChecker(), 10000);
	}
	
	/**
	 * 방을 비활성화.
	 * 방에 소속된 모든 뷰를 비활성화 한 뒤, 모든 의존성을 제거한다. 
	 */
	private void invalidateRoom() {
		for (View view : views)
			view.invalidate();
		views.clear();
		room_list.remove(this);
		Timer old = timer.getAndSet(null);
		if (old != null)
			old.cancel();
		logger.info("Invalidated Room \"{}\"", getName());	
	}
	
	/**
	 * 방의 썸네일을 반환한다.
	 * 방의 썸네일은 방에 소속된 첫번째 {@link DrawingView DrawingViwe}의 사진이다.
	 * @return 방의 썸네일
	 */
	public BufferedImage getThumbnail() {
		for (View view : views) {
			if (view instanceof DrawingView)
				return ((DrawingView) view).getImage();
		}
		return null;
	}
	
	/**
	 * 특정 Web Socket에서 데이터를 받을 시 데이터를 채팅한다.
	 * @see #sendChat(ChatObject)
	 * @see ChatObject
	 */
	@Override
	public void interact(WebSocketSession session, InteractInterface obj) {
		sendChat((ChatObject) obj);
	}
	
	/**
	 * 어떤 Web Socket과 연결되었을 경우, 거기에 최근 채팅 기록을 전달한다.
	 * 또한 연결된 Web Socket의 유저명을 알아내어 그 유저가 들어왔음을 채팅한다.
	 */
	@Override
	public void sessionIn(WebSocketSession session) {
		super.sessionIn(session);
		
		List<InteractInterface> chats;
		synchronized (recent_chats) {
			chats = new ArrayList<InteractInterface>(recent_chats);
		}
		
		if (chats.size() > 0) {
			for (InteractInterface chat : chats)
				sendTo(session, chat);
		}
		
		ChatObject chat = new ChatObject();
		chat.setUser("Admin");
		if (general_service == null)
			System.out.println("BAD");
		chat.setContent(general_service.getUserName(session) + "님이 들어왔습니다.");
		
		sendChat(chat);
	}
	
	/**
	 * 특정 Web Socket이 연결해제되었을 경우, 유저명을 알아내어 그 유저가 나갔음을 채팅한다.
	 */
	@Override
	public void sessionOut(WebSocketSession session) {
		super.sessionOut(session);
		ChatObject chat = new ChatObject();
		chat.setUser("Admin");
		chat.setContent(general_service.getUserName(session) + "님이 나갔습니다.");
		
		sendChat(chat);
	}
	
	/**
	 * 채팅 데이터를 최근 채팅 기록에 쌓은 후 연결된 모든 Web Socket에 전달한다.
	 * 최근 채팅 기록이 100개 이상 쌓였을 경우 오래된 순으로 채팅데이터를 삭제한다.
	 * @see View#send(InteractInterface)
	 * @param chat 채팅 데이터
	 */
	private void sendChat(ChatObject chat) {
		synchronized (recent_chats) {
			while(recent_chats.size() >= 100)
				recent_chats.poll();
			recent_chats.add(chat);
			send(chat);
		}
	}
	
	/**
	 * 방을 파괴하는 타이머
	 */
	private class RoomChecker extends TimerTask {
		/**
		 * 특정 시간이 지나 메소드가 실행되면 활성화된 뷰의 갯수를 확인한다.
		 * 특정 시간이 지나고서도 방에 아무도 없으면 방을 파괴한다.
		 */
		public void run() {
			timer.set(null);
			if (left_view.get() == 0)
				invalidateRoom();
		}
	}
}