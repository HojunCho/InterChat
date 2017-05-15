package com.network_project.interchat.other;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.web.socket.WebSocketSession;

import com.network_project.interchat.VO.InteractInterface;
import com.network_project.interchat.VO.LineObject;

/**
 * 그림판 뷰를 관리하는 클래스.
 * 많은 사람이 다같이 그림을 그릴 수 있도록 해준다.
 */
public class DrawingView extends View {
	/** 그려야할 {@link LineObject 라인 객체} 큐.*/
	private BlockingQueue<InteractInterface> drawing_queue = new LinkedBlockingQueue<InteractInterface>();
	/** 저장된 그림. 기본은 800X600 크기이다. */
	private BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
	/** 저장된 그림을 수정하기 위한 그래픽 객체 */
	private Graphics2D graphic = image.createGraphics();

	/** 그림을 그리는 그리기 에이전트 */
	private DrawingViewWork drawing_worker = new DrawingViewWork();

	/**
	 * 그림판 뷰 생성자.
	 * 그래픽 객체를 초기화 한다.
	 * @param parent 소속한 채팅방
	 * @param view_name 그림판 뷰의 이름
	 */
	public DrawingView(ChatRoom parent, String view_name) {
		super(parent, view_name);
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphic.setColor(Color.WHITE);
		graphic.drawRect(0, 0, 800, 600);
		graphic.setColor(Color.BLACK);
		graphic.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	}
	
	/**
	 * 그림판의 현재 그림을 반환
	 * @return 그림판의 현재 그림
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * {@link LineObject 라인 객체}를 받아 이를 큐에 넣고 그리기 에이전트를 깨운다.
	 */
	@Override
	public void interact(WebSocketSession session, InteractInterface obj) {
		if (!(obj instanceof LineObject))
			return;	
		drawing_queue.add(obj);
		drawing_worker.alarmExecutor();
	}
	
	/**
	 * 그리기 에이전트.
	 * 큐에 쌓인 {@link LineObject 라인 객체}을 그린다.
	 */
	private class DrawingViewWork extends ViewRunnableWork {
		/**
		 * 그리기 에이전트가 실행될 경우.
		 * 큐에 쌓인 {@link LineObject 라인 객체}를 그린다.
		 * 이때 객체의 값을 참조하여 각 속성에 알맞은 모양으로 그린다.
		 * 모든 요소를 그린 후, 그린 라인 객체들을 모든 연결된 Web Socket에 전달한다. 
		 */
		@Override
		public void run() {
			List<InteractInterface> lines = new ArrayList<InteractInterface>();
			drawing_queue.drainTo(lines);
			if (lines.isEmpty())
				return;
			
			for (InteractInterface rawLine : lines) {
				LineObject line = (LineObject)rawLine;
				String color = line.getColor();
				int lineWidth = line.getlineWidth();
				
				if(color.equals("black"))
					graphic.setColor(Color.BLACK);
				else if(color.equals("red"))
					graphic.setColor(Color.RED);
				else if(color.equals("blue"))
					graphic.setColor(Color.BLUE);
				else if(color.equals("white"))
					graphic.setColor(Color.WHITE);
				
				graphic.setStroke(new BasicStroke(lineWidth - 0.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				
				graphic.drawLine(line.getPrevX(), line.getPrevY(), line.getCurrX(), line.getCurrY());
			} 	
			sendAll(lines);
		}
	}
}
