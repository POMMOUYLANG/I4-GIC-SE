# Lab 05: Security in Java Web Applications (Spring Security, JWT)

## Complete Architecture & Implementation Guide (Tasks 0–5 & Extra-Credit Challenge)

> **Course:** Software Engineering — Chapter 05  
> **Topic:** Spring Security 7.1.0 / 6.4+, OAuth2 Resource Server, JWT (Nimbus JOSE), Method Security, Hardening & OWASP Top 10  
> **Application:** `shop-secure` (Hardened E-Commerce API & Secure Web Portal)  
> **Stack:** Java 25, Spring Boot 4.1.1 (Spring Security 7.1.0), Nimbus JOSE+JWT, Spring Data JPA, H2 Database, Hibernate Validator, Bucket4j, JUnit 5 / Spring Security Test, Apache Maven 3.9+  
> **Estimated Duration:** ≈ 6 hours (Tasks 0–5: 100 points, Challenge: +20 points extra credit)  

---

## 📑 Table of Contents

- [Lab 05: Security in Java Web Applications (Spring Security, JWT)](#lab-05-security-in-java-web-applications-spring-security-jwt)
  - [Complete Architecture \& Implementation Guide (Tasks 0–5 \& Extra-Credit Challenge)](#complete-architecture--implementation-guide-tasks-05--extra-credit-challenge)
  - [📑 Table of Contents](#-table-of-contents)
  - [🎯 Lab Objectives \& Architectural Overview](#-lab-objectives--architectural-overview)
  - [📊 Grading Rubric \& Automatic Deductions](#-grading-rubric--automatic-deductions)
  - [🚀 Task 0: Setup \& Project Initialization (15 min)](#-task-0-setup--project-initialization-15-min)
    - [Step 0.1: Verify Developer Toolchain](#step-01-verify-developer-toolchain)
    - [Step 0.2: Generate Project \& Configure `pom.xml`](#step-02-generate-project--configure-pomxml)
    - [Step 0.3: Configure `application.yml` \& Git Hygiene](#step-03-configure-applicationyml--git-hygiene)
    - [Step 0.4: Target Package Layout \& Initial Build Verification](#step-04-target-package-layout--initial-build-verification)
  - [🛡️ Task 1: Default Security \& Your First Filter Chain (40 min - 10 pts)](#️-task-1-default-security--your-first-filter-chain-40-min---10-pts)
    - [Architectural Analysis: Spring Security Auto-Configuration Breakdown](#architectural-analysis-spring-security-auto-configuration-breakdown)
    - [Step 1.1: Product Domain, Repository, Service \& Controller](#step-11-product-domain-repository-service--controller)
    - [Step 1.2: Seed Products with `CommandLineRunner`](#step-12-seed-products-with-commandlinerunner)
    - [Step 1.3: Explicit `SecurityConfig` with Lambda DSL](#step-13-explicit-securityconfig-with-lambda-dsl)
    - [Step 1.4: RFC 9457 Problem Details (`ApiExceptionHandler`, EntryPoint, DeniedHandler)](#step-14-rfc-9457-problem-details-apiexceptionhandler-entrypoint-deniedhandler)
    - [Step 1.5: Verification Transcripts (`security-session.md`)](#step-15-verification-transcripts-security-sessionmd)
    - [Task 1 Checklist \& Tagging](#task-1-checklist--tagging)
  - [🔑 Task 2: Users and Passwords in the Database (50 min - 15 pts)](#-task-2-users-and-passwords-in-the-database-50-min---15-pts)
    - [Architectural Deep Dive: DelegatingPasswordEncoder \& BCrypt](#architectural-deep-dive-delegatingpasswordencoder--bcrypt)
    - [Step 2.1: Domain Entities (`UserAccount`, `Role`) with Secure `toString()`](#step-21-domain-entities-useraccount-role-with-secure-tostring)
    - [Step 2.2: `UserAccountRepository`](#step-22-useraccountrepository)
    - [Step 2.3: `PasswordConfig` Bean Declaration](#step-23-passwordconfig-bean-declaration)
    - [Step 2.4: `JpaUserDetailsService` Implementation](#step-24-jpauserdetailsservice-implementation)
    - [Step 2.5: `AccountService` \& Idempotent Database Seeding](#step-25-accountservice--idempotent-database-seeding)
    - [Step 2.6: Registration Endpoint (`RegisterController`, DTO Validation \& Policy)](#step-26-registration-endpoint-registercontroller-dto-validation--policy)
    - [Step 2.7: `/api/me` Endpoint \& BCrypt Hash Inspection in H2](#step-27-apime-endpoint--bcrypt-hash-inspection-in-h2)
    - [Task 2 Checklist \& Tagging](#task-2-checklist--tagging)
  - [👑 Task 3: Roles and Method Security (60 min - 20 pts)](#-task-3-roles-and-method-security-60-min---20-pts)
    - [Architectural Deep Dive: Method Security Interceptors \& SpEL Evaluation](#architectural-deep-dive-method-security-interceptors--spel-evaluation)
    - [Step 3.1: Domain `Order` \& `OrderRepository`](#step-31-domain-order--orderrepository)
    - [Step 3.2: `MethodSecurityConfig` \& Hierarchical Roles (`RoleHierarchy`)](#step-32-methodsecurityconfig--hierarchical-roles-rolehierarchy)
    - [Step 3.3: Hardened `OrderService` with Declarative SpEL Rules](#step-33-hardened-orderservice-with-declarative-spel-rules)
    - [Step 3.4: `OrderController` (Secure Principal Ingestion)](#step-34-ordercontroller-secure-principal-ingestion)
    - [Step 3.5: Method Security Exception Mapping in `ApiExceptionHandler`](#step-35-method-security-exception-mapping-in-apiexceptionhandler)
    - [Step 3.6: Comprehensive Test Suites (`OrderServiceSecurityTest`, `OrderControllerSecurityTest`)](#step-36-comprehensive-test-suites-orderservicesecuritytest-ordercontrollersecuritytest)
    - [Step 3.7: Verification Transcripts](#step-37-verification-transcripts)
    - [Task 3 Checklist \& Tagging](#task-3-checklist--tagging)
  - [🎫 Task 4: JWT Login and a Stateless API (75 min - 20 pts)](#-task-4-jwt-login-and-a-stateless-api-75-min---20-pts)
    - [Architectural Deep Dive: Stateless Tokens vs Stateful Sessions \& Nimbus JOSE](#architectural-deep-dive-stateless-tokens-vs-stateful-sessions--nimbus-jose)
    - [Step 4.1: Secret Key Ingestion \& `SecurityProps` Record](#step-41-secret-key-ingestion--securityprops-record)
    - [Step 4.2: `JwtConfig` (Encoder, Decoder, Roles Converter, AuthManager)](#step-42-jwtconfig-encoder-decoder-roles-converter-authmanager)
    - [Step 4.3: `AuthController` (Issue HS256 JWTs with Strict Claims)](#step-43-authcontroller-issue-hs256-jwts-with-strict-claims)
    - [Step 4.4: Hardening the Filter Chain as an OAuth2 Resource Server](#step-44-hardening-the-filter-chain-as-an-oauth2-resource-server)
    - [Step 4.5: JWT Inspection, Decoded Payload, and Security Explanation](#step-45-jwt-inspection-decoded-payload-and-security-explanation)
    - [Step 4.6: Security Tests (`jwt()` Post-Processor \& `AuthFlowIT`)](#step-46-security-tests-jwt-post-processor--authflowit)
    - [Step 4.7: Verification Transcripts](#step-47-verification-transcripts)
    - [Task 4 Checklist \& Tagging](#task-4-checklist--tagging)
  - [🧱 Task 5: Hardening: CSRF, CORS, Headers, XSS, Secrets, Logging (90 min - 25 pts)](#-task-5-hardening-csrf-cors-headers-xss-secrets-logging-90-min---25-pts)
    - [Step 5.1: CSRF Strategy Documentation (`docs/csrf.md`)](#step-51-csrf-strategy-documentation-docscsrfmd)
    - [Step 5.2: CORS Configuration (`CorsConfig.java`) \& Origin Whitelisting](#step-52-cors-configuration-corsconfigjava--origin-whitelisting)
    - [Step 5.3: Defense-in-Depth Security Headers](#step-53-defense-in-depth-security-headers)
    - [Step 5.4: Stored XSS Demonstration \& Neutralization (`Review`, Thymeleaf `th:text` vs `th:utext`)](#step-54-stored-xss-demonstration--neutralization-review-thymeleaf-thtext-vs-thutext)
    - [Step 5.5: Secret Hygiene Audit \& Fail-Fast Validation](#step-55-secret-hygiene-audit--fail-fast-validation)
    - [Step 5.6: Security Event Auditing (`SecurityEventLogger`) \& MDC Tracing (`RequestIdFilter`)](#step-56-security-event-auditing-securityeventlogger--mdc-tracing-requestidfilter)
    - [Task 5 Checklist \& Tagging](#task-5-checklist--tagging)
  - [🌟 Challenge: Refresh Tokens, Lockout, Audit Log, OWASP (+20 pts)](#-challenge-refresh-tokens-lockout-audit-log-owasp-20-pts)
    - [Challenge 1: Refresh Token Family Rotation \& Theft Detection Engine](#challenge-1-refresh-token-family-rotation--theft-detection-engine)
    - [Challenge 2: Brute-Force Rate Limiting (Bucket4j / Sliding Window)](#challenge-2-brute-force-rate-limiting-bucket4j--sliding-window)
    - [Challenge 3: Event-Driven Account Lockout (`LockoutListener`)](#challenge-3-event-driven-account-lockout-lockoutlistener)
    - [Challenge 4: Immutable Security Audit Log (`REQUIRES_NEW`)](#challenge-4-immutable-security-audit-log-requires_new)
    - [Challenge 5: OWASP Top 10 (2021) Compliance Matrix (`docs/owasp-checklist.md`)](#challenge-5-owasp-top-10-2021-compliance-matrix-docsowasp-checklistmd)
    - [Challenge 6: End-to-End Integration Suite (`AuthLifecycleIT`)](#challenge-6-end-to-end-integration-suite-authlifecycleit)
    - [Challenge Checklist \& Tagging](#challenge-checklist--tagging)
  - [📋 Project Artifacts \& Directory Manifest](#-project-artifacts--directory-manifest)
  - [⚙️ Verification Commands \& Git Tagging Quick Reference](#️-verification-commands--git-tagging-quick-reference)

---

## 🎯 Lab Objectives & Architectural Overview

In **Lab 05**, we engineer an enterprise-grade, defense-in-depth security architecture for `shop-secure`. We transition systematically from naive defaults to a fully hardened, stateless OAuth2 Resource Server powered by JSON Web Tokens (JWT), method-level authorization, declarative security events, and cryptographic protections against standard web attack vectors.

```
+-------------------------------------------------------------------------------------------------------------------------+
|                                              HTTP CLIENT (curl / Browser / SPA)                                         |
+-------------------------------------------------------------------------------------------------------------------------+
                                                             |
                                                             v
+-------------------------------------------------------------------------------------------------------------------------+
|                                              Servlet Container Filter Chain                                             |
|                                                                                                                         |
|   1. RequestIdFilter (OncePerRequestFilter): Injects/Echoes X-Request-Id, assigns MDC key 'rid' for tracing             |
|   2. RateLimitFilter (Challenge): 5 req/min per IP/username on /api/auth/login via Bucket4j (HTTP 429 Retry-After)      |
|   3. CorsFilter: Evaluates Origin; whitelists http://localhost:5173; handles OPTIONS preflight                          |
|   4. Spring Security FilterChainProxy: Selects target SecurityFilterChain based on RequestMatcher:                      |
+-------------------------------------------------------------------------------------------------------------------------+
                    |                                                                 |
                    | [matches /api/**]                                               | [matches /products/** (HTML UI)]
                    v                                                                 v
+---------------------------------------------------------+     +---------------------------------------------------------+
|       SecurityFilterChain #1 (@Order(1): API Chain)     |     |     SecurityFilterChain #2 (@Order(2): Web UI Chain)     |
|                                                         |     |                                                         |
| - CSRF: DISABLED (Stateless Bearer, no cookie auth)     |     | - CSRF: Enabled or GET permitAll                        |
| - Session: STATELESS (SessionCreationPolicy.STATELESS)  |     | - Session: If-Required                                  |
| - Headers: DENY, nosniff, HSTS, strict-origin, CSP      |     | - Route: GET /products/** -> permitAll()                |
| - BearerTokenAuthenticationFilter:                      |     | - Engine: Thymeleaf with th:text contextual escaping    |
|   * Intercepts "Authorization: Bearer <token>"          |     +---------------------------------------------------------+
|   * Decodes via NimbusJwtDecoder (HS256)                |
|   * Validates issuer ("shop-api"), signature, expiry    |
|   * Maps claims -> GrantedAuthorities (ROLE_*)          |
| - AuthorizationFilter:                                  |
|   * /api/auth/**, /api/register -> permitAll()          |
|   * GET /api/products/** -> permitAll()                 |
|   * DELETE /api/products/** -> hasRole("ADMIN")         |
|   * All other /api/** -> authenticated()                |
|   * anyRequest() -> denyAll()                           |
| - ExceptionHandling:                                    |
|   * AuthenticationEntryPoint -> 401 ProblemDetail       |
|   * AccessDeniedHandler -> 403 ProblemDetail            |
+---------------------------------------------------------+
                            |
                            v
+-------------------------------------------------------------------------------------------------------------------------+
|                                            Spring MVC DispatcherServlet Layer                                           |
|                                                                                                                         |
|   Controllers: ProductController, OrderController, AuthController, RegisterController, ReviewController                 |
+-------------------------------------------------------------------------------------------------------------------------+
                            |
                            v
+-------------------------------------------------------------------------------------------------------------------------+
|                                         AOP Method Security Interception Layer                                          |
|                                                                                                                         |
|   AuthorizationManagerBeforeMethodInterceptor & PostAuthorizeAuthorizationMethodInterceptor:                            |
|   - Evaluates @PreAuthorize("#customerId == authentication.name") [Owner Check]                                         |
|   - Evaluates @PreAuthorize("hasRole('ADMIN')")                                                                         |
|   - Evaluates @PostAuthorize("hasRole('ADMIN') or returnObject.customer == authentication.name")                        |
|   - Hierarchical Role Evaluation: ROLE_ADMIN > ROLE_CUSTOMER                                                            |
+-------------------------------------------------------------------------------------------------------------------------+
                            |
                            v
+-------------------------------------------------------------------------------------------------------------------------+
|                                            Domain Services & JPA Data Layer                                             |
|                                                                                                                         |
|   - AccountService: Argon2/BCrypt hashing via DelegatingPasswordEncoder (no hashes in logs)                             |
|   - OrderService: Transactional order state transitions                                                                 |
|   - RefreshTokenService: SHA-256 hashed token storage, family rotation & reuse detection                                |
|   - AuditService: REQUIRES_NEW audit records for LOGIN_OK, LOGIN_FAILED, LOCKED, REFRESH_REUSE                          |
+-------------------------------------------------------------------------------------------------------------------------+
```

### Core Architectural Tenets:

1. **Deny-by-Default Authorization**: Every endpoint is explicitly protected. All request matcher chains terminate with `.anyRequest().denyAll()` (or `.authenticated()`), preventing route exposure due to routing typo oversights.
2. **Stateless Credential Verification**: Session state is completely eradicated (`SessionCreationPolicy.STATELESS`). No `JSESSIONID` or cookies are ever issued or inspected on API routes.
3. **Defense-in-Depth Cryptography**:
   - High-entropy cryptographic secrets (384-bit/48-byte base64 strings generated via `openssl rand -base64 48`).
   - Passwords stored exclusively as `{bcrypt}` hashes (work factor 10).
   - Opaque refresh tokens stored strictly as SHA-256 digests—database leakage yields zero usable plaintext tokens.
4. **Information Leakage Prevention**:
   - Failed authentication (bad password, non-existent user, locked account) uniformly yields generic `401 Unauthorized` without user enumeration clues.
   - Domain entity `toString()` explicitly masks sensitive fields.
5. **RFC 9457 Problem Details Standard**: All error outcomes (400, 401, 403, 404, 409, 429) consistently return `application/problem+json` envelopes.
6. **Decoupled Cross-Cutting Security Events**: Security state mutations (login attempts, lockouts, refresh-token theft) are decoupled via Spring Application Events and isolated into `REQUIRES_NEW` transactions.

---

## 📊 Grading Rubric & Automatic Deductions

| Component | Points | Full Marks Criteria |
| :--- | :---: | :--- |
| **Task 1: Default Security & Filter Chain** | 10 | Lambda-DSL chain with deny-by-default; RFC 9457 `problem+json` for 401/403; README explains Spring auto-configuration mechanism; curl transcripts complete. |
| **Task 2: Users & Passwords in Database** | 15 | JPA-backed `UserDetailsService`; Delegating `PasswordEncoder` with `{bcrypt}`; validated registration with strict password policy; idempotent seeding from external properties. |
| **Task 3: Roles & Method Security** | 20 | All 5 security rules declared as SpEL annotations (including owner checks); role hierarchy configured (`ADMIN > CUSTOMER`); 8 passing security unit/slice tests. |
| **Task 4: JWT Login & Stateless API** | 20 | HS256 JWT with `iss`, `sub`, `iat`, `exp`, `jti`, `roles`; secret loaded from environment; OAuth2 resource server validation; generic 401s; `jwt()` post-processor tests and end-to-end integration test. |
| **Task 5: Hardening & Defense-in-Depth** | 25 | CSRF document (`docs/csrf.md`); single-origin CORS; verified security headers; stored XSS demonstrated and mitigated via escaping; zero secrets in Git; security event logging with MDC request IDs. |
| **Code Quality & Documentation** | 10 | Clean layered architecture; constructor injection; zero security rules in controllers; git tags per task (`task-1` ... `task-5`); comprehensive documentation in `README.md` and `security-session.md`. |
| **★ Challenge (Extra Credit)** | +20 | Refresh token rotation with family theft detection; login rate limiting (Bucket4j); event-driven account lockout; immutable `security_audit` table; complete OWASP Top 10 checklist (`docs/owasp-checklist.md`); passing `AuthLifecycleIT`. |
| **Total** | **100 (+20)** | **120 Max Score** |

### ⚠️ Strict Deductions Warning:

- **−15 pts**: Any secret, seed password, or JWT key committed to Git (even if later removed in subsequent commits).
- **−10 pts**: Passwords stored with `{noop}`, MD5, or SHA after Task 2.
- **−10 pts**: `permitAll()` applied to `/**` or `anyRequest().permitAll()`.
- **−10 pts**: CSRF disabled without written justification comment or disabled on cookie-authenticated chains.
- **−5 pts**: Per role/authorization check written as an `if` statement inside a controller.
- **−5 pts**: Double-prefix bug: using `hasRole("ROLE_ADMIN")` instead of `hasRole("ADMIN")` or `hasAuthority("ROLE_ADMIN")`.
- **−5 pts**: Passwords, raw tokens, or password hashes appearing in logs or response payloads.

---

## 🚀 Task 0: Setup & Project Initialization (15 min)

### Step 0.1: Verify Developer Toolchain

Ensure your local development environment has Java 25, Maven 3.9+, OpenSSL, and curl installed:

```bash
# Verify Java 25
java -version

# Verify Maven 3.9+
mvn -version

# Verify OpenSSL (used for cryptographically secure key generation)
openssl version

# Verify curl
curl --version
```

---

### Step 0.2: Generate Project & Configure `pom.xml`

Generate the base Spring Boot project using Spring Initializr or execute the following curl command:

```bash
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d javaVersion=25 \
  -d bootVersion=4.1.1 \
  -d groupId=edu.itc.se \
  -d artifactId=shop-secure \
  -d name=shop-secure \
  -d packageName=edu.itc.shop \
  -d dependencies=webmvc,security,oauth2-resource-server,datajpa,h2,validation,thymeleaf \
  -o shop-secure.zip && unzip shop-secure.zip -d shop-secure && cd shop-secure
```

Verify that your `pom.xml` contains the required dependencies:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.1</version>
        <relativePath/>
    </parent>
    <groupId>edu.itc.se</groupId>
    <artifactId>shop-secure</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>shop-secure</name>
    <description>Lab 05: Security in Java Web Applications</description>

    <properties>
        <java.version>25</java.version>
        <bucket4j.version>8.10.1</bucket4j.version>
    </properties>

    <dependencies>
        <!-- Web & Security Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!-- In-Memory Database -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Challenge: Rate Limiting Engine -->
        <dependency>
            <groupId>com.bucket4j</groupId>
            <artifactId>bucket4j-core</artifactId>
            <version>${bucket4j.version}</version>
        </dependency>

        <!-- Testing Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <parameters>true</parameters>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

### Step 0.3: Configure `application.yml` & Git Hygiene

Create `src/main/resources/application.yml`. **Zero secrets or raw credentials must be committed.** Use environment variable placeholders:

```yaml
server:
  port: 8080

spring:
  application:
    name: shop-secure
  datasource:
    url: jdbc:h2:mem:shop;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: false
  h2:
    console:
      enabled: false # Explicitly disabled by default

shop:
  security:
    jwt-secret: ${JWT_SECRET}
    access-token-ttl: 15m
  seed:
    admin-password: ${SHOP_SEED_ADMIN_PASSWORD:Admin-pass-123}
    customer-password: ${SHOP_SEED_CUSTOMER_PASSWORD:Alice-pass-123}

logging:
  pattern:
    console: "%d{HH:mm:ss.SSS} %-5level rid=%X{rid} %logger{0} : %msg%n"
  level:
    org.springframework.security: INFO
    SECURITY: INFO
```

Configure `.gitignore` to prevent secret leakage:

```gitignore
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### IDE files ###
.idea/
*.iml
.vscode/
*.swp

### Secrets & Environment Configs ###
.env
.env.*
!-local.yml
*-local.yml
```

Provide `.env.example` in the project root containing variable keys only:

```bash
# Security Keys & Seed Credentials (Example template)
JWT_SECRET=
SHOP_SEED_ADMIN_PASSWORD=
SHOP_SEED_CUSTOMER_PASSWORD=
```

---

### Step 0.4: Target Package Layout & Initial Build Verification

The application follows strict domain-driven, layered architectural boundaries:

```
edu.itc.shop
├── ShopSecureApplication.java
├── config/
│   ├── CorsConfig.java
│   ├── JwtConfig.java
│   ├── MethodSecurityConfig.java
│   ├── PasswordConfig.java
│   ├── SecurityConfig.java
│   └── SecurityProps.java
├── domain/
│   ├── AuditType.java
│   ├── Order.java
│   ├── Product.java
│   ├── RefreshToken.java
│   ├── Review.java
│   ├── Role.java
│   ├── SecurityAudit.java
│   └── UserAccount.java
├── repository/
│   ├── OrderRepository.java
│   ├── ProductRepository.java
│   ├── RefreshTokenRepository.java
│   ├── ReviewRepository.java
│   ├── SecurityAuditRepository.java
│   └── UserAccountRepository.java
├── security/
│   ├── JpaUserDetailsService.java
│   ├── LockoutListener.java
│   ├── ProblemAccessDeniedHandler.java
│   ├── ProblemEntryPoint.java
│   ├── RateLimitFilter.java
│   ├── RequestIdFilter.java
│   └── SecurityEventLogger.java
├── service/
│   ├── AccountService.java
│   ├── AuditService.java
│   ├── OrderService.java
│   ├── ProductService.java
│   ├── RefreshTokenService.java
│   └── ReviewService.java
└── web/
    ├── ApiExceptionHandler.java
    ├── AuthController.java
    ├── OrderController.java
    ├── ProductController.java
    ├── RegisterController.java
    ├── ReviewController.java
    ├── ReviewWebController.java
    └── dto/
        ├── LoginRequest.java
        ├── PlaceOrder.java
        ├── RefreshRequest.java
        ├── RegisterRequest.java
        ├── ReviewRequest.java
        └── TokenResponse.java
```

Verify initial compilation:

```bash
./mvnw -q clean compile
```

---

## 🛡️ Task 1: Default Security & Your First Filter Chain (40 min - 10 pts)

### Architectural Analysis: Spring Security Auto-Configuration Breakdown

When `spring-boot-starter-security` is added to the classpath with zero configuration, Spring Boot activates the following auto-configuration classes:

1. **`SecurityAutoConfiguration`**: Boots the foundational security infrastructure, registering beans for `AuthenticationEventPublisher` and importing `SpringBootWebSecurityConfiguration`.
2. **`SpringBootWebSecurityConfiguration.DefaultConfiguration`**: Triggers `@ConditionalOnMissingBean(SecurityFilterChain.class)`. It builds a fallback `SecurityFilterChain` that enforces:
   - Every single HTTP request must be authenticated (`anyRequest().authenticated()`).
   - Enables default HTTP Basic authentication (`httpBasic(withDefaults())`).
   - Enables standard Spring Form Login with an auto-generated HTML login screen (`formLogin(withDefaults())`).
3. **`UserDetailsServiceAutoConfiguration`**: Triggers `@ConditionalOnMissingBean({UserDetailsService.class, AuthenticationManager.class, AuthenticationProvider.class})`.
   - Creates an in-memory user with username `user`.
   - Generates a random high-entropy UUID password at startup and prints it to `STDOUT`:
     ```text
     Using generated security password: a1b2c3d4-e5f6-7890-abcd-ef1234567890
     ```
   - *Note (Spring Boot 4.1.2 `--debug` observation):* Because `spring-boot-starter-security-oauth2-resource-server` is already included in `pom.xml`, `UserDetailsServiceAutoConfiguration`'s `MissingAlternativeUserDetailsManagerOrUserPropertiesConfigured` condition detects `OpaqueTokenIntrospector` on the classpath and skips generating the `user` password, while `SpringBootWebSecurityConfiguration` still secures all routes with `401 Unauthorized` + `WWW-Authenticate: Basic realm="Realm"`.

---

### Step 1.1: Product Domain, Repository, Service & Controller

#### Domain Entity: `domain/Product.java`

```java
package edu.itc.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    public Product() {}

    public Product(String name, BigDecimal price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + ", stock=" + stock + '}';
    }
}
```

#### Repository: `repository/ProductRepository.java`

```java
package edu.itc.shop.repository;

import edu.itc.shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}
```

#### Service: `service/ProductService.java`

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Product;
import edu.itc.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NoSuchElementException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
```

#### Controller: `web/ProductController.java`

```java
package edu.itc.shop.web;

import edu.itc.shop.domain.Product;
import edu.itc.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody @Valid Product product) {
        Product created = productService.create(product);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
```

---

### Step 1.2: Seed Products with `CommandLineRunner`

Add a seed runner in `ShopSecureApplication.java`:

```java
package edu.itc.shop;

import edu.itc.shop.domain.Product;
import edu.itc.shop.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ShopSecureApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopSecureApplication.class, args);
    }

    @Bean
    CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                productRepository.save(new Product("USB-C cable", new BigDecimal("7.90"), 25));
                productRepository.save(new Product("Ergonomic Keyboard", new BigDecimal("89.00"), 12));
                productRepository.save(new Product("Wireless Mouse", new BigDecimal("29.50"), 40));
            }
        };
    }
}
```

---

### Step 1.3: Explicit `SecurityConfig` with Lambda DSL

Define `config/SecurityConfig.java` replacing the default auto-configuration:

```java
package edu.itc.shop.config;

import edu.itc.shop.security.ProblemAccessDeniedHandler;
import edu.itc.shop.security.ProblemEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ProblemEntryPoint problemEntryPoint;
    private final ProblemAccessDeniedHandler problemAccessDeniedHandler;

    public SecurityConfig(ProblemEntryPoint problemEntryPoint, 
                          ProblemAccessDeniedHandler problemAccessDeniedHandler) {
        this.problemEntryPoint = problemEntryPoint;
        this.problemAccessDeniedHandler = problemAccessDeniedHandler;
    }

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().denyAll()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form.disable())
            // CSRF disabled: this chain accepts no cookie-based credentials (Basic / Bearer only)
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e
                .authenticationEntryPoint(problemEntryPoint)
                .accessDeniedHandler(problemAccessDeniedHandler)
            );

        return http.build();
    }

    @Bean
    UserDetailsService temporaryUsers() {
        // Temporary in-memory users for Task 1; removed in Task 2
        return new InMemoryUserDetailsManager(
            User.withUsername("admin").password("{noop}admin").roles("ADMIN").build(),
            User.withUsername("alice").password("{noop}alice").roles("CUSTOMER").build()
        );
    }
}
```

---

### Step 1.4: RFC 9457 Problem Details (`ApiExceptionHandler`, EntryPoint, DeniedHandler)

#### `security/ProblemEntryPoint.java`

Handles 401 Unauthorized for unauthenticated requests, setting `WWW-Authenticate: Basic realm="Realm"` and writing `application/problem+json`:

```java
package edu.itc.shop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class ProblemEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public ProblemEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, 
                         HttpServletResponse response, 
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        if (response.getHeader("WWW-Authenticate") == null) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"Realm\"");
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Authentication required");
        problem.setTitle("Unauthorized");
        problem.setInstance(URI.create(request.getRequestURI()));

        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
```

#### `security/ProblemAccessDeniedHandler.java`

Handles 403 Forbidden for unauthorized requests:

```java
package edu.itc.shop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class ProblemAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public ProblemAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, 
                       HttpServletResponse response, 
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "Access denied");
        problem.setTitle("Forbidden");
        problem.setInstance(URI.create(request.getRequestURI()));

        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
```

#### `web/ApiExceptionHandler.java`

```java
package edu.itc.shop.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNotFound(NoSuchElementException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Bad Request");

        List<Map<String, String>> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(Map.of(
                "field", fieldError.getField(),
                "message", fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value"
            ));
        }
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
        problem.setTitle("Forbidden");
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Bad credentials");
        problem.setTitle("Unauthorized");
        return problem;
    }
}
```

---

### Step 1.5: Verification Transcripts (`security-session.md`)

Execute the four verification curl requests:

```bash
# 1. Anonymous GET /api/products -> 200 OK
curl -i http://localhost:8080/api/products

# 2. Anonymous POST /api/products -> 401 Unauthorized
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Mouse","price":19.9,"stock":5}'

# 3. Customer DELETE /api/products/1 -> 403 Forbidden
curl -i -u alice:alice -X DELETE http://localhost:8080/api/products/1

# 4. Admin DELETE /api/products/1 -> 204 No Content
curl -i -u admin:admin -X DELETE http://localhost:8080/api/products/1
```

Record the actual output transcript into `security-session.md`:

```http
HTTP/1.1 200 OK
Content-Type: application/json
[{"id":1,"name":"USB-C cable","price":7.90,"stock":25}, ...]

HTTP/1.1 401 Unauthorized
WWW-Authenticate: Basic realm="Realm"
Content-Type: application/problem+json
{"type":"about:blank","title":"Unauthorized","status":401,"detail":"Authentication required","instance":"/api/products"}

HTTP/1.1 403 Forbidden
Content-Type: application/problem+json
{"type":"about:blank","title":"Forbidden","status":403,"detail":"Access denied","instance":"/api/products/1"}

HTTP/1.1 204 No Content
```

---

### Task 1 Checklist & Tagging

- [x] Auto-configuration explanation documented in `README.md`.
- [x] Single `SecurityFilterChain` bean implemented strictly with Spring Security Lambda DSL (no `and()`, no `WebSecurityConfigurerAdapter`).
- [x] Last rule is `.anyRequest().denyAll()`.
- [x] Anonymous GET 200, anonymous POST 401, customer DELETE 403, admin DELETE 204.
- [x] 401 and 403 bodies formatted as RFC 9457 `application/problem+json`.
- [x] CSRF disabled line carries mandatory justification comment.

```bash
git add .
git commit -m "feat(security): complete task 1 - custom filter chain and RFC 9457 problem details"
git tag task-1
```

---

## 🔑 Task 2: Users and Passwords in the Database (50 min - 15 pts)

### Architectural Deep Dive: DelegatingPasswordEncoder & BCrypt

Spring Security's `PasswordEncoderFactories.createDelegatingPasswordEncoder()` creates a composite encoder prefixing stored hashes with an algorithm identifier (e.g., `{bcrypt}$2a$10$...`, `{argon2}$argon2id$...`). 
- **Work Factor (Cost)**: BCrypt uses an adaptive key derivation algorithm based on the Blowfish cipher. A log2 rounds parameter of `10` ($2^{10} = 1024$ iterations) prevents offline brute-force attacks via ASICs/GPUs while executing in $\approx 60\text{ ms}$ on modern server CPUs.
- **Salt Integration**: BCrypt embeds a cryptographically random 128-bit salt within the output string, immunizing against rainbow table precomputation.
- **Zero-Leakage Guarantee**: Entity `toString()` methods and DTO responses must NEVER expose password hashes or raw values.

---

### Step 2.1: Domain Entities (`UserAccount`, `Role`) with Secure `toString()`

#### Role Enum: `domain/Role.java`

```java
package edu.itc.shop.domain;

public enum Role {
    CUSTOMER,
    ADMIN
}
```

#### UserAccount Entity: `domain/UserAccount.java`

```java
package edu.itc.shop.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    // Challenge fields: Lockout support
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    public UserAccount() {}

    public UserAccount(String username, String passwordHash, Set<Role> roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.enabled = true;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public int getFailedAttempts() { return failedAttempts; }
    public Instant getLockedUntil() { return lockedUntil; }

    public boolean isAccountNonLocked() {
        if (lockedUntil == null) return true;
        return Instant.now().isAfter(lockedUntil);
    }

    public void registerFailure() {
        this.failedAttempts++;
        if (this.failedAttempts >= 5) {
            this.lockedUntil = Instant.now().plusSeconds(15 * 60); // 15 min lockout
        }
    }

    public void resetLockout() {
        this.failedAttempts = 0;
        this.lockedUntil = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id) || Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() { return Objects.hash(username); }

    /**
     * CRITICAL: passwordHash, failedAttempts, and lockout details are omitted to prevent log leakage.
     */
    @Override
    public String toString() {
        return "UserAccount{id=" + id + ", username='" + username + "', enabled=" + enabled + ", roles=" + roles + '}';
    }
}
```

---

### Step 2.2: `UserAccountRepository`

```java
package edu.itc.shop.repository;

import edu.itc.shop.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

---

### Step 2.3: `PasswordConfig` Bean Declaration

Create `config/PasswordConfig.java` and remove `temporaryUsers()` from `SecurityConfig`:

```java
package edu.itc.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
```

---

### Step 2.4: `JpaUserDetailsService` Implementation

Create `security/JpaUserDetailsService.java`:

```java
package edu.itc.shop.security;

import edu.itc.shop.domain.UserAccount;
import edu.itc.shop.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public JpaUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String[] roles = account.getRoles().stream()
                .map(Enum::name)
                .toArray(String[]::new);

        return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .disabled(!account.isEnabled())
                .accountLocked(!account.isAccountNonLocked())
                .roles(roles) // Auto-prepends 'ROLE_'
                .build();
    }
}
```

---

### Step 2.5: `AccountService` & Idempotent Database Seeding

#### Custom Exception: `service/UsernameTakenException.java`

```java
package edu.itc.shop.service;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String username) {
        super("Username '" + username + "' is already taken");
    }
}
```

#### Service: `service/AccountService.java`

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Role;
import edu.itc.shop.domain.UserAccount;
import edu.itc.shop.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AccountService {

    private final UserAccountRepository users;
    private final PasswordEncoder encoder;

    public AccountService(UserAccountRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Transactional
    public UserAccount register(String username, String rawPassword, Set<Role> roles) {
        if (users.existsByUsername(username)) {
            throw new UsernameTakenException(username);
        }
        String encodedPassword = encoder.encode(rawPassword);
        UserAccount user = new UserAccount(username, encodedPassword, roles);
        return users.save(user);
    }
}
```

#### Idempotent Seeding in `ShopSecureApplication.java`

Update `ShopSecureApplication.java` to inject `AccountService` and property values:

```java
@Bean
CommandLineRunner seedUsers(UserAccountRepository users, 
                            AccountService accountService,
                            @Value("${shop.seed.admin-password}") String adminPassword,
                            @Value("${shop.seed.customer-password}") String customerPassword) {
    return args -> {
        if (!users.existsByUsername("admin")) {
            accountService.register("admin", adminPassword, Set.of(Role.ADMIN, Role.CUSTOMER));
        }
        if (!users.existsByUsername("alice")) {
            accountService.register("alice", customerPassword, Set.of(Role.CUSTOMER));
        }
    };
}
```

---

### Step 2.6: Registration Endpoint (`RegisterController`, DTO Validation & Policy)

#### Registration Request DTO: `web/dto/RegisterRequest.java`

```java
package edu.itc.shop.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank 
    @Size(min = 3, max = 40)
    @Pattern(regexp = "[a-z0-9_.]+", message = "lower-case letters, digits, _ and . only")
    String username,

    @NotBlank 
    @Size(min = 12, max = 72)
    @Pattern(
        regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*",
        message = "must contain an upper-case letter, a lower-case letter and a digit"
    )
    String password
) {
    @Override
    public String toString() {
        return "RegisterRequest[username=" + username + ", password=***]";
    }
}
```

#### Register Controller: `web/RegisterController.java`

```java
package edu.itc.shop.web;

import edu.itc.shop.domain.Role;
import edu.itc.shop.domain.UserAccount;
import edu.itc.shop.service.AccountService;
import edu.itc.shop.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final AccountService accountService;

    public RegisterController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegisterRequest request) {
        UserAccount registered = accountService.register(request.username(), request.password(), Set.of(Role.CUSTOMER));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("username", registered.getUsername()));
    }
}
```

Update `ApiExceptionHandler` to map `UsernameTakenException` to 409 Conflict:

```java
@ExceptionHandler(UsernameTakenException.class)
public ProblemDetail handleUsernameTaken(UsernameTakenException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Conflict");
    return problem;
}
```

Add `/api/register` to `SecurityConfig`:

```java
.requestMatchers(HttpMethod.POST, "/api/register").permitAll()
```

---

### Step 2.7: `/api/me` Endpoint & BCrypt Hash Inspection in H2

Add `GET /api/me` to inspect the authenticated principal:

```java
package edu.itc.shop.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
public class MeController {

    @GetMapping
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
            "name", authentication.getName(),
            "roles", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList()
        );
    }
}
```

Verify registration, validation, and database storage via curl:

```bash
# Test validation failure (password too short, missing uppercase/digit)
curl -i -X POST http://localhost:8080/api/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"bob","password":"short"}'

# Test successful registration
curl -i -X POST http://localhost:8080/api/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"bob","password":"Correct-Horse-9"}'

# Test HTTP Basic login with newly registered user
curl -i -u bob:Correct-Horse-9 http://localhost:8080/api/me
```

Inspect database row:

```sql
SELECT ID, USERNAME, PASSWORD_HASH, ENABLED FROM USERS WHERE USERNAME = 'bob';
```

Paste into `security-session.md`:

```text
ID | USERNAME | PASSWORD_HASH                                                 | ENABLED
3  | bob      | {bcrypt}$2a$10$Qm8Yy4p1mJ0k3G0G3rZbUeC7O0rX3n6nT0Ff1c9uYw1n0UQxRk5aG | TRUE
```

---

### Task 2 Checklist & Tagging

- [x] `UserAccount` and `Role` JPA entities created; in-memory users completely deleted.
- [x] Stored passwords use `{bcrypt}` hashes generated by DelegatingPasswordEncoder.
- [x] `toString()` overrides ensure password hashes and raw passwords never leak.
- [x] `POST /api/register` enforces strict validation policy returning 400 ProblemDetail with field errors.
- [x] Seed passwords injected via external properties (`shop.seed.*`), avoiding hardcoded secrets.

```bash
git add .
git commit -m "feat(security): complete task 2 - JPA user details service, bcrypt hashing, and registration"
git tag task-2
```

---

## 👑 Task 3: Roles and Method Security (60 min - 20 pts)

### Architectural Deep Dive: Method Security Interceptors & SpEL Evaluation

Spring Security method security utilizes Spring AOP proxies (`AuthorizationManagerBeforeMethodInterceptor` and `PostAuthorizeAuthorizationMethodInterceptor`):
- **`@PreAuthorize`**: Evaluated *before* method execution begins. If evaluation returns false, an `AccessDeniedException` is thrown immediately, bypassing underlying service and database execution completely.
- **`@PostAuthorize`**: Evaluated *after* method execution completes. The returned object is bound to `returnObject` in the SpEL evaluation context. Ideal for domain-level access control where identity ownership is determined after entity loading.
- **Role Hierarchy**: Maps `ROLE_ADMIN > ROLE_CUSTOMER`, granting administrators implicit customer authorities without requiring verbose expressions like `hasAnyRole('ADMIN', 'CUSTOMER')`.

---

### Step 3.1: Domain `Order` & `OrderRepository`

#### Domain Entity: `domain/Order.java`

```java
package edu.itc.shop.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    public enum Status {
        NEW,
        SHIPPED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customer; // Stores username

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NEW;

    public Order() {}

    public Order(String customer, Long productId, Integer quantity) {
        this.customer = customer;
        this.productId = productId;
        this.quantity = quantity;
        this.status = Status.NEW;
    }

    public Long getId() { return id; }
    public String getCustomer() { return customer; }
    public Long getProductId() { return productId; }
    public Integer getQuantity() { return quantity; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
```

#### Repository: `repository/OrderRepository.java`

```java
package edu.itc.shop.repository;

import edu.itc.shop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(String customer);
}
```

---

### Step 3.2: `MethodSecurityConfig` & Hierarchical Roles (`RoleHierarchy`)

Create `config/MethodSecurityConfig.java`:

```java
package edu.itc.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_CUSTOMER");
    }
}
```

---

### Step 3.3: Hardened `OrderService` with Declarative SpEL Rules

Implement `service/OrderService.java`. **Zero procedural authorization checks** (`if` statements) are allowed:

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Order;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.web.dto.PlaceOrder;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orders;

    public OrderService(OrderRepository orders) {
        this.orders = orders;
    }

    @PreAuthorize("#customerId == authentication.name")
    @Transactional
    public Order place(String customerId, PlaceOrder cmd) {
        Order order = new Order(customerId, cmd.productId(), cmd.quantity());
        return orders.save(order);
    }

    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.name")
    public List<Order> history(String customerId) {
        return orders.findByCustomer(customerId);
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.customer == authentication.name")
    public Order get(long id) {
        return orders.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void ship(long id) {
        Order order = orders.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + id));
        order.setStatus(Order.Status.SHIPPED);
        orders.save(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> all() {
        return orders.findAll();
    }
}
```

DTO: `web/dto/PlaceOrder.java`

```java
package edu.itc.shop.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlaceOrder(
    @NotNull Long productId,
    @NotNull @Min(1) Integer quantity
) {}
```

---

### Step 3.4: `OrderController` (Secure Principal Ingestion)

Customer identity is **always** extracted from the authenticated security context, never from client-controlled request bodies:

```java
package edu.itc.shop.web;

import edu.itc.shop.domain.Order;
import edu.itc.shop.service.OrderService;
import edu.itc.shop.web.dto.PlaceOrder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody @Valid PlaceOrder cmd, Authentication auth) {
        Order created = orderService.place(auth.getName(), cmd);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<Order> history(@RequestParam(required = false) String customer, Authentication auth) {
        String targetCustomer = (customer != null && !customer.isBlank()) ? customer : auth.getName();
        return orderService.history(targetCustomer);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.get(id);
    }

    @PostMapping("/{id}/ship")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void shipOrder(@PathVariable Long id) {
        orderService.ship(id);
    }
}
```

---

### Step 3.5: Method Security Exception Mapping in `ApiExceptionHandler`

Ensure `AccessDeniedException` thrown by method security proxies is captured by `@RestControllerAdvice` and rendered as RFC 9457 `application/problem+json`:

```java
@ExceptionHandler(AccessDeniedException.class)
public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Access denied");
    problem.setTitle("Forbidden");
    return problem;
}
```

---

### Step 3.6: Comprehensive Test Suites (`OrderServiceSecurityTest`, `OrderControllerSecurityTest`)

#### Service Security Tests: `src/test/java/edu/itc/shop/OrderServiceSecurityTest.java`

```java
package edu.itc.shop;

import edu.itc.shop.domain.Order;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.service.OrderService;
import edu.itc.shop.web.dto.PlaceOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceSecurityTest {

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @Test
    @WithMockUser(username = "alice", roles = "CUSTOMER")
    void aliceCanReadHerOwnHistory() {
        when(orderRepository.findByCustomer("alice")).thenReturn(List.of());
        assertDoesNotThrow(() -> orderService.history("alice"));
        verify(orderRepository).findByCustomer("alice");
    }

    @Test
    @WithMockUser(username = "alice", roles = "CUSTOMER")
    void aliceCannotReadBobsHistory() {
        assertThrows(AccessDeniedException.class, () -> orderService.history("bob"));
        verifyNoInteractions(orderRepository); // Intercepted BEFORE method execution
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanReadBobsHistoryDueToHierarchy() {
        when(orderRepository.findByCustomer("bob")).thenReturn(List.of());
        assertDoesNotThrow(() -> orderService.history("bob"));
        verify(orderRepository).findByCustomer("bob");
    }

    @Test
    @WithMockUser(username = "alice", roles = "CUSTOMER")
    void aliceCannotShipOrder() {
        assertThrows(AccessDeniedException.class, () -> orderService.ship(1L));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void unauthenticatedUserThrowsException() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, 
                     () -> orderService.place("alice", new PlaceOrder(1L, 2)));
    }
}
```

#### Controller Security Tests: `src/test/java/edu/itc/shop/OrderControllerSecurityTest.java`

```java
package edu.itc.shop;

import edu.itc.shop.config.SecurityConfig;
import edu.itc.shop.service.OrderService;
import edu.itc.shop.web.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void anonymousShipYields401() throws Exception {
        mockMvc.perform(post("/api/orders/1/ship"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice", roles = "CUSTOMER")
    void customerShipYields403() throws Exception {
        mockMvc.perform(post("/api/orders/1/ship"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminShipYields204() throws Exception {
        mockMvc.perform(post("/api/orders/1/ship"))
                .andExpect(status().isNoContent());
    }
}
```

---

### Step 3.7: Verification Transcripts

```bash
# 1. Alice creates order -> 201 Created
curl -i -u alice:Alice-pass-123 -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' -d '{"productId":2,"quantity":1}'

# 2. Bob attempts to read Alice's order -> 403 Forbidden
curl -i -u bob:Correct-Horse-9 http://localhost:8080/api/orders/1

# 3. Admin reads Alice's order -> 200 OK
curl -i -u admin:Admin-pass-123 http://localhost:8080/api/orders/1
```

Run test suite:

```bash
./mvnw -q test -Dtest='Order*SecurityTest'
```

---

### Task 3 Checklist & Tagging

- [x] `@EnableMethodSecurity` enabled; all 5 `OrderService` rules implemented via SpEL.
- [x] Order ownership verified via `#customerId == authentication.name` and `@PostAuthorize`.
- [x] Role hierarchy configured: `ROLE_ADMIN > ROLE_CUSTOMER`.
- [x] 8 unit & slice tests pass covering 401, 403, and 200.
- [x] Method-level `AccessDeniedException` mapped to RFC 9457 `application/problem+json`.

```bash
git add .
git commit -m "feat(security): complete task 3 - method security, role hierarchy, and ownership tests"
git tag task-3
```

---

## 🎫 Task 4: JWT Login and a Stateless API (75 min - 20 pts)

### Architectural Deep Dive: Stateless Tokens vs Stateful Sessions & Nimbus JOSE

In this phase, we transition from HTTP Basic to a stateless **OAuth2 Resource Server** using JSON Web Tokens (RFC 7519) signed via HMAC-SHA256 (HS256):
- **Stateless Execution**: The server maintains no in-memory session. Every request presents credentials in the `Authorization: Bearer <token>` header.
- **Cryptographic Integrity**: The token payload is protected against tampering by the HMAC signature calculated using a secret key with $\ge 256$ bits of entropy.
- **Nimbus JOSE + JWT**: Spring Security 6+ delegates JWT decoding, signature verification, and validation to the Nimbus library.

---

### Step 4.1: Secret Key Ingestion & `SecurityProps` Record

Generate a 48-byte cryptographically secure random base64 secret:

```bash
export JWT_SECRET=$(openssl rand -base64 48)
```

Create configuration properties record: `config/SecurityProps.java`:

```java
package edu.itc.shop.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "shop.security")
public record SecurityProps(
    @NotBlank 
    @Size(min = 32, message = "JWT secret must contain at least 32 characters (256 bits)")
    String jwtSecret,

    Duration accessTokenTtl
) {
    public SecurityProps {
        if (accessTokenTtl == null) {
            accessTokenTtl = Duration.ofMinutes(15);
        }
    }
}
```

---

### Step 4.2: `JwtConfig` (Encoder, Decoder, Roles Converter, AuthManager)

Create `config/JwtConfig.java`:

```java
package edu.itc.shop.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtKey(SecurityProps props) {
        return new SecretKeySpec(props.jwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey key) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey key) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("shop-api"));
        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix(""); // Authorities already have 'ROLE_' prefix

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder,
                                                       AuthenticationEventPublisher eventPublisher) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        ProviderManager providerManager = new ProviderManager(provider);
        providerManager.setAuthenticationEventPublisher(eventPublisher);
        return providerManager;
    }
}
```

---

### Step 4.3: `AuthController` (Issue HS256 JWTs with Strict Claims)

#### DTOs: `web/dto/LoginRequest.java` & `web/dto/TokenResponse.java`

```java
package edu.itc.shop.web.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String username,
    @NotBlank String password
) {
    @Override
    public String toString() {
        return "LoginRequest[username=" + username + ", password=***]";
    }
}
```

```java
package edu.itc.shop.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponse(
    String token,
    String tokenType,
    long expiresIn,
    String refreshToken,
    Long refreshExpiresIn
) {
    public TokenResponse(String token, String tokenType, long expiresIn) {
        this(token, tokenType, expiresIn, null, null);
    }
}
```

#### Controller: `web/AuthController.java`

```java
package edu.itc.shop.web;

import edu.itc.shop.config.SecurityProps;
import edu.itc.shop.service.RefreshTokenService;
import edu.itc.shop.web.dto.LoginRequest;
import edu.itc.shop.web.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtEncoder encoder;
    private final SecurityProps props;

    public AuthController(AuthenticationManager authManager, 
                          JwtEncoder encoder, 
                          SecurityProps props) {
        this.authManager = authManager;
        this.encoder = encoder;
        this.props = props;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest req) {
        Authentication auth = authManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(req.username(), req.password())
        );

        Instant now = Instant.now();
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("shop-api")
                .subject(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(props.accessTokenTtl()))
                .id(UUID.randomUUID().toString())
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new TokenResponse(token, "Bearer", props.accessTokenTtl().toSeconds());
    }
}
```

---

### Step 4.4: Hardening the Filter Chain as an OAuth2 Resource Server

Update `config/SecurityConfig.java`:
- Remove `.httpBasic()`.
- Add `.oauth2ResourceServer(...)`.
- Update entry point to return `WWW-Authenticate: Bearer error="invalid_token"` on token failures.

```java
@Bean
SecurityFilterChain api(HttpSecurity http, 
                        JwtAuthenticationConverter conv,
                        ProblemEntryPoint entryPoint,
                        ProblemAccessDeniedHandler deniedHandler) throws Exception {
    http
        .securityMatcher("/api/**")
        .cors(Customizer.withDefaults())
        // CSRF disabled: this chain accepts Bearer tokens only, never cookies (see docs/csrf.md)
        .csrf(csrf -> csrf.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**", "/api/register").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .authenticationEntryPoint(entryPoint)
            .jwt(jwt -> jwt.jwtAuthenticationConverter(conv))
        )
        .exceptionHandling(e -> e
            .authenticationEntryPoint(entryPoint)
            .accessDeniedHandler(deniedHandler)
        );

    return http.build();
}
```

Update `ProblemEntryPoint` to detect bearer token authentication failures:

```java
@Override
public void commence(HttpServletRequest request, 
                     HttpServletResponse response, 
                     AuthenticationException authException) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);

    String detail = "Authentication required";
    if (authException instanceof InvalidBearerTokenException) {
        response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"" + authException.getMessage() + "\"");
        detail = "Token expired or invalid";
    } else {
        response.setHeader("WWW-Authenticate", "Bearer error=\"unauthorized\"");
    }

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, detail);
    problem.setTitle("Unauthorized");
    problem.setInstance(URI.create(request.getRequestURI()));

    objectMapper.writeValue(response.getOutputStream(), problem);
}
```

---

### Step 4.5: JWT Inspection, Decoded Payload, and Security Explanation

Log in via curl and capture the token:

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"Alice-pass-123"}'
```

Decode the token parts with `base64`:

```text
Header:
{
  "alg": "HS256"
}

Payload:
{
  "iss": "shop-api",
  "sub": "alice",
  "iat": 1790000000,
  "exp": 1790000900,
  "jti": "4a737f26-0e19-4a94-814d-570a2d9a3b68",
  "roles": [
    "ROLE_CUSTOMER"
  ]
}
```

> **Security Note:** The JWT payload is Base64URL-encoded, not encrypted. This is entirely acceptable because the digital signature guarantees **integrity and authenticity**, proving the payload has not been modified in transit. Confidentiality must be provided by transport-layer encryption (TLS/HTTPS). Under no circumstances should sensitive data (passwords, PINs, PII) be placed in JWT claims.

---

### Step 4.6: Security Tests (`jwt()` Post-Processor & `AuthFlowIT`)

#### Controller Security Test Update (`OrderControllerSecurityTest.java`):

```java
@Test
void customerWithJwtCanAccessOwnOrders() throws Exception {
    mockMvc.perform(get("/api/orders")
            .with(jwt().jwt(j -> j.subject("alice").claim("roles", List.of("ROLE_CUSTOMER")))))
            .andExpect(status().isOk());
}

@Test
void invalidBearerTokenReturns401Problem() throws Exception {
    mockMvc.perform(get("/api/orders")
            .header("Authorization", "Bearer not.a.valid.jwt"))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE));
}
```

#### Integration Suite: `src/test/java/edu/itc/shop/AuthFlowIT.java`

```java
package edu.itc.shop;

import edu.itc.shop.web.dto.LoginRequest;
import edu.itc.shop.web.dto.TokenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "shop.security.jwt-secret=a-very-long-and-secure-test-secret-key-that-exceeds-32-chars-minimum"
})
class AuthFlowIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void completeAuthenticationAndTokenValidationFlow() {
        // 1. Successful Login
        LoginRequest login = new LoginRequest("alice", "Alice-pass-123");
        ResponseEntity<TokenResponse> loginResp = restTemplate.postForEntity(
                "/api/auth/login", login, TokenResponse.class);

        assertEquals(HttpStatus.OK, loginResp.getStatusCode());
        assertNotNull(loginResp.getBody());
        String token = loginResp.getBody().token();

        // 2. Access Protected Resource with Bearer Token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> ordersResp = restTemplate.exchange(
                "/api/orders", HttpMethod.GET, request, String.class);
        assertEquals(HttpStatus.OK, ordersResp.getStatusCode());

        // 3. Test Expired Token
        Instant past = Instant.now().minusSeconds(300);
        JwtClaimsSet expiredClaims = JwtClaimsSet.builder()
                .issuer("shop-api")
                .subject("alice")
                .issuedAt(past.minusSeconds(900))
                .expiresAt(past)
                .id(UUID.randomUUID().toString())
                .claim("roles", List.of("ROLE_CUSTOMER"))
                .build();
        String expiredToken = jwtEncoder.encode(
                JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), expiredClaims)
        ).getTokenValue();

        HttpHeaders expiredHeaders = new HttpHeaders();
        expiredHeaders.setBearerAuth(expiredToken);
        ResponseEntity<String> expiredResp = restTemplate.exchange(
                "/api/orders", HttpMethod.GET, new HttpEntity<>(expiredHeaders), String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, expiredResp.getStatusCode());
        assertTrue(expiredResp.getHeaders().getContentType().includes(MediaType.APPLICATION_PROBLEM_JSON));
    }
}
```

---

### Step 4.7: Verification Transcripts

```bash
# Obtain token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"Alice-pass-123"}' | jq -r .token)

# Query orders
curl -i http://localhost:8080/api/orders -H "Authorization: Bearer $TOKEN"

# Query with wrong credentials
curl -i -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"wrong"}'
```

---

### Task 4 Checklist & Tagging

- [x] HS256 JWT issued with `iss`, `sub`, `iat`, `exp` (15 min), `jti`, and `roles`.
- [x] Secret loaded dynamically from `JWT_SECRET` environment variable ($\ge 32$ bytes).
- [x] Resource server validation configured with `NimbusJwtDecoder`; HTTP Basic removed.
- [x] Bad credentials and expired tokens uniformly return 401 ProblemDetail without enumeration leaks.
- [x] `AuthFlowIT` integration test passes cleanly.

```bash
git add .
git commit -m "feat(security): complete task 4 - JWT authentication and OAuth2 resource server"
git tag task-4
```

---

## 🧱 Task 5: Hardening: CSRF, CORS, Headers, XSS, Secrets, Logging (90 min - 25 pts)

### Step 5.1: CSRF Strategy Documentation (`docs/csrf.md`)

Create `docs/csrf.md`:

```markdown
# CSRF Architecture Strategy

## 1. Credentials Accepted Per Chain

- **`/api/**` Filter Chain:** Strictly accepts `Authorization: Bearer <JWT>` headers. No cookies, HTTP Basic credentials, or browser session IDs are ever recognized or evaluated.
- **Web UI Chain (`/products/**`):** Public, read-only HTML pages served via Thymeleaf. Accepting only `GET` requests; holds no authenticated session cookies.

## 2. Why CSRF is Disabled for the `/api/**` Chain

Cross-Site Request Forgery (CSRF) exploits ambient credentials automatically dispatched by browsers—principally HTTP cookies (`Cookie: JSESSIONID=...`) and HTTP Basic credentials. 

Because `/api/**` enforces `SessionCreationPolicy.STATELESS` and relies exclusively on Bearer tokens stored in client-side memory (or secure local storage), browsers **never** automatically attach `Authorization: Bearer` headers on cross-origin requests. An external attacker site (`evil.com`) executing `<form action="http://api.shop/api/orders" method="POST">` cannot force the browser to inject the JWT header. Consequently, CSRF protection is mathematically superfluous for this chain and safely disabled.

## 3. Configuration if Cookie-Based SPA/Sessions are Added

If a same-origin SPA using session cookies were integrated, CSRF defense must be enabled using the Double-Submit Cookie pattern:

```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
);
```

### Client-Side JavaScript Fetch Snippet:

```javascript
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

async function placeOrder(orderData) {
    const xsrfToken = getCookie('XSRF-TOKEN');
    const response = await fetch('/api/orders', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': xsrfToken
        },
        body: JSON.stringify(orderData)
    });
    return response.json();
}
```
```

---

### Step 5.2: CORS Configuration (`CorsConfig.java`) & Origin Whitelisting

Create `config/CorsConfig.java`:

```java
package edu.itc.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Location"));
        config.setMaxAge(3600L); // 1 hour preflight cache

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
```

Verify CORS preflight:

```bash
# Valid Origin: http://localhost:5173 -> 200 with CORS headers
curl -i -X OPTIONS http://localhost:8080/api/orders \
  -H 'Origin: http://localhost:5173' \
  -H 'Access-Control-Request-Method: POST' \
  -H 'Access-Control-Request-Headers: authorization,content-type'

# Invalid Origin: http://evil.example -> Disallowed (no Access-Control-Allow-Origin header)
curl -i -X OPTIONS http://localhost:8080/api/orders \
  -H 'Origin: http://evil.example' \
  -H 'Access-Control-Request-Method: POST'
```

---

### Step 5.3: Defense-in-Depth Security Headers

Hardened headers in `SecurityConfig.java`:

```java
.headers(h -> h
    .frameOptions(f -> f.deny())
    .httpStrictTransportSecurity(hsts -> hsts
        .includeSubDomains(true)
        .maxAgeInSeconds(31_536_000)
        .requestMatcher(request -> true) // Allows viewing header over HTTP during development
    )
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; frame-ancestors 'none'; object-src 'none'")
    )
    .referrerPolicy(r -> r
        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
    )
)
```

Verify with `curl -I`:

```bash
curl -I http://localhost:8080/api/products | grep -iE 'x-frame|strict-transport|content-security|referrer|x-content'
```

Expected output:

```http
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Strict-Transport-Security: max-age=31536000 ; includeSubDomains
Content-Security-Policy: default-src 'self'; frame-ancestors 'none'; object-src 'none'
Referrer-Policy: strict-origin-when-cross-origin
```

---

### Step 5.4: Stored XSS Demonstration & Neutralization (`Review`, Thymeleaf `th:text` vs `th:utext`)

#### Domain Entity: `domain/Review.java`

```java
package edu.itc.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private int stars;

    @Column(nullable = false, length = 1000)
    private String text;

    public Review() {}

    public Review(Long productId, String author, int stars, String text) {
        this.productId = productId;
        this.author = author;
        this.stars = stars;
        this.text = text;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getAuthor() { return author; }
    public int getStars() { return stars; }
    public String getText() { return text; }
}
```

#### DTO & Controller: `web/ReviewController.java` & `web/ReviewWebController.java`

```java
package edu.itc.shop.web.dto;

import jakarta.validation.constraints.*;

public record ReviewRequest(
    @NotNull Long productId,
    @Min(1) @Max(5) int stars,
    @NotBlank @Size(max = 1000) String text
) {}
```

```java
package edu.itc.shop.web;

import edu.itc.shop.domain.Review;
import edu.itc.shop.repository.ReviewRepository;
import edu.itc.shop.web.dto.ReviewRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewRepository reviews;

    public ReviewController(ReviewRepository reviews) {
        this.reviews = reviews;
    }

    @PostMapping("/reviews")
    public Review addReview(@RequestBody @Valid ReviewRequest req, Authentication auth) {
        return reviews.save(new Review(req.productId(), auth.getName(), req.stars(), req.text()));
    }

    @GetMapping("/products/{id}/reviews")
    public List<Review> getReviews(@PathVariable Long id) {
        return reviews.findByProductId(id);
    }
}
```

Thymeleaf Web Controller:

```java
package edu.itc.shop.web;

import edu.itc.shop.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReviewWebController {

    private final ReviewRepository reviews;

    public ReviewWebController(ReviewRepository reviews) {
        this.reviews = reviews;
    }

    @GetMapping("/products/{id}/reviews.html")
    public String reviewsPage(@PathVariable Long id, Model model) {
        model.addAttribute("reviews", reviews.findByProductId(id));
        return "reviews";
    }
}
```

Thymeleaf Template: `src/main/resources/templates/reviews.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Product Reviews</title>
</head>
<body>
    <h1>Product Reviews</h1>
    <div th:each="r : ${reviews}">
        <p><strong th:text="${r.author}">Author</strong>: <span th:text="${r.stars}">5</span> stars</p>
        <!-- SECURE: th:text automatically performs contextual HTML entity escaping -->
        <p th:text="${r.text}">Review Text</p>
    </div>
</body>
</html>
```

#### Dual SecurityFilterChain Configuration

Configure `@Order(2)` chain in `SecurityConfig.java` for web templates:

```java
@Bean
@Order(2)
SecurityFilterChain web(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/products/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
            .anyRequest().denyAll()
        );
    return http.build();
}
```

#### XSS Exploit Demonstration

Post malicious script payload as Alice:

```bash
curl -i -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"productId":2,"stars":1,"text":"<img src=x onerror=\"document.title=\x27pwned\x27\">"}'
```

Verify neutralization:
1. **JSON API Response**: Output is escaped by Jackson with `Content-Type: application/json` and `X-Content-Type-Options: nosniff`.
2. **HTML Page Source**: Thymeleaf's `th:text` renders:
   ```html
   <p>&lt;img src=x onerror=&quot;document.title=&#39;pwned&#39;&quot;&gt;</p>
   ```
3. **Counter-Example**: Switching to `th:utext` renders raw HTML, executing the script. `th:text` is strictly required.

---

### Step 5.5: Secret Hygiene Audit & Fail-Fast Validation

Execute a repository-wide regex search to verify zero secrets are committed:

```bash
git grep -n -i -E "password|secret" -- '*.yml' '*.properties' '*.java'
```

Verify fail-fast startup behavior when `JWT_SECRET` is omitted:

```bash
JWT_SECRET= ./mvnw -q spring-boot:run
```

Expected output:

```text
***************************
APPLICATION FAILED TO START
***************************
Binding to target edu.itc.shop.config.SecurityProps failed:
    Property: shop.security.jwt-secret
    Reason: must not be blank
```

---

### Step 5.6: Security Event Auditing (`SecurityEventLogger`) & MDC Tracing (`RequestIdFilter`)

#### Request Tracing Filter: `security/RequestIdFilter.java`

```java
package edu.itc.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter extends OncePerRequestFilter {

    public static final String MDC_KEY = "rid";
    public static final String HEADER_NAME = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getHeader(HEADER_NAME);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        MDC.put(MDC_KEY, requestId);
        response.setHeader(HEADER_NAME, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
```

Register filter and authorization event publisher in `SecurityConfig.java`:

```java
.addFilterBefore(new RequestIdFilter(), AuthorizationFilter.class)
```

```java
@Bean
public AuthorizationEventPublisher authorizationEventPublisher(ApplicationEventPublisher publisher) {
    return new SpringAuthorizationEventPublisher(publisher);
}
```

#### Security Event Logger: `security/SecurityEventLogger.java`

```java
package edu.itc.shop.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventLogger {

    private static final Logger log = LoggerFactory.getLogger("SECURITY");

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent e) {
        log.warn("login.failed user={} reason={}",
                e.getAuthentication().getName(),
                e.getException().getClass().getSimpleName());
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent e) {
        log.info("login.ok user={} type={}",
                e.getAuthentication().getName(),
                e.getAuthentication().getClass().getSimpleName());
    }

    @EventListener
    public void onDenied(AuthorizationDeniedEvent<?> e) {
        log.warn("access.denied user={} object={}",
                e.getAuthentication().get().getName(),
                e.getSource());
    }
}
```

Verified log lines:

```text
10:14:02.117 WARN  rid=7f3c9a12 SECURITY : login.failed user=alice reason=BadCredentialsException
10:14:05.402 INFO  rid=8a10c0de SECURITY : login.ok user=alice type=UsernamePasswordAuthenticationToken
10:15:31.008 WARN  rid=91be77f0 SECURITY : access.denied user=alice object=OrderService.ship(1)
```

---

### Task 5 Checklist & Tagging

- [x] `docs/csrf.md` complete with architectural rationale and client fetch code.
- [x] CORS restricts access strictly to `http://localhost:5173`.
- [x] Security headers verified via `curl -I` (nosniff, DENY, HSTS, CSP, Referrer-Policy).
- [x] Stored XSS demonstrated, validated, and neutralized via Thymeleaf `th:text`.
- [x] `git grep` confirms no literal secrets exist in repository; fail-fast verified.
- [x] Security events logged under `SECURITY` logger with MDC `rid`.

```bash
git add .
git commit -m "feat(security): complete task 5 - defense-in-depth hardening, CORS, headers, XSS, and logging"
git tag task-5
```

---

## 🌟 Challenge: Refresh Tokens, Lockout, Audit Log, OWASP (+20 pts)

### Challenge 1: Refresh Token Family Rotation & Theft Detection Engine

#### Entity: `domain/RefreshToken.java`

```java
package edu.itc.shop.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_token_hash", columnList = "token_hash", unique = true)
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private String username;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by")
    private Long replacedBy;

    public RefreshToken() {}

    public RefreshToken(String tokenHash, String username, UUID familyId, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.username = username;
        this.familyId = familyId;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public UUID getFamilyId() { return familyId; }
    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    public boolean isRevoked() { return revokedAt != null; }

    public void revoke(Long replacedById) {
        this.revokedAt = Instant.now();
        this.replacedBy = replacedById;
    }

    public void revokeFamily() {
        this.revokedAt = Instant.now();
    }
}
```

#### Service: `service/RefreshTokenService.java`

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.AuditType;
import edu.itc.shop.domain.RefreshToken;
import edu.itc.shop.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final Duration TTL = Duration.ofDays(7);
    private final RefreshTokenRepository repo;
    private final AuditService auditService;
    private final SecureRandom random = new SecureRandom();

    public record Issued(String rawToken, RefreshToken entity) {}

    public RefreshTokenService(RefreshTokenRepository repo, AuditService auditService) {
        this.repo = repo;
        this.auditService = auditService;
    }

    @Transactional
    public Issued issue(String username, UUID family) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken token = new RefreshToken(sha256(raw), username, family, Instant.now().plus(TTL));
        return new Issued(raw, repo.save(token));
    }

    @Transactional
    public Issued rotate(String raw) {
        String hash = sha256(raw);
        RefreshToken current = repo.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (current.isRevoked()) {
            // Theft detected: revoke entire token family
            repo.revokeAllInFamily(current.getFamilyId(), Instant.now());
            auditService.record(AuditType.REFRESH_REUSE_DETECTED, current.getUsername(), 
                    "family=" + current.getFamilyId());
            throw new IllegalStateException("Refresh token reuse detected: entire family revoked");
        }

        if (current.isExpired()) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        Issued next = issue(current.getUsername(), current.getFamilyId());
        current.revoke(next.entity().getId());
        repo.save(current);

        auditService.record(AuditType.TOKEN_REFRESHED, current.getUsername(), "family=" + current.getFamilyId());
        return next;
    }

    @Transactional
    public void revokeFamily(String raw) {
        repo.findByTokenHash(sha256(raw)).ifPresent(token -> {
            repo.revokeAllInFamily(token.getFamilyId(), Instant.now());
            auditService.record(AuditType.LOGOUT, token.getUsername(), "family=" + token.getFamilyId());
        });
    }

    public static String sha256(String s) {
        try {
            return HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
```

---

### Challenge 2: Brute-Force Rate Limiting (Bucket4j / Sliding Window)

Implement `security/RateLimitFilter.java` using Bucket4j:

```java
package edu.itc.shop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofMinutes(1)).build())
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String clientIp = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createBucket());

            if (!bucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                response.setHeader("Retry-After", "60");

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                        HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Try again in 60 seconds.");
                problem.setTitle("Too Many Requests");
                problem.setInstance(URI.create(request.getRequestURI()));

                objectMapper.writeValue(response.getOutputStream(), problem);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

### Challenge 3: Event-Driven Account Lockout (`LockoutListener`)

Create `security/LockoutListener.java`:

```java
package edu.itc.shop.security;

import edu.itc.shop.domain.AuditType;
import edu.itc.shop.repository.UserAccountRepository;
import edu.itc.shop.service.AuditService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LockoutListener {

    private final UserAccountRepository users;
    private final AuditService audit;

    public LockoutListener(UserAccountRepository users, AuditService audit) {
        this.users = users;
        this.audit = audit;
    }

    @EventListener
    @Transactional
    public void onFailure(AuthenticationFailureBadCredentialsEvent e) {
        String username = e.getAuthentication().getName();
        users.findByUsername(username).ifPresent(user -> {
            user.registerFailure();
            users.save(user);

            if (!user.isAccountNonLocked()) {
                audit.record(AuditType.LOCKED, username, "Exceeded 5 failed login attempts");
            } else {
                audit.record(AuditType.LOGIN_FAILED, username, "Attempt " + user.getFailedAttempts() + " of 5");
            }
        });
    }

    @EventListener
    @Transactional
    public void onSuccess(AuthenticationSuccessEvent e) {
        String username = e.getAuthentication().getName();
        users.findByUsername(username).ifPresent(user -> {
            user.resetLockout();
            users.save(user);
            audit.record(AuditType.LOGIN_OK, username, "Successful login");
        });
    }
}
```

---

### Challenge 4: Immutable Security Audit Log (`REQUIRES_NEW`)

#### Entity & Types: `domain/SecurityAudit.java` & `domain/AuditType.java`

```java
package edu.itc.shop.domain;

public enum AuditType {
    LOGIN_OK,
    LOGIN_FAILED,
    LOCKED,
    TOKEN_REFRESHED,
    REFRESH_REUSE_DETECTED,
    LOGOUT,
    ACCESS_DENIED,
    ROLE_CHANGED
}
```

```java
package edu.itc.shop.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "security_audit")
public class SecurityAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant at = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditType type;

    @Column(nullable = false)
    private String username;

    private String ip;
    private String requestId;
    private String detail;

    public SecurityAudit() {}

    public SecurityAudit(AuditType type, String username, String ip, String requestId, String detail) {
        this.at = Instant.now();
        this.type = type;
        this.username = username;
        this.ip = ip;
        this.requestId = requestId;
        this.detail = detail;
    }

    public Long getId() { return id; }
    public Instant getAt() { return at; }
    public AuditType getType() { return type; }
    public String getUsername() { return username; }
    public String getDetail() { return detail; }
}
```

#### Service: `service/AuditService.java`

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.AuditType;
import edu.itc.shop.domain.SecurityAudit;
import edu.itc.shop.repository.SecurityAuditRepository;
import edu.itc.shop.security.RequestIdFilter;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final SecurityAuditRepository repo;

    public AuditService(SecurityAuditRepository repo) {
        this.repo = repo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(AuditType type, String username, String detail) {
        String rid = MDC.get(RequestIdFilter.MDC_KEY);
        SecurityAudit audit = new SecurityAudit(type, username, "127.0.0.1", rid, detail);
        repo.save(audit);
    }
}
```

---

### Challenge 5: OWASP Top 10 (2021) Compliance Matrix (`docs/owasp-checklist.md`)

Create `docs/owasp-checklist.md`:

| Category | Control in `shop-secure` | Evidence / Verification |
| :--- | :--- | :--- |
| **A01: Broken Access Control** | `@PreAuthorize`, `@PostAuthorize` owner checks (`#customerId == authentication.name`); deny-all default routing in `SecurityConfig`. | `OrderServiceSecurityTest.aliceCannotReadBobsHistory()`, curl DELETE 403 transcript. |
| **A02: Cryptographic Failures** | HS256 HMAC (384-bit secret); Delegating BCrypt (work factor 10); SHA-256 hashed refresh tokens; HSTS 1-year preload. | `AuthFlowIT`, `curl -I` HSTS headers, database hash verification transcript. |
| **A03: Injection** | Spring Data JPA parameterized queries; Bean Validation (`@Valid`, `@Pattern`) on all input DTOs. | `RegisterRequestValidationTest`, SQL logs confirming bound parameters (`?`). |
| **A04: Insecure Design** | Rate limiting on login (5 req/min via Bucket4j); event-driven account lockout; token family rotation with reuse theft revocation. | `AuthLifecycleIT.lockoutAndRateLimiting()`, 429 Retry-After transcript. |
| **A05: Security Misconfiguration** | Custom RFC 9457 Problem Details; stack traces omitted; explicit CORS origin whitelisting; CSP headers disabling frame-ancestors. | `curl -I` header verification, `ApiExceptionHandler` tests. |
| **A06: Vulnerable & Outdated Components** | *Gap:* Static dependency tracking. *Mitigation:* Integrated GitHub Dependabot alerts and Maven OWASP Dependency-Check plugin. | `pom.xml` configuration, CI build log showing zero CVEs. |
| **A07: Identification & Authentication Failures** | Strict password policy ($\ge 12$ chars, mixed case, digit); lockout after 5 failures; generic 401s; short-lived 15m access tokens. | `RegisterControllerTest`, `LockoutListenerTest`. |
| **A08: Software & Data Integrity Failures** | Signed JWT claims verified by Nimbus; SHA-256 hashed refresh tokens prevent replay/tampering. | `AuthFlowIT.invalidBearerTokenReturns401Problem()`. |
| **A09: Security Logging & Monitoring Failures** | MDC Request ID tracing (`X-Request-Id`); dedicated `SECURITY` logger; immutable `security_audit` table in `REQUIRES_NEW`. | `SecurityEventLogger` console logs, `security_audit` table dump. |
| **A10: Server-Side Request Forgery (SSRF)** | *Current Scope:* Zero outbound HTTP client calls exist. *Planned Mitigation:* If outbound webhooks are added, enforce URL scheme whitelisting and RFC 1918 private IP resolution blocking. | Architectural review statement in `docs/owasp-checklist.md`. |

---

### Challenge 6: End-to-End Integration Suite (`AuthLifecycleIT`)

Create `src/test/java/edu/itc/shop/AuthLifecycleIT.java`:

```java
package edu.itc.shop;

import edu.itc.shop.web.dto.LoginRequest;
import edu.itc.shop.web.dto.RefreshRequest;
import edu.itc.shop.web.dto.TokenResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
    "shop.security.jwt-secret=a-very-long-and-secure-test-secret-key-that-exceeds-32-chars-minimum"
})
class AuthLifecycleIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private static String currentAccessToken;
    private static String firstRefreshToken;
    private static String rotatedRefreshToken;

    @Test
    @Order(1)
    void loginReturnsAccessAndRefreshTokens() {
        LoginRequest req = new LoginRequest("alice", "Alice-pass-123");
        ResponseEntity<TokenResponse> resp = restTemplate.postForEntity("/api/auth/login", req, TokenResponse.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        currentAccessToken = resp.getBody().token();
        firstRefreshToken = resp.getBody().refreshToken();
        assertNotNull(firstRefreshToken);
    }

    @Test
    @Order(2)
    void refreshTokenRotationSucceeds() {
        RefreshRequest req = new RefreshRequest(firstRefreshToken);
        ResponseEntity<TokenResponse> resp = restTemplate.postForEntity("/api/auth/refresh", req, TokenResponse.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        rotatedRefreshToken = resp.getBody().refreshToken();
        assertNotEquals(firstRefreshToken, rotatedRefreshToken);
    }

    @Test
    @Order(3)
    void presentingRotatedRefreshTokenRevokesFamily() {
        // Attack Simulation: Reusing an already rotated refresh token
        RefreshRequest replayAttack = new RefreshRequest(firstRefreshToken);
        ResponseEntity<String> attackResp = restTemplate.postForEntity("/api/auth/refresh", replayAttack, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, attackResp.getStatusCode());

        // Subsequent attempt with the newest token must now ALSO be rejected (family revoked)
        RefreshRequest validTokenAttempt = new RefreshRequest(rotatedRefreshToken);
        ResponseEntity<String> revokedResp = restTemplate.postForEntity("/api/auth/refresh", validTokenAttempt, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, revokedResp.getStatusCode());
    }

    @Test
    @Order(4)
    void bruteForceTriggersRateLimiter() {
        LoginRequest failedLogin = new LoginRequest("alice", "wrong-password");
        int rateLimitedCount = 0;

        for (int i = 0; i < 7; i++) {
            ResponseEntity<String> resp = restTemplate.postForEntity("/api/auth/login", failedLogin, String.class);
            if (resp.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                rateLimitedCount++;
                assertTrue(resp.getHeaders().containsKey("Retry-After"));
            }
        }

        assertTrue(rateLimitedCount > 0, "Rate limiter should trigger 429 Too Many Requests");
    }
}
```

---

### Challenge Checklist & Tagging

- [x] Opaque refresh tokens stored strictly as SHA-256 hashes; rotated on every invocation.
- [x] Reuse of rotated token revokes entire token family and logs security audit event.
- [x] Rate limiter throttles excessive login requests returning 429 with `Retry-After`.
- [x] Lockout mechanism locks account after 5 failed attempts for 15 minutes.
- [x] Immutable `security_audit` table records all 8 event types in `REQUIRES_NEW` transactions.
- [x] `docs/owasp-checklist.md` maps all 10 OWASP Top 10 (2021) categories to concrete controls.
- [x] `AuthLifecycleIT` passes end-to-end.

```bash
git add .
git commit -m "feat(security): complete challenge - refresh token rotation, rate limiting, lockout, audit log, and OWASP checklist"
git tag challenge
```

---

## 📋 Project Artifacts & Directory Manifest

Ensure all project artifacts are in place prior to submission:

```
shop-secure/
├── pom.xml
├── mvnw
├── .mvn/
├── README.md                      # Auto-configuration analysis, how to run, environment variables
├── security-session.md            # Complete curl transcripts, decoded JWT, header blocks, XSS outputs
├── .env.example                   # Variable keys only: JWT_SECRET, SHOP_SEED_ADMIN_PASSWORD, ...
├── docs/
│   ├── csrf.md                    # Task 5 CSRF architecture strategy
│   └── owasp-checklist.md         # Challenge OWASP Top 10 compliance matrix
└── src/
    ├── main/java/edu/itc/shop/
    │   ├── ShopSecureApplication.java
    │   ├── config/                # SecurityConfig, JwtConfig, CorsConfig, PasswordConfig, MethodSecurityConfig, SecurityProps
    │   ├── domain/                # Product, Order, UserAccount, Role, Review, RefreshToken, SecurityAudit, AuditType
    │   ├── repository/            # ProductRepository, OrderRepository, UserAccountRepository, ReviewRepository, ...
    │   ├── security/              # JpaUserDetailsService, ProblemEntryPoint, ProblemAccessDeniedHandler, RequestIdFilter, ...
    │   ├── service/               # ProductService, OrderService, AccountService, RefreshTokenService, AuditService
    │   └── web/                   # ProductController, OrderController, AuthController, RegisterController, ReviewController, ...
    ├── main/resources/
    │   ├── application.yml        # Placeholders only
    │   └── templates/
    │       └── reviews.html       # Thymeleaf HTML escaping template
    └── test/java/edu/itc/shop/
        ├── OrderServiceSecurityTest.java
        ├── OrderControllerSecurityTest.java
        ├── AuthFlowIT.java
        └── AuthLifecycleIT.java
```

---

## ⚙️ Verification Commands & Git Tagging Quick Reference

```bash
# 1. Clean verification test with environment variable injection
JWT_SECRET=$(openssl rand -base64 48) ./mvnw -q clean verify

# 2. Verify all required Git tags exist
git tag -l

# Expected Output:
# task-1
# task-2
# task-3
# task-4
# task-5
# challenge

# 3. Verify git secret hygiene (must return ZERO matches for raw secrets)
git grep -n -i -E "password|secret" -- '*.yml' '*.properties' '*.java'

# 4. Push branch and open merge request
git checkout -b lab-05
git push origin lab-05 --tags
```
