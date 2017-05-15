/**
 * 서버에서 그림판 데이터의 교환을 담당하는 코드. 
 */

var black_button, red_button, blue_button, eraser_button, lineWidth_incre, lineWidth_decre;
var canvas, ctx;
var canvas_rect;
var drawing = false;
var prevX = 0, prevY = 0, currX = 0, currY = 0;
var color = "black";
var lineWidth = 2;
var scale = 1.0;
var initImg;

var wsDrawingUri = "ws://" + window.location.host + window.location.pathname + "/websocket/drawing.do";
var drawing_websocket;

/*
 * 윈도우 창이 켜지면 실행되는 함수
 * 그림판의 버튼틀을 통한 입력, 마우스의 움직임을 입력받는다.
 * */
window.onload = function() {
	
	/*
	 * 그림판의 색, 굵기 버튼의 입력을 받아 이벤트 핸들러 함수를 실행하는 실행문.
	 */
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
	
	/*
	 * 캔버스 위의 마우스 움직임을 읽고 핸들러를 실행.
	 */
	canvas = document.getElementById("canvas");
		
	canvas.addEventListener("mousemove", function(e) { mouseEvent("mousemove", e); });
	canvas.addEventListener("mousedown", function(e) { mouseEvent("mousedown", e); });
	canvas.addEventListener("mouseup", function(e) { mouseEvent("mouseup", e); });
	canvas.addEventListener("mouseleave", function(e) { mouseEvent("mouseleave", e); });
	
	canvas.addEventListener("touchstart", function(e) { mouseEvent("mousedown", e.changedTouches[0]); });
	canvas.addEventListener("touchmove", function(e) { mouseEvent("mousemove", e.changedTouches[0]); });
	canvas.addEventListener("touchend", function(e) { mouseEvent("mouseup", e.changedTouches[0]); });
	canvas.addEventListener("touchleave", function(e) { mouseEvent("mouseleave", e.changedTouches[0]); });
	
	/*
	 * 웹 소켓과 현재 캔버스를 해당 view_id와 짝짓는다
	 */
	initImg = new Image();
	initImg.onload = initWebSocket;
	initImg.src = "image?viewid=" + view_id + "&dummy=" + Math.floor(Math.random() * 100);	
}

/*
 * onload에서 호출되는 initWebSocket함수.
 * drawing에서 사용하는 웹소켓을 초기화.
 * 설정들을 상황에 맞게 초기화, 최적화 한다.
 * 새로운 입력(그림)이 들어온 경우, 이 내용이 모든 유저의 캔버스에 보이도록 전송한다.
 * 역시, 연결이 끊기지 않도록 heartbeat를 사용한다.
 */
function initWebSocket() {
	ctx = canvas.getContext("2d");
	ctx.drawImage(initImg, 0, 0);
	canvas.style.display = "block";
	resizeCanvas();
	window.onresize = resizeCanvas;		
	canvas_rect = canvas.getBoundingClientRect();
	drawing_websocket = new WebSocket (wsDrawingUri);

	drawing_websocket.onopen = function (evt) {
		drawing_websocket.send(JSON.stringify({userid : user_code, viewid : view_id}));
		drawing_websocket.onmessage = function (evt) {	
			var data = JSON.parse(evt.data);
			for(var i = 0; i < data.length; i++) {
				draw(data[i].prevX, data[i].prevY, data[i].currX, data[i].currY, data[i].color, data[i].lineWidth);
			}
		}
	}
	setInterval(drawingHeartBeat, 9000);
}

/*
 * 창의 크기가 변한경우, 이에 맞게 캔버스의 크기를 조절한다.
 */
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

/*
 * 웹소켓에 획 정보를 보낸다.
 * 획의 정보는 좌표, 색, 굵기로 이루어진다.
 */
function sendDraw(prevX, prevY, currX, currY, color, lineWidth) {
	var data = {};
	data.prevX = prevX;
	data.prevY = prevY;
	data.currX = currX;
	data.currY = currY;
	data.color = color;
	data.lineWidth = lineWidth;
	drawing_websocket.send(JSON.stringify(data));
}

/*
 * 연결이 끊기지 않도록 주기마다 NULL을 보내는 함수
 */
function drawingHeartBeat() {
	drawing_websocket.send("NULL");
}

/*
 * sendDraw로 받은 정보를 이용해, 캔버스에 그림을 그리는 함수.
 * 점과 획으로 구분된다.
 * 점의 경우는 같은 좌표에 입력받은 색과 두께의 사각형을 그린다.
 * 획의 경우에는 지나는 좌표들에 입력받은 색과 두께로 획을 그린다.
 */
function draw(prevX, prevY, currX, currY, color, lineWidth) {
	if(prevX == currX && prevY == currY) {
		ctx.beginPath();
	    ctx.arc(currX, currY, lineWidth/2, 0, 2 * Math.PI);
	    ctx.fillStyle = color;
	    ctx.fill();
	    ctx.closePath();
	}
	else {
		ctx.beginPath();
		ctx.moveTo(prevX, prevY);
		ctx.lineTo(currX, currY);
		ctx.strokeStyle = color;
		ctx.lineWidth = lineWidth;
		ctx.lineCap = "round";
		ctx.stroke();
		ctx.closePath();
	}
}

/*
 * 마우스의 움직임에 따라 호출되는 이벤트 핸들러 함수.
 * 마우스가 눌러진 경우에 그리기 위해 축척을 고려해 계산한 좌표를 현재 점으로 저장하고 이 내용을 sendDraw함수로 소켓에 보낸다.
 * 눌러진 채로 마우스가 움직이는 경우 이동한 점들을 축척을 계산해서 저장하고 이 내용을 sendDraw함수로 소켓에 보낸다.
 * 마우스가 눌러지지 않거나, 캔버스 밖으로 나가면 그림 그리는 것을 멈춘다. 이는 boolean변수를 이용한다.
 */
function mouseEvent(e_name, e) {
	if (e_name == "mousedown") {
		currX = Math.round((e.clientX - canvas_rect.left) * scale);
		currY = Math.round((e.clientY - canvas_rect.top) * scale);
		prevX = currX;
		prevY = currY;
		
		sendDraw(prevX, prevY, currX, currY, color, lineWidth);
		drawing = true;
	}
	else if (e_name == "mousemove") {
		if (!drawing)
			return;
		prevX = currX;
		prevY = currY;
		currX = Math.round((e.clientX - canvas_rect.left) * scale);
		currY = Math.round((e.clientY - canvas_rect.top) * scale);
		sendDraw(prevX, prevY, currX, currY, color, lineWidth);
	}
	else if (e_name == "mouseup" || e_name == "mouseleave") {
		drawing = false;
	}
}

/*
 * 캔버스 위의 버튼들에 관한 이벤트핸들러 함수.
 * 색깔의 버튼을 누르면 해당 색으로 색의 정보를 바꾸고,
 * 굵기 버튼을 누르면 굵기 정보를 바꾼다.
 */
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
