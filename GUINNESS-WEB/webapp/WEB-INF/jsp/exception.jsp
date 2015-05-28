<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>500 Internal Server Error</title>
	<%@ include file="./commons/_favicon.jspf"%>
	<link rel="stylesheet" href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css">
	<link rel="stylesheet" href="/css/mainStyle.css">
</head>
<body>
	<%@ include file="./commons/_guest_topnav.jspf"%>
	<div class="sendmail-content">
		<h1>현재 정상적인 서비스를 제공하기에 어려움이 있습니다. </h1>
		<p>서비스를 재개할 수 있도록 최선을 다하겠습니다.<br><br>감사합니다.</p>
		<a href="/">홈으로 돌아가기</a>
	</div>
</body>
</html>