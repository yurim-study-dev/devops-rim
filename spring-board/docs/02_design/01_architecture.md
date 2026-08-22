# 1. 시스템 아키텍처 구성도

## 목차
- [1. 시스템 아키텍처 구성도](#1-시스템-아키텍처-구성도)
- [1.1 계층별 시스템 아키텍처](#11-계층별-시스템-아키텍처)
- [1.2 소프트웨어 스택 상세](#12-소프트웨어-스택-상세)

---

## 1.1 계층별 시스템 아키텍처

### 1.1.1 클라이언트 및 뷰 계층 (Presentation Layer)
- 브라우저 기반 HTTP/HTTPS 통신
- HTML5, CSS3, JavaScript
- Thymeleaf Template Engine (Server-Side Rendering)

### 1.1.2 애플리케이션 계층 (Application Layer)
- Spring Boot Web (Spring MVC)
- HttpSession 세션 기반 로그인 인증 및 HandlerInterceptor 인증 체크
- Controller / Service / Repository 계층 분리 3-Tier Layered Architecture

### 1.1.3 데이터 계층 (Data Access & Storage Layer)
- Spring JDBC (JdbcTemplate 기반 데이터 접근 및 RowMapper 데이터 매핑)
- MySQL Database

---

## 1.2 소프트웨어 스택 상세

```text
[Browser Client] 
       │ (HTTP Request / Response - Cookie JSESSIONID)
       ▼
[Spring Boot Server]
  ├── Controller (Spring MVC / HandlerInterceptor / Thymeleaf View Resolver)
  ├── Service (Business Logic / Transaction Management / HttpSession)
  └── Repository (Spring JDBC - JdbcTemplate / RowMapper)
       │ (JDBC Driver / SQL Query)
       ▼
[MySQL Database]
```
