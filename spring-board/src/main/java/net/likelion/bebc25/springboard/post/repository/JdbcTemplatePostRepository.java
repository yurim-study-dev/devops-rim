package net.likelion.bebc25.springboard.post.repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import net.likelion.bebc25.springboard.post.dto.PostDto;

/**
 * Spring JdbcTemplate 기반의 게시글 데이터 접근 레포지토리 구현체 클래스.
 * 데이터베이스(H2 DB)와 직접 연결하여 SQL 문을 실행하고 결과를 자바 객체(PostDto)로 변환해 주는 역할을 담당합니다.
 * 작성자 정보(author)는 post 테이블의 중복 저장을 방지하기 위해 member 테이블과의 JOIN을 통해 동적으로 가져옵니다.
 */
@Repository
public class JdbcTemplatePostRepository implements PostRepository {

  private final JdbcTemplate jdbcTemplate;

  /**
   * JdbcTemplatePostRepository 생성자.
   *
   * @param jdbcTemplate 데이터베이스 쿼리 실행용 Spring JdbcTemplate
   */
  public JdbcTemplatePostRepository(JdbcTemplate jdbcTemplate){
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * ResultSet을 PostDto 객체로 매핑하는 RowMapper 식별자.
   * DB 테이블의 한 줄(Row) 결과를 자바의 PostDto 객체 필드 하나하나에 대입해 주는 자동 매핑 변환기입니다.
   */
  private final RowMapper<PostDto> postRowMapper = (ResultSet rs, int rowNum) -> {
    return PostDto.builder()
        .id(rs.getInt("id"))
        .title(rs.getString("title"))
        .author(rs.getString("author")) // member 테이블 조인 결과(m.username)를 author에 대입
        .createdAt(rs.getObject("created_at", LocalDateTime.class))
        .content(rs.getString("content"))
        .secret(rs.getBoolean("secret"))
        .memberId(rs.getInt("member_id"))
        .originalFilename(rs.getString("original_filename"))
        .storeFilename(rs.getString("store_filename"))
        .contentType(rs.getString("content_type"))
        .build();
  };

  // 공통 조인 SELECT 쿼리: post 테이블과 member 테이블을 외래키(member_id = m.id)로 연결하여 작성자 아이디(m.username AS author)를 함께 가져옵니다.
  private static final String BASE_SELECT_SQL =
      "SELECT p.id, p.member_id, p.title, p.content, p.secret, p.created_at, p.original_filename, p.store_filename, p.content_type, m.username AS author " +
      "FROM post p LEFT JOIN member m ON p.member_id = m.id";

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PostDto> findAll() {
    return jdbcTemplate.query(BASE_SELECT_SQL + " ORDER BY p.id DESC", postRowMapper);
  }

  /**
   * {@inheritDoc}
   * 검색 조건(제목, 내용, 작성자 등)과 키워드가 입력되었을 때 동적으로 SQL WHERE 조건절을 추가하여 검색합니다.
   */
  @Override
  public List<PostDto> search(String type, String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return findAll();
    }
    String searchKeyword = "%" + keyword.trim() + "%";
    if ("title".equals(type)) {
      return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.title LIKE ? ORDER BY p.id DESC", postRowMapper, searchKeyword);
    } else if ("content".equals(type)) {
      return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.content LIKE ? ORDER BY p.id DESC", postRowMapper, searchKeyword);
    } else if ("author".equals(type)) {
      return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE m.username LIKE ? ORDER BY p.id DESC", postRowMapper, searchKeyword);
    } else {
      return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.title LIKE ? OR p.content LIKE ? ORDER BY p.id DESC", postRowMapper, searchKeyword, searchKeyword);
    }
  }

  /**
   * {@inheritDoc}
   * LIMIT ? OFFSET ? 구문을 활용해 원하는 개수만큼 잘라서 가져오는 데이터베이스 페이징 조회를 수행합니다.
   */
  @Override
  public List<PostDto> search(String type, String keyword, int offset, int limit) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return jdbcTemplate.query(BASE_SELECT_SQL + " ORDER BY p.id DESC LIMIT ? OFFSET ?", postRowMapper, limit, offset);
    }
    String searchKeyword = "%" + keyword.trim() + "%";
    if ("title".equals(type)) {
      return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.title LIKE ? ORDER BY p.id DESC LIMIT ? OFFSET ?", postRowMapper, searchKeyword, limit, offset);
    } else if ("content".equals(type)) {
      return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.content LIKE ? ORDER BY p.id DESC LIMIT ? OFFSET ?", postRowMapper, searchKeyword, limit, offset);
    } else if ("author".equals(type)) {
      return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE m.username LIKE ? ORDER BY p.id DESC LIMIT ? OFFSET ?", postRowMapper, searchKeyword, limit, offset);
    } else {
      return jdbcTemplate.query(BASE_SELECT_SQL + " WHERE p.title LIKE ? OR p.content LIKE ? ORDER BY p.id DESC LIMIT ? OFFSET ?", postRowMapper, searchKeyword, searchKeyword, limit, offset);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int count(String type, String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post", Integer.class);
      return count != null ? count : 0;
    }
    String searchKeyword = "%" + keyword.trim() + "%";
    if ("title".equals(type)) {
      Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post p WHERE p.title LIKE ?", Integer.class, searchKeyword);
      return count != null ? count : 0;
    } else if ("content".equals(type)) {
      Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post p WHERE p.content LIKE ?", Integer.class, searchKeyword);
      return count != null ? count : 0;
    } else if ("author".equals(type)) {
      Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post p JOIN member m ON p.member_id = m.id WHERE m.username LIKE ?", Integer.class, searchKeyword);
      return count != null ? count : 0;
    } else {
      Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post p WHERE p.title LIKE ? OR p.content LIKE ?", Integer.class, searchKeyword, searchKeyword);
      return count != null ? count : 0;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PostDto findById(int id) {
    return jdbcTemplate.queryForObject(BASE_SELECT_SQL + " WHERE p.id = ?", postRowMapper, id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void save(PostDto post) {
    jdbcTemplate.update("INSERT INTO post (title, content, secret, member_id, original_filename, store_filename, content_type) VALUES (?, ?, ?, ?, ?, ?, ?)"
        , post.getTitle()
        , post.getContent()
        , post.isSecret()
        , post.getMemberId()
        , post.getOriginalFilename()
        , post.getStoreFilename()
        , post.getContentType());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(PostDto post) {
    jdbcTemplate.update("UPDATE post SET title = ?, content = ?, secret = ?, original_filename = ?, store_filename = ?, content_type = ? WHERE id = ?"
        , post.getTitle()
        , post.getContent()
        , post.isSecret()
        , post.getOriginalFilename()
        , post.getStoreFilename()
        , post.getContentType()
        , post.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteById(int id) {
    jdbcTemplate.update("DELETE FROM post WHERE id = ?", id);
  }
}
