<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Insert title here</title>
	<link rel="stylesheet" href = "resources/css/newroom.css" type="text/css">
<script>
	sessionStorage.user_code = "${user_code}";
</script>
</head>
<body>
	<h1>Chatting-room Name</h1>
	<div>
		<form method = "POST" action = "/interchat/makeroom"> 
    	<label for="room-name">Name</label>
    	<input type="text" id="room-name" name="room_name" placeholder="Room name..">  
    	<input type="submit" value="Submit">
  	</form>
	</div>
</body>
</html>