INSERT INTO member (username, password, email, role, created_at) VALUES ('admin', '1111', 'admin@example.com', 'admin', CURRENT_TIMESTAMP);
INSERT INTO member (username, password, email, role, created_at) VALUES ('haru', '1111', 'haru@example.com', 'user', CURRENT_TIMESTAMP);
INSERT INTO member (username, password, email, role, created_at) VALUES ('namu', '1111', 'namu@example.com', 'user', CURRENT_TIMESTAMP);

INSERT INTO post (title, content, secret, created_at, member_id) VALUES ('HTTP 비상태성(Stateless)과 쿠키·세션의 도입 배경', 'HTTP 프로토콜은 기본적으로 비상태성(Stateless) 특성을 가집니다.

클라이언트의 상태(로그인 정보, 장바구니 등)를 서버나 브라우저가 유지하기 위해 쿠키(Cookie)와 세션(Session) 개념이 도입되었습니다.
- 쿠키: 클라이언트(브라우저) 측 저장소에 보관되는 키-값 쌍의 데이터
- 세션: 서버 측 메모리에 안전하게 보관되는 사용자 상태 데이터', false, CURRENT_TIMESTAMP, 1);

INSERT INTO post (title, content, secret, created_at, member_id) VALUES ('클라이언트 측 상태 저장소: 쿠키(Cookie)의 동작 원리 및 속성', '쿠키는 서버가 HTTP 응답 헤더(Set-Cookie)로 클라이언트에 전달하면 브라우저에 저장됩니다.

이후 동일 도메인 요청 시 HTTP 요청 헤더(Cookie)에 자동으로 첨부되어 서버로 전송됩니다.
- HttpOnly: 자바스크립트(document.cookie) 접근 차단으로 XSS 방어
- Secure: HTTPS 암호화 채널에서만 전송
- SameSite: 외부 사이트에서의 크로스 사이트 요청 제어로 CSRF 방어', false, CURRENT_TIMESTAMP, 2);

INSERT INTO post (title, content, secret, created_at, member_id) VALUES ('서버 측 상태 관리: 서블릿 세션(HttpSession)의 동작 메커니즘', '서블릿 세션(HttpSession)은 민감한 사용자 정보(회원 ID, 권한 등)를 서버 측 메모리에 안전하게 관리합니다.

1. 클라이언트 첫 요청 시 서버에서 세션 객체 생성
2. 추정이 불가능한 고유 식별자(JSESSIONID)를 생성하여 쿠키로 클라이언트에 발급
3. 브라우저는 이후 요청 시 JSESSIONID 쿠키를 전달하여 서버에서 상태 식별', false, CURRENT_TIMESTAMP, 3);

INSERT INTO post (title, content, secret, created_at, member_id) VALUES ('쿠키와 세션의 보안 비교 및 실무 활용 요약 메모', '이 글은 쿠키와 세션의 보안 특성을 비교한 비밀 메모입니다.

- 쿠키: 브라우저에 노출되므로 탈취 및 위변조 위험이 존재함 (HttpOnly/Secure 필수)
- 세션: 서버 메모리를 사용하므로 보안성이 높으나 동접자 증가 시 메모리 부담 및 스케일 아웃 시 세션 동기화 필요
- 실무 결론: JWT 토큰 방식이나 Redis 기반 분산 세션 저장소를 고려하여 시스템 구조 설계', true, CURRENT_TIMESTAMP, 2);
