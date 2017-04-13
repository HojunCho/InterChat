/**
 * 
 */

var canvas, ctx;
var canvas_rect;
var drawing = false;
var prevX = 0, prevY = 0, currX = 0, currY = 0;

var beforeOnload = window.onload;

var wsDrawingUri = "ws://" + server_ip + ":8000/interchat/websocket/drawing.do";
var drawing_websocket;

window.onload = function() {
	beforeOnload();
	canvas = document.createElement("CANVAS");
	document.getElementById("content").appendChild(canvas);
	canvas.width = "800";
	canvas.height = "600";
	
	
	canvas.style.position = "relative";
	canvas.style.margin = "10px";
	canvas.style.dragable = "false";
	canvas.style.backgroundColor = "white";
	
	ctx = canvas.getContext("2d");
	canvas.addEventListener("mousemove", e => mouseEvent("mousemove", e));
	canvas.addEventListener("mousedown", e => mouseEvent("mousedown", e));
	canvas.addEventListener("mouseup", e => mouseEvent("mouseup", e));
	canvas.addEventListener("mouseout", e => mouseEvent("mouseleave", e));
	canvas_rect = canvas.getBoundingClientRect();
	
	drawing_websocket = new WebSocket (wsDrawingUri);
	drawing_websocket.onmessage = function (evt) {	
		var data = JSON.parse(evt.data);
		draw(data.prevX, data.prevY, data.currX, data.currY);
	}
	drawing_websocket.onerror = function (evt) {
		chat_window.innerHTML += "Connection error. Please refresh the page. <br />";
	}
	drawing_websocket.onclose = function (evt) {
		chat_window.innerHTML += "Connection closed. Please refresh the page. <br />";
	}
	setInterval(drawingHeartBeat, 9000);
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
		currX = e.clientX - canvas_rect.left;
		currY = e.clientY - canvas_rect.top;
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
		currX = e.clientX - canvas_rect.left;
		currY = e.clientY - canvas_rect.top;
		sendDraw(prevX, prevY, currX, currY);
	}
	else if (e_name == "mouseup" || e_name == "mouseleave") {
		drawing = false;
	}
}