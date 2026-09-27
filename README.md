# Member Board API

Spring Boot와 Spring Security로 구현한 회원·게시글·댓글 REST API입니다.

## 실행 방법

### Docker Compose

Docker와 Docker Compose만 설치되어 있으면 다음 명령 하나로 파일형 H2 DB와 API 서버를 실행할 수 있습니다.

```bash
docker compose up --build
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- 종료: `docker compose down`
- DB 데이터까지 삭제: `docker compose down -v`

### 로컬 실행

JDK 21과 MySQL 8이 필요합니다.

```bash
export DB_URL='jdbc:mysql://localhost:3306/memberboard?serverTimezone=Asia/Seoul&characterEncoding=UTF-8'
export DB_USERNAME='memberboard'
export DB_PASSWORD='memberboard'
export JWT_SECRET_KEY='32자 이상의 안전한 비밀 키를 입력합니다'
./gradlew bootRun
```

테스트는 별도의 H2 DB를 사용합니다.

```bash
./gradlew test
```

## 인증

로그인 성공 시 JWT Access Token을 반환합니다. 인증이 필요한 요청에는 다음 헤더를 전달합니다.

```http
Authorization: Bearer {accessToken}
```

서버가 세션 상태를 저장하지 않아 REST API 서버를 단순하게 확장할 수 있고, 클라이언트가 매 요청의 인증 정보를 명시할 수 있어 JWT를 선택했습니다. 현재 과제 범위에서는 Access Token만 사용합니다.

## API 명세

| 기능 | 메서드 | 주소 | 인증 | 성공 상태 |
| --- | --- | --- | --- | --- |
| 회원가입 | `POST` | `/api/v1/members/signup` | 불필요 | `201` |
| 로그인 | `POST` | `/api/v1/members/login` | 불필요 | `200` |
| 게시글 작성 | `POST` | `/api/v1/posts` | 필요 | `201` |
| 게시글 목록 | `GET` | `/api/v1/posts?page=0&size=10` | 불필요 | `200` |
| 게시글 상세 | `GET` | `/api/v1/posts/{postId}` | 불필요 | `200` |
| 게시글 수정 | `PUT` | `/api/v1/posts/{postId}` | 작성자 | `200` |
| 게시글 삭제 | `DELETE` | `/api/v1/posts/{postId}` | 작성자 | `204` |
| 댓글 작성 | `POST` | `/api/v1/posts/{postId}/comments` | 필요 | `201` |
| 댓글 목록 | `GET` | `/api/v1/posts/{postId}/comments` | 불필요 | `200` |
| 댓글 수정 | `PUT` | `/api/v1/comments/{commentId}` | 작성자 | `200` |
| 댓글 삭제 | `DELETE` | `/api/v1/comments/{commentId}` | 작성자 | `204` |

### 회원가입

```json
{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "사용자"
}
```

```json
{
  "memberId": 1,
  "email": "user@example.com",
  "nickname": "사용자",
  "createdAt": "2026-09-28T01:00:00"
}
```

이메일 형식, 비밀번호 8자 이상, 닉네임 20자 이하를 검증합니다. 중복 이메일은 `409 Conflict`입니다.

### 로그인

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

```json
{
  "memberId": 1,
  "accessToken": "eyJ..."
}
```

이메일 또는 비밀번호가 틀리면 `401 Unauthorized`입니다.

### 게시글 작성·수정

```json
{
  "title": "첫 게시글",
  "content": "게시글 본문"
}
```

```json
{
  "postId": 1,
  "title": "첫 게시글",
  "content": "게시글 본문",
  "authorId": 1,
  "authorNickname": "사용자",
  "createdAt": "2026-09-28T01:01:00",
  "updatedAt": "2026-09-28T01:01:00"
}
```

제목은 필수이며 최대 100자이고, 본문은 필수입니다.

### 게시글 목록

`page`는 0부터 시작하며 `size`는 1~100입니다. 생성 시각 내림차순으로 조회합니다.

```json
{
  "posts": [
    {
      "postId": 1,
      "title": "첫 게시글",
      "authorNickname": "사용자",
      "commentCount": 1,
      "createdAt": "2026-09-28T01:01:00",
      "updatedAt": "2026-09-28T01:01:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### 댓글 작성·수정

```json
{
  "content": "첫 댓글"
}
```

```json
{
  "commentId": 1,
  "postId": 1,
  "content": "첫 댓글",
  "authorId": 1,
  "authorNickname": "사용자",
  "createdAt": "2026-09-28T01:02:00",
  "updatedAt": "2026-09-28T01:02:00"
}
```

댓글 목록 응답은 다음 형태입니다.

```json
{
  "comments": [
    {
      "commentId": 1,
      "postId": 1,
      "content": "첫 댓글",
      "authorId": 1,
      "authorNickname": "사용자",
      "createdAt": "2026-09-28T01:02:00",
      "updatedAt": "2026-09-28T01:02:00"
    }
  ]
}
```

## 오류 응답

모든 애플리케이션 및 Security 오류는 같은 형태로 반환합니다.

```json
{
  "status": 403,
  "message": "게시글 작성자만 수정하거나 삭제할 수 있습니다."
}
```

| 상황 | 상태 코드 |
| --- | --- |
| 입력 형식 또는 검증 실패 | `400 Bad Request` |
| 로그인 실패 또는 인증 정보 없음 | `401 Unauthorized` |
| 다른 작성자의 글·댓글 변경 | `403 Forbidden` |
| 회원·게시글·댓글 없음 | `404 Not Found` |
| 중복 이메일 | `409 Conflict` |

## 설계 설명

- `Member`와 `Post`, `Member`와 `Comment`, `Post`와 `Comment`는 각각 일대다 관계입니다. 연관관계의 다대일 쪽은 지연 로딩합니다.
- Docker 환경은 별도 DB 준비 없이 실행되도록 파일형 H2를 사용하고, 로컬 개발 환경에서는 MySQL을 사용합니다.
- 게시글 목록은 작성자 닉네임과 댓글 수를 JPQL 집계 쿼리 한 번으로 조회합니다. 게시글별 추가 조회가 발생하지 않아 N+1 문제가 없습니다.
- 댓글 목록은 `@EntityGraph`로 작성자를 함께 조회합니다.
- 게시글 삭제 시 `cascade = ALL`, `orphanRemoval = true`로 댓글을 함께 삭제합니다.
- 리소스 생성은 `POST`, 전체 수정은 `PUT`, 조회는 `GET`, 삭제는 `DELETE`를 사용했습니다.
- 엔티티는 요청·응답에 직접 사용하지 않고 DTO로 변환합니다.

## 실행 결과

아래 명령은 회원가입 → 로그인 → 게시글 작성 → 댓글 작성 → 목록 조회 순서입니다. `jq`가 설치되어 있다고 가정합니다.

```bash
curl -i -X POST http://localhost:8080/api/v1/members/signup \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com","password":"password123","nickname":"사용자"}'
```

```text
HTTP/1.1 201
{"memberId":1,"email":"user@example.com","nickname":"사용자","createdAt":"2026-09-27T16:02:14.486401544"}
```

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/members/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"user@example.com","password":"password123"}' | jq -r '.accessToken')
```

```text
HTTP/1.1 200
{"memberId":1,"accessToken":"eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzkwNTI0OTM0LCJleHAiOjE3OTA1Mjg1MzR9.dmmUKix8VgdNfosznLOFPYAXeX-6GdIXL5wlAJY8AEwzA9VZzBMYo_9D2H0M53sZ"}
```

```bash
curl -i -X POST http://localhost:8080/api/v1/posts \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"title":"첫 게시글","content":"게시글 본문"}'
```

```text
HTTP/1.1 201
{"postId":1,"title":"첫 게시글","content":"게시글 본문","authorId":1,"authorNickname":"사용자","createdAt":"2026-09-27T16:02:14.776024378","updatedAt":"2026-09-27T16:02:14.776024378"}
```

```bash
curl -i -X POST http://localhost:8080/api/v1/posts/1/comments \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"content":"첫 댓글"}'
```

```text
HTTP/1.1 201
{"commentId":1,"postId":1,"content":"첫 댓글","authorId":1,"authorNickname":"사용자","createdAt":"2026-09-27T16:02:14.804742003","updatedAt":"2026-09-27T16:02:14.804742003"}
```

```bash
curl -i 'http://localhost:8080/api/v1/posts?page=0&size=10'
```

```text
HTTP/1.1 200
{"posts":[{"postId":1,"title":"첫 게시글","authorNickname":"사용자","commentCount":1,"createdAt":"2026-09-27T16:02:14.776024","updatedAt":"2026-09-27T16:02:14.776024"}],"page":0,"size":10,"totalElements":1,"totalPages":1}
```

인증 없이 글을 작성하면 `401`입니다.

```bash
curl -i -X POST http://localhost:8080/api/v1/posts \
  -H 'Content-Type: application/json' \
  -d '{"title":"제목","content":"본문"}'
```

```text
HTTP/1.1 401
{"status":401,"message":"인증이 필요합니다."}
```

다른 회원의 토큰으로 글을 수정하면 `403`입니다.

```bash
curl -s -X POST http://localhost:8080/api/v1/members/signup \
  -H 'Content-Type: application/json' \
  -d '{"email":"other@example.com","password":"password123","nickname":"다른사용자"}' > /dev/null

OTHER_TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/members/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"other@example.com","password":"password123"}' | jq -r '.accessToken')
```

```bash
curl -i -X PUT http://localhost:8080/api/v1/posts/1 \
  -H "Authorization: Bearer $OTHER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"title":"수정","content":"수정 본문"}'
```

```text
HTTP/1.1 403
{"status":403,"message":"게시글 작성자만 수정하거나 삭제할 수 있습니다."}
```
