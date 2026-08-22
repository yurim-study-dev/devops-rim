package net.likelion.bebc25.springboard.post.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import net.likelion.bebc25.springboard.member.dto.SessionMemberDto;
import net.likelion.bebc25.springboard.post.dto.PageDto;
import net.likelion.bebc25.springboard.post.dto.PostDto;
import net.likelion.bebc25.springboard.post.service.PostService;

import net.likelion.bebc25.springboard.file.FileStore;

// BoardController 순수 단위 테스트 (MockMvc standaloneSetup 기반)
@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

  // HTTP 요청 및 응답 시뮬레이션을 위한 MockMvc 객체
  private MockMvc mockMvc;

  // BoardController가 의존하는 PostService를 Mock(가짜 객체)으로 지정
  @Mock
  private PostService postService;

  // 파일 저장을 담당하는 FileStore를 Mock(가짜 객체)으로 지정
  @Mock
  private FileStore fileStore;

  // 컨트롤러 객체에 Mock 서비스 및 FileStore 자동 주입
  @InjectMocks
  private BoardController boardController;

  // 각 테스트 실행 전 standaloneSetup을 사용하여 MockMvc 환경 구성
  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(boardController).build();
  }

  @Test
  @DisplayName("게시글 목록 조회 요청 시 뷰(board/list) 및 모델 데이터 반환 검증")
  void getBoardListTest() throws Exception {
    // given (테스트 준비: 서비스 반환 데이터 모의 설정)
    PageDto<PostDto> mockPage = new PageDto<>(List.of(), 1, 5, 0, 5);
    given(postService.searchPosts(null, null, 1, 5)).willReturn(mockPage);

    // when & then (GET /post/list 요청 시 200 OK 응답 및 뷰/Model 속성 검증)
    mockMvc.perform(get("/post/list"))
        .andExpect(status().isOk())
        .andExpect(view().name("board/list"))
        .andExpect(model().attributeExists("pageResponse", "posts"));
  }

  @Test
  @DisplayName("일반 게시글 상세 조회 요청 성공 검증")
  void getDetailPublicPostTest() throws Exception {
    // given (테스트 준비: 일반 공개 게시글 데이터 모의 설정)
    int postId = 1;
    PostDto mockPost = PostDto.builder()
        .id(postId)
        .title("공개 게시글")
        .content("내용")
        .author("홍길동")
        .secret(false)
        .memberId(10)
        .build();
    given(postService.getPost(postId)).willReturn(mockPost);

    // when & then (GET /post/detail?id=1 요청 시 board/detail 뷰 반환 검증)
    mockMvc.perform(get("/post/detail").param("id", String.valueOf(postId)))
        .andExpect(status().isOk())
        .andExpect(view().name("board/detail"))
        .andExpect(model().attributeExists("post"));
  }

  @Test
  @DisplayName("비밀글 조회 시 비로그인 또는 권한 없는 사용자의 목록 리다이렉트 검증")
  void getDetailSecretPostUnauthorizedTest() throws Exception {
    // given (테스트 준비: 비밀글 생성 및 작성자가 아닌 타인 세션 설정)
    int postId = 1;
    PostDto secretPost = PostDto.builder()
        .id(postId)
        .title("비밀글")
        .secret(true)
        .memberId(10) // 작성자 회원 ID: 10
        .build();
    given(postService.getPost(postId)).willReturn(secretPost);

    MockHttpSession session = new MockHttpSession();
    SessionMemberDto otherMember = SessionMemberDto.builder()
        .id(99)
        .username("otherUser")
        .email("other@example.com")
        .role("user")
        .build();
    session.setAttribute("loginMember", otherMember);

    // when & then (비밀글 접근 시 목록으로 리다이렉트 확인)
    mockMvc.perform(get("/post/detail").param("id", String.valueOf(postId)).session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/post/list"));
  }

  @Test
  @DisplayName("비로그인 사용자의 게시글 작성 요청 시 로그인 페이지 리다이렉트 검증")
  void writePostUnauthenticatedTest() throws Exception {
    // given & when & then (세션 없이 POST /post/write 요청 시 /member/login 리다이렉트 확인)
    mockMvc.perform(post("/post/write")
            .param("title", "테스트 제목")
            .param("content", "테스트 내용")
            .param("author", "작성자"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/member/login"));
  }

  @Test
  @DisplayName("로그인 사용자의 정상 게시글 작성 요청 성공 검증")
  void writePostAuthenticatedTest() throws Exception {
    // given (테스트 준비: 로그인 유저 세션 설정)
    MockHttpSession session = new MockHttpSession();
    SessionMemberDto loginMember = SessionMemberDto.builder()
        .id(1)
        .username("testUser")
        .email("user@example.com")
        .role("user")
        .build();
    session.setAttribute("loginMember", loginMember);

    // when & then (POST /post/write 성공 시 /post/list 리다이렉트 및 서비스 writePost 호출 확인)
    mockMvc.perform(post("/post/write")
            .session(session)
            .param("title", "테스트 제목")
            .param("content", "테스트 내용")
            .param("author", "작성자"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/post/list"));

    verify(postService).writePost(any(PostDto.class));
  }

  @Test
  @DisplayName("타인의 게시글 수정 시도 시 권한 거부 및 목록 리다이렉트 검증")
  void editPostUnauthorizedTest() throws Exception {
    // given (테스트 준비: 기존 게시글 작성자 ID 10, 접근 시도 유저 ID 99)
    int postId = 1;
    PostDto existingPost = PostDto.builder()
        .id(postId)
        .memberId(10)
        .build();
    given(postService.getPost(postId)).willReturn(existingPost);

    MockHttpSession session = new MockHttpSession();
    SessionMemberDto otherUser = SessionMemberDto.builder()
        .id(99)
        .username("otherUser")
        .email("other@example.com")
        .role("user")
        .build();
    session.setAttribute("loginMember", otherUser);

    // when & then (권한이 없는 게시글 수정 요청 시 목록으로 리다이렉트 확인)
    mockMvc.perform(post("/post/edit")
            .session(session)
            .param("id", String.valueOf(postId))
            .param("title", "수정 제목")
            .param("content", "수정 내용")
            .param("author", "작성자"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/post/list"));
  }
}
