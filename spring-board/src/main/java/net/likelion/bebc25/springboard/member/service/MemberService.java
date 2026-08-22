package net.likelion.bebc25.springboard.member.service;

import net.likelion.bebc25.springboard.member.dto.MemberDto;

import java.util.List;

/**
 * 회원 가입, 로그인, 정보 수정, 회원 탈퇴 등 핵심 비즈니스 로직을 수행하는 서비스 인터페이스입니다.
 */
public interface MemberService {
  /**
   * 신규 회원 가입을 처리합니다.
   *
   * @param member 회원 가입을 요청할 회원 정보 DTO
   */
  void register(MemberDto member);

  /**
   * 로그인을 처리하기 위해 아이디(username)와 비밀번호로 회원을 인증합니다.
   *
   * @param username 로그인 아이디(사용자의 고유 식별 ID)
   * @param password 로그인 비밀번호
   * @return 인증에 성공한 회원 정보 DTO, 실패 시 null 반환
   */
  MemberDto login(String username, String password);

  /**
   * 회원 정보를 수정합니다.
   *
   * @param member 수정 정보가 담긴 회원 DTO
   */
  void modifyInfo(MemberDto member);

  /**
   * 회원 탈퇴(삭제) 처리를 수행합니다.
   *
   * @param id 탈퇴시킬 회원 일련번호
   */
  void withdraw(int id);

  /**
   * 가입된 모든 회원 목록을 가져옵니다.
   *
   * @return 전체 회원 리스트
   */
  List<MemberDto> getMembers();

  /**
   * 일련번호로 단일 회원의 정보를 상세 조회합니다.
   *
   * @param id 회원 일련번호
   * @return 조회된 회원 DTO
   */
  MemberDto getMember(int id);
}
