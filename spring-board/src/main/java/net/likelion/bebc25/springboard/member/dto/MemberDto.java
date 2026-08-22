package net.likelion.bebc25.springboard.member.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 회원 정보 데이터를 전달하기 위한 데이터 객체(DTO)입니다.
 * 유효성 검증을 위한 어노테이션이 적용되어 있습니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MemberDto {
  /**
   * 회원 일련번호
   */
  private int id;

  /**
   * 회원 로그인 아이디
   */
  @NotBlank(message = "아이디는 필수 입력 항목입니다.")
  @Size(min = 4, max = 50, message = "아이디는 4자 이상 50자 이하여야 합니다.")
  private String username;

  /**
   * 회원 로그인 비밀번호
   */
  @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
  @Size(min = 4, max = 100, message = "비밀번호는 4자 이상 100자 이하여야 합니다.")
  private String password;

  /**
   * 회원 이메일 주소
   */
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private String email;

  /**
   * 회원 권한 구분 (user, admin)
   */
  @Builder.Default // Builder 패턴 적용 시 기본값("user")이 null로 초기화되는 것을 방지
  private String role = "user";

  /**
   * 회원 가입 일시
   */
  private LocalDateTime createdAt;
}
