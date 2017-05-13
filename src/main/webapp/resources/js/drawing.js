/**
 * 
 */

var canvas, ctx;
var canvas_rect;
var drawing = false;
var prevX = 0, prevY = 0, currX = 0, currY = 0;
var scale = 1.0;
var initImg;
var wsDrawingUri = "ws://" + location.host + "/interchat/websocket/drawing.do";
var drawing_websocket;

if (sessionStorage.getItem("user_code") != null) {
	window.onload = function() {
		canvas = document.getElementById("canvas");
		
		canvas.addEventListener("mousemove", function(e) { mouseEvent("mousemove", e); });
		canvas.addEventListener("mousedown", function(e) { mouseEvent("mousedown", e); });
		canvas.addEventListener("mouseup", function(e) { mouseEvent("mouseup", e); });
		canvas.addEventListener("mouseleave", function(e) { mouseEvent("mouseleave", e); });
	
		canvas.addEventListener("touchstart", function(e) { mouseEvent("mousedown", e.changedTouches[0]); });
		canvas.addEventListener("touchmove", function(e) { mouseEvent("mousemove", e.changedTouches[0]); });
		canvas.addEventListener("touchend", function(e) { mouseEvent("mouseup", e.changedTouches[0]); });
		canvas.addEventListener("touchleave", function(e) { mouseEvent("mouseleave", e.changedTouches[0]); });
	
		initImg = new Image();
		initImg.onload = initWebSocket;
		initImg.src = "/interchat/image?viewid=" + view_id;	
	}
}

function initWebSocket() {
	ctx = canvas.getContext("2d");
	ctx.drawImage(initImg, 0, 0);
	canvas.style.display = "block";
	resizeCanvas();
	window.onresize = resizeCanvas;		
	canvas_rect = canvas.getBoundingClientRect();
	drawing_websocket = new WebSocket (wsDrawingUri);
	drawing_websocket.onopen = function (evt) {
		drawing_websocket.send(JSON.stringify({userid : sessionStorage.user_code, viewid : view_id}));
		drawing_websocket.onmessage = function (evt) {	
			var data = JSON.parse(evt.data);
			for(var i = 0; i < data.length; i++) {
				draw(data[i].prevX, data[i].prevY, data[i].currX, data[i].currY);
			}
		}
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
		ctx.lineCap = "round";
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