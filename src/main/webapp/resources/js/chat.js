/**
 * 
 */

var wsChatUri = "ws://" + server_ip + ":8000/interchat/websocket/chat.do";
var chat_websocket;
var chat_window;
var chat_input;

window.onload = function() {
	chat_window = document.getElementById('div_chat_content');
	chat_input = document.getElementById('chat_cnt');
	chat_websocket = new WebSocket (wsChatUri);
	chat_websocket.onmessage = function (evt) {
		var chat = JSON.parse(evt.data);
		receiveChat(chat);
	}
	chat_websocket.onerror = function (evt) {
		chat_window.innerHTML += "Connection error. Please refresh the page. <br />";
	}
	chat_websocket.onclose = function (evt) {
		chat_window.innerHTML += "Connection closed. Please refresh the page. <br />";
	}
	setInterval(heartBeat, 9000);
}

function receiveChat(chat) {
	if (chat.user == "Admin")
		chat_window.innerHTML += "<b>" + chat.content + "</b>";
	else
		chat_window.innerHTML += chat.user + " : " + chat.content;
	chat_window.innerHTML += "<br/>";
}

function sendChat() {
	chat_websocket.send(chat_input.value);
	chat_input.value = "";
}

function heartBeat() {
	chat_websocket.send("NULL");
}