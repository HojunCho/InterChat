<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=0">
	<title>Drawing View</title>
	<script>
		var server_ip = "<%=request.getServerName()%>";
		var server_port = "<%=request.getServerPort()%>";
		var view_id = "${view_id}";
	</script>
	<script src="resources/js/drawing.js"></script>
	<link rel="stylesheet" href="resources/css/drawing.css" type="text/css">
</head>
<body>
	<button class="button button1" id="black_button">Black</button>
	<button class="button button2" id="red_button">Red</button>
	<button class="button button3" id="blue_button">Blue</button>
	<button class="button button4" id="eraser_button">Eraser</button>
	<button class="button button5" id="lineWidth_incre">+</button>
	<button class="button button5" id="lineWidth_decre">-</button>

	<br/>	

	<canvas id="canvas" width="800" height="600">
	Your browser does not support the HTML5 canvas tag.
	</canvas>
</body>
</html>