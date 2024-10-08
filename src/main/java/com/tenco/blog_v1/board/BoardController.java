package com.tenco.blog_v1.board;

import com.tenco.blog_v1.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        User sessionUser = (User)session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }
        boardRepository.deleteById(id, sessionUser.getId());
        return "redirect:/";
    }

    // 게시글 수정 화면 요청
    @GetMapping("board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
        Board board = boardNativeRepository.findById(id);
        request.setAttribute("board", board);
        return "board/update-form";
    }

    // 게시글 수정 요청 기능
    @PostMapping("board/{id}/update")
    public String update(@PathVariable(name = "id") Integer id,
                         @RequestParam(name = "title") String title,
                         @RequestParam(name = "content") String content) {
        boardNativeRepository.updateById(id, title, content);
        return "redirect:/board/" + id;
    }
}
