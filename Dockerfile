FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# 1. JAR 파일 복사 (이 안에 application.yml, application-prod.yml이 다 들어있음)
COPY build/libs/*-SNAPSHOT.jar app.jar

# 2. 불필요한 YML 복사 라인 삭제!
# (이미 JAR 안에 들어있으므로 굳이 밖으로 꺼낼 필요가 없습니다)

EXPOSE 8080

# 3. 실행 명령어 변경 (핵심!)
# -Dspring.config.location (X) -> 이거 쓰면 공통 설정 무시됨
# -Dspring.profiles.active=prod (O) -> 공통(application.yml) + 운영(prod) 합쳐서 실행됨
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]