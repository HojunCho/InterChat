package com.network_project.interchat.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		model.addAttribute("server_ip", server_ip.getHostAddress());
		model.addAttribute("content", "drawing");
		return "chat";
	}
	
	@RequestMapping(value = "/drawing", method = RequestMethod.GET)
	public String drawingView(Locale locale, Model model) {
		model.addAttribute("server_ip", server_ip.getHostAddress());
		return "drawing";
	}	
}
