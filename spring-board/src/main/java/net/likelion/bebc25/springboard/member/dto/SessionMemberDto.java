package net.likelion.bebc25.springboard.member.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 세션 저장소에 보관되는 로그인 회원 정보 DTO입니다.
 * 보안을 위해 비밀번호(password) 항목은 제외되어 있습니다.
 * 세션 클러스터링 및 영속화를 위해 Serializable을 구현합니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SessionMemberDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 회원 일련번호
     */
    private int id;

    /**
     * 회원 로그인 아이디
     */
    private String username;

    /**
     * 회원 이메일 주소
     */
    private String email;

    /**
     * 회원 권한 구분 (user, admin)
     */
    private String role;

    /**
     * MemberDto 객체로부터 세션 DTO를 생성하는 변환 생성자
     *
     * @param memberDto 회원 정보 DTO
     */
    public SessionMemberDto(MemberDto memberDto) {
        if (memberDto != null) {
            this.id = memberDto.getId();
            this.username = memberDto.getUsername();
            this.email = memberDto.getEmail();
            this.role = memberDto.getRole();
        }
    }
}
