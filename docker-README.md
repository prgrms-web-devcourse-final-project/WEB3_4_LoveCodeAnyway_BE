# LoveCodeAnyway BE 도커 설정 가이드

## 도커 이미지 빌드 및 배포 방법

### 1. 이미지 빌드

프로젝트 루트 디렉토리에서 다음 명령어를 실행합니다:

```bash
docker build -t lovecodeanyway-backend .
```

### 2. 이미지 태그 설정 (Docker Hub 업로드용)

```bash
docker tag lovecodeanyway-backend [Docker Hub 사용자명]/lovecodeanyway-backend:latest
```

### 3. Docker Hub 로그인

```bash
docker login
```

### 4. 이미지 푸시

```bash
docker push [Docker Hub 사용자명]/lovecodeanyway-backend:latest
```

## 도커 컨테이너 실행 방법

### 방법 1: docker-compose 사용 (권장)

```bash
docker-compose up -d
```

### 방법 2: docker run 명령어 사용

```bash
docker run -d -p 8080:8080 --name lovecodeanyway-backend -e SPRING_PROFILES_ACTIVE=dev [Docker Hub 사용자명]/lovecodeanyway-backend:latest
```

## 컨테이너 로그 확인

```bash
docker logs -f lovecodeanyway-backend
```

## 컨테이너 중지

```bash
docker stop lovecodeanyway-backend
```

## 컨테이너 삭제

```bash
docker rm lovecodeanyway-backend
```

## 이미지 삭제

```bash
docker rmi [Docker Hub 사용자명]/lovecodeanyway-backend:latest
```

## 애플리케이션 접속

브라우저에서 다음 주소로 접속할 수 있습니다:

- API 엔드포인트: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- H2 콘솔: http://localhost:8080/api/v1/h2-console
