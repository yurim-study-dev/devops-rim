package net.likelion.bebc25.springboard.config;

import net.likelion.bebc25.springboard.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 애플리케이션 웹 MVC 설정 클래스.
 * 인터셉터 등록, 정적 리소스(이미지) 매핑 경로 등 스프링 웹 동작 규칙을 관리합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  // application.properties 설정의 file.dir 프로퍼티 주입
  @Value("${file.dir}")
  private String fileDir;

  /**
   * 스프링 인터셉터를 등록하고 대상 경로 및 제외 경로를 설정합니다.
   * 인증되지 않은 사용자가 게시글 작성/수정/삭제 등 보호된 URL에 접근 시 로그인 화면으로 리다이렉트합니다.
   *
   * @param registry 인터셉터 등록 레지스트리
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginCheckInterceptor())
        .order(1)
        .addPathPatterns("/member/*", "/post/write", "/post/edit", "/post/delete")
        .excludePathPatterns("/member/login", "/member/register", "/css/**", "/js/**", "/*.ico", "/error");
  }

  /**
   * 브라우저의 /images/** 요청 경로를 외부 로컬 저장소 파일 디렉터리와 매핑합니다.
   * 브라우저에서 이미지 <img> 태그 요청 시 서버 외부 폴더(application.properties 설정의 file.dir 프로퍼티)에 위치한 실제 이미지를 찾아 연결합니다.
   *
   * @param registry 정적 리소스 핸들러 레지스트리
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 상대 경로를 절대 URI 경로 형식(file:///...)으로 변환
    String uploadPath = java.nio.file.Paths.get(fileDir).toAbsolutePath().toUri().toString();
    registry.addResourceHandler("/images/**")
        .addResourceLocations(uploadPath);
  }
}
