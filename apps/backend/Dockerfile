# ==============================================================================
# Stage 1 — DEPS: Cache de dependências Maven
# ==============================================================================
# Usa imagem oficial maven+temurin que já tem Maven 3.9 + JDK 21 integrados.
# Copia apenas pom.xml e resolve todas as dependências offline.
# Só invalida o cache se o pom.xml mudar, economizando minutos de rebuild.
# ==============================================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS deps

WORKDIR /build

# Copia apenas o POM para cachear dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# ==============================================================================
# Stage 2 — BUILD: Compilação e empacotamento
# ==============================================================================
# Usa o cache de dependências do estágio anterior.
# Gera o fat JAR com spring-boot-maven-plugin.
# ==============================================================================
FROM deps AS build

# Copia o código-fonte
COPY src ./src
COPY checkstyle.xml .

# Build sem testes (testes rodam no CI, não no build da imagem)
RUN mvn package -B -q \
    -DskipTests \
    -Dmaven.test.skip=true \
    -Dcheckstyle.skip=true \
    -Dspotbugs.skip=true \
    && mv target/*.jar target/app.jar

# ==============================================================================
# Stage 3 — EXTRACT: Extração com jarmode=tools (Spring Boot 4.x)
# ==============================================================================
# Separa o fat JAR em app.jar (classes) + lib/ (dependências).
# Isso cria 2 camadas Docker:
#   1. lib/     → Muda raramente (dependências)
#   2. app.jar  → Muda a cada commit (código da aplicação)
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine AS extract

WORKDIR /build
COPY --from=build /build/target/app.jar ./app.jar

RUN java -Djarmode=tools -jar app.jar extract --destination extracted

# ==============================================================================
# Stage 4 — RUNTIME: Imagem final mínima
# ==============================================================================
# Usa eclipse-temurin JRE Alpine (~80MB vs ~400MB do JDK).
# Roda como usuário não-root por segurança.
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Labels de metadados OCI
LABEL org.opencontainers.image.title="PetLife Backend" \
      org.opencontainers.image.description="API REST Spring Boot 4.1 — PetLife" \
      org.opencontainers.image.source="https://github.com/andreyvdecarvalho/PetLife"

# Cria usuário não-root
RUN addgroup -S petlife && adduser -S petlife -G petlife

WORKDIR /app

# Copia libs (mudam raramente) antes do app.jar (muda a cada commit)
COPY --from=extract /build/extracted/lib ./lib
COPY --from=extract /build/extracted/app.jar ./app.jar

# Healthcheck nativo via Spring Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Troca para o usuário não-root
USER petlife

# Configura a JVM para containers:
#   -XX:+UseContainerSupport     → Respeita limites de CPU/memória do container
#   -XX:MaxRAMPercentage=75.0    → Usa no máximo 75% da RAM do container para heap
#   -XX:+UseZGC                  → ZGC: baixa latência, ideal para APIs
#   -XX:+ZGenerational           → ZGC geracional (JDK 21+), melhor throughput
#   -Djava.security.egd          → Evita bloqueio em /dev/random
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseZGC", \
    "-XX:+ZGenerational", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
