<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>페이지 준비중</title>
<style>
body {
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	height: 100vh;
	font-family: sans-serif;
	background-color: #f5f5f9;
	color: #566a7f;
	text-align: center;
}

h1 {
	font-size: 2em;
	margin-bottom: 0.5em;
}

p {
	font-size: 1.2em;
}

.uri {
	font-family: monospace;
	background-color: #e0e0e0;
	padding: 0.2em 0.5em;
	border-radius: 4px;
}
</style>
</head>
<body>
	<div class="container">
		<h1>페이지 준비중입니다.</h1>
		<p>
			<span class="uri"><%=request.getRequestURI()%></span> 요청에 대한 페이지입니다.
		</p>
		<p>빠른 시일 내에 준비하여 찾아뵙겠습니다.</p>
	</div>
</body>
</html>