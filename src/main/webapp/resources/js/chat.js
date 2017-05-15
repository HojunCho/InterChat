/**
 * Home controller에서 호출되어
 * 서버상에서 실행되는 코드
 */

var wsChatUri = "ws://" + window.location.host + window.location.pathname + "/websocket/chat.do";
var chat_websocket;
var chat_window;
var chat_input;

/*
 * 윈도우 창이 켜지면 실행되는 함수
 * 웹소켓을 만들어서 사용자가 입력한 닉네임과 선택한 방의 정보를 보낸다.
 * 연결에 오류가 발생했는지, 연결이 끊겼는지 확인한다.
 * 연결이 끊기지 않도록 heartBeat를 지시한다.
 * */
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

/*
 * 서버가 새 chat을 받았을때 실행되는 함수
 * 보내는 주체에 따라 다른 String을 보낸다.
 * 보내는 주체는 운영자, 그 이외의 클라이언트 이 두가지로 구분된다.
 * 운영자는 클라이언트가 입장했을때 입장 메세지를 보낸다.
 * 서버는 이를 굵은 글씨의 입장 메세지 String형태로 저장한다.
 * 그렇지 않고 클라이언트가 보낸 메세지 경우
 * 채팅 이름: 내용 형태로 저장한다.
 */
function receiveChat(chat) {
	if (chat.user == "Admin")
		chat_window.innerHTML += "<b>" + chat.content + "</b>";
	else
		chat_window.innerHTML += chat.user + " : " + chat.content;
	chat_window.innerHTML += "<br/>";

	chat_window.scrollTop = chat_window.scrollHeight;
}

/*
 * 사용자가 엔터키를 눌렀을 때 실행되는 함수
 * 메세지가 있으면 이를 보낸다.
 * */
function enterpress(e) {
	if (e.keyCode === 13)
	{
		e.preventDefault(); 
		sendChat();
	}
}

/*
 * 메세지를 보내는 함수
 * 사용자 : 메세지
 * 형태로 메세지를 소켓으로 보낸다.
 */
function sendChat() {
	if (chat_input.value == "")
		return;

	var chat = {"user" : user_code, "content" : chat_input.value};
	chat_input.value = "";
	chat_websocket.send(JSON.stringify(chat));
}

/*
 * 사용하지 않는 시간동안 연결이 끊기는 것을 대비해
 * 연결이 끊기지 않도록 특정 시간마다 NULL값을 보낼때 사용하는 함수.
 * */
function heartBeat() {
	chat_websocket.send("NULL");
}