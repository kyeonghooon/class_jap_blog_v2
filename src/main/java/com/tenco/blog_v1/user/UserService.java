package com.tenco.blog_v1.user;

import com.tenco.blog_v1.common.errors.Exception400;
import com.tenco.blog_v1.common.errors.Exception401;
import com.tenco.blog_v1.common.errors.Exception404;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // IoC
@RequiredArgsConstructor
public class UserService {

    private final UserJPARepository userJPARepository;

    /**
     * 회원 가입 서비스
     */
    @Transactional
    public void signUp(UserDTO.JoinDTO reqDto) {
        // 1. username <-- 유니크 확인
        Optional<User> userOpt = userJPARepository.findByUsername(reqDto.getUsername());
        if (userOpt.isPresent()) {
            throw new Exception400("중복된 유저네임입니다.");
        }

        userJPARepository.save(reqDto.toEntity());
    }

    /**
     * 로그인 서비스
     */
    public User signIn(UserDTO.LoginDTO reqDto) {
        User sessionUser = userJPARepository
                .findByUsernameAndPassword(reqDto.getUsername(), reqDto.getPassword())
                .orElseThrow(() -> new Exception401("인증되지 않았습니다."));
        return sessionUser;
    }

    /**
     * 회원 정보 조회 서비스
     *
     * @param id 조회할 사용자 ID
     * @return 조회된 사용자 객체
     * @throws Exception404 사용자를 찾을 수 없는 경우 발생
     */
    public User readUser(Integer id) {
        return userJPARepository.findById(id)
                .orElseThrow(() -> new Exception404("회원정보를 찾을 수 없습니다"));
    }

    /**
     * 회원 정보 수정 서비스
     *
     * @param id     수정할 사용자 ID
     * @param reqDTO 수정된 사용자 정보 DTO
     * @return 수정된 사용자 객체
     * @throws Exception404 사용자를 찾을 수 없는 경우 발생
     */
    @Transactional
    public User updateUser(UserDTO.UpdateDTO reqDto, Integer id) {
        User user = userJPARepository.findById(id).orElseThrow(() -> new Exception404("회원정보를 찾을 수 없습니다"));
        user.setPassword(reqDto.getPassword());
        user.setEmail(reqDto.getEmail());
        return user;
    }
}
