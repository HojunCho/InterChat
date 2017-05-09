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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.springframework.web.socket.WebSocketSession;

import com.network_project.interchat.VO.InteractInterface;
import com.network_project.interchat.VO.LineObject;
import com.network_project.interchat.service.GeneralService;

public class DrawingView extends View {
	private BlockingQueue<InteractInterface> drawing_queue = new LinkedBlockingQueue<InteractInterface>();
	private BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
	private Graphics2D graphic = image.createGraphics();

	private DrawingViewWork drawing_worker = new DrawingViewWork();

	public DrawingView(ChatRoom parent, String view_name) {
		super(parent, view_name);
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphic.setColor(Color.WHITE);
		graphic.drawRect(0, 0, 800, 600);
		graphic.setColor(Color.BLACK);
		graphic.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	@Override
	public void interact(WebSocketSession session, InteractInterface obj) {
		if (!(obj instanceof LineObject))
			return;	
		drawing_queue.add(obj);
		drawing_worker.alarmExecutor();
	}
	
	private class DrawingViewWork extends ViewRunnableWork {
	@Override
	public void run() {
			List<InteractInterface> lines = new ArrayList<InteractInterface>();
			drawing_queue.drainTo(lines);
			if (lines.isEmpty())
				return;
			
			for (InteractInterface rawLine : lines) {
				LineObject line = (LineObject)rawLine;
				if (line.getPrevX() == line.getCurrX() && line.getPrevY() == line.getCurrY())
					graphic.drawRect(line.getCurrX(), line.getCurrY(), 1, 1);
				else
					graphic.drawLine(line.getPrevX(), line.getPrevY(), line.getCurrX(), line.getCurrY());
			} 	
			sendAll(lines);
		}
	}
}
