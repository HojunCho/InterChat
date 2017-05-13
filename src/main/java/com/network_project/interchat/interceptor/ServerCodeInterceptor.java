package com.network_project.interchat.interceptor;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ServerCodeInterceptor extends HandlerInterceptorAdapter  {
	private static final Logger logger = LoggerFactory.getLogger(ServerCodeInterceptor.class);
	private static final String server_code = String.valueOf((new Random()).nextInt());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		try {
			if(request.getSession().getAttribute("server_code") == null || ((String) request.getSession().getAttribute("server_code")).compareTo(server_code) != 0) {
				request.getSession().setAttribute("server_code", server_code);
				request.getSession().setAttribute("user_code", null);
				response.sendRedirect("/interchat/");
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
}
