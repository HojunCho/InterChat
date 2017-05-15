package com.network_project.interchat.VO;

/** 
 * Web Socket Initializing Object
 * Web Socket을 초기화하기 위하여 전달되는 객체.
 */
public class InitWebSocketObject {
	/** Web Socket이 연결된 유저의 유저 code */
	private String userid;
	/** Web Socket이 연결하고자 하는 뷰의 id */
	private String viewid;
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getViewid() {
		return viewid;
	}
	
	public void setViewid(String viewid) {
		this.viewid = viewid;
	}
}
