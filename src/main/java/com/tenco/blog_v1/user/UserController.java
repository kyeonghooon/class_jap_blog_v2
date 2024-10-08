package com.tenco.blog_v1.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Slf4j
@Controller
public class UserController {

    // DI 처리
    private final UserRepository userRepository;
    private final HttpSession session;

    /**
     * 자원에 요청은 GET 방식이지만 보안에 이유로 예외 !
     * 로그인 처리 메서드
     * 요청 주소 POST : http://localhost:8080/login
     *
     * @param reqDto
     * @return
     */
    @PostMapping("/login")
    public String login(UserDTO.LoginDTO reqDto) {
        try {
            User sessionUser = userRepository.findByUsernameAndPassword(reqDto.getUsername(), reqDto.getPassword());
            session.setAttribute("sessionUser", sessionUser);
            return "redirect:/";
        } catch (Exception e) {
            // 로그인 실패
            return "redirect:/login-form?error";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션을 무효화 (로그아웃)
        return "redirect:/";
    }


    /**
     * 회원가입 페이지 요청
     * 주소설계 : http://localhost:8080/join-form
     *
     * @param model
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스태치 템플릿 엔진을 통해서 뷰 파일을 렌더링 합니다.
     */
    @GetMapping("/join-form")
    public String joinForm(Model model) {
        log.info("회원가입 페이지");
        model.addAttribute("name", "회원가입 페이지");
        return "user/join-form"; // 템플릿 경로 : user/join-form.mustache
    }

    /**
     * 로그인 페이지 요청
     * 주소설계 : http://localhost:8080/login-form
     *
     * @param model
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스태치 템플릿 엔진을 통해서 뷰 파일을 렌더링 합니다.
     */
    @GetMapping("/login-form")
    public String loginForm(Model model, @RequestParam(name = "error", required = false) String error) {
        log.info("로그인 페이지");
        model.addAttribute("name", "로그인 페이지");
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "user/login-form"; // 템플릿 경로 : user/join-form.mustache
    }

    /**
     * 회원 정보 수정 페이지 요청
     * 주소설계 : http://localhost:8080/user/update-form
     *
     * @param model
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스태치 템플릿 엔진을 통해서 뷰 파일을 렌더링 합니다.
     */
    @GetMapping("/user/update-form")
    public String updateForm(Model model) {
        log.info("회원 수정 페이지");
        model.addAttribute("name", "회원 수정 페이지");
        return "user/update-form"; // 템플릿 경로 : user/join-form.mustache
    }


}






