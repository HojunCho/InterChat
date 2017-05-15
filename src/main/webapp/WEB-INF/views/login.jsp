<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=0"> 
	<title>Login InterChat</title>
	<!-- The specific design of new-room is supervised by "login.css" -->
	<link rel="stylesheet" href= "resources/css/login.css" type="text/css">
</head>
<body>
<!-- Design of login -->
	<div>
		<h2>Chatting Name</h2>
		<!--Use method "POST" send information(room-name) to "/interchat/login" -->
		<form method = "POST" action = "/interchat/login">
    	<label for="fname">Name</label>
    	<!--Input text interface & Click submit interface-->
    	<input type="text" id="fname" name="user_name" placeholder="Your name..">  
    	<input type="submit" value="Submit">
  	</form>
	</div>
</body>
</html>