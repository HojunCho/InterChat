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
 * 홈 컨트롤러, 주소창으로 들어오는 입력들을 처리한다.
 */
@Controller
public class HomeController {
	/** 로그 기록기 */
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * 404 페이지를 반환하는 Exception.
	 */
	@ResponseStatus(value=HttpStatus.NOT_FOUND)  // 404
	public class NotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}	

	/** 비즈니스 로직을 담당하는 {@link GeneralService GeneralService} Bean. */
	@Resource(name="GeneralService")
	private GeneralService general_service;
	
	/**
	 * Controller가 Bean으로 만들어진 후 실행되는 생성자.
	 */
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
	
	/**
	 * 주소 "/"에 연결되는 메소드
	 * 로그인 창을 보여준다.
	 * @see LoginObject
	 * @see #loginCheck(Model, HttpSession, HttpServletRequest)
	 * @param requset http 요청 정보
	 * @return 입력 폼이 담긴 login.jsp
	 */
	@RequestMapping(value ="/", method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest requset) {
		if (requset.getSession().getAttribute("user_code") != null)
			return new ModelAndView(new RedirectView("roomlist"));
		return new ModelAndView("login","command",new LoginObject());
	}

	/**
	 * 주소 "/login"에 연결되는 메소드
	 * form으로 입력받은 로그인 정보를 처리한다. 
	 * 로그인에 성공할 시 로그인 정보를 http 세션에 저장한 후 "{@link #getRoomList(Model, HttpServletRequest) /roomlist}"로 리다이렉트.
	 * 로그인에 실패할 시 "{@link #login(HttpServletRequest) /}"로 리다이렉트.
	 * @see #login(HttpServletRequest)
	 * @param model 모델 정보
	 * @param session http 세션 정보
	 * @param request http 요청 정보
	 * @return 리다이렉트 정보
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginCheck(Model model, HttpSession session, HttpServletRequest request) {
		String new_user_name = request.getParameter("user_name");
 		if (new_user_name != null && general_service.insertUserName(new_user_name))
 			session.setAttribute("user_code", general_service.getUserCode(new_user_name));
		else
			return "redirect:/";
		return "redirect:/roomlist";
	}
	
	/**
	 * 주소 "/logout"에 연결되는 메소드
	 * 로그인 정보를 http 세션에서 제거하여 로그아웃한다.
	 * @param model 모델 정보
	 * @param session http 세션 정보
	 * @return "{@link #login(HttpServletRequest) /}"로 리다이렉트
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model, HttpSession session) {
		session.setAttribute("user_code", null);
		return "redirect:/";
	}
	
	/**
	 * 주소 "/roomlist"에 연결되는 메소드
	 * 현재 존재하는 방들의 목록 창을 보여준다.
	 * @see GeneralService#getRoomList()
	 * @param model 모델 정보
	 * @param request http 요청 정보
	 * @return 방들의 정보가 담긴 roomlist.jsp
	 */
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
	
	/**
	 * 주소 "/newroom"에 연결되는 메소드
	 * 새로운 방을 만드는 창을 보여준다.
	 * @see #makeRoom(Model, HttpServletRequest)
	 * @param model 모델 정보
	 * @param request http 요청 정보
	 * @return 입력 폼이 담긴 newroom.jsp
	 */
	@RequestMapping(value = "/newroom", method = RequestMethod.GET)
	public ModelAndView newroom(Model model, HttpServletRequest request) {
		return new ModelAndView("newroom","command",new LoginObject());
	}
	
	/**
	 * 주소 "/makeroom"에 연결되는 메소드.
	 * form으로 입력받은 새 방의 정보를 기초로 새로운 방을 만든다.
	 * @see #newroom(Model, HttpServletRequest)
	 * @see ChatRoom
	 * @param model 모델 정보
	 * @param request http 요청 정보
	 * @return "{@link #getRoom(Model, HttpServletRequest, String) /room}"으로 리다이렉트, 주소창 매개변수 'roomid'로 새방의 id를 지정. 
	 */
	@RequestMapping(value = "/makeroom", method = RequestMethod.POST)
	public String makeRoom(Model model, HttpServletRequest request) {
		String new_room_name = request.getParameter("room_name");
		if (new_room_name.compareTo("") == 0)
			new_room_name = "Inter Chatting";
		ChatRoom room = general_service.roomFactory(new_room_name);
		DrawingView drawing_view = new DrawingView(room, "Drawing");
		room.addView(drawing_view);
		model.addAttribute("roomid", room.getID());		
		return "redirect:/room";
	}

	/**
	 * 주소 "/room"에 연결되는 메소드.
	 * 주소창 매개변수로 'roomid' 정보를 받아 해당되는 방의 창을 띄워준다.
	 * 방이 없을 경우 "{@link #getRoomList(Model, HttpServletRequest) /roomlist}"로 리다이렉트. 
	 * @see ChatRoom
	 * @param model 모델 정보
	 * @param request http 요청 정보
	 * @param room_id 들어가고자하는 방의 id
	 * @return 해당하는 방의 정보가 담긴 chat.jsp
	 */
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
	
	/**
	 * 주소창 "/view"에 연결되는 메소드.
	 * 주소창 매개변수로 'viewid'를 받아 해당하는 뷰의 창을 띄워준다.
	 * 뷰의 종류에 따라 해당되는 jsp를 반환한다. (Ex. {@link DrawingView DrawingView}는 drawing.jsp를 반환)
	 * 뷰가 없을 경우 {@link NotFoundException 404 error}.
	 * @see View
	 * @throws NotFoundException id에 해당하는 뷰가 존재하지 않을 경우
	 * @param model 모델 정보
	 * @param view_id 해당 view의 id
	 * @return 해당하는 뷰의 jsp
	 */
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
	
	/**
	 * 주소창 "/image"에 연결되는 메소드.
	 * 주소창 매개변수로 'viewid'를 받아 해당하는 뷰의 이미지를 보여준다.
	 * 해당되는 이미지를 png 파일 형식으로 전달.
	 * {@link DrawingView DrawingView}의 경우, 그리고 있는 그림의 사진을 반환.
	 * {@link ChatRoom ChatRoom}의 경우, 그 방의 썸네일 사진을 반환.
	 * @see DrawingView#getImage()
	 * @see ChatRoom#getThumbnail()
	 * @param view_id 해당 view의 id
	 * @return 해당 뷰의 png 이미지
	 */
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
 