package com.tenco.blog_v1.board;

import com.tenco.blog_v1.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {

    // 네이티브 쿼리 연습
    private final BoardNativeRepository boardNativeRepository;
    // JPA, API, JPQL
    private final BoardRepository boardRepository;
    private final HttpSession session;

    // 특정 게시글 요청 화면
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
        // JPA API 사용
        // Board board = boardRepository.findById(id);

        // JPQL FETCH join 사용
        Board board = boardRepository.findByIdJoinUser(id);

        // 권한 체크
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser != null && board.getUser().getId().equals(sessionUser.getId())){
            request.setAttribute("authUser", true);
        }
        request.setAttribute("board", board);
        return "board/detail";
    }

    @GetMapping("/")
    public String index(Model model) {
        // List<Board> boardList = boardNativeRepository.findAll();
        // 코드 수정
        List<Board> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);
        return "index";
    }

    // 게시글 작성 화면
    @GetMapping("/board/save-form")
    public String saveForm() {
        return "board/save-form";
    }

    // 게시글 저장
    @PostMapping("/board/save")
    public String save(BoardDTO.SaveDTO reqDTO) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }
        // 파라미터가 올바르게 전달 되었는지 확인
        log.warn("save 실행 : 제목={}, 내용={}", reqDTO.getTitle(), reqDTO.getContent());

        // SaveDTO에서 toEntity 사용해서 BOard 엔티티로 변환하고 인수 값으로 User 정보를 넣었다.
        boardRepository.save(reqDTO.toEntity(sessionUser));
        return "redirect:/";
    }

    // 게시글 삭제
    // form 태그에서는 GET, POST 방식만 지원
    @PostMapping("/board/{id}/delete") // form 활용이기 때문에 delete 선언
    public String delete(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
        // 유효성, 인증검사
        // 세션에서 로그인 사용자 정보 가져오기 -> 인증, 인가(권한)
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        // 권한 체크
        Board board = boardRepository.findById(id);
        if (board == null) {
            return "redirect:/error-404";
        }

        if (!board.getUser().getId().equals(sessionUser.getId())) {
            return "redirect:/error-403";
        }
        boardRepository.deleteByIdWithAPI(id);
        return "redirect:/";
    }

    // 게시글 수정 화면 요청
    @GetMapping("board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
        // 1. 게시글 조회
        Board board = boardNativeRepository.findById(id);
        // 2. 요청 속성에 조회한 게시글 속성 및 값 추가
        request.setAttribute("board", board);
        // 뷰 리졸브 - 템플릿 반환
        return "board/update-form";
    }

    // 게시글 수정 요청 기능
    @PostMapping("board/{id}/update")
    public String update(@ModelAttribute BoardDTO.UpdateDTO reqDTO, @PathVariable(name = "id") Integer id) {

        // 1. 데이터 바인딩 방식 수정
        // 2. 인증 검사 - 로그인 여부 판단
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }
        // 3. 권한 체크 - 내 글이 맞니?
        Board board = boardRepository.findById(id);
        if (board == null) {
            return "redirect:/error-404";
        }

        if (!board.getUser().getId().equals(sessionUser.getId())) {
            return "redirect:/error-403";
        }
        // 4. 유효성 검사
        if (reqDTO.getTitle() == null || reqDTO.getContent() == null
                || reqDTO.getTitle().trim().length() == 0 || reqDTO.getContent().trim().length() == 0) {
            return "redirect:/error-400";
        }
        // 5. 서비스 측 위임 (직접 구현) - 레파지토리 사용
        //boardRepository.updateByIdJPQL(id, reqDTO.getTitle(), reqDTO.getContent());
        boardRepository.updateByIdJPA(id, reqDTO.getTitle(), reqDTO.getContent());
        // 6. 리다이렉트 처리

        return "redirect:/board/" + id;
    }
}
