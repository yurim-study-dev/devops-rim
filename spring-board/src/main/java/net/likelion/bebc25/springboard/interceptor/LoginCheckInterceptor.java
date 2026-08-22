package net.likelion.bebc25.springboard.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 사용자 인증(로그인) 상태를 검사하는 핸들러 인터셉터 클래스.
 * 세션 정보 유무를 통해 미인증 사용자의 접근을 차단하고 로그인 페이지로 리다이렉트합니다.
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

  /**
   * 컨트롤러 수행 전 실행되어 세션 기반 로그인 인증을 검사합니다.
   *
   * @param request HTTP 요청 객체
   * @param response HTTP 응답 객체
   * @param handler 실행 대상 핸들러
   * @return 인증 성공 시 true, 미인증 시 false (로그인 페이지 리다이렉트)
   * @throws Exception 예외 발생 시
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String requestUri = request.getRequestURI();
    HttpSession session = request.getSession(false);

    if(session == null || session.getAttribute("loginMember") == null){
      log.info("로그인 안된 사용자의 요청: " + requestUri);
      // 미인증 사용자일 경우 로그인 페이지로 리다이렉트 시킴
      response.sendRedirect("/member/login");
      return false; // HandlerInterceptor가 false 리턴할 경우 컨트롤러 핸들러를 실행하지 않음
    }

    return true; // HandlerInterceptor가 true를 리턴할 경우 다음 HandlerInterceptor나 컨트롤러 핸들러를 실행함
  }
}
