/**
 * 
 */

var black_button, red_button, blue_button, eraser_button, lineWidth_incre, lineWidth_decre;
var canvas, ctx;
var canvas_rect;
var drawing = false;
var prevX = 0, prevY = 0, currX = 0, currY = 0;
var color = "black";
var lineWidth = 2;
var scale = 1.0;

var wsDrawingUri = "ws://" + server_ip + ":8080/interchat/websocket/drawing.do";
var drawing_websocket;

window.onload = function() {
	
	black_button = document.getElementById("black_button");
	red_button = document.getElementById("red_button");
	blue_button = document.getElementById("blue_button");
	eraser_button = document.getElementById("eraser_button");
	lineWidth_incre = document.getElementById("lineWidth_incre");
	lineWidth_decre = document.getElementById("lineWidth_decre");

	black_button.addEventListener("click", e => buttonEvent("black_button",e));
	red_button.addEventListener("click", e => buttonEvent("red_button",e));
	blue_button.addEventListener("click", e => buttonEvent("blue_button",e));
	eraser_button.addEventListener("click", e => buttonEvent("eraser_button",e));
	lineWidth_incre.addEventListener("click", e => buttonEvent("lineWidth_incre",e));
	lineWidth_decre.addEventListener("click", e => buttonEvent("lineWidth_decre",e));
	
	canvas = document.getElementById("canvas");
	
	ctx = canvas.getContext("2d");
	canvas.addEventListener("mousemove", e => mouseEvent("mousemove", e));
	canvas.addEventListener("mousedown", e => mouseEvent("mousedown", e));
	canvas.addEventListener("mouseup", e => mouseEvent("mouseup", e));
	canvas.addEventListener("mouseleave", e => mouseEvent("mouseleave", e));
	
	canvas.addEventListener("touchstart", e => mouseEvent("mousedown", e.changedTouches[0]));
	canvas.addEventListener("touchmove", e => mouseEvent("mousemove", e.changedTouches[0]));
	canvas.addEventListener("touchend", e => mouseEvent("mouseup", e.changedTouches[0]));
	canvas.addEventListener("touchleave", e => mouseEvent("mouseleave", e.changedTouches[0]));
	
	
	resizeCanvas();
	window.onresize = resizeCanvas;
	canvas_rect = canvas.getBoundingClientRect();
	
	drawing_websocket = new WebSocket (wsDrawingUri);
	drawing_websocket.onmessage = function (evt) {	
		var data = JSON.parse(evt.data);
		draw(data.prevX, data.prevY, data.currX, data.currY, color, lineWidth);
	}
	setInterval(drawingHeartBeat, 9000);
}

function resizeCanvas() {
	if (canvas.clientWidth == 0 || canvas.clientHeight == 0)
		return;
	
	if (canvas.clientWidth < canvas.width || canvas.clientHeight < canvas.height) {
		let xScale = 1.0;
		let yScale = 1.0;

		xScale = canvas.width / canvas.clientWidth;
		yScale = canvas.height / canvas.clientHeight;
		
		scale = xScale > yScale ? xScale : yScale;
		
		if (scale == xScale)
			canvas.style.width = canvas.clientWidth * canvas.height / canvas.width;
		else
			canvas.style.height = canvas.clientHeight * canvas.width / canvas.height;
	}
	else
		scale = 1.0;
}

function sendDraw(prevX, prevY, currX, currY) {
	var data = {};
	data.prevX = prevX;
	data.prevY = prevY;
	data.currX = currX;
	data.currY = currY;
	drawing_websocket.send(JSON.stringify(data));
}

function drawingHeartBeat() {
	drawing_websocket.send("NULL");
}

function draw(prevX, prevY, currX, currY, color, lineWidth) {
	if (prevX == currX && prevY == currY) {
		ctx.beginPath();
        ctx.fillStyle = color;
        ctx.fillRect(currX, currY, 2, 2);
        ctx.closePath();
	}
	else {
		ctx.beginPath();
		ctx.moveTo(prevX, prevY);
		ctx.lineTo(currX, currY);
		ctx.strokeStyle = color;
		ctx.lineWidth = lineWidth;
		ctx.stroke();
		ctx.closePath();
	}
}

function mouseEvent(e_name, e) {
	if (e_name == "mousedown") {
		currX = Math.round((e.clientX - canvas_rect.left) * scale);
		currY = Math.round((e.clientY - canvas_rect.top) * scale);
		prevX = currX;
		prevY = currY;
		
		sendDraw(prevX, prevY, currX, currY, color);
		drawing = true;
	}
	else if (e_name == "mousemove") {
		if (!drawing)
			return;
		prevX = currX;
		prevY = currY;
		currX = Math.round((e.clientX - canvas_rect.left) * scale);
		currY = Math.round((e.clientY - canvas_rect.top) * scale);
		sendDraw(prevX, prevY, currX, currY, color);
	}
	else if (e_name == "mouseup" || e_name == "mouseleave") {
		drawing = false;
	}
}

function buttonEvent(e_name, e) {
	if(e_name == "black_button")
		color = "black";	
	else if(e_name == "red_button")
		color = "red";
	else if(e_name == "blue_button")
		color = "blue";
	else if(e_name == "eraser_button")
		color = "white";
	
	else if(e_name == "lineWidth_incre")
		lineWidth += 2;
	else if(e_name == "lineWidth_decre")
		lineWidth -= 2;
	
	
}
