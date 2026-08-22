package net.likelion.bebc25.springboard.post.service;

import java.util.List;
import net.likelion.bebc25.springboard.post.dto.PageDto;
import net.likelion.bebc25.springboard.post.dto.PostDto;
import net.likelion.bebc25.springboard.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PostService 인터페이스의 비즈니스 로직 구현 클래스.
 */
@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;

  /**
   * PostServiceImpl 생성자.
   *
   * @param postRepository 스프링 빈으로 등록된 PostRepository 구현체
   */
  public PostServiceImpl(@Qualifier("jdbcTemplatePostRepository") PostRepository postRepository){
    this.postRepository = postRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PostDto> getPosts() {
    return postRepository.findAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PostDto> searchPosts(String type, String keyword) {
    return postRepository.search(type, keyword);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PageDto<PostDto> searchPosts(String type, String keyword, int page, int size) {
    int validPage = page < 1 ? 1 : page;
    int validSize = size < 1 ? 10 : size;
    int offset = (validPage - 1) * validSize;
    int totalElements = postRepository.count(type, keyword);
    List<PostDto> posts = postRepository.search(type, keyword, offset, validSize);
    return new PageDto<>(posts, validPage, validSize, totalElements, 5);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PostDto getPost(int id) {
    return postRepository.findById(id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void writePost(PostDto post) {
    postRepository.save(post);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void editPost(PostDto post) {
    postRepository.update(post);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public void removePost(int id) {
    postRepository.deleteById(id);
  }
}
