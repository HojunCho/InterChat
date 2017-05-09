<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
	<meta name="viewport" content="width=devide-width, initial-scale=1.0 user-scalable=0">
	<title>Login view</title>
	<link rel="stylesheet" href="resources/css/login.css" type="text/css">
</head>
<body>
	<h2>Chatting Name</h2>
	<form method = "POST" action = "/interchat/login"> 
	<table>
		<tr>
			<td><label path = "name">Name</label></td>
			<td ><input path = "name" /></td>
		</tr>
		<tr>
			<td colspan = "2">
				<input type = "submit" value = "Enter"/>
			</td>
		</tr>
	</table>
	</form>
</body>
</html>