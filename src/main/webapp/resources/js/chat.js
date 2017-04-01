/**
 * 
 */

var wsUri = "ws://119.202.81.211:8000/interchat/websocket/chat.do";
var websocket;
var chat_window;
var chat_input;

window.onload = function() {
	chat_window = document.getElementById('div_chat_content');
	chat_input = document.getElementById('chat_cnt');
	websocket = new WebSocket (wsUri);
	websocket.onmessage = function (evt) {
		var chat = JSON.parse(evt.data);
		receiveChat(chat);
	}
	websocket.onerror = function (evt) {
		chat_window.innerHTML += "Connection error. Please refresh the page. <br />";
	}
	websocket.onclose = function (evt) {
		chat_window.innerHTML += "Connection closed. Please refresh the page. <br />";
	}
	setInterval(heartBeat, 9000);
}

function receiveChat(chat) {
	chat_window.innerHTML += (chat.user + " : " + chat.content + " <br />");
}

function sendChat() {
	websocket.send(chat_input.value);
	chat_input.value = "";
}

function heartBeat() {
	websocket.send("NULL");
}