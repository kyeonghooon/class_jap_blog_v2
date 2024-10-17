package com.tenco.blog_v1.board;

import com.tenco.blog_v1.common.errors.Exception403;
import com.tenco.blog_v1.common.errors.Exception404;
import com.tenco.blog_v1.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service // IoC 처리
public class BoardService {

    private final BoardJPARepository boardJPARepository;

    /**
     * 게시글 ID로 조회 서비스
     */
    public Board getBoard(Integer boardId) {
        return boardJPARepository
                .findById(boardId)
                .orElseThrow(() -> new Exception404("존재하지 않는 게시글입니다."));
    }

    /**
     * 게시글 상세보기 서비스, 게시글 주인 여부 판별
     */
    public Board getBoardDetails(Integer boardId, User sessionUser) {
        // 1번 전략
//        Board board = boardJPARepository
//                .findById(boardId)
//                .orElseThrow(() -> new Exception404("존재하지 않는 게시글입니다."));

        // 2번 전략
        Board board = boardJPARepository
                .findByIdJoinUser(boardId)
                .orElseThrow(() -> new Exception404("존재하지 않는 게시글입니다."));

        boolean boardOwner = false;
        if (board.getUser().getId().equals(sessionUser.getId())) {
            boardOwner = true;
        }

        // 내가 작성한 댓글인가를 구현 해야 한다.
        board.getReplies().forEach(reply -> {
            reply.setReplyOwner(sessionUser.getId().equals(reply.getUser().getId()));
        });
        board.setBoardOwner(boardOwner);
        return board;
    }

    /**
     * 게시글 삭제 서비스
     */
    @Transactional
    public void deleteBoard(Integer boardId, Integer sessionUserId) {
        // 1.
        Board board = boardJPARepository
                .findById(boardId)
                .orElseThrow(() -> new Exception404("존재하지 않는 게시글입니다."));

        // 2. 권한 확인 - 현재 사용자가 게시글 주인이 맞는가?
        if (sessionUserId != board.getUser().getId()) {
            throw new Exception403("권한이 없습니다.");
        }

        // 3. 게시글 삭제 하기
        boardJPARepository.deleteById(boardId);
    }

    /**
     * 게시글 수정 서비스
     */
    @Transactional
    public void updateBoard(Integer boardId, Integer sessionUserId, BoardDTO.UpdateDTO reqDto) {
        // 1. 게시글 존재 여부 확인
        Board board = boardJPARepository
                .findById(boardId)
                .orElseThrow(() -> new Exception404("존재하지 않는 게시글입니다."));

        // 2. 권한 확인
        if (sessionUserId != board.getUser().getId()) {
            throw new Exception403("권한이 없습니다.");
        }

        // 3. 게시글 수정
        board.setTitle(reqDto.getTitle());
        board.setContent(reqDto.getContent());
    }

    /**
     * 모든 게시글 조회 서비스
     */
    public List<Board> getAllBoards() {
        // 게시글을 ID 기준으로 내림차순으로 정렬해서 조회 해라.
        return boardJPARepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 새로운 게시글을 작성하여 저장합니다.
     *
     * @param reqDTO 게시글 작성 요청 DTO
     * @param sessionUser 현재 세션에 로그인한 사용자
     */
    @Transactional // 트랜잭션 관리: 데이터베이스 연산이 성공적으로 완료되면 커밋, 실패하면 롤백
    public void createBoard(BoardDTO.SaveDTO reqDTO, User sessionUser){
        // 요청 DTO를 엔티티로 변환하여 저장합니다.
        boardJPARepository.save(reqDTO.toEntity(sessionUser));
    }
}

