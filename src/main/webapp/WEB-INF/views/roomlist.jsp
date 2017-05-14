<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" href="resources/css/roomlist.css" type="text/css">
<title>Room List</title>
</head>
<body>
	<header>
		<h1>Select Room</h1>
		<input type="button" value="Logout" onclick="location.href='logout'">
	</header>
	<div id="roomlist">
		<c:forEach items = "${room_list}" var="room">
			<div class="room" OnClick="location.href='room?roomid=${room.id}'">
				<label>${room.name}</label>
				<img src="image?viewid=${room.id}" alt="${room.name}"/>
			</div>
		</c:forEach>
		<a target="_blank" OnClick="location.href='/interchat/newroom'">
		<img src = "resources/image/addroombutton.png" alt="Add Room" style="width:150px">
		</a>
		<!--
		<div class="room" OnClick="location.href='/interchat/newroom'">
			<label>Make New Room!</label>
			<img src="resources/image/addroombutton.png" alt="Add Room"/>
		</div>
		 -->
	</div>
</body>
</html>