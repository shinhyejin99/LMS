package kr.or.jsu.lms.professor.service;

import java.util.List;

import org.slf4j.Logger; // Added this import
import org.slf4j.LoggerFactory; // Added this import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Added this import

import kr.or.jsu.lms.professor.approval.mapper.ProfApprovalMapper;
import kr.or.jsu.vo.ApprovalVO;

@Service
public class ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class); // Added logger

    @Autowired
    private ProfApprovalMapper profApprovalMapper;

    @Transactional
    public void createApproval(ApprovalVO approvalVO, List<String> approverIds, String userId) {
        // Set the userId from the authenticated user (passed as a parameter)
        approvalVO.setUserId(userId);

        logger.info("Attempting to insert approval for user {}: {}", userId, approvalVO);

        // Insert the approval into the database
        profApprovalMapper.insertApproval(approvalVO);

        logger.info("Successfully inserted approval with ID: {}", approvalVO.getApproveId()); // Log after insert

        // TODO: Process approverIds and create entries in APPROVAL_LINE table
        // For now, just print them
        System.out.println("Approvers to be processed: " + approverIds);
    }

    public List<ApprovalVO> getApprovalList(String userId) {
        // Fetch actual approval list from the database for the given user
        return profApprovalMapper.selectApprovalList(userId);
    }
}
