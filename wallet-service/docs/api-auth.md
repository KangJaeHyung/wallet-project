# Wallet Service 인증 API 문서

## 개요

현재 `wallet-service`에서 구현되어 있는 인증 관련 API 문서입니다.

- 기본 서버 주소: `http://localhost:8210`
- 공통 경로: `/api/auth`
- 응답 형식: `ResponseDto<T>`

## 공통 응답 형식

성공 응답 예시

```json
{
  "message": "요청이 정상 처리되었습니다.",
  "data": {},
  "statusCode": 200,
  "errorMessage": null,
  "errorCode": null
}
```

실패 응답 예시

```json
{
  "message": "이미 가입된 이메일입니다.",
  "data": null,
  "statusCode": 409,
  "errorMessage": "이미 가입된 이메일입니다.",
  "errorCode": "AUTH_001"
}
```

## 1. 회원가입

- Method: `POST`
- Path: `/api/auth/signup`
- 인증 필요 여부: `No`

### Request Body

```json
{
  "email": "user@example.com",
  "password": "Abcd1234!",
  "name": "홍길동",
  "role": "USER"
}
```

### 요청 필드

- `email`: 필수, 이메일 형식
- `password`: 필수, 8자 이상이며 영문 대문자/소문자, 숫자, 특수문자 포함
- `name`: 필수
- `role`: 선택, 미입력 시 기본값 `USER`

### Success Response

```json
{
  "message": "회원가입이 완료되었습니다.",
  "data": null,
  "statusCode": 201,
  "errorMessage": null,
  "errorCode": null
}
```

## 2. 로그인

- Method: `POST`
- Path: `/api/auth/login`
- 인증 필요 여부: `No`

### Request Body

```json
{
  "email": "user@example.com",
  "password": "Abcd1234!",
  "clientType": "WEB",
  "deviceId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 요청 필드

- `email`: 필수, 이메일 형식
- `password`: 필수
- `clientType`: 필수, `WEB`, `AOS`, `IOS` 중 하나
- `deviceId`: 필수, 클라이언트가 보관하는 고유 식별자

### Success Response

```json
{
  "message": "로그인이 완료되었습니다.",
  "data": {
    "accessToken": "jwt-access-token",
    "accessTokenExpiresIn": 3600,
    "refreshToken": "jwt-refresh-token",
    "refreshTokenExpiresIn": 1209600,
    "tokenType": "Bearer"
  },
  "statusCode": 200,
  "errorMessage": null,
  "errorCode": null
}
```

### 설명

- 로그인 성공 시 `accessToken`, `refreshToken`을 함께 발급합니다.
- 같은 사용자라도 `clientType + deviceId`가 다르면 다른 세션으로 관리합니다.
- 같은 `userId + clientType + deviceId`로 다시 로그인하면 기존 refresh token 정보가 갱신됩니다.

## 3. 로그아웃

- Method: `POST`
- Path: `/api/auth/logout`
- 인증 필요 여부: `No`

### Request Body

```json
{
  "refreshToken": "jwt-refresh-token",
  "clientType": "WEB",
  "deviceId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Success Response

```json
{
  "message": "로그아웃이 완료되었습니다.",
  "data": null,
  "statusCode": 200,
  "errorMessage": null,
  "errorCode": null
}
```

### 설명

- 요청에 포함된 `refreshToken`, `clientType`, `deviceId` 조합으로 특정 세션만 로그아웃합니다.
- 웹과 앱이 동시에 로그인된 경우에도 다른 세션에는 영향을 주지 않습니다.

## 4. Access Token 재발급

- Method: `POST`
- Path: `/api/auth/refresh`
- 인증 필요 여부: `No`

### Request Body

```json
{
  "refreshToken": "jwt-refresh-token",
  "clientType": "WEB",
  "deviceId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Success Response

```json
{
  "message": "토큰 재발급이 완료되었습니다.",
  "data": {
    "accessToken": "new-jwt-access-token",
    "accessTokenExpiresIn": 3600,
    "tokenType": "Bearer"
  },
  "statusCode": 200,
  "errorMessage": null,
  "errorCode": null
}
```

### 설명

- 일반 API 호출에는 `accessToken`만 사용합니다.
- `accessToken`이 만료되었을 때만 `refreshToken`으로 재발급 API를 호출합니다.
- 현재 구현 기준으로는 `refreshToken` 자체를 회전 재발급하지 않고, 새 `accessToken`만 발급합니다.

## 인증 헤더 예시

보호된 API 호출 시에는 아래 형식으로 `Authorization` 헤더를 보냅니다.

```http
Authorization: Bearer {accessToken}
```

## 에러 코드

- `AUTH_001`: 이미 가입된 이메일입니다.
- `AUTH_002`: 인증이 필요합니다.
- `AUTH_003`: 접근 권한이 없습니다.
- `AUTH_004`: 유효하지 않은 토큰입니다.
- `AUTH_005`: 만료된 토큰입니다.
- `COMMON_001`: 입력값이 올바르지 않습니다.
- `COMMON_002`: 요청 본문이 올바르지 않습니다.
- `COMMON_999`: 서버 내부 오류가 발생했습니다.

## 현재 범위

- 현재 문서는 실제 구현되어 있는 인증 API만 포함합니다.
- 사용자 정보 조회, 지갑 조회, 포인트 사용, 거래내역 API는 아직 문서화할 만큼 구현되지 않은 상태입니다.
