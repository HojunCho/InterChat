<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">
	sessionStorage.user_code = "${user_code}";
	document.location.href='/interchat/room?roomid=${room_id}'; 
</script>
<title>Beta Page</title>
</head>
<body>
</body>
</html>