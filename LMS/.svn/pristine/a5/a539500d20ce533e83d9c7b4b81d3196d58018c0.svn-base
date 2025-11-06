package kr.or.jsu.classroom.professor.service;

import java.util.List;

import kr.or.jsu.classroom.dto.request.LctPostReplyReq;
import kr.or.jsu.classroom.dto.request.LecturePostReq;
import kr.or.jsu.classroom.dto.response.board.LctPostDetailResp_PRF;
import kr.or.jsu.classroom.dto.response.board.LctPostLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.board.LctPostReplyResp;

public interface ClassPrfBoardService {
	
	/**
	 * 특정 강의에 대한 교수의 글쓰기 요청을 받아, <br>
	 * 작성 권한을 확인한 뒤 글쓰기 작업을 처리하고,  <br>
	 * 생성된 게시글 ID를 반환합니다.
	 * 
	 * @param newPost 글쓰기 요청
	 * @param lectureId 게시글을 작성할 강의
	 * @param prfNo 작성 요청한 교수
	 * @return 생성된 게시글 ID
	 */
	public String createPost(
		LecturePostReq newPost
		, String lectureId
		, String prfNo
	);
	
	/**
	 * 특정 강의에 대한 게시글 목록 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 목록을 반환합니다.
	 * 
	 * @param lectureId 강의 ID
	 * @param prfNo 열람 요청한 교수
	 * @param postType 요청한 게시글 타입, null일 경우 모두
	 * @return 게시글 목록(목록에 필요한 정보만)
	 */
	public List<LctPostLabelResp_PRF> readPostList(
		String lectureId
		, String prfNo
		, String postType
	);
	
	/**
	 * 특정 게시글 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 게시글 상세 내용을 반환합니다. <br>
	 * 삭제된 게시글을 조회하려고 할 경우 따로 알립니다.
	 * 
	 * @param lctPostId 강의 게시글 ID
	 * @param prfNo 열람 요청한 교수
	 * @return 게시글 상세 정보
	 */
	public LctPostDetailResp_PRF readPost(
		String lctPostId
		, String prfNo
	);
	
	/**
	 * 특정 게시글 삭제 요청을 받아, <br>
	 * 권한을 확인한 뒤 삭제 처리합니다.
	 * 
	 * @param lectureId
	 * @param lctPostId
	 * @param prfNo
	 */
	public void removePost(
		String lectureId
		, String lctPostId
		, String prfNo
	);
	
	/**
	 * 특정 강의에 대한 교수의 글 수정 요청을 받아, <br>
	 * 작성 권한을 확인한 뒤 수정 작업을 처리합니다.
	 * 
	 * @param changedPost 글 수정 요청 내용
	 * @param lectureId 게시글을 작성할 강의
	 * @param prfNo 수정 요청한 교수
	 */
	public void editPost(
		LecturePostReq changedPost
		, String lectureId
		, String lctPostId
		, String prfNo
	);
	
	/**
	 * 유효성 검사 후 댓글을 작성합니다. <br>
	 * 검사 목록 <br>
	 * 1. 사용자가 강의와 관련있는가? <br>
	 * 2. 게시글이 존재하고 삭제되지 않았으며, 강의와 관련있는가? <br>
	 * 3. 대댓글일 경우, 요청한 부모 댓글이 존재하며 삭제되지 않았는가?
	 * 
	 * @param request 부모댓글(optional), 댓글 내용이 포함된 요청 객체
	 * @param lectureId 강의ID(경로변수)
	 * @param lctPostId 강의게시글ID(경로변수)
	 */
	public String createReply(
		LctPostReplyReq request
		, String lectureId
		, String lctPostId
	);
	
	/**
	 * 한 게시글에 달린 모든 댓글/대댓글을 조회합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param lctPostId 게시글ID
	 * @return 게시글의 모든 댓글/대댓글
	 */
	public List<LctPostReplyResp> readReplyList(
		String lectureId
		, String lctPostId
	);
	
	/**
	 * 특정 댓글과 해당 댓글의 자식 대댓글을 삭제 상태로 변경합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param lctPostId 게시글ID
	 * @param lctReplyId 댓글ID
	 */
	public void deleteReply(
		String lectureId
		, String lctPostId
		, String lctReplyId
	);
}