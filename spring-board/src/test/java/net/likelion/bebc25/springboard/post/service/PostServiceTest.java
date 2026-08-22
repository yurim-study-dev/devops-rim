package net.likelion.bebc25.springboard.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.likelion.bebc25.springboard.post.dto.PageDto;
import net.likelion.bebc25.springboard.post.dto.PostDto;
import net.likelion.bebc25.springboard.post.repository.PostRepository;

// Mockito 전용 JUnit5 확장 어노테이션 (스프링 컨텍스트를 띄우지 않고 빠른 단위 테스트 수행)
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  // PostRepository 가짜 객체(Mock) 생성
  @Mock
  private PostRepository postRepository;

  // 생성된 Mock 객체들을 테스트 대상 서비스 구현체(PostServiceImpl)에 자동 주입
  @InjectMocks
  private PostServiceImpl postService;

  @Test
  @DisplayName("게시글 단건 조회 성공 테스트")
  void getPostSuccess() {
    // given (테스트 준비: 모의 데이터 객체 생성 및 Mock 실행 동작 정의)
    int postId = 1;
    PostDto mockPost = PostDto.builder()
        .id(postId)
        .title("테스트 제목")
        .content("테스트 내용")
        .author("홍길동")
        .build();

    // postRepository.findById(1)이 호출되면 mockPost 객체를 반환하도록 행위 정의
    given(postRepository.findById(postId)).willReturn(mockPost);

    // when (테스트 실행: 실제 서비스 메서드 호출)
    PostDto result = postService.getPost(postId);

    // then (결과 검증: 반환된 결과 확인 및 Mock 메서드 호출 여부 검증)
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(postId);
    assertThat(result.getTitle()).isEqualTo("테스트 제목");

    // postRepository의 findById 메서드가 정상 호출되었는지 검증
    verify(postRepository).findById(postId);
  }

  @Test
  @DisplayName("게시글 페이징 검색 성공 및 계산 검증 테스트")
  void searchPostsWithPaginationSuccess() {
    // given (테스트 준비: 검색 조건, 페이징 조건 및 반환될 데이터 설정)
    String type = "title";
    String keyword = "스프링";
    int page = 1;
    int size = 5;
    List<PostDto> mockPosts = List.of(
        PostDto.builder().id(1).title("스프링 제목 1").build(),
        PostDto.builder().id(2).title("스프링 제목 2").build()
    );

    // 전체 개수가 12개, 페이징 데이터가 2개 반환되도록 Mock 행위 설정
    given(postRepository.count(type, keyword)).willReturn(12);
    given(postRepository.search(type, keyword, 0, 5)).willReturn(mockPosts);

    // when (테스트 실행: 서비스의 페이징 검색 메서드 실행)
    PageDto<PostDto> result = postService.searchPosts(type, keyword, page, size);

    // then (결과 검증: PageDto 내 페이징 로직 계산 검증)
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(12);
    assertThat(result.getTotalPages()).isEqualTo(3); // 총 12개 항목 / 페이지당 5개 = 총 3페이지 계산 검증

    // 레포지토리 메서드들의 호출 검증
    verify(postRepository).count(type, keyword);
    verify(postRepository).search(type, keyword, 0, 5);
  }

  @Test
  @DisplayName("게시글 등록 성공 테스트")
  void writePostSuccess() {
    // given (테스트 준비: 등록할 신규 게시글 객체 생성)
    PostDto post = PostDto.builder()
        .title("새 게시글")
        .content("새 내용")
        .author("작성자")
        .memberId(1)
        .build();

    // when (테스트 실행: 서비스의 게시글 등록 호출)
    postService.writePost(post);

    // then (결과 검증: postRepository.save(post)가 정확히 호출되었는지 확인)
    verify(postRepository).save(post);
  }

  @Test
  @DisplayName("게시글 삭제 성공 테스트")
  void removePostSuccess() {
    // given (테스트 준비: 삭제할 게시글 ID)
    int postId = 1;

    // when (테스트 실행: 서비스의 게시글 삭제 호출)
    postService.removePost(postId);

    // then (결과 검증: postRepository.deleteById(postId)가 정상적으로 호출되었는지 확인)
    verify(postRepository).deleteById(postId);
  }
}
