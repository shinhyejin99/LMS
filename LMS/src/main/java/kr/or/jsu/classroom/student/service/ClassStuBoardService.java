package kr.or.jsu.classroom.student.service;

import java.util.List;

import kr.or.jsu.classroom.dto.request.LctPostReplyReq;
import kr.or.jsu.classroom.dto.response.board.LctPostDetailResp_STU;
import kr.or.jsu.classroom.dto.response.board.LctPostLabelResp_STU;
import kr.or.jsu.classroom.dto.response.board.LctPostReplyResp;
import kr.or.jsu.vo.UsersVO;

public interface ClassStuBoardService {
	
	/**
	 * 특정 강의에 대한 게시글 목록 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 목록을 반환합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의 ID
	 * @param postType 요청한 게시글 타입, null일 경우 모두
	 * @return 게시글 목록(목록에 필요한 정보만)
	 */
	public List<LctPostLabelResp_STU> readPostList(
		UsersVO user
		, String lectureId
		, String postType
	);
	
	/**
	 * 특정 게시글 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 게시글 상세 내용을 반환합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lctPostId 강의 게시글 ID
	 * @return 게시글 상세 정보
	 */
	public LctPostDetailResp_STU readPost(
		UsersVO user
		, String lctPostId
	);
	
	/**
	 * 유효성 검사 후 댓글을 작성합니다. <br>
	 * 검사 목록 <br>
	 * 1. 사용자가 강의와 관련있는가? <br>
	 * 2. 게시글이 존재하고 삭제되지 않았으며, 강의와 관련있는가? <br>
	 * 3. 학생은 대댓글을 작성할 수 없습니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param request 부모댓글(optional), 댓글 내용이 포함된 요청 객체
	 * @param lectureId 강의ID(경로변수)
	 * @param lctPostId 강의게시글ID(경로변수)
	 */
	public void createReply(
		UsersVO user
		, LctPostReplyReq request
		, String lectureId
		, String lctPostId
	);
	
	/**
	 * 한 게시글에 달린 모든 댓글/대댓글을 조회합니다. <br>
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param lctPostId 게시글ID
	 * @return 게시글의 모든 댓글/대댓글
	 */
	public List<LctPostReplyResp> readReplyList(
		UsersVO user
		, String lectureId
		, String lctPostId
	);
	
	/**
	 * 자신이 작성한 댓글인지 확인한 후, <br>
	 * 자신이 작성한 댓글을 삭제 상태로 변경합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lctReplyId 댓글ID
	 */
	public void deleteReply(
		UsersVO user
		, String lctReplyId
	);
}