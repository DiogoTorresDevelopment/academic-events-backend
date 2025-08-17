# Academic Events Backend (Spring Boot) — README

> **Stack**: Java 21 • Spring Boot 3.3.x • Maven • PostgreSQL • Redis • RabbitMQ • (opcional) MinIO • Flyway • springdoc-openapi • OpenHTMLtoPDF

---

## Sumário

- [Visão geral](#visão-geral)
- [Requisitos](#requisitos)
- [Infra local (Docker)](#infra-local-docker)
- [Configuração (application.yml)](#configuração-applicationyml)
- [Build & Run](#build--run)
- [Módulos e responsabilidades](#módulos-e-responsabilidades)
- [Banco de dados & Migrações](#banco-de-dados--migrações)
- [Autenticação & Segurança](#autenticação--segurança)
- [Multi-tenant (X-Tenant)](#multi-tenant-x-tenant)
- [API & Swagger](#api--swagger)
- [Exemplos de uso (cURL)](#exemplos-de-uso-curl)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Comandos úteis](#comandos-úteis)
- [Troubleshooting](#troubleshooting)

---

## Visão geral

Repositório **multi-módulos Maven** para um backend de **Controle de Eventos Acadêmicos**. Arquitetura em camadas com **DDD leve**:

- **core-domain**: entidades e regras de negócio puras
- **core-application**: casos de uso/portas (orquestra a regra de negócio)
- **adapters-**\*: implementações para Web, Persistência, Mensageria, Arquivos, PDF
- **security**: Resource Server (JWT)
- **tenancy**: contexto/filtro multi-tenant
- **bootstrap**: aplicação Spring Boot (entrypoint) + Actuator

## Requisitos

- **JDK 21**
- **Maven ≥ 3.9**
- **Docker** + **Docker Compose**

Verifique versões:

```bash
java -version
mvn -v
docker --version
docker compose version
```

> Dica (Maven): para eliminar avisos de encoding, garanta no **parent **``:

```xml
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

## Infra local (Docker)

Suba os serviços locais (Postgres, Redis, RabbitMQ, MinIO, Mailhog):

```bash
docker compose -f docker/docker-compose.yml up -d
```

Portas padrão:

- Postgres `5432` • Redis `6379` • RabbitMQ `5672` (console `15672`) • MinIO `9000/9001` • Mailhog `1025/8025`

## Configuração (`application.yml`)

Arquivo base de desenvolvimento:

```
modules/bootstrap/src/main/resources/application.yml
```

Trechos relevantes:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/academic
    username: academic
    password: academic
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate.format_sql: true
      hibernate.jdbc.time_zone: UTC
  flyway:
    enabled: true
    locations: classpath:db/migration

  rabbitmq:
    host: localhost
    port: 5672

  redis:
    host: localhost
    port: 6379

  security:
    oauth2:
      resourceserver:
        jwt:
          secret-key: "changeme-32+chars-secret"  # DEV somente

server:
  port: 8080

springdoc:
  api-docs.path: /v3/api-docs
  swagger-ui.path: /swagger-ui
```

> Em produção, **não** use `secret-key` simples; integre com um IdP (Keycloak/Okta) e JWKs.

## Build & Run

1. Instalar dependências/compilar:

```bash
mvn -DskipTests clean install
```

2. Rodar a aplicação (módulo **bootstrap**):

```bash
mvn -pl modules/bootstrap spring-boot:run
```

3. Abrir Swagger UI:

```
http://localhost:8080/swagger-ui
```

> **Header obrigatório nas rotas de negócio**: `X-Tenant: <slug ou UUID do cliente>`.

## Módulos e responsabilidades

### `core-domain`

- **O que é**: entidades JPA, agregados, enums; regras de negócio puras
- **Usa**: `jakarta.persistence`

### `core-application`

- **O que é**: casos de uso/serviços de aplicação; **portas** chamadas pelos adapters
- **Usa**: `core-domain`

### `adapters-persistence`

- **O que é**: repositórios **Spring Data JPA**, implementações de serviços que persistem dados
- **Usa**: `spring-boot-starter-data-jpa`, **PostgreSQL**, **Flyway**; depende de `core-domain` e `core-application`
- **Exemplos atuais**: `StudentRepository`, `StudentServiceImpl` (consulta e gravação de alunos)
- **Migrações**: `src/main/resources/db/migration`

### `adapters-web`

- **O que é**: **Controllers REST**, DTOs, validação, documentação OpenAPI e `@ControllerAdvice`
- **Usa**: `spring-boot-starter-web`, `spring-boot-starter-validation`, `springdoc-openapi`
- **Integra**: `security`, `tenancy`, `core-application`, `adapters-persistence`

### `security`

- **O que é**: configuração **Spring Security** (Resource Server + RBAC com escopos)
- **Usa**: `spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server`
- **DEV**: JWT **HS256** com `secret-key` (apenas para desenvolvimento)

### `tenancy`

- **O que é**: **TenantContext** e **TenantFilter** (lê `X-Tenant` ou subdomínio; disponibiliza o tenant por request)
- **Usa**: `spring-web` para o filtro

### `bootstrap`

- **O que é**: classe `Application` (entrypoint), **Actuator**, configuração geral
- **Usa**: `spring-boot-starter`, `spring-boot-starter-actuator`, depende de `adapters-web`

### `adapters-messaging`

- **O que é**: integração AMQP (ex.: emissão de certificados em lote)
- **Usa**: `spring-boot-starter-amqp` • **Status atual**: base criada, sem producers/consumers ativos

### `adapters-files`

- **O que é**: armazenamento de arquivos (S3/MinIO)
- **Usa**: `software.amazon.awssdk:s3` • **Status atual**: base criada

### `adapters-pdf`

- **O que é**: geração de PDFs (certificados)
- **Usa**: `com.openhtmltopdf:openhtmltopdf-pdfbox` • **Status atual**: base criada

## Banco de dados & Migrações

- **Flyway** habilitado; scripts em `modules/adapters-persistence/src/main/resources/db/migration`.
- **Primeira migração** (`V1__init.sql`): cria tabelas base (ex.: `cliente`, `aluno`, e/ou demais tabelas conforme evolução do projeto). Consulte os scripts para a versão exata atual.
- **Seed (exemplo)** — criar um cliente para testes locais e usar seu **UUID** como tenant:

```sql
insert into cliente (id, nome, slug, criado_em)
values ('11111111-1111-1111-1111-111111111111', 'Cliente Demo', 'demo', now());
```

Depois, use `X-Tenant: 11111111-1111-1111-1111-111111111111` (ou `X-Tenant: demo` se houver mapeamento slug→UUID implementado).

## Autenticação & Segurança

- **Resource Server (JWT)** com escopos (ex.: `admin:full`, `organizer:write`, `gate:checkin`, `student:self`).
- Para desenvolvimento rápido, gere um **JWT HS256** usando o `secret-key` de `application.yml` com `scope` (ou `scp`) contendo os escopos necessários.
- Endpoints públicos (exemplos): `/actuator/health`, Swagger e `GET /api/v1/{tenant}/healthz`.

> **Nota de compatibilidade**: para evitar aviso de depreciação no Spring Security 6.3, a configuração recomendada é:

```java
.oauth2ResourceServer(oauth -> oauth.jwt(org.springframework.security.config.Customizer.withDefaults()))
```

## Multi-tenant (X-Tenant)

- O tenant é resolvido por **header **`` (ou subdomínio, se configurado).
- Repositórios/consultas devem filtrar por `cliente_id`/tenant.

## API & Swagger

- **Swagger UI**: `http://localhost:8080/swagger-ui`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Base path**: `/api/v1/{tenant}` (ex.: `/api/v1/11111111-1111-1111-1111-111111111111/students`)

## Exemplos de uso (cURL)

Health (público):

```bash
curl -s http://localhost:8080/api/v1/11111111-1111-1111-1111-111111111111/healthz
```

Listar alunos (requer token com escopo `organizer:write` ou `admin:full`):

```bash
curl -s \
  -H "Authorization: Bearer <JWT_HS256>" \
  -H "X-Tenant: 11111111-1111-1111-1111-111111111111" \
  http://localhost:8080/api/v1/11111111-1111-1111-1111-111111111111/students
```

Criar aluno:

```bash
curl -s -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_HS256>" \
  -H "X-Tenant: 11111111-1111-1111-1111-111111111111" \
  -d '{"nome":"Alice","email":"alice@example.com","cpf":"00000000000"}' \
  http://localhost:8080/api/v1/11111111-1111-1111-1111-111111111111/students
```

## Estrutura do projeto

```
academic-events-backend/
├─ pom.xml                      # parent (packaging=pom)
├─ docker/
│  └─ docker-compose.yml        # postgres, redis, rabbitmq, minio, mailhog
└─ modules/
   ├─ bootstrap/
   │  └─ src/main/java/.../Application.java
   ├─ security/
   │  └─ src/main/java/.../SecurityConfig.java
   ├─ tenancy/
   │  └─ src/main/java/.../{TenantContext.java,TenantFilter.java}
   ├─ core-domain/
   │  └─ src/main/java/.../domain/*.java
   ├─ core-application/
   │  └─ src/main/java/.../application/*.java
   ├─ adapters-persistence/
   │  ├─ src/main/java/.../persistence/*.java
   │  └─ src/main/resources/db/migration/*.sql
   ├─ adapters-web/
   │  └─ src/main/java/.../web/**/*.java
   ├─ adapters-messaging/
   ├─ adapters-files/
   └─ adapters-pdf/
```

## Comandos úteis

Compilar tudo (sem testes):

```bash
mvn -DskipTests clean install
```

Compilar/rodar apenas o **bootstrap**:

```bash
mvn -pl modules/bootstrap spring-boot:run
```

Refazer build a partir de um módulo específico (ex.: persistência):

```bash
mvn -DskipTests -rf :adapters-persistence clean install
```

Parar e remover a infra local:

```bash
docker compose -f docker/docker-compose.yml down -v
```

## Troubleshooting

**Plugins/compilação**

- Aviso "platform encoding": adicione `project.build.sourceEncoding=UTF-8` no parent POM (vide [Requisitos](#requisitos)).
- Depreciação `oauth2ResourceServer().jwt()`: use `Customizer.withDefaults()` na config do Security.

**Banco de dados/Flyway**

- Erros de validação: confira a ordem/nomes `V__` dos scripts e se o schema alvo está limpo.
- Reconciliação local: derrube e suba o Postgres novamente (`docker compose down -v && up -d`).

**Portas em uso**

- Ajuste as portas no `docker-compose.yml` ou finalize serviços concorrentes.

**Swagger não abre**

- Verifique se o **bootstrap** está rodando e se a URL é `http://localhost:8080/swagger-ui`.

---

> **Licença**: defina a licença do projeto (MIT/Apache-2.0/etc.).

> **Contribuição**: PRs e issues são bem-vindos. Padronize commits (Conventional Commits) e garanta testes/verificações locais antes do PR.

