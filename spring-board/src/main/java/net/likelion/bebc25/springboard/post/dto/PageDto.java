package net.likelion.bebc25.springboard.post.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PageDto<T> {
  private final List<T> content;
  private final int page;          // 현재 페이지 번호 (1부터 시작)
  private final int size;          // 한 페이지 당 게시글 수
  private final long totalElements; // 전체 게시글 수
  private final int totalPages;    // 전체 페이지 수
  private final int startPage;     // 페이지 바 시작 번호
  private final int endPage;       // 페이지 바 끝 번호
  private final boolean hasPrev;   // 이전 페이지 존재 여부
  private final boolean hasNext;   // 다음 페이지 존재 여부

  public PageDto(List<T> content, int page, int size, long totalElements, int pageBlockSize) {
    this.content = content;
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = (int) Math.ceil((double) totalElements / size);

    int currentBlock = (int) Math.ceil((double) page / pageBlockSize);
    int calcEndPage = currentBlock * pageBlockSize;
    this.startPage = Math.max(1, calcEndPage - pageBlockSize + 1);
    this.endPage = Math.min(this.totalPages == 0 ? 1 : this.totalPages, calcEndPage);

    this.hasPrev = this.page > 1;
    this.hasNext = this.page < this.totalPages;
  }
}
