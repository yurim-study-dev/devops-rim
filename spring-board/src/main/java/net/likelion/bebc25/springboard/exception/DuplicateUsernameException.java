package net.likelion.bebc25.springboard.exception;

/**
 * 회원가입 시 아이디 중복 발생 상황을 처리하기 위한 사용자 정의 비즈니스 예외 클래스
 */
public class DuplicateUsernameException extends RuntimeException {

    /**
     * 예외 메시지를 전달받는 생성자
     *
     * @param message 예외 상세 메시지
     */
    public DuplicateUsernameException(String message) {
        super(message);
    }

    /**
     * 예외 메시지와 원인 예외(Cause)를 함께 전달받는 생성자
     *
     * @param message 예외 상세 메시지
     * @param cause 원인이 되는 상위 예외
     */
    public DuplicateUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
