package net.likelion.bebc25.springboard.post.service;

import java.util.List;
import net.likelion.bebc25.springboard.post.dto.PageDto;
import net.likelion.bebc25.springboard.post.dto.PostDto;

/**
 * 게시글 비즈니스 로직을 제공하는 서비스 인터페이스.
 */
public interface PostService {

  /**
   * 전체 게시글 목록을 조회합니다.
   *
   * @return 전체 게시글 DTO 리스트
   */
  List<PostDto> getPosts();

  /**
   * 검색 조건 및 키워드에 따라 게시글 목록을 조회합니다.
   *
   * @param type 검색 유형
   * @param keyword 검색 키워드
   * @return 검색된 게시글 DTO 리스트
   */
  List<PostDto> searchPosts(String type, String keyword);

  /**
   * 검색 조건 및 키워드, 페이지 번호와 페이지 크기를 기반으로 페이징된 게시글 정보를 조회합니다.
   *
   * @param type 검색 유형
   * @param keyword 검색 키워드
   * @param page 조회할 페이지 번호
   * @param size 페이지 당 게시글 수
   * @return 페이징 계산 결과가 담긴 PageDto
   */
  PageDto<PostDto> searchPosts(String type, String keyword, int page, int size);

  /**
   * 식별자(ID)로 특정 게시글의 상세 정보를 조회합니다.
   *
   * @param id 게시글 식별자
   * @return 게시글 DTO
   */
  PostDto getPost(int id);

  /**
   * 신규 게시글을 등록합니다.
   *
   * @param post 등록할 게시글 DTO
   */
  void writePost(PostDto post);

  /**
   * 기존 게시글 정보를 수정합니다.
   *
   * @param post 수정할 정보를 담은 게시글 DTO
   */
  void editPost(PostDto post);

  /**
   * 식별자(ID)에 해당하는 게시글을 삭제합니다.
   *
   * @param id 삭제할 게시글 식별자
   */
  void removePost(int id);
}
