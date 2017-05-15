package com.network_project.interchat.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 사용자의 로그인 정보를 체크하는 인터셉터.
 * "{@link com.network_project.interchat.controller.HomeController#login(HttpServletRequest) /}"주소를 제외한 모든 접근을 전처리한다.
 */
public class HomeInterceptor extends HandlerInterceptorAdapter {
	/** 로그 기록기 */
	private static final Logger logger = LoggerFactory.getLogger(HomeInterceptor.class);

	/**
	 * 사용자의 http 세션 정보를 검사하여 로그인을 하였는지 확인한다.
	 * 로그인을 하지 않고 페이지에 접근할려고 시도할 시,
	 * "{@link com.network_project.interchat.controller.HomeController#login(HttpServletRequest) /}"로 리다이렉트.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		try {
			if(request.getSession().getAttribute("user_code") == null) {
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
