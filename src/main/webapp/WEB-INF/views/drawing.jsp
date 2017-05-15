<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0 user-scalable=0">

	<title>${view_name}</title>
	<script>
		var view_id = "${view_id}";
		var user_code = "${sessionScope.user_code}";
	</script>
	<!-- the function of drawing is supervised by "drawing.js" -->
	<script src="resources/js/drawing.js"></script>
	<!-- the specific design of entire drawing is connected with "drawing.css" -->
	<link rel="stylesheet" href="resources/css/drawing.css" type="text/css">
</head>
<body>
	<div id="toolbar">
	<!-- the interface of drawing part -->
	<!-- there are buttons that choose color or thickness -->
		<button class="button button1" id="black_button">Black</button>
		<button class="button button2" id="red_button">Red</button>
		<button class="button button3" id="blue_button">Blue</button>
		<button class="button button4" id="eraser_button">Eraser</button>
		<button class="button button5" id="lineWidth_incre">+</button>
		<button class="button button5" id="lineWidth_decre">-</button>
	</div>

	<!-- interface canvas where user can draw lines -->
	<div id="contents">
		<canvas id="canvas" width="800" height="600">
		Your browser does not support the HTML5 canvas tag.
		</canvas>
	</div>
</body>
</html>