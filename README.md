# wallet-project

Spring Boot 기반의 지갑(`wallet-service`)과 결제(`payment`) 서비스를 함께 관리하는 멀티 프로젝트 저장소입니다.

## 프로젝트 구성

- `wallet-service`
  - 회원가입, 로그인, 로그아웃, 토큰 재발급
  - 사용자 정보 조회 및 수정
  - 지갑 조회, 생성, 수정, 삭제
- `payment`
  - 결제 도메인 API 기본 구조
  - 결제 및 관리자 API 확장용 서비스

## 기술 스택

- Java 17
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA
- Spring Security (`wallet-service`)
- MySQL
- Gradle

## 디렉터리 구조

```text
walletPJ/
├─ README.md
├─ wallet-service/
└─ payment/
```

## 기본 포트

- `wallet-service`: `8210`
- `payment`: `8220`

## 실행 전 준비

### 1. 요구사항

- JDK 17
- MySQL 8.x

### 2. 환경변수

두 서비스 모두 MySQL 연결을 위해 아래 환경변수를 사용할 수 있습니다.

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/wallet_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8"
$env:DB_USERNAME="your_db_user"
$env:DB_PASSWORD="your_db_password"
```

`wallet-service`는 JWT 설정도 환경변수로 덮어쓸 수 있습니다.

```powershell
$env:APP_JWT_SECRET="change-this-secret"
$env:APP_JWT_ACCESS_TOKEN_EXPIRATION_SECONDS="3600"
$env:APP_JWT_REFRESH_TOKEN_EXPIRATION_SECONDS="1209600"
```

## 실행 방법

각 서비스는 개별적으로 실행합니다.

### wallet-service

```powershell
cd .\wallet-service
.\gradlew.bat bootRun
```

### payment

```powershell
cd .\payment
.\gradlew.bat bootRun
```

## 테스트

```powershell
cd .\wallet-service
.\gradlew.bat test

cd ..\payment
.\gradlew.bat test
```

## 주요 API

### wallet-service

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `POST /api/auth/refresh`
- `GET /api/user/me`
- `POST /api/user/me`
- `GET /api/wallets`
- `POST /api/wallets`
- `PUT /api/wallets/{walletId}`
- `DELETE /api/wallets/{walletId}`

추가 문서는 [wallet-service/docs](C:/dev/walletPJ/wallet-service/docs) 에 정리되어 있습니다.

### payment

- base path: `/api/payments`
- base path: `/api/admin`

현재 `payment` 서비스는 결제 도메인 기본 구조와 컨트롤러 뼈대를 중심으로 구성되어 있으며, 상세 API 구현은 계속 확장 중입니다.

## 참고

- 기본 프로필은 두 서비스 모두 `mysql`입니다.
- 로컬 개발 환경에서는 CORS 기본 허용 주소가 `http://localhost:3000`, `http://127.0.0.1:3000`로 설정되어 있습니다.
