FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# 그래들 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 권한 설정
RUN chmod +x ./gradlew

# 의존성만 먼저 다운로드
RUN ./gradlew dependencies

# 소스 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew clean bootJar

# 런타임 이미지
FROM eclipse-temurin:21-jre

WORKDIR /app

# JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 환경 설정
ENV SPRING_PROFILES_ACTIVE=dev
ENV TZ=Asia/Seoul

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]