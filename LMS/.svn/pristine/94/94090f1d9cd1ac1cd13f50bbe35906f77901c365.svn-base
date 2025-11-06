<!-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 28.     		송태호            최초 생성
 *
-->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>파일 업로드</title>
<style>
body {
	font-family: system-ui, Arial, sans-serif;
	line-height: 1.5;
	padding: 24px
}

label {
	display: block;
	margin: .5rem 0 .25rem
}

input[type="text"], select {
	padding: .4rem .6rem;
	min-width: 280px
}

.hint {
	color: #666;
	font-size: .9rem
}

.btn {
	margin-top: 12px;
	padding: .6rem 1rem
}
</style>

<script>
	// multiple로 선택 시 5개 초과 방지(클라이언트측)
	function handleFilesLimit(input) {
		if (input.files && input.files.length > 5) {
			alert("파일은 최대 5개까지 업로드할 수 있습니다.");
			input.value = ""; // 선택 해제
		}
	}
</script>

</head>

<body>
	<h1>파일 업로드</h1>

	<form action="<c:url value='/devtemp/files/upload'/>" method="post" enctype="multipart/form-data">
		
		<label for="noticeTypeCd">공지사항 타입(일반/학사)</label>
		<input type="text" id="noticeTypeCd" name="noticeTypeCd" maxlength="50" required />
		
		<label for="title">제목</label>
		<input type="text" id="title" name="title" maxlength="200" required />
		
		<label for="content">내용</label>
		<textarea id="content" name="content" maxlength="4000" rows="8" required></textarea>

		<div>
			<label for="files">첨부파일 (최대 5개)</label>
			<input id="files" type="file" name="files" multiple	onchange="handleFilesLimit(this)" />
			<div class="hint">브라우저에서 5개 이하로 선택해주세요.</div>
		</div>

		<button class="btn" type="submit">업로드</button>
	</form>
</body>
</html>