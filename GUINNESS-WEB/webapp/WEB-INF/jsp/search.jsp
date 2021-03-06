<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<title>{stag}</title>
<%@ include file="./commons/_favicon.jspf"%>
<link rel="stylesheet"
	href="http://fonts.googleapis.com/earlyaccess/nanumgothic.css">
<link rel="stylesheet" href="/css/mainStyle.css">
<link rel="stylesheet" href="/css/font-awesome.min.css">
<link rel="stylesheet" href="/css/datepickr.css">
<link rel="stylesheet" href="/css/markdown.css">
</head>
<body>

	<%@ include file="./commons/_topnav.jspf"%>
	
	<input type="hidden" id="sessionUserId" name="sessionUserId"
		value="${sessionUser.userId}">
	<input type="hidden" id="noteId" name="noteId" value="${noteId}">
	<div id="note-list-container" class="content wrap">
		<ul class="search-note-list">
			<li></li>
		</ul>
	</div>

	<!-- TODO 일관성을 위해 <template> 태그로 통일 -->
	<script type="template" id="popupCommentBtnTemplate">
    	<div class="popupCommentBtn">
        	댓글 달기
    	</div>
	</script>
	<script type="template" class="pCommentTemplate">
        <div class="pCommentBox">
            <p class="inputP" contenteditable="true">Leave here</p>
            <p><span>확인</span><span>취소</span></p>
        </div>
    </script>
	<script type="template" class="noteTemplate">
			<img class="avatar" >
			<div class="content-container">
				<div>
					<span class="userName"></span>
					<span class="userId"></span>
				</div>
				<div>
					<span class="note-date"></span>
				</div>
				<div class="markdown-body">
					<div class="noteText"></div>
				</div>
				<div>
					<i class="fa fa-comments"></i>
				</div>
			</div>
	</script>
	<script src="/js/datepickr.js"></script>
	<script src="/js/pComment.js"></script>
	<script src="/js/${functionSelect}"></script>
</body>
</html>
