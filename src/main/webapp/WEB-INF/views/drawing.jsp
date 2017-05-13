<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=0">
	<title>Drawing View</title>
	<script>
		var view_id = "${view_id}";
	</script>
	<script src="resources/js/drawing.js"></script>
	<link rel="stylesheet" href="resources/css/drawing.css" type="text/css">
</head>
<body>
	<canvas id="canvas" width="800" height="600">
	Your browser does not support the HTML5 canvas tag.
	</canvas>
</body>
</html>