package com.network_project.interchat.controller;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.network_project.interchat.VO.LoginObject;
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
	
	@RequestMapping(value ="/", method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest requset) {
		if (requset.getSession().getAttribute("user_code") != null)
			return new ModelAndView(new RedirectView("roomlist"));
		return new ModelAndView("login","command",new LoginObject());
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String home(Model model, HttpSession session, HttpServletRequest request) {
		String new_user_name = request.getParameter("user_name");
 		if (new_user_name != null && general_service.insertUserName(new_user_name))
 			session.setAttribute("user_code", general_service.getUserCode(new_user_name));
		else
			return "redirect:/";
		return "redirect:/roomlist";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model, HttpSession session) {
		session.setAttribute("user_code", null);
		return "redirect:/";
	}
	
	@RequestMapping(value= "/roomlist", method = RequestMethod.GET)
	public String getRoomList(Model model, HttpServletRequest request) {
		Set<ChatRoom> room_list = general_service.getRoomList();
		List<Map<String, String>> parameters = new ArrayList<Map<String, String>>() ;
		for(ChatRoom room : room_list) {
			Map<String, String> parameter = new HashMap<String, String>();
			parameter.put("id", room.getID());
			parameter.put("name", room.getName());
			parameters.add(parameter);
		}
		model.addAttribute("room_list", parameters);
		return "roomlist";
	}
	
	@RequestMapping(value = "/newroom", method = RequestMethod.GET)
	public ModelAndView newroom(Model model, HttpServletRequest request) {
		return new ModelAndView("newroom","command",new LoginObject());
	}
	
	@RequestMapping(value = "/makeroom", method = RequestMethod.POST)
	public String makeRoom(Model model, HttpServletRequest request) {
		ChatRoom room = general_service.roomFactory(request.getParameter("room_name"));
		DrawingView drawing_view = new DrawingView(room, "Drawing");
		room.addView(drawing_view);
		model.addAttribute("roomid", room.getID());		
		return "redirect:/room";
	}

	@RequestMapping(value = "/room", method = RequestMethod.GET)
	public String getRoom(Model model, HttpServletRequest request, @RequestParam("roomid") String room_id) {
		View view = general_service.getView(room_id);
		if (view != null && view instanceof ChatRoom) {
			model.addAttribute("room_name", view.getName());
			model.addAttribute("roomid", room_id);
			List<View> view_list= ((ChatRoom)view).getViewList();
			List<String> view_id_list = new ArrayList<String>();
			for (ListIterator<View> view_iter = view_list.listIterator(1); view_iter.hasNext();) 
				view_id_list.add(view_iter.next().getID());
			model.addAttribute("view_list", view_id_list);
			return "chat";
		}
		else
			return "redirect:/roomlist";
	}
	
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String getView(Model model, @RequestParam("viewid") String view_id) {
		model.addAttribute("view_id", view_id);
		View view = general_service.getView(view_id);
		if (view != null && view instanceof DrawingView)
		{
			model.addAttribute("view_name", view.getName());
			return "drawing";
		}
		else
			throw new NotFoundException();
	}
	
	@ResponseBody
	@RequestMapping(value = "/image", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getImage(@RequestParam("viewid") String view_id) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			if (general_service.getView(view_id) instanceof DrawingView)
				ImageIO.write(((DrawingView) general_service.getView(view_id)).getImage(), "png", stream);
			else if (general_service.getView(view_id) instanceof ChatRoom)
				ImageIO.write(((ChatRoom) general_service.getView(view_id)).getThumbnail(), "png", stream);
			stream.flush();
			byte[] imageBytes =stream.toByteArray();
			stream.close();
			return imageBytes;
		}
		catch (Exception e) {
			return new byte[0];
		}
	}	
}
 