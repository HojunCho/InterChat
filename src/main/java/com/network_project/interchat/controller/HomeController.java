package com.network_project.interchat.controller;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.network_project.interchat.other.ChatRoom;
import com.network_project.interchat.other.DrawingView;
import com.network_project.interchat.other.View;
import com.network_project.interchat.service.GeneralService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@ResponseStatus(value=HttpStatus.NOT_FOUND)  // 404
	public class NotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}	

	@Resource(name="GeneralService")
	private GeneralService general_service;
	
	private static int user_num = 0;
	
	@PostConstruct
	public void constructor() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			logger.info("Server IP : " + ip.getHostAddress());
		} catch (Exception e) {
			logger.error("Error in getting server IP");
		}
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		Set<ChatRoom> room_list = general_service.getRoomList();
		if (room_list.size() == 0) {
			ChatRoom room = general_service.roomFactory("Inter Chat");
			DrawingView drawing_view = new DrawingView(room, "Drawing");
			room.addView(drawing_view);
			model.addAttribute("room_id", room.getID());
		}
		else
			model.addAttribute("room_id", room_list.iterator().next().getID());
		
		String new_user_name = "³¸¼± »ç¶÷" + Integer.toString(user_num++);
		if (general_service.insertUserName(new_user_name))
			model.addAttribute("user_code", general_service.getUserCode(new_user_name));
		else
			throw new NotFoundException();
		
		return "beta";
	}

	@RequestMapping(value = "/room", method = RequestMethod.GET)
	public String getRoom(Model model, @RequestParam("roomid") String room_id) {
		View view = general_service.getView(room_id);
		if (view != null && view instanceof ChatRoom) {
			model.addAttribute("room_id", room_id);
			List<View> view_list= ((ChatRoom)view).getViewList();
			List<String> view_id_list = new ArrayList<String>();
			for (ListIterator<View> view_iter = view_list.listIterator(1); view_iter.hasNext();) 
				view_id_list.add(view_iter.next().getID());
			model.addAttribute("view_list", view_id_list);
			return "chat";
		}
		else
			return "redirect:/";
	}
	
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String getView(Model model, @RequestParam("viewid") String view_id) {
		model.addAttribute("view_id", view_id);
		
		View view = general_service.getView(view_id);
		if (view != null && view instanceof DrawingView)
			return "drawing";
		else
			throw new NotFoundException();
	}
	
	@ResponseBody
	@RequestMapping(value = "/image", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getImage(@RequestParam("viewid") String view_id) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(((DrawingView) general_service.getView(view_id)).getImage(), "png", stream);
			stream.flush();
			byte[] imageBytes =stream.toByteArray();
			stream.close();
			return imageBytes;
		}
		catch (Exception e) {
			return new byte[0];
		}
	}
	
	@RequestMapping(value = "/beta/new", method = RequestMethod.GET)
	public String newRoom(Model model) {
		ChatRoom room = general_service.roomFactory("Inter Chat");
		DrawingView drawing_view = new DrawingView(room, "Drawing");
		room.addView(drawing_view);
		model.addAttribute("room_id", room.getID());
		
		String new_user_name = "³¸¼± »ç¶÷" + Integer.toString(user_num++);
		if (general_service.insertUserName(new_user_name))
			model.addAttribute("user_code", general_service.getUserCode(new_user_name));
		else
			throw new NotFoundException();
		
		return "beta";
	}
}
