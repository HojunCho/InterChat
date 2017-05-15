package com.network_project.interchat.VO;

/**
 * 채팅 객체.
 * 채팅 내용 등을 담는다.
 */
public class ChatObject implements InteractInterface {
	/** 채팅을 보낸 유저의 이름 혹은 코드 */
	private String user;
	/** 채팅 내용 */
	private String content;
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
}
