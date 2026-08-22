package net.likelion.bebc25.springboard.member.service;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.likelion.bebc25.springboard.exception.DuplicateUsernameException;
import net.likelion.bebc25.springboard.member.dto.MemberDto;
import net.likelion.bebc25.springboard.member.repository.MemberRepository;

/**
 * MemberService 인터페이스의 비즈니스 로직을 처리하는 기본 구현 클래스입니다.
 */
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  /**
   * 생성자를 통해 MemberRepository 의존성을 주입받습니다.
   *
   * @param memberRepository 주입받을 MemberRepository 스프링 빈 객체
   */
  public MemberServiceImpl(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void register(MemberDto member) {
    // 실습 영역
    try{
      memberRepository.save(member);
    }catch(DuplicateKeyException e){ // 중복 에러가 발생할 경우(username이 이미 등록되어 있을 경우)
      throw new DuplicateUsernameException("이미 사용중인 아이디입니다.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MemberDto login(String username, String password) {
    MemberDto member = memberRepository.findByUsername(username);
    if(member != null && member.getPassword().equals(password)){
      return member;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void modifyInfo(MemberDto member) {
    // 실습 영역
    memberRepository.update(member);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void withdraw(int id) {
    // 실습 영역
    memberRepository.deleteById(id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<MemberDto> getMembers() {
    return memberRepository.findAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MemberDto getMember(int id) {
    return memberRepository.findById(id);
  }
}
