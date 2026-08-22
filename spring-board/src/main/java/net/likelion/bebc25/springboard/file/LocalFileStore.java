package net.likelion.bebc25.springboard.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 로컬 서버 디렉터리 기반의 파일 저장소 구현체 클래스.
 * 파일 업로드/다운로드 처리 시 파일 저장 위치 및 유일한 파일명 생성을 관리합니다.
 */
@Component
public class LocalFileStore implements FileStore {

  // application.properties 설정 파일의 file.dir 프로퍼티 값을 주입받음 (기본값: ./uploads/)
  @Value("${file.dir}")
  private String fileDir;

  /**
   * 저장소의 절대 경로 Path를 반환합니다.
   * 내장 톰캣 구동 시 상대 경로가 임시 디렉터리로 상이하게 해석되는 현상을 방지합니다.
   *
   * @return 절대 경로 Path 객체
   */
  private Path getUploadPath() {
    return Paths.get(fileDir).toAbsolutePath();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getFullPath(String filename) {
    return getUploadPath().resolve(filename).toString();
  }

  /**
   * {@inheritDoc}
   * 업로드된 파일(MultipartFile)을 검사하여 중복 없는 유일한 파일명(UUID)으로 저장합니다.
   */
  @Override
  public String storeFile(MultipartFile multipartFile) throws IOException {
    // 업로드된 파일 데이터가 없거나 비어있는 경우 진행하지 않음
    if (multipartFile == null || multipartFile.isEmpty()) {
      return null;
    }

    // 사용자가 업로드한 원본 파일명 (예: photo.png)
    String originalFilename = multipartFile.getOriginalFilename();
    // 서버에 저장할 겹치지 않는 유일한 파일명 생성 (예: 550e8400-e29b-41d4-a716-446655440000.png)
    String storeFileName = createStoreFileName(originalFilename);

    // 지정된 업로드 저장소 폴더가 없으면 자동으로 디렉터리 생성
    Path uploadPath = getUploadPath();
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    // 실제 지정 경로로 업로드 바이너리 파일 저장
    File destination = uploadPath.resolve(storeFileName).toFile();
    multipartFile.transferTo(destination);
    return storeFileName;
  }

  /**
   * {@inheritDoc}
   * 저장된 파일명을 기반으로 스프링이 바이너리를 읽을 수 있는 Resource 객체로 감싸서 반환합니다.
   */
  @Override
  public Resource getFileResource(String storeFilename) throws IOException {
    return new UrlResource("file:" + getFullPath(storeFilename));
  }

  /**
   * {@inheritDoc}
   * MIME 타입(예: image/png) 또는 파일 확장자(.jpg, .png 등)를 검사하여 이미지 파일 여부를 판별합니다.
   */
  @Override
  public boolean isImage(String filename) {
    if (filename == null || filename.isEmpty()) {
      return false;
    }
    try {
      // 자바 표준 시스템을 통한 파일 MIME 타입 확인
      String contentType = Files.probeContentType(Paths.get(filename));
      if (contentType != null && contentType.startsWith("image/")) {
        return true;
      }
    } catch (IOException ignored) {
    }
    // 예외 처리 상황 시 파일 확장자 기반으로 백업 검사
    String lower = filename.toLowerCase();
    return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
        || lower.endsWith(".gif") || lower.endsWith(".webp") || lower.endsWith(".svg");
  }

  /**
   * UUID를 활용하여 중복되지 않는 서버 저장용 파일명을 생성합니다.
   * 동일한 이름의 파일(예: test.jpg)을 여러 사용자가 업로드할 때 발생하는 덮어쓰기 사고를 방지합니다.
   *
   * @param originalFilename 원본 파일명
   * @return UUID가 조합된 저장 파일명
   */
  private String createStoreFileName(String originalFilename) {
    String ext = extractExt(originalFilename);
    String uuid = UUID.randomUUID().toString(); // 128비트 임의 난수 식별자 생성
    return uuid + "." + ext;
  }

  /**
   * 파일명에서 확장자를 추출합니다.
   * 원본 파일명에서 마지막 점(.) 위치의 다음 문자를 추출합니다.
   *
   * @param originalFilename 원본 파일명
   * @return 파일 확장자
   */
  private String extractExt(String originalFilename) {
    if (originalFilename == null) {
      return "";
    }
    int pos = originalFilename.lastIndexOf(".");
    return pos >= 0 ? originalFilename.substring(pos + 1) : "";
  }
}
