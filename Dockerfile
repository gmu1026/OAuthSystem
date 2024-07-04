# 빌드 스테이지
FROM gradle:8.8-jdk17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 파일들을 복사
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle

# 소스 코드 복사
COPY src /app/src

# Gradle 빌드 수행 (clean build -x test -x processAot)
RUN gradle clean build -x test -x processAot --no-daemon

# 실행 스테이지
FROM ghcr.io/graalvm/jdk:ol8-java17-22.3.1

WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일을 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너가 시작될 때 실행될 명령어 지정
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# 애플리케이션 포트 노출
EXPOSE 8091