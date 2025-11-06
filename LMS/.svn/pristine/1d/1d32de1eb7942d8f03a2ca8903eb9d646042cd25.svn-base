package kr.or.jsu.classroom.professor.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.info.LctPostInfo;
import kr.or.jsu.classroom.dto.info.LctPostReplyInfo;
import kr.or.jsu.classroom.dto.request.LctPostReplyReq;
import kr.or.jsu.classroom.dto.request.LecturePostReq;
import kr.or.jsu.classroom.dto.response.board.LctPostDetailResp_PRF;
import kr.or.jsu.classroom.dto.response.board.LctPostLabelResp_PRF;
import kr.or.jsu.classroom.dto.response.board.LctPostReplyResp;
import kr.or.jsu.classroom.professor.service.ClassPrfBoardService;
import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.response.FileDetailResp;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.mybatis.mapper.LctPostMapper;
import kr.or.jsu.mybatis.mapper.LctPostReplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassPrfBoardServiceImpl implements ClassPrfBoardService {
	
	private final LctPostMapper lctPostMapper;
	private final LctPostReplyMapper replyMapper;
	
	private final LMSFilesService fileService;
	private final ClassroomCommonService classService;
	
	private CustomUserDetails getUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null && auth.isAuthenticated()) {
			Object principal = auth.getPrincipal();
			
			if (principal instanceof CustomUserDetails) {
				CustomUserDetails user = (CustomUserDetails) principal;
				return user;
			}
		}
		
		throw new RuntimeException("사용자 정보를 가져올 수 없습니다.");
	}
	
	@Override
	public String createPost(
		LecturePostReq newPost // 게시글 작성 요청
		, String lectureId // 강의 번호
		, String prfNo // 교수 교번
	) {
		// 특정 클래스룸에 글쓰기 요청하는 교수가 담당교수가 맞는지 확인
		if(!classService.isRelevantClassroom(prfNo, lectureId))
			throw new RuntimeException("담당 교수만 작성할 수 있습니다.");
		
		String fileId = newPost.getFileId();
		
		// DB 저장용 Entity 생성, 필드 옮겨담기
		LctPostInfo lecturePost = new LctPostInfo();
		BeanUtils.copyProperties(newPost, lecturePost);
		lecturePost.setLectureId(lectureId);
		
		// 파일 첨부가 되어있으면 필요한 처리
		if(!fileId.isBlank()) {
			lecturePost.setAttachFileId(fileId);
			// 업로드했던 파일들 중 실제 사용하는 파일 체크
			fileService.changeUsingStatus(fileId, true);
		}
		
		lctPostMapper.insertLctPost(lecturePost);
		
		return lecturePost.getLctPostId();
	}

	@Override
	public List<LctPostLabelResp_PRF> readPostList(
		String lectureId // 강의 번호
		, String prfNo // 교수 교번
		, String postType
	) {
		// TODO 예외처리
		if(!classService.isRelevantClassroom(prfNo, lectureId))
			throw new RuntimeException("담당 교수만 열람할 수 있습니다.");
		// TODO 이거 hivernate validator 로 처리해야하지않나?
		if(postType != null && !"NOTICE".equals(postType) && !"MATERIAL".equals(postType))
			throw new RuntimeException("게시글 타입은 NOTICE/MATERIAL 중 선택하세요.");
		
		List<LctPostInfo> postList = lctPostMapper.selectPostListForPrf(lectureId, postType);
		return postList.stream().map(post -> {
			LctPostLabelResp_PRF resp = new LctPostLabelResp_PRF();
			BeanUtils.copyProperties(post, resp);
			return resp;
		}).collect(Collectors.toList());
	}
	
	@Override
	public LctPostDetailResp_PRF readPost(
		String lctPostId
		, String prfNo
	) {
		// 먼저 요청한 게시글을 가져와본다.
		LctPostInfo post = lctPostMapper.selectPost(lctPostId);
		// TODO 제대로 된 예외처리
		// 그런 게시글 자체가 없으면 예외 던지기
		if(post == null) throw new RuntimeException("존재하지 않는 게시글입니다.");
		
		// 어느 강의에 달린 게시글인지 확인
		String baseLectureId = post.getLectureId();
		
		// TODO 예외처리
		// 요청한 교수가 강의와 관련없으면 예외 던지기
		if(!classService.isRelevantClassroom(prfNo, baseLectureId))
			throw new RuntimeException("담당 교수만 열람할 수 있습니다.");
		
		// TODO 예외처리
		// 요청한 게시글이 삭제 상태면 예외 던지기
		if("Y".equals(post.getDeleteYn())) throw new RuntimeException("이미 삭제된 게시글입니다.");
		
		// 응답으로 나갈 객체 생성
		LctPostDetailResp_PRF resp = new LctPostDetailResp_PRF();
		// DB에서 가져온 결과 deleteYn 빼고 옮겨담기
		BeanUtils.copyProperties(post, resp);
		
		// 파일 서비스 호출해서 파일 정보 가져오기
		String fileId = post.getAttachFileId();
		if(fileId != null) {
			List<FileDetailResp> fileList = fileService.readFileDetailList(fileId);
			resp.setAttachFileList(fileList);
		}
		
		return resp;
	}

	@Override
	public void removePost(
		String lectureId
		, String lctPostId
		, String prfNo
	) {
		// 먼저 게시글 데이터 가져오기
		LctPostInfo targetPost = lctPostMapper.selectPost(lctPostId);
		// 게시글이 없거나 이미 삭제되었으면
		if(targetPost == null || "Y".equals(targetPost.getDeleteYn()))
			throw new RuntimeException("존재하지 않거나 이미 삭제된 게시글입니다."); 
		
		// 주어진 강의 소속 게시글이 맞는지 확인
		if(lectureId.equals(targetPost.getLctPostId()))
			throw new RuntimeException("해당 강의의 게시글이 아닙니다.");
		
		// 강의 소속 게시글이 맞으면, 담당 교수인지 확인
		boolean canDelete = classService.isRelevantClassroom(prfNo, lectureId); 
		if(!canDelete)
			throw new RuntimeException("글 삭제 권한이 없습니다."); 
		
		lctPostMapper.deleteLctPostSoft(lctPostId);
	}
	
	@Override
	public void editPost(
		LecturePostReq changedPost
		, String lectureId
		, String lctPostId
		, String prfNo
	) {
		// 특정 게시글 수정 요청하는 교수가 담당교수가 맞는지 확인
		if(!classService.isRelevantClassroom(prfNo, lectureId))
			// TODO 예외처리
			throw new RuntimeException("담당 교수만 수정할 수 있습니다.");
		
		// 실제 게시글 꺼내와서 어떤 강의 소속 게시글인지 확인
		LctPostInfo origin = lctPostMapper.selectPost(lctPostId);
		if(!lctPostId.equals(origin.getLctPostId()))
			throw new RuntimeException("요청하신 강의에 작성된 게시글이 아닙니다.");
		
		String originFileId = origin.getAttachFileId();
		String changedFileId = changedPost.getFileId();
		// 1. 기존에 파일이 없었음
		if(originFileId == null) {
			if(changedFileId == null) { // 수정할 때도 없음
				// doNothing
			} else { // 수정할 때는 있음 
				origin.setAttachFileId(changedFileId);
				fileService.changeUsingStatus(changedFileId, true);
			}
		// 2. 기존에 파일이 있었음
		} else {
			if(changedFileId == null) { // 수정할 때는 없음
				fileService.changeUsingStatus(originFileId, false);	
			} else {
				if(changedFileId.equals(originFileId)) { // 파일 변경 안함
					// doNothing					
				} else {
					fileService.changeUsingStatus(originFileId, false);
					origin.setAttachFileId(changedFileId);
					fileService.changeUsingStatus(changedFileId, true);
				}
			}
		}
				
		// 변경된 값만 옮겨담기
		// (파일ID는 필드명이 달라서 이 메서드로는 안 들어감)  
		BeanUtils.copyProperties(changedPost, origin);
		
		lctPostMapper.updateLctPost(origin);		
	}
	
	/**
	 * 유효성 검사 후 댓글을 작성합니다. <br>
	 * 검사 목록 <br>
	 * 1. 사용자가 강의와 관련있는가? <br>
	 * 2. 게시글이 존재하고 삭제되지 않았으며, 강의와 관련있는가? <br>
	 * 3. 대댓글일 경우, 요청한 부모 댓글이 존재하며 삭제되지 않았는가? <br>
	 * 
	 * @param request 부모댓글(optional), 댓글 내용이 포함된 요청 객체
	 * @param lectureId 강의ID(경로변수)
	 * @param lctPostId 강의게시글ID(경로변수)
	 */
	@Override
	public String createReply(
		LctPostReplyReq request
		, String lectureId
		, String lctPostId
	) {
		// 1. 사용자가 강의와 관련있는가?
		String userNo = getUser().getRealUser().getUserNo();
		boolean relevant = classService.isRelevantClassroom(userNo, lectureId);
		if(!relevant) throw new RuntimeException("관련없는 강의엔 댓글못달음.");
		
		// 2. 게시글이 존재하고 삭제되지 않았으며 강의와 관련있는가?
		LctPostInfo lctPost = lctPostMapper.selectPost(lctPostId);
		if(lctPost == null) throw new RuntimeException("삭제되었거나 존재하지 않는 게시글");
		if(!lectureId.equals(lctPost.getLectureId()))
			throw new RuntimeException("요청한 강의에 작성된 게시글이 아님");
		
		// 3. 대댓글 작성 요청일 경우
		String parentReplyId = request.getParentReplyId();
		if(StringUtils.isNotBlank(parentReplyId)) {
			LctPostReplyInfo parentReply = replyMapper.selectReply(parentReplyId);
			if(parentReply == null)
				throw new RuntimeException("부모 댓글이 삭제되었거나 존재하지 않음");
		}
		
		// 4. 댓글 작성용 객체 생성
		LctPostReplyInfo newReply = new LctPostReplyInfo();
		newReply.setLctPostId(lctPostId);
		newReply.setParentReply(parentReplyId == null ? null : parentReplyId);
		newReply.setUserId(getUser().getRealUser().getUserId());
		newReply.setReplyContent(request.getReplyContent());
		
		// 5. 댓글생성
		replyMapper.insertReply(newReply);
		
		// 6. 생성된 댓글ID 반환
		return newReply.getLctReplyId();
	}

	@Override
	public List<LctPostReplyResp> readReplyList(
		String lectureId
		, String lctPostId
	) {
		// 1. 사용자가 강의와 관련있는가?
		String userNo = getUser().getRealUser().getUserNo();
		boolean relevant = classService.isRelevantClassroom(userNo, lectureId);
		if(!relevant) throw new RuntimeException("관련없는 강의엔 댓글못달음.");
		
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
		String lectureId
		, String lctPostId
		, String lctReplyId
	) {
		// 1. 사용자가 강의와 관련있는가?
		String userNo = getUser().getRealUser().getUserNo();
		boolean relevant = classService.isRelevantClassroom(userNo, lectureId);
		if(!relevant) throw new RuntimeException("관련없는 강의엔 댓글못달음.");
		
		// 2. 게시글이 존재하고 삭제되지 않았으며 강의와 관련있는가?
		LctPostInfo lctPost = lctPostMapper.selectPost(lctPostId);
		if(lctPost == null) throw new RuntimeException("삭제되었거나 존재하지 않는 게시글");
		if(!lectureId.equals(lctPost.getLectureId()))
			throw new RuntimeException("요청한 강의에 작성된 게시글이 아님");
		
		// 3. 댓글이 존재하고 삭제되지 않았으며 게시글과 관련있는가?
		LctPostReplyInfo reply = replyMapper.selectReply(lctReplyId);
		if(reply == null) throw new RuntimeException("이미 삭제되었거나 존재하지 않는 댓글");
		if(!lctPostId.equals(reply.getLctPostId()))
			throw new RuntimeException("요청한 게시글에 작성된 댓글이 아님");
		
		// 4. 해당 댓글과 그 댓글에 대한 모든 대댓글 삭제 처리
		replyMapper.deleteReplySoftly(lctReplyId);
	}
}