<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=devide-width, initial-scale=1.0 user-scalable=0">
	<title>Login view</title>
	<link rel="stylesheet" href="resources/css/login.css" type="text/css">
</head>
<style>
input[type=text], select {
    width: 100%;
    padding: 12px 20px;
    margin: 8px 0;
    display: inline-block;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}
input[type=submit] {
    width: 100%;
    background-color: #4CAF50;
    color: white;
    padding: 14px 20px;
    margin: 8px 0;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}
input[type=submit]:hover {
    background-color: #45a049;
}
div {
    border-radius: 5px;
    background-color: #f2f2f2;
    padding: 20px;
}
</style>
<body>
	<h1 class="aav">Chatting Name</h1>
	<div>
		<form method = "POST" action = "/interchat/login"> 
    <label for="fname">Name</label>
    <input type="text" id="fname" name="user_name" placeholder="Your name..">  
    <input type="submit" value="Submit">
  </form>
</div>
		
	<!--
	<table>
		<tr>
			<td><label path = "name">Name</label></td>
			<td ><input path = "name" name = "user_name" /></td>
		</tr>
		<tr>
			<td colspan = "2">
				<input type = "submit" value = "Enter"/>
			</td>
		</tr>
	</table>
	 -->
	</form>
</body>
</html>