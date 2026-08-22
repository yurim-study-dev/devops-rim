package net.likelion.bebc25.springboard.post.repository;

import java.util.List;
import net.likelion.bebc25.springboard.post.dto.PostDto;

/**
 * 게시글 데이터 접근 처리를 담당하는 레포지토리 인터페이스.
 */
public interface PostRepository {

  /**
   * 전체 게시글 목록을 조회합니다.
   *
   * @return 전체 게시글 DTO 리스트
   */
  List<PostDto> findAll();

  /**
   * 검색 조건 및 키워드에 맞는 게시글 목록을 조회합니다.
   *
   * @param type 검색 유형 (title, content, author 등)
   * @param keyword 검색 키워드
   * @return 조건에 맞는 게시글 DTO 리스트
   */
  List<PostDto> search(String type, String keyword);

  /**
   * 검색 조건 및 키워드, 페이징 범위에 맞는 게시글 목록을 조회합니다.
   *
   * @param type 검색 유형
   * @param keyword 검색 키워드
   * @param offset 조회 시작 위치 (0부터 시작)
   * @param limit 조회할 레코드 수
   * @return 페이징 처리된 게시글 DTO 리스트
   */
  List<PostDto> search(String type, String keyword, int offset, int limit);

  /**
   * 검색 조건 및 키워드에 해당하는 게시글 총 개수를 조회합니다.
   *
   * @param type 검색 유형
   * @param keyword 검색 키워드
   * @return 총 게시글 수
   */
  int count(String type, String keyword);

  /**
   * 식별자(ID)로 단건 게시글을 조회합니다.
   *
   * @param id 게시글 식별자
   * @return 조회된 게시글 DTO
   */
  PostDto findById(int id);

  /**
   * 신규 게시글을 데이터베이스에 저장합니다.
   *
   * @param post 저장할 게시글 DTO
   */
  void save(PostDto post);

  /**
   * 기존 게시글 정보를 수정합니다.
   *
   * @param post 수정할 정보를 담은 게시글 DTO
   */
  void update(PostDto post);

  /**
   * 식별자(ID)에 해당하는 게시글을 삭제합니다.
   *
   * @param id 삭제할 게시글 식별자
   */
  void deleteById(int id);
}
