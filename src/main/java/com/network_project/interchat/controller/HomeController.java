package com.network_project.interchat.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.network_project.interchat.VO.LoginObject;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final InetAddress server_ip = getServerAddress();	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	private static InetAddress getServerAddress() {
		try {
			logger.info("Server IP : " + InetAddress.getLocalHost().getHostAddress());
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			logger.error(e.toString());
			return null;
		}
	}
	@RequestMapping(value ="/",method = RequestMethod.GET)
	public ModelAndView login() {
		return new ModelAndView("login","command",new LoginObject());
	}
	/*
	public String LoginView(Locale locale, Model model){
		model.addAttribute("server_ip","localhost");
		return "login";
	}
	*/
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String home(@ModelAttribute("interchat")LoginObject login, ModelMap model) {
		model.addAttribute("server_ip", /*server_ip.getHostAddress()*/ "localhost");
		model.addAttribute("content", "drawing");
		model.addAttribute("name",login.getName());
		return "chat";
	}
	
	@RequestMapping(value = "/drawing", method = RequestMethod.GET)
	public String drawingView(Locale locale, Model model) {
		model.addAttribute("server_ip", /*server_ip.getHostAddress()*/ "localhost");
		return "drawing";
	}	
}
