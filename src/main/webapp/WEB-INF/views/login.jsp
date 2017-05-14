<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Login view</title>
	<link rel="stylesheet" href= "resources/css/login.css" type="text/css">
</head>
<body>
	<h1>Chatting Name</h1>
	<div>
		<form method = "POST" action = "/interchat/login"> 
    	<label for="fname">Name</label>
    	<input type="text" id="fname" name="user_name" placeholder="Your name..">  
    	<input type="submit" value="Submit">
  	</form>
	</div>
</body>
</html>