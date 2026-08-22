package net.likelion.bebc25.springboard.member.repository;

import net.likelion.bebc25.springboard.member.dto.MemberDto;

import java.util.List;

/**
 * 데이터베이스와의 회원 데이터 처리를 담당하는 저장소 인터페이스입니다.
 */
public interface MemberRepository {
  /**
   * 새로운 회원 정보를 저장소에 저장합니다.
   *
   * @param member 저장할 회원 정보 DTO
   */
  void save(MemberDto member);

  /**
   * 회원 로그인 아이디(username)를 기반으로 회원 정보를 조회합니다.
   * username은 사용자의 실명이 아닌 로그인 시 사용하는 고유한 식별자(ID)입니다.
   *
   * @param username 조회할 로그인 아이디(고유 식별 ID)
   * @return 조회된 회원 정보 DTO, 없을 경우 null 반환
   */
  MemberDto findByUsername(String username);

  /**
   * 회원 일련번호를 기반으로 회원 정보를 조회합니다.
   *
   * @param id 조회할 회원 일련번호
   * @return 조회된 회원 정보 DTO, 없을 경우 null 반환
   */
  MemberDto findById(int id);

  /**
   * 기존 회원 정보를 수정합니다.
   *
   * @param member 수정할 회원 정보 DTO
   */
  void update(MemberDto member);

  /**
   * 회원 일련번호를 기반으로 회원 정보를 삭제합니다.
   *
   * @param id 삭제할 회원 일련번호
   */
  void deleteById(int id);

  /**
   * 저장소에 있는 모든 회원 목록을 조회합니다.
   *
   * @return 전체 회원 목록
   */
  List<MemberDto> findAll();
}
