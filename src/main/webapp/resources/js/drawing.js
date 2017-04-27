/**
 * 
 */

var canvas, ctx;
var canvas_rect;
var drawing = false;
var prevX = 0, prevY = 0, currX = 0, currY = 0;
var scale = 1.0;

var wsDrawingUri = "ws://" + server_ip + ":8000/interchat/websocket/drawing.do";
var drawing_websocket;

window.onload = function() {
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
		draw(data.prevX, data.prevY, data.currX, data.currY);
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

function draw(prevX, prevY, currX, currY) {
	if (prevX == currX && prevY == currY) {
		ctx.beginPath();
        ctx.fillStyle = "black";
        ctx.fillRect(currX, currY, 2, 2);
        ctx.closePath();
	}
	else {
		ctx.beginPath();
		ctx.moveTo(prevX, prevY);
		ctx.lineTo(currX, currY);
		ctx.strokeStyle = "black";
		ctx.lineWidth = 2;
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
		
		sendDraw(prevX, prevY, currX, currY);
		drawing = true;
	}
	else if (e_name == "mousemove") {
		if (!drawing)
			return;
		prevX = currX;
		prevY = currY;
		currX = Math.round((e.clientX - canvas_rect.left) * scale);
		currY = Math.round((e.clientY - canvas_rect.top) * scale);
		sendDraw(prevX, prevY, currX, currY);
	}
	else if (e_name == "mouseup" || e_name == "mouseleave") {
		drawing = false;
	}
}