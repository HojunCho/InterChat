package com.network_project.interchat.interceptor;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 사용자의 서버 관련 정보를 체크하는 인터셉터.
 * 모든 주소 접근에 대하여 전처리를 한다.
 */
public class ServerCodeInterceptor extends HandlerInterceptorAdapter  {
	/** 로그 기록기 */
	private static final Logger logger = LoggerFactory.getLogger(ServerCodeInterceptor.class);

	/**
	 * 서버 측의 서버 코드.
	 * 서버를 재시작할때마다 새로운 서버 코드를 할당한다. 
	 */
	private static final String server_code = String.valueOf((new Random()).nextInt());

	/**
	 * 사용자의 서버 코드와 서버의 서버 코드를 비교하여 다를 경우 세션의 정보를 초기화 한다.
	 * 이후 "{@link com.network_project.interchat.controller.HomeController#login(HttpServletRequest) /}"로 리다이렉트.
	 * 같을 경우에는 접속을 허용한다.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		try {
			if(request.getSession().getAttribute("server_code") == null || ((String) request.getSession().getAttribute("server_code")).compareTo(server_code) != 0) {
				request.getSession().setAttribute("server_code", server_code);
				request.getSession().setAttribute("user_code", null);
				response.sendRedirect("");
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
}
