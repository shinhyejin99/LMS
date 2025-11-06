package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.info.LctPostReplyInfo;

@Mapper
public interface LctPostReplyMapper {
	
	/**
	 * 강의 게시글에 댓글/대댓글을 작성합니다. <br>
	 * 댓글ID는 셀렉트키로 생성합니다.
	 * 
	 * @param newReply 새 댓글
	 * @return 작성된 레코드 수 + 셀렉트키로 생성한 댓글ID
	 */
	public int insertReply(
		LctPostReplyInfo newReply
	);
	
	/**
	 * 특정 강의 게시글에 달린 댓글/대댓글 목록을 조회합니다. <br>
	 * 댓글/대댓글 가리지 않고 조회하며, 대댓글 작업은 프론트에서 수행합니다.
	 * 
	 * @param lctPostId 강의 게시글ID
	 * @return 게시글에 달린 모든 댓글/대댓글
	 */
	public List<LctPostReplyInfo> selectReplyList(
		String lctPostId
	);
	
	/**
	 * 댓글ID로 댓글 하나를 선택합니다. <br>
	 * 대댓글 작성 시 댓글이 존재하는지 확인하는 용도로 사용합니다.
	 * 
	 * @param lctReplyId 대상 댓글ID
	 * @return 없거나 삭제된 댓글일 경우 null
	 */
	public LctPostReplyInfo selectReply(
		String lctReplyId 
	);
	
	/**
	 * 댓글/대댓글 하나의 삭제 상태를 변경합니다.
	 * 
	 * @param lctReplyId 삭제상태 변경 대상 댓글ID
	 * @return 변경된 레코드 수
	 */
	public int deleteReplySoftly(
		String lctReplyId
	);
}
