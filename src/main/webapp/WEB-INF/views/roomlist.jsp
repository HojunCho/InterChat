<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=0"> 
	<!-- connet "roomlist.jsp" with "roomlist.css" -->
	<link rel="stylesheet" href="resources/css/roomlist.css" type="text/css">
<title>Room List</title>
</head>
<body>
	<!-- both of the select room and logout is categorized by "header" -->
	<div id="header">
		<h1>Select Room</h1>
		<!-- Logout interface / if click then logout is -->
		<input type="button" value="Logout" onclick="location.href='logout'">
		<!-- if click the button then go to the "/logout" -->
	</div>
	<!-- every room & items are categorized by "roomlist" -->
	<div id="roomlist">
		<c:forEach items = "${room_list}" var="room">
			<div class="room" OnClick="location.href='room?roomid=${room.id}'">
			<!--  if click the room then go to the "/room?roomid=${room.id}" -->
			<!--  ${room.id} is index of room which is related to the number of rooms -->
				<label>${room.name}</label>
				<img src="image?viewid=${room.id}&dummy=<%=(int) (Math.random()*100)%>" alt="${room.name}"/>
				<!-- the interface of the roomlist is related with showing drawing of the some specific room -->
			</div>
		</c:forEach>
		
		<div class="room" OnClick="location.href='newroom'">
			<label>Make New Room!</label>
			<img src="resources/image/addroombutton.png" alt="Add Room"/>
			<!-- if click the image then go to the "/interchat/newroom"  -->
		</div>
	</div>
</body>
</html>