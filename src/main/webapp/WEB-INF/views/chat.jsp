<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=0"> 
	<title>InterChat</title>
	<script> 
		var server_ip = "<%=request.getServerName()%>";
		var server_port = "<%=request.getServerPort()%>";
		var room_id = "${room_id}";
	</script>
	<script src="resources/js/chat.js"></script>
	<link rel="stylesheet" href="resources/css/chat.css" type="text/css">
</head>
<body>
	<div id="container">
		<!-- Interacting Contents -->
		<div id="content">
			<c:forEach items = "${view_list}" var="view">
				<iframe id="view_${view}" class="view" src="/interchat/view?viewid=${view}"></iframe>
			</c:forEach>
		</div>	
		<!-- Chat Box -->
		<div id="div_chat_box">
			<div id="div_chat_content">
			</div>
			<div id="div_chat_input">
				<textarea id="chat_cnt"></textarea>
				<button id="chat_btn" type="button" onclick="sendChat()">Chat</button>
			</div>
		</div>
	</div>
</body>
</html>