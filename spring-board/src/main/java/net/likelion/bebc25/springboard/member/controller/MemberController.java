package net.likelion.bebc25.springboard.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.springboard.exception.DuplicateUsernameException;
import net.likelion.bebc25.springboard.member.dto.MemberDto;
import net.likelion.bebc25.springboard.member.dto.SessionMemberDto;
import net.likelion.bebc25.springboard.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 회원 관련 요청(회원 가입, 로그인, 정보 수정, 탈퇴 등)을 처리하여 해당 화면 또는 동작으로 분기하는 컨트롤러 클래스입니다.
 */
@Controller
@Slf4j
@RequestMapping("/member")
public class MemberController {

  private final MemberService memberService;

  /**
   * 생성자를 통해 MemberService 의존성을 주입받습니다.
   *
   * @param memberService 주입받을 MemberService 스프링 빈 객체
   */
  public MemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  /**
   * 전체 회원 목록을 조회하고 회원 목록 정적 화면으로 유도합니다.
   *
   * @param model 화면에 전달할 데이터를 담는 Model 객체
   * @return 회원 목록 화면으로의 redirect 경로
   */
  @GetMapping("/list")
  public String getMemberList(Model model) {
    // 실습 영역
    List<MemberDto> members = memberService.getMembers();
    model.addAttribute("members", members);

    return "member/list";
  }

  /**
   * 회원 가입 양식 화면으로 유도합니다.
   *
   * @return 회원 가입 화면으로의 redirect 경로
   */
  @GetMapping("/register")
  public String getRegisterForm(@ModelAttribute("memberForm") MemberDto member) { // 모델에 자동으로 주입까지 됨(memberForm 이름으로)
    // 실습 영역
    return "member/register";
  }

  /**
   * 신규 회원 가입 요청 데이터를 받아 등록 처리를 수행합니다.
   *
   * @param memberDto 회원 가입 폼 입력 데이터 DTO
   * @return 로그인 화면으로의 redirect 경로
   */
  @PostMapping("/register")
  public String register(@Valid @ModelAttribute("memberForm") MemberDto memberDto  // Validation 검증 대상 객체
                              , BindingResult bindingResult) { // Validation 검증 결과 저장 객체(대상 객체 뒤에 기술해야 함)
    // 실습 영역
    if(bindingResult.hasErrors()){ // 검증에 실패했을 경우
      return "member/register"; // 작성중이던 페이지로 다시 보낸다.
    }

    try{
      memberService.register(memberDto);
    }catch(DuplicateUsernameException e){
      // username 이 중복되는 예외 발생 시 username 필드 에러로 바인딩
      // rejectValue(에러가 발생한 필드, 에러코드, 기본 에러메세지)
      // 에러코드: 메세지 설정파일(errors.properties, messages.properties)에 정의한 키값(없을 경우 세번째 에러메세지로 대체됨)
      bindingResult.rejectValue("username", "duplicate", e.getMessage());
      return "member/register";
    }

    return "redirect:/member/login"; // 브라우저에 login으로 재요청하라고 응답
  }

  /**
   * 로그인 양식 화면으로 유도합니다.
   *
   * @return 로그인 화면으로의 redirect 경로
   */
  @GetMapping("/login")
  public String getLoginForm(@ModelAttribute("loginForm") MemberDto memberDto) {  // 모델에 자동으로 주입까지 됨(loginForm 이름으로)
    // 실습 영역
    return "member/login";
  }

  /**
   * 로그인 인증 요청을 처리합니다.
   *
   * @param member 사용자가 입력한 username, password가 들어있는 DTO
   * @return 회원 목록 화면으로의 redirect 경로
   */
  @PostMapping("/login")
  public String login(@Valid @ModelAttribute("loginForm") MemberDto member,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) { // 로그인 실패시 에러메세지와 함께
    // 실습 영역
    if(bindingResult.hasErrors()){ // 검증에 실패했을 경우
      return "member/login"; // 작성중이던 페이지로 다시 보낸다.
    }

    // 로그인 시도
    MemberDto memberInfo = memberService.login(member.getUsername(), member.getPassword());
    if(memberInfo == null){ // 로그인 실패시
      // 실패 메시지를 담고 다시 로그인 페이지로 리다이렉트
      // addFlashAttribute: 임시로 세션에 속성을 담아서 리다이렉트 된 페이지에서 꺼내어 사용 후 속성값은 세션에서 제거함
      redirectAttributes.addFlashAttribute("errorMessage", "아이디 또는 비밀번호를 확인하세요.");
      redirectAttributes.addFlashAttribute("loginForm", member); // 입력 폼 데이터 유지
      return "redirect:/member/login"; // 로그인 폼 페이지로 이동
    }

    // 로그인 성공 시 세션 생성해서 사용자 정보를 저장
    SessionMemberDto sessionMember = new SessionMemberDto(memberInfo);
    session.setAttribute("loginMember", sessionMember);

    return "redirect:/member/list";
  }

  @PostMapping("/logout")
  public String logout(HttpSession httpSession){
    httpSession.invalidate(); // 세션 파기
    return "redirect:/";
  }

  /**
   * 회원 정보 수정 화면으로 유도합니다.
   *
   * @param id 수정할 회원의 일련번호
   * @param model 화면에 전달할 데이터를 담는 Model 객체
   * @return 회원 정보 수정 화면으로의 redirect 경로
   */
  @GetMapping("/edit")
  public String getEditForm(@RequestParam int id, Model model) {
    // 실습 영역
    MemberDto member = memberService.getMember(id);
    model.addAttribute("memberForm", member);

    return "member/edit";
  }

  /**
   * 회원 정보 수정 요청 데이터를 받아 반영 처리를 수행합니다.
   *
   * @param memberDto 수정 요청 데이터 DTO
   * @return 회원 목록 화면으로의 redirect 경로
   */
  @PostMapping("/edit")
  public String edit(@Valid @ModelAttribute("memberForm") MemberDto memberDto,
                     BindingResult bindingResult) {
    // 실습 영역
    if(bindingResult.hasErrors()){ // 검증에 실패했을 경우
      return "member/edit"; // 작성중이던 페이지로 다시 보낸다.
    }

    // 수정 작업
    memberService.modifyInfo(memberDto);

    return "redirect:/member/list";
  }

  /**
   * 회원 탈퇴 요청을 받아 삭제 처리를 수행합니다.
   *
   * @param id 탈퇴할 회원의 일련번호
   * @return 회원 목록 화면으로의 redirect 경로
   */
  @PostMapping("/withdraw")
  public String withdraw(@RequestParam int id) {
    // 실습 영역
    memberService.withdraw(id);
    return "redirect:/member/list";
  }
}
