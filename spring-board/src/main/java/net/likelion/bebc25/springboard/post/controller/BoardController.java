package net.likelion.bebc25.springboard.post.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.springboard.file.FileStore;
import net.likelion.bebc25.springboard.member.dto.SessionMemberDto;
import net.likelion.bebc25.springboard.post.dto.PageDto;
import net.likelion.bebc25.springboard.post.dto.PostDto;
import net.likelion.bebc25.springboard.post.service.PostService;

/**
 * 게시글 관련 웹 요청을 처리하는 컨트롤러 클래스.
 * 사용자의 브라우저 요청(URL)을 제일 먼저 받아서 적절한 서비스를 호출하고 뷰 템플릿(HTML)으로 데이터를 전달합니다.
 */
@Controller
@Slf4j
@RequestMapping("/post")
public class BoardController {

  private final PostService postService;
  private final FileStore fileStore;

  /**
   * BoardController 생성자.
   * 스프링의 의존성 주입(DI) 메커니즘을 사용하여 필요한 서비스 및 파일 저장소 객체를 주입받습니다.
   *
   * @param postService 게시글 비즈니스 로직을 처리하는 서비스
   * @param fileStore 파일 업로드/저장을 처리하는 파일 스토어 (나중에 S3 구현체 등으로 교체 가능하도록 @Qualifier 지정)
   */
  public BoardController(PostService postService, @Qualifier("localFileStore") FileStore fileStore){
    this.postService = postService;
    this.fileStore = fileStore;
  }

  /**
   * 게시글 목록 및 검색, 페이징 조회 요청을 처리합니다.
   *
   * @param page 현재 페이지 번호 (기본값: 1)
   * @param size 페이지당 게시글 수 (기본값: 5)
   * @param type 검색 유형 (title, content, author 등)
   * @param keyword 검색 키워드
   * @param model 뷰(HTML)에 전달할 데이터 상자 객체
   * @return 게시글 목록 뷰 템플릿 경로 (board/list.html)
   */
  @GetMapping("/list")
  public String getBoardList(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "size", defaultValue = "5") int size,
                             @RequestParam(value = "type", required = false) String type,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             Model model){
    PageDto<PostDto> pageResponse = postService.searchPosts(type, keyword, page, size);
    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("posts", pageResponse.getContent());
    model.addAttribute("type", type);
    model.addAttribute("keyword", keyword);
    return "board/list";
  }

  /**
   * 게시글 상세 정보를 조회합니다.
   * 비밀글인 경우 본인 글이거나 관리자 권한인지 체크 후 뷰로 전달합니다.
   *
   * @param id 조회할 게시글 식별자
   * @param model 뷰에 전달할 데이터 모델
   * @param session 현재 사용자 세션 객체
   * @return 게시글 상세 뷰 템플릿 경로 또는 목록 리다이렉트
   */
  @GetMapping("/detail")
  public String getDetail(@RequestParam("id") int id, Model model, HttpSession session){
    PostDto post = postService.getPost(id);

    // 비밀글 접근 권한 체크 (본인 작성글이 아니거나 관리자가 아닌 경우 목록으로 이동)
    if(post.isSecret()){
      SessionMemberDto loginMember = (SessionMemberDto) session.getAttribute("loginMember");
      if(loginMember == null || (loginMember.getId() != post.getMemberId() && !"admin".equals(loginMember.getRole()))){
        return "redirect:/post/list";
      }
    }

    // 파일이 이미지 형식인지 확인하여 뷰(detail.html)에 isImage boolean 값 전달
    boolean isImage = post.getContentType() != null && post.getContentType().startsWith("image/");
    if(!isImage && post.getOriginalFilename() != null){
      isImage = fileStore.isImage(post.getOriginalFilename());
    }
    model.addAttribute("post", post);
    model.addAttribute("isImage", isImage);
    return "board/detail";
  }

  /**
   * 게시글 작성 양식 화면을 반환합니다.
   *
   * @param post 작성 폼 데이터 바인딩용 DTO
   * @return 게시글 작성/수정 뷰 템플릿 경로 (board/write.html)
   */
  @GetMapping("/write")
  public String getWriteForm(@ModelAttribute("postForm") PostDto post){
    return "board/write";
  }

  /**
   * 게시글 수정 양식 화면을 반환합니다.
   * 수정하려는 글의 작성자 본인이거나 관리자 권한인지 체크합니다.
   *
   * @param id 수정할 게시글 식별자
   * @param model 뷰에 전달할 데이터 모델
   * @param session 현재 사용자 세션
   * @return 게시글 수정 뷰 템플릿 경로 또는 목록 리다이렉트
   */
  @GetMapping("/edit")
  public String getEditForm(@RequestParam("id") int id, Model model, HttpSession session){
    PostDto post = postService.getPost(id);
    SessionMemberDto loginMember = (SessionMemberDto) session.getAttribute("loginMember");

    // 수정 권한 체크 (본인 작성글이 아니거나 관리자가 아닌 경우 목록으로 이동)
    if(loginMember == null || (loginMember.getId() != post.getMemberId() && !"admin".equals(loginMember.getRole()))){
      return "redirect:/post/list";
    }

    model.addAttribute("postForm", post);
    return "board/write";
  }

  /**
   * 게시글 작성 요청을 처리합니다 (첨부파일 업로드 연동).
   * 세션에서 로그인한 회원의 식별자(memberId)를 꺼내 작성자 고유 ID로 지정합니다.
   *
   * @param post 저장할 게시글 DTO (@Valid로 입력값 유효성 검증)
   * @param bindingResult 입력값 검증 에러 결과를 담는 객체
   * @param session 현재 사용자 세션
   * @return 게시글 목록 리다이렉트 또는 입력 폼 뷰 템플릿 경로
   * @throws IOException 파일 저장 실패 시
   */
  @PostMapping("/write")
  public String writePost(@Valid @ModelAttribute("postForm") PostDto post,
                          BindingResult bindingResult,
                          HttpSession session) throws IOException {
    SessionMemberDto loginMember = (SessionMemberDto) session.getAttribute("loginMember");
//    if(loginMember == null){
//      return "redirect:/member/login";
//    }
    if(bindingResult.hasErrors()){
      return "board/write";
    }
    // 로그인된 회원의 PK(memberId)를 게시글의 작성자 ID로 지정
    post.setMemberId(loginMember.getId());

    // 파일이 업로드된 경우 파일 저장소(FileStore)를 이용해 물리 저장 후 파일 정보 저장
    if(post.getFile() != null && !post.getFile().isEmpty()){
      String storeFilename = fileStore.storeFile(post.getFile());
      post.setOriginalFilename(post.getFile().getOriginalFilename());
      post.setStoreFilename(storeFilename);
      post.setContentType(post.getFile().getContentType());
    }

    postService.writePost(post);
    return "redirect:/post/list";
  }

  /**
   * 게시글 수정 요청을 처리합니다.
   *
   * @param post 수정할 게시글 DTO
   * @param bindingResult 입력값 유효성 검증 결과
   * @param session 현재 사용자 세션
   * @return 게시글 상세 리다이렉트 또는 입력 폼 뷰 템플릿 경로
   * @throws IOException 파일 저장 실패 시
   */
  @PostMapping("/edit")
  public String editPost(@Valid @ModelAttribute("postForm") PostDto post,
                         BindingResult bindingResult,
                         HttpSession session) throws IOException {
    SessionMemberDto loginMember = (SessionMemberDto) session.getAttribute("loginMember");
    if(loginMember == null){
      return "redirect:/member/login";
    }

    PostDto existingPost = postService.getPost(post.getId());
    // 수정 권한 체크 (본인 작성글이 아니거나 관리자가 아닌 경우 목록으로 이동)
    if(loginMember.getId() != existingPost.getMemberId() && !"admin".equals(loginMember.getRole())){
      return "redirect:/post/list";
    }

    if(bindingResult.hasErrors()){
      return "board/write";
    }

    // 새로운 파일이 들어온 경우 새로 저장, 없으면 기존 첨부파일 정보 유지
    if(post.getFile() != null && !post.getFile().isEmpty()){
      String storeFilename = fileStore.storeFile(post.getFile());
      post.setOriginalFilename(post.getFile().getOriginalFilename());
      post.setStoreFilename(storeFilename);
      post.setContentType(post.getFile().getContentType());
    } else {
      post.setOriginalFilename(existingPost.getOriginalFilename());
      post.setStoreFilename(existingPost.getStoreFilename());
      post.setContentType(existingPost.getContentType());
    }

    postService.editPost(post);
    return "redirect:/post/detail?id=" + post.getId();
  }

  /**
   * 게시글 삭제 요청을 처리합니다.
   *
   * @param id 삭제할 게시글 식별자
   * @param session 현재 사용자 세션
   * @return 게시글 목록 리다이렉트
   */
  @PostMapping("/delete")
  public String deletePost(@RequestParam int id, HttpSession session){
    PostDto postDto = postService.getPost(id);
    SessionMemberDto loginMember = (SessionMemberDto) session.getAttribute("loginMember");

    // 삭제 권한 체크 (작성자 본인이거나 관리자인 경우만 삭제 허용)
    if(loginMember != null && (loginMember.getId() == postDto.getMemberId() || "admin".equals(loginMember.getRole()))){
      postService.removePost(id);
    }

    return "redirect:/post/list";
  }

  /**
   * 게시글에 첨부된 파일을 다운로드합니다.
   * 브라우저에 파일을 텍스트로 띄우지 않고 다운로드 창이 뜨도록 HTTP 응답 헤더(Content-Disposition)를 설정하여 파일을 전송합니다.
   *
   * @param id 게시글 식별자
   * @return 파일 바이너리를 담은 HTTP Response 객체
   * @throws IOException 파일 읽기 오류 발생 시
   */
  @GetMapping("/download/{id}")
  public ResponseEntity<Resource> downloadFile(@PathVariable("id") int id) throws IOException {
    PostDto post = postService.getPost(id);
    if(post == null || post.getStoreFilename() == null){
      return ResponseEntity.notFound().build();
    }

    Resource resource = fileStore.getFileResource(post.getStoreFilename());
    // 한글 파일명이 깨지지 않도록 UTF-8 인코딩 적용
    String encodedOriginalFileName = UriUtils.encode(post.getOriginalFilename(), StandardCharsets.UTF_8);
    String contentDisposition = "attachment; filename=\"" + encodedOriginalFileName + "\"";

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .body(resource);
  }
}