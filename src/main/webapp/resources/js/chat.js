/**
 * 
 */

var wsChatUri = "ws://" + location.host + "/interchat/websocket/chat.do";
var chat_websocket;
var chat_window;
var chat_input;

window.onload = function() {		
	chat_window = document.getElementById('div_chat_content');
	chat_input = document.getElementById('chat_cnt');
	chat_websocket = new WebSocket (wsChatUri);

	chat_websocket.onopen = function (evt) {
		
		chat_websocket.send(JSON.stringify({userid : user_code, viewid : room_id}));
		chat_websocket.onmessage = function (evt) {
			var chat = JSON.parse(evt.data);
			receiveChat(chat);
		}
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

	chat_window.scrollTop = chat_window.scrollHeight;
}

function enterpress(e) {
	if (e.keyCode === 13)
	{
		e.preventDefault(); 
		sendChat();
	}
}

function sendChat() {
	if (chat_input.value == "")
		return;

	var chat = {"user" : user_code, "content" : chat_input.value};
	chat_input.value = "";
	chat_websocket.send(JSON.stringify(chat));
}

function heartBeat() {
	chat_websocket.send("NULL");
}