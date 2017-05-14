<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=0"> 
	<title>InterChat</title>
	<script> 
		var room_id = "${roomid}";
		var user_code = "${sessionScope.user_code}";
	</script>
	<script src="resources/js/chat.js"></script>
	<link rel="stylesheet" href="resources/css/chat.css" type="text/css">
</head>
<body>
	<div id="container">
		<!-- Interacting Contents -->
		<div id="content">
			<c:forEach items = "${view_list}" var="view">
				<iframe id="view_${view}" class="view" src="view?viewid=${view}"></iframe>
			</c:forEach>
		</div>	
		<!-- Chat Box -->
		<div id="div_chat_box"> <!-- Parent of div_chat_content -->
			<div id="div_chat_content">
			</div>
			<div id="div_chat_input">
				<textarea id="chat_cnt" onkeypress="enterpress(event)"></textarea>
				<button id="chat_btn" type="button" onclick="sendChat()">Chat</button>
			</div>
		</div>
	</div>
</body>
</html>