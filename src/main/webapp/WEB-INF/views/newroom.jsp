<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=0"> 
	<title>New Room</title>
	<!-- The specific design of new-room is supervised by "newroom.css" -->
	<link rel="stylesheet" href = "resources/css/newroom.css" type="text/css">
<script>
	sessionStorage.user_code = "${user_code}";
</script>
</head>
<body>
 <!-- Design of new room -->
	<div>
		<h2>Chatting-room Name</h2>
		<!--Use method "POST" send information(room-name) to "/interchat/makeroom" -->
		<form method = "POST" action = "makeroom"> 
    	<label for="room-name">Name</label>
    	<!--Input text interface & Click submit interface-->
    	<input type="text" id="room-name" name="room_name" placeholder="Room name..">  
    	<input type="submit" value="Submit">
  	</form>
	</div>
</body>
</html>