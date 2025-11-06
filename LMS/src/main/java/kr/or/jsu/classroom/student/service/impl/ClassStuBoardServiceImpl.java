package kr.or.jsu.classroom.student.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.dto.info.LctPostInfo;
import kr.or.jsu.classroom.dto.info.LctPostReplyInfo;
import kr.or.jsu.classroom.dto.request.LctPostReplyReq;
import kr.or.jsu.classroom.dto.response.board.LctPostDetailResp_STU;
import kr.or.jsu.classroom.dto.response.board.LctPostLabelResp_STU;
import kr.or.jsu.classroom.dto.response.board.LctPostReplyResp;
import kr.or.jsu.classroom.student.service.ClassStuBoardService;
import kr.or.jsu.classroom.student.service.ClassroomStudentService;
import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.response.FileDetailResp;
import kr.or.jsu.mybatis.mapper.LctPostMapper;
import kr.or.jsu.mybatis.mapper.LctPostReplyMapper;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassStuBoardServiceImpl implements ClassStuBoardService {
	
	private final LctPostMapper lctPostMapper;
	private final LctPostReplyMapper replyMapper;
	
	private final ClassroomStudentService classRootService;
	private final LMSFilesService fileService;
	
	/**
	 * 특정 강의에 대한 게시글 목록 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 목록을 반환합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의 ID
	 * @param postType 요청한 게시글 타입, null일 경우 모두
	 * @return 게시글 목록(목록에 필요한 정보만)
	 */
	@Override
	public List<LctPostLabelResp_STU> readPostList(
		UsersVO user
		, String lectureId
		, String postType
	) {
		// 1. 관련 있는 학생인지 확인
		classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// TODO 파라미터 유효성검증은 컨트롤러에서 하고들어옵시다 
		if(postType != null && !"NOTICE".equals(postType) && !"MATERIAL".equals(postType))
			throw new RuntimeException("게시글 타입은 NOTICE/MATERIAL 중 선택하세요.");
		
		// 2. 조건에 따라 검색
		List<LctPostInfo> postList = lctPostMapper.selectPostListForPrf(lectureId, postType);
		
		// 3. 응답용 객체로 바꿔서 반환
		return postList.stream().map(post -> {
			LctPostLabelResp_STU resp = new LctPostLabelResp_STU();
			BeanUtils.copyProperties(post, resp);
			return resp;
		}).collect(Collectors.toList());
	}

	/**
	 * 특정 게시글 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 게시글 상세 내용을 반환합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lctPostId 강의 게시글 ID
	 * @return 게시글 상세 정보
	 */
	@Override
	public LctPostDetailResp_STU readPost(
		UsersVO user
		, String lctPostId
	) {
		// TODO 예외처리
		
		// 1. 먼저 요청한 게시글을 가져와본다.
		LctPostInfo post = lctPostMapper.selectPost(lctPostId);
		// 그런 게시글 자체가 없으면 예외 던지기
		if(post == null) throw new RuntimeException("존재하지 않는 게시글입니다.");
		
		// 2. 어느 강의에 달린 게시글인지 확인
		String baseLectureId = post.getLectureId();
		
		// 3. 요청한 학생이 강의와 관련없으면 예외 던지기
		classRootService.checkRelevantAndGetMyEnrollInfo(user, baseLectureId);
		
		// 4. 요청한 게시글이 삭제 상태면 예외 던지기
		if("Y".equals(post.getDeleteYn())) throw new RuntimeException("이미 삭제된 게시글입니다.");
		
		// 5. 응답으로 나갈 객체 생성, 옮겨담기
		LctPostDetailResp_STU resp = new LctPostDetailResp_STU();
		BeanUtils.copyProperties(post, resp);
		
		// 파일 서비스 호출해서 파일 정보 가져오기
		String fileId = post.getAttachFileId();
		if(fileId != null) {
			List<FileDetailResp> fileList = fileService.readFileDetailList(fileId);
			resp.setAttachFileList(fileList);
		}
		
		return resp;
	}
	
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
	@Override
	public void createReply(
		UsersVO user
		, LctPostReplyReq request
		, String lectureId
		, String lctPostId
	) {
		// 1. 사용자가 강의와 관련있는가?
		classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 게시글이 존재하고 삭제되지 않았으며 해당 강의 소속인가?
		LctPostInfo lctPost = lctPostMapper.selectPost(lctPostId);
		if(lctPost == null) throw new RuntimeException("삭제되었거나 존재하지 않는 게시글");
		if(!lectureId.equals(lctPost.getLectureId()))
			throw new RuntimeException("요청한 강의에 작성된 게시글이 아님");
		
		// 3. 대댓글 작성 요청일 경우
		if(request.getParentReplyId() != null)
			throw new RuntimeException("학생은 대댓글을 작성할 수 없습니다.");
		
		// 4. 댓글 작성용 객체 생성
		LctPostReplyInfo newReply = new LctPostReplyInfo();
		newReply.setLctPostId(lctPostId);
		newReply.setUserId(user.getUserId());
		newReply.setReplyContent(request.getReplyContent());
		
		// 5. 댓글생성
		replyMapper.insertReply(newReply);
	}
	
	/**
	 * 한 게시글에 달린 모든 댓글/대댓글을 조회합니다. <br>
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @param lctPostId 게시글ID
	 * @return 게시글의 모든 댓글/대댓글
	 */
	@Override
	public List<LctPostReplyResp> readReplyList(
		UsersVO user
		, String lectureId
		, String lctPostId
	) {
		// 1. 사용자가 강의와 관련있는가?
		classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 게시글이 존재하고 삭제되지 않았으며 강의와 관련있는가?
		LctPostInfo lctPost = lctPostMapper.selectPost(lctPostId);
		if(lctPost == null) throw new RuntimeException("삭제되었거나 존재하지 않는 게시글");
		if(!lectureId.equals(lctPost.getLectureId()))
			throw new RuntimeException("요청한 강의에 작성된 게시글이 아님");
		
		// 3. 삭제 안 된 댓글만 찾아오기
		List<LctPostReplyInfo> list = replyMapper.selectReplyList(lctPostId);
		
		// 4. 응답용 객체로 바꿔서 반환
		return list.stream().map(reply -> {
			LctPostReplyResp resp = new LctPostReplyResp();
			BeanUtils.copyProperties(reply, resp);
			return resp;
		}).collect(Collectors.toList());
	}

	@Override
	public void deleteReply(
		UsersVO user
		, String lctReplyId
	) {
		// 1. 댓글이 존재하고 삭제되지 않았는가?
		LctPostReplyInfo reply = replyMapper.selectReply(lctReplyId);
		if(reply == null) throw new RuntimeException("이미 삭제되었거나 존재하지 않는 댓글");
		
		// 2. 해당 댓글의 작성자가 사용자 본인인가?
		if(!user.getUserId().equals(reply.getUserId()))
			throw new RuntimeException("자신이 작성한 댓글만 삭제 가능.");
		
		replyMapper.deleteReplySoftly(lctReplyId);
	} 
}
