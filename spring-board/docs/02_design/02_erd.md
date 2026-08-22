# 1. 데이터베이스 ERD 및 테이블 정의서

## 목차
- [1. 데이터베이스 ERD 및 테이블 정의서](#1-데이터베이스-erd-및-테이블-정의서)
- [1.1 Mermaid 기반 ERD 다이어그램](#11-mermaid-기반-erd-다이어그램)
- [1.2 테이블 상세 명세서](#12-테이블-상세-명세서)
- [1.3 테이블 생성 DDL 스크립트](#13-테이블-생성-ddl-스크립트)

---

## 1.1 Mermaid 기반 ERD 다이어그램

```mermaid
erDiagram
    MEMBER ||--o{ POST : writes
    MEMBER ||--o{ REPLY : writes
    POST ||--o{ REPLY : contains

    MEMBER {
        int id PK
        varchar_100 email
        varchar_255 password
        varchar_50 name
        varchar_20 phone
        int recommender_id FK
        datetime created_at
    }

    POST {
        int id PK
        int member_id FK
        varchar_50 writer_name
        varchar_255 password
        varchar_200 title
        text content
        int view_count
        datetime created_at
    }

    REPLY {
        int id PK
        int post_id FK
        int member_id FK
        text content
        datetime created_at
    }
```

---

## 1.2 테이블 상세 명세서

### 1.2.1 member (회원 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (회원 고유 식별자)
- email: VARCHAR(100), NOT NULL (이메일 아이디)
- password: VARCHAR(255), NOT NULL (비밀번호)
- name: VARCHAR(50), NOT NULL (회원 이름)
- phone: VARCHAR(20), NULL (전화번호)
- recommender_id: INT, FOREIGN KEY (추천인 식별자)
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP (가입 일시)

### 1.2.2 post (게시글 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (게시글 고유 식별자)
- member_id: INT, FOREIGN KEY, NULL (작성자 회원 식별자, 익명 글인 경우 NULL)
- writer_name: VARCHAR(50), NULL (익명 작성자 이름)
- password: VARCHAR(255), NULL (익명 게시글 삭제용 비밀번호)
- title: VARCHAR(200), NOT NULL (게시글 제목)
- content: TEXT, NOT NULL (게시글 본문)
- view_count: INT, DEFAULT 0 (조회수)
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP (작성 일시)

### 1.2.3 reply (댓글 테이블)
- id: INT, PRIMARY KEY, AUTO_INCREMENT (댓글 고유 식별자)
- post_id: INT, FOREIGN KEY (대상 게시글 식별자)
- member_id: INT, FOREIGN KEY (댓글 작성자 식별자)
- content: TEXT, NOT NULL (댓글 내용)
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP (작성 일시)

---

## 1.3 테이블 생성 DDL 스크립트

```sql
CREATE TABLE member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    recommender_id INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_recommender FOREIGN KEY (recommender_id) REFERENCES member(id) ON DELETE SET NULL
);

CREATE TABLE post (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NULL,
    writer_name VARCHAR(50) NULL,
    password VARCHAR(255) NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INT NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_post_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL
);

CREATE TABLE reply (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    member_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reply_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);
```
