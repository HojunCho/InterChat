package com.network_project.interchat.VO;

public class LineObject implements InteractInterface {
	private int prevX;
	private int prevY;
	private int currX;
	private int currY;
	
	private String color;
	private int lineWidth;
	
	public int getPrevX() {
		return prevX;
	}
	
	public void setPrevX(int _prevX) {
		prevX = _prevX;
	}
	
	public int getPrevY() {
		return prevY;
	}
	
	public void setPrevY(int _prevY) {
		prevY = _prevY;
	}
	
	public int getCurrX() {
		return currX;
	}
	
	public void setCurrX(int _currX) {
		currX = _currX;
	}
	
	public int getCurrY() {
		return currY;
	}
	
	public void setCurrY(int _currY) {
		currY = _currY;
	}
	public String getColor() {
		return color;
	}
	
	public void setColor(String _color) {
		color = _color;
	}
	public int getlineWidth() {
		return lineWidth;
	}
	
	public void setlineWidth(int _lineWidth) {
		lineWidth = _lineWidth;
	}
	
}
