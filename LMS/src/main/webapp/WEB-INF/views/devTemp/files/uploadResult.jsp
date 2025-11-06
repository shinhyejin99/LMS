<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 29.     			정태일            최초 생성
 *
-->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>업로드 완료</title>
<style>
body {
	font-family: system-ui, Arial, sans-serif;
	line-height: 1.5;
	padding: 24px
}

.kv {
	margin: .25rem 0
}

.kv b {
	display: inline-block;
	min-width: 100px
}

a.btn {
	display: inline-block;
	margin-top: 12px;
	padding: .5rem .9rem;
	border: 1px solid #ddd;
	text-decoration: none
}
</style>
</head>
<body>
	<h2>업로드 완료</h2>
	<p class="kv">
		<c:forEach items="${fileDetailVO}" var="file">
			파일순번 : ${file.fileOrder}<br>
			원래이름 : ${file.originName}<br>
			원확장자 : ${file.extension}<br>
			저장경로 : ${file.saveDir}<br>
			파일크기 : ${file.fileSize}<br>
			저장이름 : ${file.saveName}<br>		
		</c:forEach>
		<b>DB에 저장된 FILE_ID</b> ${fileId}
	</p>
	<a class="btn" href="<%=request.getContextPath()%>/devtemp/files/upload">다시 업로드</a>
</body>
</html>