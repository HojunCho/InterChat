package com.network_project.interchat.VO;

/**
 * 라인 객체.
 * 라인의 정보를 담는다.
 */
public class LineObject implements InteractInterface {
	/** 과거의 X 좌표 */
	private int prevX;
	/** 과거의 Y 좌표 */
	private int prevY;
	/** 현재의 X 좌표 */
	private int currX;
	/** 현재의 Y 좌표 */
	private int currY;
	/** 라인의 색깔 */
	private String color;
	/** 라인의 두께 */
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
