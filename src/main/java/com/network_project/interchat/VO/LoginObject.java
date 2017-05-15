package com.network_project.interchat.VO;

/**
 * 입력폼 객체.
 * HomeController에서 입력 폼을 다루기 위해 사용하는 객체.
 * @see com.network_project.interchat.controller.HomeController#login(javax.servlet.http.HttpServletRequest)
 * @see com.network_project.interchat.controller.HomeController#newroom(org.springframework.ui.Model, javax.servlet.http.HttpServletRequest)
 */
public class LoginObject {
	/** 입력 값 */
	private String name;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
}
