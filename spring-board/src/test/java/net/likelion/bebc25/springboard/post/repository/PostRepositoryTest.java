package net.likelion.bebc25.springboard.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import net.likelion.bebc25.springboard.post.dto.PostDto;

// 스프링 통합 테스트 어노테이션 (스프링 컨텍스트 및 테스트용 인메모리 H2 DB 로드)
@SpringBootTest
// 테스트 실행 후 변경된 데이터베이스 변경 사항을 자동으로 롤백하여 테스트 독립성 보장
@Transactional
class PostRepositoryTest {

  // 테스트 대상 레포지토리 빈 주입
  @Autowired
  private PostRepository postRepository;

  @Test
  @DisplayName("제목으로 게시글 검색 테스트")
  void searchByTitle() {
    // given & when (테스트 실행: 'title' 조건으로 '세션' 검색어 조회)
    List<PostDto> posts = postRepository.search("title", "세션");

    // then (결과 검증: 검색 결과 목록이 비어있지 않고, 모든 게시글 제목에 '세션'이 포함되어 있는지 확인)
    assertThat(posts).isNotEmpty();
    assertThat(posts).allMatch(post -> post.getTitle().contains("세션"));
  }

  @Test
  @DisplayName("내용으로 게시글 검색 테스트")
  void searchByContent() {
    // given & when (테스트 실행: 'content' 조건으로 '내용' 검색어 조회)
    List<PostDto> posts = postRepository.search("content", "내용");

    // then (결과 검증: 반환 객체가 null이 아닌지 검증)
    assertThat(posts).isNotNull();
  }

  @Test
  @DisplayName("작성자로 게시글 검색 테스트")
  void searchByAuthor() {
    // given & when (테스트 실행: 'author' 조건으로 'haru' 검색어 조회)
    List<PostDto> posts = postRepository.search("author", "haru");

    // then (결과 검증: 반환 객체가 null이 아닌지 검증)
    assertThat(posts).isNotNull();
  }

  @Test
  @DisplayName("제목+내용으로 게시글 검색 테스트")
  void searchByTitleContent() {
    // given & when (테스트 실행: 'titleContent' 조건으로 '게시글' 검색어 조회)
    List<PostDto> posts = postRepository.search("titleContent", "게시글");

    // then (결과 검증: 반환 객체가 null이 아닌지 검증)
    assertThat(posts).isNotNull();
  }

  @Test
  @DisplayName("검색어가 없을 경우 전체 목록 반환 테스트")
  void searchWithEmptyKeyword() {
    // given & when (테스트 실행: 전체 조회 결과와 빈 검색어로 조회한 결과 비교)
    List<PostDto> allPosts = postRepository.findAll();
    List<PostDto> searchPosts = postRepository.search("title", "");

    // then (결과 검증: 검색어가 없을 때 전체 개수와 동일한지 검증)
    assertThat(searchPosts.size()).isEqualTo(allPosts.size());
  }

  @Test
  @DisplayName("페이징 조회 테스트")
  void searchWithPagination() {
    // given & when (테스트 실행: offset=0, limit=2로 페이징 조회)
    List<PostDto> pagePosts = postRepository.search(null, null, 0, 2);

    // then (결과 검증: 반환된 게시글 수가 요청한 limit(2개) 이하인지 검증)
    assertThat(pagePosts).isNotNull();
    assertThat(pagePosts.size()).isLessThanOrEqualTo(2);
  }

  @Test
  @DisplayName("게시글 개수 조회 테스트")
  void countTest() {
    // given & when (테스트 실행: 전체 게시글 개수 조회)
    int totalCount = postRepository.count(null, null);

    // then (결과 검증: 게시글 개수가 0개 이상인지 검증)
    assertThat(totalCount).isGreaterThanOrEqualTo(0);
  }
}
