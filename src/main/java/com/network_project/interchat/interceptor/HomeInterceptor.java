package com.network_project.interchat.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class HomeInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(HomeInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		try {
			if(request.getSession().getAttribute("user_code") == null) {
				response.sendRedirect("/");
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
}
