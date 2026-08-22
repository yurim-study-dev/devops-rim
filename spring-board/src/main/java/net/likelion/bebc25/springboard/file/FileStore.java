package net.likelion.bebc25.springboard.file;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 업로드 및 파일 저장소 관리를 위한 인터페이스.
 */
public interface FileStore {

  /**
   * 저장 경로를 반환합니다.
   *
   * @param filename 파일명
   * @return 전체 파일 경로 문자열
   */
  String getFullPath(String filename);

  /**
   * 업로드된 파일을 저장소에 저장하고 저장된 파일명을 반환합니다.
   *
   * @param multipartFile 업로드된 파일
   * @return 저장된 유일한 파일명 (파일이 없을 경우 null)
   * @throws IOException 파일 저장 실패 시
   */
  String storeFile(MultipartFile multipartFile) throws IOException;

  /**
   * 저장된 파일명을 기반으로 다운로드용 Resource를 생성합니다.
   *
   * @param storeFilename 저장된 파일명
   * @return Resource 객체
   * @throws IOException Resource 생성 실패 시
   */
  Resource getFileResource(String storeFilename) throws IOException;

  /**
   * 파일명을 기반으로 이미지 파일 여부를 판별합니다.
   *
   * @param filename 판별할 파일명
   * @return 이미지 파일 여부 (true/false)
   */
  boolean isImage(String filename);
}
