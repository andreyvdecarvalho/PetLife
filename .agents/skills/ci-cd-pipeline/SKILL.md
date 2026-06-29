---
name: ci-cd-pipeline
description: >
  Skill de configuração e manutenção do pipeline CI/CD do PetLife com GitHub Actions.
  Ativa quando o agente precisa criar ou modificar workflows de CI, configurar gates de
  cobertura JaCoCo, setup de Testcontainers no CI, deploy automatizado com Maven ou
  integração com ferramentas de qualidade.
  Palavras-chave: CI, CD, GitHub Actions, pipeline, workflow, deploy, JaCoCo, coverage gate,
  Maven, checkstyle, SpotBugs, build, Testcontainers, Docker.
---

# Skill: CI/CD Pipeline — PetLife (Java + Maven)

## Visão Geral do Pipeline

```
Push / PR → Checkstyle + SpotBugs → Compilação → Testes (Unit + Integration) → JaCoCo Gate → Build JAR → Deploy
```

---

## Workflow Principal — CI (Pull Requests)

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  # ─── BACKEND (Java) ───────────────────────────────────
  backend-quality:
    name: Backend — Java Quality
    runs-on: ubuntu-latest

    services:
      # Testcontainers gerencia o próprio banco — não precisamos de service aqui
      # A menos que seja necessário para testes fora do Testcontainers

    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Compile
        working-directory: apps/backend
        run: mvn compile -B -q

      - name: Checkstyle
        working-directory: apps/backend
        run: mvn checkstyle:check -B

      - name: SpotBugs
        working-directory: apps/backend
        run: mvn spotbugs:check -B

      - name: Run Tests (Unit + Integration com Testcontainers)
        working-directory: apps/backend
        run: mvn verify -B
        env:
          # Testcontainers precisa de Docker socket
          DOCKER_HOST: unix:///var/run/docker.sock
          TESTCONTAINERS_RYUK_DISABLED: "true"

      - name: JaCoCo Coverage Gate (≥ 80%)
        # O gate já está configurado no pom.xml — mvn verify falha se abaixo do threshold
        # Este passo apenas publica o relatório
        working-directory: apps/backend
        run: |
          COVERAGE=$(python3 -c "
          import xml.etree.ElementTree as ET
          tree = ET.parse('target/site/jacoco/jacoco.xml')
          root = tree.getroot()
          counters = {c.get('type'): c for c in root.findall('counter')}
          missed = int(counters['LINE'].get('missed'))
          covered = int(counters['LINE'].get('covered'))
          total = missed + covered
          print(f'{covered/total*100:.1f}' if total > 0 else '0')
          ")
          echo "Coverage: $COVERAGE%"

      - name: Upload JaCoCo report
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: jacoco-report
          path: apps/backend/target/site/jacoco/

      - name: Publish Test Results
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: JUnit Test Results
          path: apps/backend/target/surefire-reports/*.xml
          reporter: java-junit

  # ─── FRONTEND WEB ─────────────────────────────────────
  web-quality:
    name: Web — Quality
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: apps/web

    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-node@v4
        with:
          node-version: '22'

      - name: Setup pnpm
        uses: pnpm/action-setup@v4
        with:
          version: 9
          run_install: false

      - name: Get pnpm store directory
        id: pnpm-cache
        run: echo "STORE_PATH=$(pnpm store path)" >> $GITHUB_OUTPUT

      - name: Cache pnpm store
        uses: actions/cache@v4
        with:
          path: ${{ steps.pnpm-cache.outputs.STORE_PATH }}
          key: ${{ runner.os }}-pnpm-${{ hashFiles('**/pnpm-lock.yaml') }}
          restore-keys: ${{ runner.os }}-pnpm-

      - name: Install dependencies (frozen lockfile)
        run: pnpm install --frozen-lockfile
        working-directory: . # instala todo o workspace

      - name: TypeScript Check
        run: pnpm tsc --noEmit

      - name: Lint
        run: pnpm eslint src --max-warnings 0

      - name: Run component tests with coverage
        run: pnpm vitest run --coverage

      - name: Coverage Gate Frontend (≥ 75%)
        run: |
          COVERAGE=$(cat coverage/coverage-summary.json | jq '.total.lines.pct')
          if (( $(echo "$COVERAGE < 75" | bc -l) )); then
            echo "❌ Coverage $COVERAGE% below 75%"
            exit 1
          fi
          echo "✅ Coverage $COVERAGE%"

      - name: Build PWA
        run: pnpm run build


  # ─── E2E ──────────────────────────────────────────────
  e2e:
    name: E2E — Playwright
    runs-on: ubuntu-latest
    needs: [backend-quality, web-quality]
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'

    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - uses: actions/setup-node@v4
        with:
          node-version: '22'

      - name: Setup pnpm
        uses: pnpm/action-setup@v4
        with:
          version: 9

      - name: Install dependencies (workspace root)
        run: pnpm install --frozen-lockfile

      - name: Install Playwright browsers
        working-directory: apps/web
        run: pnpm playwright install --with-deps chromium

      - name: Build backend JAR
        working-directory: apps/backend
        run: mvn package -DskipTests -B

      - name: Start backend
        working-directory: apps/backend
        run: java -jar target/petlife-backend-*.jar &
        env:
          SPRING_PROFILES_ACTIVE: test
          DATABASE_URL: ${{ secrets.TEST_DATABASE_URL }}

      - name: Start web dev server
        working-directory: apps/web
        run: pnpm run dev &

      - name: Wait for servers
        run: pnpm dlx wait-on http://localhost:8080/actuator/health http://localhost:5173

      - name: Run Playwright E2E tests
        working-directory: apps/web
        run: pnpm playwright test

      - name: Upload Playwright report
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: playwright-report
          path: apps/web/playwright-report/
```

---

## Workflow de Deploy — Staging

```yaml
# .github/workflows/deploy-staging.yml
name: Deploy Staging

on:
  push:
    branches: [develop]

jobs:
  deploy-backend:
    name: Deploy Backend to Staging
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Build JAR
        working-directory: apps/backend
        run: mvn package -DskipTests -B

      - name: Run Flyway migrations on staging
        working-directory: apps/backend
        run: mvn flyway:migrate -B
        env:
          SPRING_DATASOURCE_URL: ${{ secrets.STAGING_DATABASE_URL }}
          SPRING_DATASOURCE_USERNAME: ${{ secrets.STAGING_DB_USER }}
          SPRING_DATASOURCE_PASSWORD: ${{ secrets.STAGING_DB_PASS }}

      - name: Deploy JAR (ex: AWS ECS / EC2 / Railway)
        run: echo "Deploy step aqui..."

      - name: Notify Slack
        uses: slackapi/slack-github-action@v1
        with:
          payload: |
            {"text": "✅ Deploy staging: ${{ github.ref_name }} (${{ github.sha }})"}
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

---

## Plugins Maven Essenciais (`pom.xml`)

```xml
<build>
  <plugins>

    <!-- Compilação Java 21 -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.13.0</version>
      <configuration>
        <source>21</source>
        <target>21</target>
        <parameters>true</parameters>
      </configuration>
    </plugin>

    <!-- JaCoCo — Coverage Gate -->
    <plugin>
      <groupId>org.jacoco</groupId>
      <artifactId>jacoco-maven-plugin</artifactId>
      <version>0.8.12</version>
      <executions>
        <execution><goals><goal>prepare-agent</goal></goals></execution>
        <execution>
          <id>report</id>
          <phase>test</phase>
          <goals><goal>report</goal></goals>
        </execution>
        <execution>
          <id>check</id>
          <goals><goal>check</goal></goals>
          <configuration>
            <rules>
              <rule>
                <element>BUNDLE</element>
                <limits>
                  <limit>
                    <counter>LINE</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.80</minimum>
                  </limit>
                  <limit>
                    <counter>BRANCH</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.75</minimum>
                  </limit>
                </limits>
              </rule>
            </rules>
            <excludes>
              <exclude>com/petlife/*Application.class</exclude>
              <exclude>com/petlife/config/**</exclude>
              <exclude>com/petlife/**/*Dto.class</exclude>
              <exclude>com/petlife/**/*Request.class</exclude>
              <exclude>com/petlife/**/*Response.class</exclude>
              <exclude>com/petlife/**/entity/**</exclude>
            </excludes>
          </configuration>
        </execution>
      </executions>
    </plugin>

    <!-- Checkstyle -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-checkstyle-plugin</artifactId>
      <version>3.4.0</version>
      <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <failOnViolation>true</failOnViolation>
      </configuration>
    </plugin>

    <!-- SpotBugs -->
    <plugin>
      <groupId>com.github.spotbugs</groupId>
      <artifactId>spotbugs-maven-plugin</artifactId>
      <version>4.8.6.2</version>
      <configuration>
        <effort>Max</effort>
        <threshold>High</threshold>
        <failOnError>true</failOnError>
      </configuration>
    </plugin>

    <!-- Surefire — testes unitários (sem Spring) -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>3.3.1</version>
      <configuration>
        <excludes>
          <exclude>**/*IT.java</exclude>
        </excludes>
      </configuration>
    </plugin>

    <!-- Failsafe — testes de integração (com Testcontainers) -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-failsafe-plugin</artifactId>
      <version>3.3.1</version>
      <configuration>
        <includes>
          <include>**/*IT.java</include>
          <include>**/*IntegrationTest.java</include>
        </includes>
      </configuration>
      <executions>
        <execution>
          <goals>
            <goal>integration-test</goal>
            <goal>verify</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

  </plugins>
</build>
```

---

## Scripts pnpm — Monorepo Frontend

```json
// apps/web/package.json
{
  "name": "web",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "test": "vitest run",
    "test:watch": "vitest",
    "test:coverage": "vitest run --coverage",
    "test:e2e": "playwright test",
    "lint": "eslint src --max-warnings 0",
    "typecheck": "tsc --noEmit"
  },
  "packageManager": "pnpm@9.15.0"
}
```

**Comandos pnpm úteis no monorepo:**

```bash
# Instalar todas as deps do workspace
pnpm install

# No CI — rejeita qualquer diferença com o lockfile
pnpm install --frozen-lockfile

# Rodar script em workspace específico
pnpm --filter web run dev
pnpm --filter mobile run start

# Adicionar dep apenas no web
pnpm --filter web add react-query

# Adicionar dep de desenvolvimento na raiz
pnpm add -D typescript -w

# Audit de segurança (apenas pacotes com vulnerabilidades HIGH+)
pnpm audit --audit-level=high

# Verificar pacotes desatualizados
pnpm outdated --recursive

# Limpar store e reinstalar tudo
pnpm store prune && pnpm install
```

---

## Gates de Qualidade (Bloqueantes)

| Gate                   | Threshold        | Bloqueia Merge? |
|------------------------|------------------|-----------------|
| Compilação Java        | 0 erros          | ✅ Sim          |
| Checkstyle             | 0 violations     | ✅ Sim          |
| SpotBugs               | 0 HIGH bugs      | ✅ Sim          |
| Cobertura Backend      | ≥ 80% linhas     | ✅ Sim          |
| Cobertura Frontend     | ≥ 75% linhas     | ✅ Sim          |
| Testes unitários       | 100% pass        | ✅ Sim          |
| Testes de integração   | 100% pass        | ✅ Sim          |
| Build JAR              | Sucesso          | ✅ Sim          |
| E2E Playwright (main)  | 100% pass        | ✅ Sim (main)   |

---

## Variáveis de Ambiente por Ambiente

| Variável                        | Dev       | Test (CI)    | Staging   | Prod      |
|---------------------------------|-----------|--------------|-----------|-----------|
| `SPRING_DATASOURCE_URL`         | local     | Testcontainers | staging | prod RDS  |
| `SPRING_DATA_REDIS_HOST`        | localhost | Testcontainers | staging | prod      |
| `JWT_PRIVATE_KEY`               | dev key   | test key     | staging   | prod key  |
| `FIREBASE_CREDENTIALS_JSON`     | dev proj  | mock         | staging   | prod      |
| `AWS_S3_BUCKET`                 | local S3  | minio        | staging   | prod      |
| `SENTRY_DSN`                    | —         | —            | staging   | prod      |
| `LOGGING_LEVEL_ROOT`            | DEBUG     | WARN         | INFO      | WARN      |
| `SPRING_PROFILES_ACTIVE`        | dev       | test         | staging   | prod      |
