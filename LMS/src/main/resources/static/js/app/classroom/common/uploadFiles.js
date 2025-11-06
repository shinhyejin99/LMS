/**
 * << 개정이력(Modification Information) >>
 *  -----------    -------------    ---------------------------
 *  2025. 10. 2.   송태호           최초 생성
 *
 * 역할
 * - 강의별 공통 업로드 엔드포인트로 파일 업로드
 * - uploadType을 쿼리스트링으로 전달 (예: post, task, exam, qrcode ...)
 * - 서버 응답의 fileId/bundleId를 hidden input에 주입
 *
 * 사용 예:
 *   await uploadClassroomFiles(lectureId, 'post', document.getElementById('files'), 'attachFileId');
 */
export async function uploadClassroomFiles(
  lectureId,
  uploadType, // 쿼리스트링으로 전달할 파일 성격: 'post' | 'task' | 'exam' | 'qrcode' | ...
  fileInput,
  hiddenInputId
) {
  // 1) 기본 검증
  if (!lectureId) {
    throw new Error("lectureId가 비어있습니다.");
  }
  if (!fileInput || !fileInput.files) {
    throw new Error("파일 입력 요소를 찾을 수 없습니다.");
  }
  const files = Array.from(fileInput.files);
  if (files.length === 0) {
    throw new Error("업로드할 파일이 없습니다.");
  }
  if (files.length > 5) {
    throw new Error("최대 5개까지만 업로드할 수 있습니다.");
  }

  // 2) uploadType 검증 (화이트리스트; 필요시 확장)
  const allowedTypes = new Set(["board", "task", "exam", "qrcode"]);
  if (!uploadType || !allowedTypes.has(String(uploadType).toLowerCase())) {
    throw new Error(
      `uploadType이 유효하지 않습니다. 허용: ${Array.from(allowedTypes).join(
        ", "
      )}`
    );
  }

  // 3) FormData 구성
  const formData = new FormData();
  for (const f of files) {
    formData.append("files", f); // 서버 @RequestParam("files") 일치
  }

  // 4) URL + 쿼리스트링 구성
  //   - 예시: /classroom/api/v1/common/{lectureId}/upload?type=post
  const qs = new URLSearchParams({ type: String(uploadType).toLowerCase() });
  const url = `/classroom/api/v1/common/${encodeURIComponent(
    lectureId
  )}/upload?${qs}`;

  // 5) 요청 (FormData 사용 시 Content-Type 직접 지정 금지)
  const resp = await fetch(url, {
    method: "POST",
    body: formData,
    credentials: "same-origin",
  });

  if (!resp.ok) {
    const msg = await resp.text().catch(() => resp.statusText);
    throw new Error(`업로드 실패(${resp.status}): ${msg}`);
  }

  const data = await resp.json();
  // 서버 응답 예시 (유연 처리):
  // { fileId: 'FILE0001', files: [{fileId:'FD001', order:1}, ...] }
  // 또는 { bundleId: 'FILE0001', details: [...] }
  // 또는 { fileIds: ['FD001','FD002', ...] }

  // 6) hidden input 세팅: bundleId/fileId/fileIds[0] 중 우선순위 적용
  if (hiddenInputId) {
    const hiddenInput = document.getElementById(hiddenInputId);
    if (hiddenInput) {
      const bundleId = data.bundleId || data.fileId;
      const firstDetailId =
        (Array.isArray(data.fileIds) && data.fileIds[0]) ||
        (Array.isArray(data.files) && data.files[0]?.fileId) ||
        (Array.isArray(data.details) && data.details[0]?.fileId) ||
        null;

      hiddenInput.value = bundleId || firstDetailId || "";
    } else {
		console.error("숨겨진 인풋을 찾지 못했습니다.");
	}
  }

  return data; // 호출부에서 전체 응답을 활용할 수 있도록 반환
}