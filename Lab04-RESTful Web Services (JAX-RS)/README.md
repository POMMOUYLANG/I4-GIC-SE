# Lab 04: RESTful Web Services (JAX-RS)

## Complete Architecture & Implementation Guide (Tasks 0–5 & Extra-Credit Challenge)

> **Course:** Software Engineering — Chapter 04  
> **Topic:** Jakarta RESTful Web Services 4.0, Eclipse Jersey 4.0, & Embedded Grizzly Server  
> **Application:** `shop-rest` (Product Catalogue & Order Management REST API)  
> **Stack:** Java 25, Jakarta REST 4.0 (`jakarta.ws.rs-api`), Eclipse Jersey 4.0.0, Grizzly 2 HTTP Container, JSON-B (Yasson 3.0), JAXB 4.0.5, Hibernate Validator 8.x, SmallRye OpenAPI 3.1, JUnit 6.1.3, Apache Maven 3.9+  
> **Estimated Duration:** ≈ 5 hours (Tasks 0–5: 100 points, Challenge: +20 points extra credit)

---

## 📑 Table of Contents

- [Lab 04: RESTful Web Services (JAX-RS)](#lab-04-restful-web-services-jax-rs)
  - [Complete Architecture \& Implementation Guide (Tasks 0–5 \& Extra-Credit Challenge)](#complete-architecture--implementation-guide-tasks-05--extra-credit-challenge)
  - [📑 Table of Contents](#-table-of-contents)
  - [🎯 Lab Objectives \& Architectural Overview](#-lab-objectives--architectural-overview)
  - [📊 Grading Rubric \& Automatic Deductions](#-grading-rubric--automatic-deductions)
  - [🚀 Task 0: Setup \& Project Initialization (15 min)](#-task-0-setup--project-initialization-15-min)
    - [Step 0.1: Verify Developer Tooling](#step-01-verify-developer-tooling)
    - [Step 0.2: Project POM Configuration (`pom.xml`)](#step-02-project-pom-configuration-pomxml)
    - [Step 0.3: Target Directory Structure \& Package Layout](#step-03-target-directory-structure--package-layout)
    - [Step 0.4: Initial Compilation Verification](#step-04-initial-compilation-verification)
  - [📦 Task 1: First Resource and Server (30 min - 10 pts)](#-task-1-first-resource-and-server-30-min---10-pts)
    - [Step 1.1: Domain Record \& Repository Interface](#step-11-domain-record--repository-interface)
    - [Step 1.2: Thread-Safe In-Memory Repository](#step-12-thread-safe-in-memory-repository)
    - [Step 1.3: Server Bootstrap \& HK2 Dependency Injection (`Main.java`)](#step-13-server-bootstrap--hk2-dependency-injection-mainjava)
    - [Step 1.4: First REST Resource (`ProductResource.java`)](#step-14-first-rest-resource-productresourcejava)
    - [Step 1.5: Server Execution \& HTTP Verification](#step-15-server-execution--http-verification)
    - [Step 1.6: Architectural Note on Resource Statelessness](#step-16-architectural-note-on-resource-statelessness)
    - [Task 1 Checklist](#task-1-checklist)
  - [🔄 Task 2: Full CRUD with Status Codes, Headers, JSON and XML (45 min - 15 pts)](#-task-2-full-crud-with-status-codes-headers-json-and-xml-45-min---15-pts)
    - [Step 2.1: Unified JAXB / JSON-B Data Transfer Object (`ProductDto`)](#step-21-unified-jaxb--json-b-data-transfer-object-productdto)
    - [Step 2.2: Stock Mutation Payload (`StockChange`) \& Domain Exceptions](#step-22-stock-mutation-payload-stockchange--domain-exceptions)
    - [Step 2.3: Updating the Resource for Full CRUD \& Content Negotiation](#step-23-updating-the-resource-for-full-crud--content-negotiation)
    - [Step 2.4: Verification Transcript (`docs/task2-http.md`)](#step-24-verification-transcript-docstask2-httpmd)
    - [Task 2 Checklist](#task-2-checklist)
  - [🔍 Task 3: Parameters, Validation, and RFC 9457 Problem Details (60 min - 20 pts)](#-task-3-parameters-validation-and-rfc-9457-problem-details-60-min---20-pts)
    - [Step 3.1: Domain Value Object `Money` \& Custom `ParamConverter`](#step-31-domain-value-object-money--custom-paramconverter)
    - [Step 3.2: Aggregated Query Parameter Filter (`@BeanParam ProductFilter`)](#step-32-aggregated-query-parameter-filter-beanparam-productfilter)
    - [Step 3.3: Declarative Bean Validation Constraints](#step-33-declarative-bean-validation-constraints)
    - [Step 3.4: RFC 9457 Problem Details Representation (`Problem.java`)](#step-34-rfc-9457-problem-details-representation-problemjava)
    - [Step 3.5: Specific and Global Exception Mappers](#step-35-specific-and-global-exception-mappers)
    - [Step 3.6: Verification Transcript (`docs/task3-http.md`)](#step-36-verification-transcript-docstask3-httpmd)
    - [Task 3 Checklist](#task-3-checklist)
  - [🛡️ Task 4: Providers, Filters, and Interceptors (60 min - 20 pts)](#️-task-4-providers-filters-and-interceptors-60-min---20-pts)
    - [Step 4.1: Request Tracing \& Access Logging (`CorrelationFilter`)](#step-41-request-tracing--access-logging-correlationfilter)
    - [Step 4.2: Cross-Origin Resource Sharing (`CorsFilter`)](#step-42-cross-origin-resource-sharing-corsfilter)
    - [Step 4.3: Pre-Matching Authentication (`ApiKeyFilter`)](#step-43-pre-matching-authentication-apikeyfilter)
    - [Step 4.4: Dynamic Response Compression (`GzipWriterInterceptor`)](#step-44-dynamic-response-compression-gzipwriterinterceptor)
    - [Step 4.5: Verifying Filter Chain Ordering](#step-45-verifying-filter-chain-ordering)
    - [Step 4.6: Verification Transcript (`docs/task4-http.md`)](#step-46-verification-transcript-docstask4-httpmd)
    - [Task 4 Checklist](#task-4-checklist)
  - [⚡ Task 5: Pagination, ETag Caching, Versioning, and Sub-Resources (75 min - 25 pts)](#-task-5-pagination-etag-caching-versioning-and-sub-resources-75-min---25-pts)
    - [Step 5.1: Pagination with RFC 8288 Link Headers \& Total Count](#step-51-pagination-with-rfc-8288-link-headers--total-count)
    - [Step 5.2: HTTP Caching with ETag \& `Cache-Control` (304 Not Modified)](#step-52-http-caching-with-etag--cache-control-304-not-modified)
    - [Step 5.3: Optimistic Concurrency Control via `If-Match` (428 / 412)](#step-53-optimistic-concurrency-control-via-if-match-428--412)
    - [Step 5.4: Media Type Versioning (`application/vnd.shop.v2+json`)](#step-54-media-type-versioning-applicationvndshopv2json)
    - [Step 5.5: Hierarchical Sub-Resource Locator (`/products/{id}/reviews`)](#step-55-hierarchical-sub-resource-locator-productsidreviews)
    - [Step 5.6: Verification Transcript (`docs/task5-http.md`)](#step-56-verification-transcript-docstask5-httpmd)
    - [Task 5 Checklist](#task-5-checklist)
  - [🌟 Challenge: Orders API, HATEOAS, Idempotency, OpenAPI, & JerseyTest (+20 pts)](#-challenge-orders-api-hateoas-idempotency-openapi--jerseytest-20-pts)
    - [Challenge 1: Orders Sub-API Domain \& State Lifecycle](#challenge-1-orders-sub-api-domain--state-lifecycle)
    - [Challenge 2: HATEOAS Hypermedia Controls](#challenge-2-hateoas-hypermedia-controls)
    - [Challenge 3: Race-Free Idempotent POST Engine (SHA-256 Fingerprinting)](#challenge-3-race-free-idempotent-post-engine-sha-256-fingerprinting)
    - [Challenge 4: Automated OpenAPI 3.1 Generation \& Serving](#challenge-4-automated-openapi-31-generation--serving)
    - [Challenge 5: Comprehensive 13+ HTTP Status Code JerseyTest Suite](#challenge-5-comprehensive-13-http-status-code-jerseytest-suite)
    - [Challenge Checklist](#challenge-checklist)
  - [⚖️ Comprehensive Architectural Comparison: JAX-RS vs Spring MVC](#️-comprehensive-architectural-comparison-jax-rs-vs-spring-mvc)
    - [Side-by-Side Code Comparison: `GET /products/{id}`](#side-by-side-code-comparison-get-productsid)
    - [Side-by-Side Code Comparison: `POST /products`](#side-by-side-code-comparison-post-products)
    - [Key Differences Matrix (8 Architectural Dimensions)](#key-differences-matrix-8-architectural-dimensions)
  - [📋 Submission Instructions \& Verification Commands](#-submission-instructions--verification-commands)

---

## 🎯 Lab Objectives & Architectural Overview

In **Lab 04**, we step outside framework-specific abstractions and build a high-performance, specification-compliant RESTful Web Service using **Jakarta RESTful Web Services 4.0 (JAX-RS)**, implemented by **Eclipse Jersey 4.0** on an embedded **Grizzly 2 HTTP server**.

```
+-------------------------------------------------------------------------------------------------------+
|                                        HTTP CLIENT (curl / Browser)                                   |
+-------------------------------------------------------------------------------------------------------+
                                                     |
                                                     v
+-------------------------------------------------------------------------------------------------------+
|                                    Pre-Matching Request Filters                                        |
|   - ApiKeyFilter (@PreMatching, Priorities.AUTHENTICATION): 401 Unauthorized before route matching    |
|   - CorsFilter (@PreMatching): short-circuits OPTIONS preflight with 200 OK                            |
+-------------------------------------------------------------------------------------------------------+
                                                     |
                                                     v
+-------------------------------------------------------------------------------------------------------+
|                                        Jersey URL Routing / Matching                                  |
|   Evaluates @Path, Regex templates ({id: \\d+}), HTTP verbs (@GET, @POST...), and @Consumes / @Produces|
+-------------------------------------------------------------------------------------------------------+
                                                     |
                                                     v
+-------------------------------------------------------------------------------------------------------+
|                                    Post-Matching Request Filters                                       |
|   - CorrelationFilter (Priorities.USER): Injects/Echoes X-Correlation-Id, captures start nanoTime     |
+-------------------------------------------------------------------------------------------------------+
                                                     |
                                                     v
+-------------------------------------------------------------------------------------------------------+
|                                        Resource Endpoint Execution                                    |
|   ProductResource / ReviewResource / OrderResource                                                    |
|   - Injected with Singletons via HK2 AbstractBinder (ProductRepository, OrderService)                 |
|   - Validates input payloads via Jakarta Bean Validation (@Valid, @NotNull)                           |
|   - Content Negotiation: JSON-B (JSON) & JAXB (XML) via unified ProductDto                            |
|   - Evaluates conditional cache tags via Request.evaluatePreconditions(ETag)                          |
+-------------------------------------------------------------------------------------------------------+
                                                     |
                                                     v
+-------------------------------------------------------------------------------------------------------+
|                                 Writer Interceptors & Entity Writers                                  |
|   - GzipWriterInterceptor (@Compress): Compresses entity stream if Accept-Encoding contains gzip       |
|   - MessageBodyWriter (JSON-B / JAXB): Serializes DTO to application/json or application/xml           |
+-------------------------------------------------------------------------------------------------------+
                                                     |
                                                     v
+-------------------------------------------------------------------------------------------------------+
|                                        Container Response Filters                                     |
|   - CorsFilter: Injects Access-Control-Allow-Origin, Allow-Headers, Expose-Headers                     |
|   - CorrelationFilter: Emits X-Correlation-Id header, computes duration ms, logs structured access log|
+-------------------------------------------------------------------------------------------------------+
                                                     |
                                                     v
+-------------------------------------------------------------------------------------------------------+
|                                        Exception Mappers (RFC 9457)                                   |
|   - ValidationMapper: Maps ConstraintViolationException -> 400 application/problem+json               |
|   - ProblemMapper: Maps WebApplicationException & Uncaught Throwable -> 4xx/500 problem+json (No leaks)|
+-------------------------------------------------------------------------------------------------------+
```

### Key Architectural Tenets:

1. **Stateless Resource Semantics**: In JAX-RS, resource classes are request-scoped by default (created per HTTP request). They hold no mutable state across calls. Shared state is delegated to thread-safe singletons registered in HK2.
2. **Proper HTTP Status & Header Fidelity**:
   - `201 Created` with `Location` header pointing to the canonical URI of the created resource.
   - `204 No Content` for successful deletes.
   - `304 Not Modified` when client caching preconditions match (`If-None-Match`).
   - `400 Bad Request` with constraint error lists for malformed input.
   - `401 Unauthorized` with `WWW-Authenticate: ApiKey realm="shop"` for unauthenticated write mutations.
   - `404 Not Found` for nonexistent entities or invalid path templates.
   - `409 Conflict` for duplicate SKUs or invalid business state transitions.
   - `412 Precondition Failed` / `428 Precondition Required` for stale or missing ETags.
   - `415 Unsupported Media Type` when payload formats cannot be parsed.
   - `422 Unprocessable Content` for idempotency body mismatches.
3. **Transparent RFC 9457 Problem Details**: All errors (4xx and 5xx) emit `application/problem+json`. Stack traces are never leaked; internal server errors emit an opaque UUID reference ID for internal log correlation.
4. **Rich Content Negotiation**: Simultaneous support for JSON (`JSON-B`) and XML (`JAXB`) via unified DTOs, plus media-type-driven API versioning (`application/vnd.shop.v2+json`).
5. **Decoupled Cross-Cutting Concerns**: Authentication, tracing, CORS, and GZIP compression are encapsulated entirely within `@Provider` filters and interceptors without polluting resource classes.

---

## 📊 Grading Rubric & Automatic Deductions

| Component | Points | Full Marks Criteria |
| :--- | :---: | :--- |
| **Task 1: First Resource & Server** | 10 | Grizzly server bootstraps cleanly from `Main`; repository is a bound singleton injected via constructor; list and item endpoints function; non-numeric IDs and missing IDs return 404; stateless resource design documented. |
| **Task 2: Full CRUD, Status Codes, JSON & XML** | 15 | All endpoints return specified status codes and headers (`201` + `Location`, `204`, `404`, `409`, `415`); `ProductDto` seamlessly handles both JSON and XML; version attribute is hidden; business rules reside outside resource. |
| **Task 3: Parameters & Validation** | 20 | `@BeanParam` with `ProductFilter`; custom `ParamConverter` for `Money`; whitelisted sorting; declarative Bean Validation on bodies/params; every error returns RFC 9457 `application/problem+json`; 500 error hides stack trace. |
| **Task 4: Providers & Filters** | 20 | `CorrelationFilter` echoes/generates `X-Correlation-Id` and logs latency; `CorsFilter` short-circuits OPTIONS preflight; `@PreMatching` `ApiKeyFilter` enforces auth on writes; `@Compress` `GzipWriterInterceptor` honors `Accept-Encoding`. |
| **Task 5: Pagination, Caching, Versioning** | 25 | Validated `page`/`size` with `X-Total-Count` and RFC 8288 `Link` headers; `ETag` / `304 Not Modified` / `Cache-Control`; `If-Match` conditional PUT (`428` missing, `412` stale); vendor media type `v2` versioning; `/reviews` sub-resource locator. |
| **Code Quality & Documentation** | 10 | Clean separation of domain, DTO, params, error, filter layers; zero try/catch in resource classes; all curl transcripts in `docs/` complete and reproducible; comprehensive setup and testing guide in `README.md`. |
| **★ Challenge (Extra Credit)** | +20 | Orders sub-API with HATEOAS `_links`; race-free `Idempotency-Key` engine with SHA-256 body hash; OpenAPI 3.1 specification served at `/api/openapi`; 13+ status codes covered in `JerseyTest`; JAX-RS vs Spring MVC comparison. |
| **Total** | **100 (+20)** | |

### ⚠️ Strict Deductions Warning:

- **−5 pts**: Returning any `2xx` HTTP status code for an error condition.
- **−5 pts**: Any error response returned without an RFC 9457 `application/problem+json` body.
- **−5 pts**: Hard-coding `http://localhost:8080` in `Location` or `Link` headers instead of resolving dynamically via `UriInfo`.
- **−10 pts**: Placing business validation/domain mutation logic or `try/catch` blocks inside resource methods.
- **−5 pts**: Storing mutable per-request state in resource instance fields or using a non-thread-safe collection in a singleton repository.
- **−5 pts**: Passing query sort or filter fields to repository/stream without whitelist validation.
- **−10 pts**: Hard-coding or committing secrets/API keys to version control, or committing `target/` build directories.

---

## 🚀 Task 0: Setup & Project Initialization (15 min)

### Step 0.1: Verify Developer Tooling

Ensure that your environment meets the modern Java & Maven prerequisites:

```bash
# Verify Java version (JDK 25 recommended, JDK 21+ supported)
java -version

# Verify Apache Maven version (3.9.x+)
mvn -version

# Verify HTTP client (curl)
curl --version
```

---

### Step 0.2: Project POM Configuration (`pom.xml`)

Create `shop-rest/pom.xml`. The POM imports the official **Jersey BOM 4.0.0** and **JUnit BOM 6.1.3**, configuring Grizzly 2 container, JSON-B (Yasson), JAXB, Hibernate Bean Validation, and SmallRye OpenAPI:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>edu.itc</groupId>
    <artifactId>shop-rest</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.release>25</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <jersey.version>4.0.0</jersey.version>
        <junit.version>6.1.3</junit.version>
        <smallrye.openapi.version>3.13.0</smallrye.openapi.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Eclipse Jersey Bill of Materials -->
            <dependency>
                <groupId>org.glassfish.jersey</groupId>
                <artifactId>jersey-bom</artifactId>
                <version>${jersey.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- JUnit 5/6 Bill of Materials -->
            <dependency>
                <groupId>org.junit</groupId>
                <artifactId>junit-bom</artifactId>
                <version>${junit.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- Jakarta REST 4.0 Core & Embedded Grizzly 2 HTTP Container -->
        <dependency>
            <groupId>org.glassfish.jersey.containers</groupId>
            <artifactId>jersey-container-grizzly2-http</artifactId>
        </dependency>

        <!-- HK2 Dependency Injection Provider -->
        <dependency>
            <groupId>org.glassfish.jersey.inject</groupId>
            <artifactId>jersey-hk2</artifactId>
        </dependency>

        <!-- JSON-B (Eclipse Yasson 3.x) -->
        <dependency>
            <groupId>org.glassfish.jersey.media</groupId>
            <artifactId>jersey-media-json-binding</artifactId>
        </dependency>

        <!-- JAXB XML Support -->
        <dependency>
            <groupId>org.glassfish.jersey.media</groupId>
            <artifactId>jersey-media-jaxb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jaxb</groupId>
            <artifactId>jaxb-runtime</artifactId>
            <version>4.0.5</version>
        </dependency>

        <!-- Jakarta Bean Validation (Hibernate Validator Provider) -->
        <dependency>
            <groupId>org.glassfish.jersey.ext</groupId>
            <artifactId>jersey-bean-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.glassfish.expressly</groupId>
            <artifactId>expressly</artifactId>
            <version>5.0.0</version>
        </dependency>

        <!-- MicroProfile OpenAPI Annotations (Challenge) -->
        <dependency>
            <groupId>org.eclipse.microprofile.openapi</groupId>
            <artifactId>microprofile-openapi-api</artifactId>
            <version>4.0</version>
        </dependency>

        <!-- Testing Framework: JUnit Jupiter & Jersey Test Framework (Grizzly2) -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jersey.test-framework.providers</groupId>
            <artifactId>jersey-test-framework-provider-grizzly2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <release>25</release>
                </configuration>
            </plugin>

            <!-- Surefire Test Runner -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.3</version>
            </plugin>

            <!-- Exec Plugin for Running Grizzly Server -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <mainClass>edu.itc.shop.Main</mainClass>
                </configuration>
            </plugin>

            <!-- SmallRye OpenAPI Schema Generator Plugin (Challenge) -->
            <plugin>
                <groupId>io.smallrye</groupId>
                <artifactId>smallrye-open-api-maven-plugin</artifactId>
                <version>${smallrye.openapi.version}</version>
                <executions>
                    <execution>
                        <phase>process-classes</phase>
                        <goals>
                            <goal>generate-schema</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.outputDirectory}/META-INF</outputDirectory>
                            <schemaFilename>openapi</schemaFilename>
                            <infoTitle>shop-rest API</infoTitle>
                            <infoVersion>1.0.0</infoVersion>
                            <infoDescription>Jakarta REST 4.0 Product Catalogue &amp; Order API</infoDescription>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

### Step 0.3: Target Directory Structure & Package Layout

Create the following package hierarchy inside `shop-rest/`:

```
shop-rest/
├── pom.xml
├── README.md
├── .gitignore
├── docs/
│   ├── task1-http.md
│   ├── task2-http.md
│   ├── task3-http.md
│   ├── task4-http.md
│   ├── task5-http.md
│   └── challenge-http.md
└── src/
    ├── main/
    │   ├── java/edu/itc/shop/
    │   │   ├── Main.java                          # Server Bootstrap & HK2 Binding
    │   │   ├── domain/                            # Business Entities & Repositories (No JAX-RS / HTTP)
    │   │   │   ├── Product.java
    │   │   │   ├── Money.java
    │   │   │   ├── Review.java
    │   │   │   ├── Order.java
    │   │   │   ├── OrderStatus.java
    │   │   │   ├── ProductRepository.java
    │   │   │   ├── InMemoryProductRepository.java
    │   │   │   ├── ReviewRepository.java
    │   │   │   ├── OrderRepository.java
    │   │   │   └── OrderService.java
    │   │   └── api/                               # HTTP Web Layer (@Path, @Provider, DTOs)
    │   │       ├── ProductResource.java           # Primary Resource & Sub-resource Locators
    │   │       ├── ReviewResource.java            # Sub-Resource
    │   │       ├── OrderResource.java             # Orders & HATEOAS
    │   │       ├── OpenApiResource.java           # Serves openapi.yaml
    │   │       ├── dto/                           # Data Transfer Objects & Problem Details
    │   │       │   ├── ProductDto.java
    │   │       │   ├── ProductV2.java
    │   │       │   ├── StockChange.java
    │   │       │   ├── ReviewDto.java
    │   │       │   ├── CreateReviewDto.java
    │   │       │   ├── PlaceOrder.java
    │   │       │   ├── OrderDto.java
    │   │       │   └── Problem.java
    │   │       ├── params/                        # Query BeanParams & ParamConverters
    │   │       │   ├── ProductFilter.java
    │   │       │   └── MoneyConverterProvider.java
    │   │       ├── error/                         # Exception Mappers (RFC 9457)
    │   │       │   ├── ValidationMapper.java
    │   │       │   └── ProblemMapper.java
    │   │       └── filter/                        # Request/Response Filters & Interceptors
    │   │           ├── CorrelationFilter.java
    │   │           ├── CorsFilter.java
    │   │           ├── ApiKeyFilter.java
    │   │           ├── Compress.java
    │   │           └── GzipWriterInterceptor.java
    │   └── resources/
    │       └── META-INF/                          # Generated openapi.yaml
    └── test/
        └── java/edu/itc/shop/
            └── ShopApiTest.java                   # JerseyTest Suite (13+ HTTP Status Codes)
```

Configure `shop-rest/.gitignore`:

```gitignore
target/
!.mvn/wrapper/maven-wrapper.jar
.idea/
*.iml
.vscode/
.DS_Store
*.log
```

---

### Step 0.4: Initial Compilation Verification

Execute Maven to pre-download all dependencies and verify plugin configuration:

```bash
mvn clean compile -q
```

---

## 📦 Task 1: First Resource and Server (30 min - 10 pts)

### 🎯 Goal
Bootstrap Eclipse Jersey 4.0 inside an embedded Grizzly 2 HTTP server bound to `http://localhost:8080/api/`. Expose endpoints `GET /products` and `GET /products/{id}` backed by a concurrent in-memory repository. Ensure unknown IDs return a clean `404 Not Found`.

---

### Step 1.1: Domain Record & Repository Interface

Create `src/main/java/edu/itc/shop/domain/Product.java` as an immutable Java record:

```java
package edu.itc.shop.domain;

import java.math.BigDecimal;

public record Product(
        long id,
        String name,
        String sku,
        BigDecimal price,
        int stock,
        int version
) {
    public Product withId(long newId) {
        return new Product(newId, name, sku, price, stock, version);
    }

    public Product withVersion(int newVersion) {
        return new Product(id, name, sku, price, stock, newVersion);
    }

    public Product withStock(int newStock) {
        return new Product(id, name, sku, price, newStock, version + 1);
    }
}
```

Create `src/main/java/edu/itc/shop/domain/ProductRepository.java`:

```java
package edu.itc.shop.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(long id);
    Optional<Product> findBySku(String sku);
    Product save(Product product);
    boolean delete(long id);
}
```

---

### Step 1.2: Thread-Safe In-Memory Repository

Create `src/main/java/edu/itc/shop/domain/InMemoryProductRepository.java`. In accordance with concurrency fundamentals, multiple HTTP request threads access this repository concurrently. We back it with a `ConcurrentHashMap` and an `AtomicLong`:

```java
package edu.itc.shop.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryProductRepository implements ProductRepository {

    private final ConcurrentMap<Long, Product> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public InMemoryProductRepository() {
        // Seed initial catalogue items
        save(new Product(0, "Kettle", "KT-01", new BigDecimal("24.90"), 12, 0));
        save(new Product(0, "Mug", "MG-01", new BigDecimal("6.50"), 40, 0));
        save(new Product(0, "Tea", "TE-01", new BigDecimal("4.20"), 0, 0));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Product> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return storage.values().stream()
                .filter(p -> p.sku().equalsIgnoreCase(sku))
                .findFirst();
    }

    @Override
    public Product save(Product product) {
        if (product.id() == 0) {
            long newId = idGenerator.incrementAndGet();
            Product assigned = new Product(newId, product.name(), product.sku(), product.price(), product.stock(), 1);
            storage.put(newId, assigned);
            return assigned;
        } else {
            Product updated = new Product(product.id(), product.name(), product.sku(), product.price(), product.stock(), product.version() + 1);
            storage.put(product.id(), updated);
            return updated;
        }
    }

    @Override
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }
}
```

---

### Step 1.3: Server Bootstrap & HK2 Dependency Injection (`Main.java`)

Create `src/main/java/edu/itc/shop/Main.java`. We configure an embedded Grizzly server listening at `http://localhost:8080/api/`. Package scanning discovers all `@Path` and `@Provider` classes under `edu.itc.shop.api`, while an `AbstractBinder` binds our repository singleton to its interface:

```java
package edu.itc.shop;

import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import edu.itc.shop.domain.InMemoryProductRepository;
import edu.itc.shop.domain.ProductRepository;

public class Main {
    public static final URI BASE = URI.create("http://localhost:8080/api/");

    public static ResourceConfig config() {
        InMemoryProductRepository productRepository = new InMemoryProductRepository();
        return new ResourceConfig()
                .packages("edu.itc.shop.api")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(productRepository).to(ProductRepository.class);
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE, config());
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        System.out.println("shop-rest listening on " + BASE);
        Thread.currentThread().join();
    }
}
```

---

### Step 1.4: First REST Resource (`ProductResource.java`)

Create `src/main/java/edu/itc/shop/api/ProductResource.java`. Notice that the `{id}` path parameter uses a regular expression constraint `{id: \\d+}` so that non-digit requests like `/products/abc` are rejected by Jersey's router before reaching the method:

```java
package edu.itc.shop.api;

import java.util.List;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import edu.itc.shop.domain.Product;
import edu.itc.shop.domain.ProductRepository;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    private final ProductRepository repo;

    @Inject
    public ProductResource(ProductRepository repo) {
        this.repo = repo;
    }

    @GET
    public List<Product> list() {
        return repo.findAll();
    }

    @GET
    @Path("{id: \\d+}")
    public Product get(@PathParam("id") long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }
}
```

---

### Step 1.5: Server Execution & HTTP Verification

Start the application:

```bash
mvn -q compile exec:java
```

In another terminal, run curl verification:

```bash
# 1. Fetch all products
curl -i http://localhost:8080/api/products

# Expected HTTP/1.1 200 OK
# [{"id":1,"name":"Kettle","price":24.90,"sku":"KT-01","stock":12,"version":1}, ...]

# 2. Fetch single existing product
curl -i http://localhost:8080/api/products/2

# Expected HTTP/1.1 200 OK
# {"id":2,"name":"Mug","price":6.50,"sku":"MG-01","stock":40,"version":1}

# 3. Explicit NotFoundException for non-existent ID
curl -i http://localhost:8080/api/products/99

# Expected HTTP/1.1 404 Not Found

# 4. Unmatched path template for non-numeric ID
curl -i http://localhost:8080/api/products/abc

# Expected HTTP/1.1 404 Not Found
```

Save the full output with headers to `docs/task1-http.md`.

---

### Step 1.6: Architectural Note on Resource Statelessness

> **Why Resource Classes Have No Mutable Instance Fields:**  
> In Jakarta REST (JAX-RS), resource classes (`ProductResource`) are **request-scoped by default**—a brand new instance is instantiated by the container for every incoming HTTP request and garbage-collected once the response stream is closed. Even though instance fields cannot leak between distinct concurrent requests, resource classes must **never declare mutable state** (such as counters, caches, or client buffers). Holding mutable state in a resource creates false assumptions of persistence, introduces subtle race conditions if reconfigured as `@Singleton`, and violates the REST architectural constraint of stateless communication. All state must strictly live in thread-safe domain repositories.

---

### Task 1 Checklist

- [x] Grizzly server runs on port 8080 from `Main.java`.
- [x] `ProductRepository` is bound as an injected singleton via HK2 `AbstractBinder`.
- [x] `GET /api/products` returns 3 seeded products in JSON format.
- [x] `GET /api/products/2` returns product 2.
- [x] Non-existent ID `/products/99` and non-numeric path `/products/abc` both return `404 Not Found`.
- [x] HTTP transcript recorded in `docs/task1-http.md`.

---

## 🔄 Task 2: Full CRUD with Status Codes, Headers, JSON and XML (45 min - 15 pts)

### 🎯 Goal
Implement full CRUD endpoints (`POST`, `PUT`, `PATCH`, `DELETE`) with precise HTTP status codes (`201` + `Location`, `204`, `404`, `409`, `415`). Ensure that representations support both **JSON** (JSON-B) and **XML** (JAXB) via a decoupled DTO, while hiding internal metadata (`version`).

---

### Step 2.1: Unified JAXB / JSON-B Data Transfer Object (`ProductDto`)

Create `src/main/java/edu/itc/shop/api/dto/ProductDto.java`. Notice that JAXB requires a mutable POJO with a default no-argument constructor and public/gettable fields. JSON-B can also serialize and deserialize this POJO directly:

```java
package edu.itc.shop.api.dto;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import edu.itc.shop.domain.Product;

@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductDto {

    @XmlAttribute
    public long id;

    public String name;
    public String sku;
    public BigDecimal price;
    public int stock;

    public ProductDto() {
    }

    public static ProductDto from(Product p) {
        var d = new ProductDto();
        d.id = p.id();
        d.name = p.name();
        d.sku = p.sku();
        d.price = p.price();
        d.stock = p.stock();
        return d;
    }

    public Product toProduct(long id, int version) {
        return new Product(id, name, sku, price, stock, version);
    }
}
```

---

### Step 2.2: Stock Mutation Payload (`StockChange`) & Domain Exceptions

Create `src/main/java/edu/itc/shop/api/dto/StockChange.java`:

```java
package edu.itc.shop.api.dto;

public record StockChange(int delta) {
}
```

Create domain exception classes in `src/main/java/edu/itc/shop/domain/`:

```java
package edu.itc.shop.domain;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class DuplicateSkuException extends ClientErrorException {
    public DuplicateSkuException(String sku) {
        super("SKU " + sku + " already exists", Response.Status.CONFLICT);
    }
}
```

```java
package edu.itc.shop.domain;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class InsufficientStockException extends ClientErrorException {
    public InsufficientStockException(long id, int current, int delta) {
        super("Insufficient stock for product " + id + ": current=" + current + ", delta=" + delta, Response.Status.CONFLICT);
    }
}
```

---

### Step 2.3: Updating the Resource for Full CRUD & Content Negotiation

Update `ProductResource.java` to support both JSON and XML across all standard endpoints:

```java
package edu.itc.shop.api;

import java.net.URI;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import edu.itc.shop.api.dto.ProductDto;
import edu.itc.shop.api.dto.StockChange;
import edu.itc.shop.domain.DuplicateSkuException;
import edu.itc.shop.domain.InsufficientStockException;
import edu.itc.shop.domain.Product;
import edu.itc.shop.domain.ProductRepository;

@Path("/products")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ProductResource {

    private final ProductRepository repo;

    @Inject
    public ProductResource(ProductRepository repo) {
        this.repo = repo;
    }

    @GET
    public List<ProductDto> list() {
        return repo.findAll().stream().map(ProductDto::from).toList();
    }

    @GET
    @Path("{id: \\d+}")
    public ProductDto get(@PathParam("id") long id) {
        return repo.findById(id)
                .map(ProductDto::from)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

    @POST
    public Response create(ProductDto dto, @Context UriInfo uriInfo) {
        repo.findBySku(dto.sku).ifPresent(p -> {
            throw new DuplicateSkuException(dto.sku);
        });

        Product saved = repo.save(dto.toProduct(0, 0));
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(Long.toString(saved.id()))
                .build();
        return Response.created(location)
                .entity(ProductDto.from(saved))
                .build();
    }

    @PUT
    @Path("{id: \\d+}")
    public ProductDto update(@PathParam("id") long id, ProductDto dto) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));

        Product updated = repo.save(new Product(id, dto.name, dto.sku, dto.price, dto.stock, existing.version()));
        return ProductDto.from(updated);
    }

    @DELETE
    @Path("{id: \\d+}")
    public Response delete(@PathParam("id") long id) {
        if (!repo.delete(id)) {
            throw new NotFoundException("Product " + id + " not found");
        }
        return Response.noContent().build();
    }

    @PATCH
    @Path("{id: \\d+}/stock")
    @Consumes(MediaType.APPLICATION_JSON)
    public ProductDto changeStock(@PathParam("id") long id, StockChange change) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));

        int newStock = existing.stock() + change.delta();
        if (newStock < 0) {
            throw new InsufficientStockException(id, existing.stock(), change.delta());
        }

        Product updated = repo.save(existing.withStock(newStock));
        return ProductDto.from(updated);
    }
}
```

---

### Step 2.4: Verification Transcript (`docs/task2-http.md`)

Execute the complete set of CRUD and media type operations:

```bash
# 1. Create a product (POST) -> 201 Created with Location header
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Teapot","sku":"TP-01","price":31.00,"stock":5}'

# 2. Duplicate SKU -> 409 Conflict
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Teapot","sku":"TP-01","price":31.00,"stock":5}'

# 3. Content Negotiation (Accept: application/xml) -> 200 OK XML
curl -s http://localhost:8080/api/products/4 -H 'Accept: application/xml'

# 4. XML Roundtrip: Create product using XML payload
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/xml' \
  -d '<product><name>Bowl</name><sku>BW-01</sku><price>9.90</price><stock>3</stock></product>'

# 5. Decrement stock to negative -> 409 Conflict
curl -i -X PATCH http://localhost:8080/api/products/3/stock \
  -H 'Content-Type: application/json' \
  -d '{"delta":-1}'

# 6. Delete existing item -> 204 No Content
curl -i -X DELETE http://localhost:8080/api/products/4

# 7. Delete again -> 404 Not Found
curl -i -X DELETE http://localhost:8080/api/products/4

# 8. Post invalid media type (text/plain) -> 415 Unsupported Media Type
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: text/plain' \
  -d 'Teapot'
```

Save the transcripts to `docs/task2-http.md`.

---

### Task 2 Checklist

- [x] POST returns `201 Created` with dynamic `Location` built from `UriInfo`.
- [x] Duplicate SKU returns `409 Conflict`.
- [x] PUT returns `200 OK` or `404 Not Found`.
- [x] PATCH `/stock` returns `200 OK` or `409 Conflict` (when stock < 0).
- [x] DELETE returns `204 No Content` or `404 Not Found`.
- [x] JSON and XML representations supported for both consumption and production.
- [x] Unsupported `Content-Type: text/plain` rejected with `415 Unsupported Media Type`.
- [x] Internal entity `version` field is never exposed in `ProductDto`.

---

## 🔍 Task 3: Parameters, Validation, and RFC 9457 Problem Details (60 min - 20 pts)

### 🎯 Goal
Aggregate query parameters into a validated `@BeanParam`, parse custom currency strings (`"12.50 EUR"`) into a `Money` value object with a JAX-RS `ParamConverter`, validate bodies with Jakarta Bean Validation, and map all application errors to RFC 9457 `application/problem+json` without leaking stack traces.

---

### Step 3.1: Domain Value Object `Money` & Custom `ParamConverter`

Create `src/main/java/edu/itc/shop/domain/Money.java`:

```java
package edu.itc.shop.domain;

import java.math.BigDecimal;
import java.util.Objects;
import jakarta.ws.rs.BadRequestException;

public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
    }

    public static Money parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BadRequestException("Money string cannot be empty");
        }
        String[] parts = raw.trim().split("\\s+");
        if (parts.length != 2) {
            throw new BadRequestException("Cannot parse Money from '" + raw + "': expected format '<amount> <currency>'");
        }
        try {
            BigDecimal amt = new BigDecimal(parts[0]);
            String cur = parts[1].toUpperCase();
            return new Money(amt, cur);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Cannot parse Money from '" + raw + "'");
        }
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}
```

Create `src/main/java/edu/itc/shop/api/params/MoneyConverterProvider.java`:

```java
package edu.itc.shop.api.params;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import edu.itc.shop.domain.Money;

@Provider
public class MoneyConverterProvider implements ParamConverterProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (Money.class.equals(rawType)) {
            return (ParamConverter<T>) new ParamConverter<Money>() {
                @Override
                public Money fromString(String value) {
                    if (value == null || value.isBlank()) {
                        return null;
                    }
                    return Money.parse(value);
                }

                @Override
                public String toString(Money value) {
                    return value == null ? null : value.toString();
                }
            };
        }
        return null;
    }
}
```

---

### Step 3.2: Aggregated Query Parameter Filter (`@BeanParam ProductFilter`)

Create `src/main/java/edu/itc/shop/api/params/ProductFilter.java`:

```java
package edu.itc.shop.api.params;

import java.util.Comparator;
import java.util.Map;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Product;

public class ProductFilter {

    @QueryParam("q")
    public String q;

    @QueryParam("minPrice")
    public Money minPrice;

    @QueryParam("maxPrice")
    public Money maxPrice;

    @QueryParam("inStock")
    @DefaultValue("false")
    public boolean inStock;

    @QueryParam("sort")
    @DefaultValue("name,asc")
    @Pattern(regexp = "[a-z]+,(asc|desc)", message = "use field,asc or field,desc")
    public String sort;

    private static final Map<String, Comparator<Product>> COMPARATORS = Map.of(
            "name", Comparator.comparing(Product::name, String.CASE_INSENSITIVE_ORDER),
            "price", Comparator.comparing(Product::price),
            "stock", Comparator.comparingInt(Product::stock)
    );

    public String sortField() {
        return sort.split(",")[0];
    }

    public boolean descending() {
        return sort.endsWith(",desc");
    }

    public Comparator<Product> comparator() {
        String field = sortField();
        Comparator<Product> comp = COMPARATORS.get(field);
        if (comp == null) {
            throw new BadRequestException("Unknown sort field " + field);
        }
        return descending() ? comp.reversed() : comp;
    }

    public boolean matches(Product p) {
        if (q != null && !q.isBlank() && !p.name().toLowerCase().contains(q.toLowerCase())) {
            return false;
        }
        if (inStock && p.stock() <= 0) {
            return false;
        }
        if (minPrice != null) {
            if (!"EUR".equalsIgnoreCase(minPrice.currency())) {
                throw new BadRequestException("Only EUR currency comparisons supported");
            }
            if (p.price().compareTo(minPrice.amount()) < 0) {
                return false;
            }
        }
        if (maxPrice != null) {
            if (!"EUR".equalsIgnoreCase(maxPrice.currency())) {
                throw new BadRequestException("Only EUR currency comparisons supported");
            }
            if (p.price().compareTo(maxPrice.amount()) > 0) {
                return false;
            }
        }
        return true;
    }
}
```

---

### Step 3.3: Declarative Bean Validation Constraints

Update `ProductDto.java` with validation annotations:

```java
    @NotBlank(message = "must not be blank")
    @Size(max = 80, message = "must be at most 80 characters")
    public String name;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "[A-Z]{2}-\\d{2}", message = "must match \"[A-Z]{2}-\\d{2}\"")
    public String sku;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0.01", message = "must be greater than or equal to 0.01")
    @Digits(integer = 8, fraction = 2, message = "must have at most 8 integer digits and 2 decimals")
    public BigDecimal price;

    @PositiveOrZero(message = "must be greater than or equal to 0")
    public int stock;
```

Update `StockChange.java`:

```java
package edu.itc.shop.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record StockChange(
        @Min(value = -1000, message = "delta cannot be less than -1000")
        @Max(value = 1000, message = "delta cannot exceed 1000")
        int delta
) {}
```

In `ProductResource.java`, annotate body arguments with `@Valid @NotNull`:
```java
@POST
public Response create(@Valid @NotNull ProductDto dto, @Context UriInfo uriInfo) { ... }

@PUT
@Path("{id: \\d+}")
public ProductDto update(@PathParam("id") long id, @Valid @NotNull ProductDto dto) { ... }

@PATCH
@Path("{id: \\d+}/stock")
@Consumes(MediaType.APPLICATION_JSON)
public ProductDto changeStock(@PathParam("id") long id, @Valid @NotNull StockChange change) { ... }
```

---

### Step 3.4: RFC 9457 Problem Details Representation (`Problem.java`)

Create `src/main/java/edu/itc/shop/api/dto/Problem.java`:

```java
package edu.itc.shop.api.dto;

import java.util.List;
import java.util.Map;

public record Problem(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        List<Map<String, String>> errors
) {
    public static final String MEDIA_TYPE = "application/problem+json";

    public Problem(String type, String title, int status, String detail, String instance) {
        this(type, title, status, detail, instance, null);
    }
}
```

---

### Step 3.5: Specific and Global Exception Mappers

Create `src/main/java/edu/itc/shop/api/error/ValidationMapper.java`:

```java
package edu.itc.shop.api.error;

import java.util.List;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import edu.itc.shop.api.dto.Problem;

@Provider
public class ValidationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        List<Map<String, String>> errors = ex.getConstraintViolations().stream()
                .map(v -> Map.of(
                        "field", lastNode(v.getPropertyPath()),
                        "message", v.getMessage()
                ))
                .toList();

        Problem problem = new Problem(
                "about:blank",
                "Invalid request",
                400,
                "Validation failed",
                uriInfo.getRequestUri().getPath(),
                errors
        );

        return Response.status(400)
                .type(Problem.MEDIA_TYPE)
                .entity(problem)
                .build();
    }

    static String lastNode(Path path) {
        String name = null;
        for (Path.Node n : path) {
            name = n.getName();
        }
        return name;
    }
}
```

Create `src/main/java/edu/itc/shop/api/error/ProblemMapper.java`:

```java
package edu.itc.shop.api.error;

import java.lang.System.Logger.Level;
import java.util.UUID;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import edu.itc.shop.api.dto.Problem;

@Provider
public class ProblemMapper implements ExceptionMapper<Throwable> {

    private static final System.Logger LOG = System.getLogger("api");

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable ex) {
        String path = (uriInfo != null && uriInfo.getRequestUri() != null)
                ? uriInfo.getRequestUri().getPath()
                : "/";

        if (ex instanceof WebApplicationException w) {
            int status = w.getResponse().getStatus();
            Response.Status statusEnum = Response.Status.fromStatusCode(status);
            String title = (statusEnum != null) ? statusEnum.getReasonPhrase() : "Error";
            String detail = ex.getMessage();

            Problem problem = new Problem("about:blank", title, status, detail, path);
            return Response.status(status)
                    .type(Problem.MEDIA_TYPE)
                    .entity(problem)
                    .build();
        }

        // Uncaught server exception: assign opaque UUID reference id, never leak stack trace
        String refId = UUID.randomUUID().toString();
        LOG.log(Level.ERROR, "Unhandled server error [" + refId + "] at " + path, ex);

        Problem problem = new Problem(
                "about:blank",
                "Internal Server Error",
                500,
                "reference " + refId,
                path
        );

        return Response.status(500)
                .type(Problem.MEDIA_TYPE)
                .entity(problem)
                .build();
    }
}
```

---

### Step 3.6: Verification Transcript (`docs/task3-http.md`)

```bash
# 1. Filtered and sorted query
curl -s 'http://localhost:8080/api/products?minPrice=5%20EUR&inStock=true&sort=price,desc'

# 2. ParamConverter failure on invalid Money format -> 400 Bad Request
curl -i 'http://localhost:8080/api/products?minPrice=abc'

# 3. Invalid sort field -> 400 Bad Request
curl -i 'http://localhost:8080/api/products?sort=colour,asc'

# 4. Invalid POST body with Bean Validation constraint violations -> 400 Bad Request
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"","sku":"kettle","price":0,"stock":-1}'

# 5. Non-existent resource -> 404 Problem JSON
curl -i http://localhost:8080/api/products/99
```

Record the complete transcripts in `docs/task3-http.md`.

---

### Task 3 Checklist

- [x] Custom `Money` value object converted via `ParamConverterProvider`.
- [x] Query parameters encapsulated in `@BeanParam ProductFilter` with sorting whitelist.
- [x] Jakarta Bean Validation enforces constraints on DTO properties and mutation requests.
- [x] `ValidationMapper` outputs detailed `field` and `message` lists under `errors`.
- [x] `ProblemMapper` translates all exceptions to RFC 9457 `application/problem+json`.
- [x] Unhandled 500 errors print opaque UUID reference IDs and conceal internal stack traces.

---

## 🛡️ Task 4: Providers, Filters, and Interceptors (60 min - 20 pts)

### 🎯 Goal
Attach decoupled cross-cutting infrastructure:
1. **Correlation Tracking & Access Logging** (`CorrelationFilter`).
2. **CORS Handling & Preflight Short-Circuiting** (`CorsFilter`).
3. **Pre-Matching API Key Security** (`ApiKeyFilter`).
4. **Name-Bound GZIP Stream Compression** (`GzipWriterInterceptor` with `@Compress`).

---

### Step 4.1: Request Tracing & Access Logging (`CorrelationFilter`)

Create `src/main/java/edu/itc/shop/api/filter/CorrelationFilter.java`. We calculate request duration using `System.nanoTime()` passed through container properties:

```java
package edu.itc.shop.api.filter;

import java.io.IOException;
import java.lang.System.Logger.Level;
import java.util.UUID;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class CorrelationFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String START_TIME = "shop.startNanoTime";
    public static final String HEADER_CORRELATION = "X-Correlation-Id";
    private static final System.Logger LOG = System.getLogger("api");

    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        String corrId = req.getHeaderString(HEADER_CORRELATION);
        if (corrId == null || corrId.isBlank()) {
            corrId = UUID.randomUUID().toString();
        }
        req.setProperty(HEADER_CORRELATION, corrId);
        req.setProperty(START_TIME, System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        String corrId = (String) req.getProperty(HEADER_CORRELATION);
        if (corrId != null) {
            res.getHeaders().putSingle(HEADER_CORRELATION, corrId);
        }

        Long start = (Long) req.getProperty(START_TIME);
        long elapsedMs = (start != null) ? (System.nanoTime() - start) / 1_000_000 : 0;

        LOG.log(Level.INFO, "{0} {1} -> {2} [{3}] {4} ms",
                req.getMethod(),
                req.getUriInfo().getRequestUri().getPath(),
                res.getStatus(),
                corrId,
                elapsedMs
        );
    }
}
```

---

### Step 4.2: Cross-Origin Resource Sharing (`CorsFilter`)

Create `src/main/java/edu/itc/shop/api/filter/CorsFilter.java`. Notice that preflight `OPTIONS` requests are intercepted at `@PreMatching` to return immediately without routing to a resource method:

```java
package edu.itc.shop.api.filter;

import java.io.IOException;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public static final String ALLOWED_ORIGIN = "http://localhost:5173";

    @Override
    public void filter(ContainerRequestContext req) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            req.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) throws IOException {
        res.getHeaders().putSingle("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        res.getHeaders().putSingle("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE");
        res.getHeaders().putSingle("Access-Control-Allow-Headers", "Content-Type,X-Api-Key,X-Correlation-Id,If-Match");
        res.getHeaders().putSingle("Access-Control-Expose-Headers", "Location,ETag,Link,X-Total-Count,X-Correlation-Id");
    }
}
```

---

### Step 4.3: Pre-Matching Authentication (`ApiKeyFilter`)

Create `src/main/java/edu/itc/shop/api/filter/ApiKeyFilter.java`. Safe methods (`GET`, `HEAD`, `OPTIONS`) remain public, while write operations require `X-Api-Key`:

```java
package edu.itc.shop.api.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import edu.itc.shop.api.dto.Problem;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class ApiKeyFilter implements ContainerRequestFilter {

    private static final Set<String> SAFE = Set.of("GET", "HEAD", "OPTIONS");

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        if (SAFE.contains(ctx.getMethod())) {
            return;
        }

        String rawKeys = System.getProperty("shop.api.keys", "demo-123");
        Set<String> validKeys = Arrays.stream(rawKeys.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        String clientKey = ctx.getHeaderString("X-Api-Key");
        if (clientKey != null && validKeys.contains(clientKey)) {
            return;
        }

        Problem problem = new Problem(
                "about:blank",
                "Unauthorized",
                401,
                "Missing or invalid X-Api-Key",
                ctx.getUriInfo().getRequestUri().getPath(),
                null
        );

        ctx.abortWith(Response.status(401)
                .header("WWW-Authenticate", "ApiKey realm=\"shop\"")
                .type(Problem.MEDIA_TYPE)
                .entity(problem)
                .build());
    }
}
```

---

### Step 4.4: Dynamic Response Compression (`GzipWriterInterceptor`)

Define the `@NameBinding` annotation `src/main/java/edu/itc/shop/api/filter/Compress.java`:

```java
package edu.itc.shop.api.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.ws.rs.NameBinding;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Compress {
}
```

Create `src/main/java/edu/itc/shop/api/filter/GzipWriterInterceptor.java`:

```java
package edu.itc.shop.api.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

@Provider
@Compress
public class GzipWriterInterceptor implements WriterInterceptor {

    @Context
    private HttpHeaders requestHeaders;

    @Override
    public void aroundWriteTo(WriterInterceptorContext ctx) throws IOException, WebApplicationException {
        String acceptEncoding = requestHeaders.getHeaderString(HttpHeaders.ACCEPT_ENCODING);
        if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
            ctx.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, "gzip");
            ctx.getHeaders().putSingle(HttpHeaders.VARY, HttpHeaders.ACCEPT_ENCODING);
            ctx.getHeaders().remove(HttpHeaders.CONTENT_LENGTH);

            final OutputStream originalStream = ctx.getOutputStream();
            GZIPOutputStream gzipStream = new GZIPOutputStream(originalStream);
            ctx.setOutputStream(gzipStream);
            try {
                ctx.proceed();
            } finally {
                gzipStream.finish();
            }
        } else {
            ctx.proceed();
        }
    }
}
```

Annotate the `list()` method in `ProductResource`:
```java
    @GET
    @Compress
    public List<ProductDto> list() { ... }
```

---

### Step 4.5: Verifying Filter Chain Ordering

To verify that `@PreMatching` `ApiKeyFilter` executes before post-matching `CorrelationFilter`:
1. Temporarily place `System.out.println(getClass().getSimpleName() + " running");` inside each filter.
2. Send a POST request without `X-Api-Key`.
3. Confirm that `ApiKeyFilter` fires first, aborts the request, and passes control to response filters. Remove print statements before committing.

---

### Step 4.6: Verification Transcript (`docs/task4-http.md`)

```bash
# 1. POST without API key -> 401 Unauthorized with WWW-Authenticate header
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Sample","sku":"SP-01","price":10.00,"stock":1}'

# 2. POST with correct API key -> 201 Created
curl -i -X POST http://localhost:8080/api/products \
  -H 'X-Api-Key: demo-123' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Sample","sku":"SP-01","price":10.00,"stock":1}'

# 3. OPTIONS preflight request -> 200 OK with CORS headers
curl -i -X OPTIONS http://localhost:8080/api/products \
  -H 'Origin: http://localhost:5173' \
  -H 'Access-Control-Request-Method: POST'

# 4. Echoed correlation identifier
curl -si http://localhost:8080/api/products/1 -H 'X-Correlation-Id: trace-12345' | grep -i correlation

# 5. GZIP compressed response
curl -sv --compressed http://localhost:8080/api/products -o /dev/null 2>&1 | grep -i -E 'encoding|vary'

# 6. Uncompressed request (omitting Accept-Encoding)
curl -sv http://localhost:8080/api/products -o /dev/null 2>&1 | grep -i -E 'content-encoding'
```

Record the output in `docs/task4-http.md`.

---

### Task 4 Checklist

- [x] `CorrelationFilter` captures request duration, logs access, and echoes `X-Correlation-Id`.
- [x] `CorsFilter` short-circuits `OPTIONS` preflight requests with `200 OK`.
- [x] `ApiKeyFilter` operates at `@PreMatching` with `Priorities.AUTHENTICATION`, rejecting write requests without `X-Api-Key`.
- [x] `@Compress` `GzipWriterInterceptor` wraps output streams only when `Accept-Encoding: gzip` is present.
- [x] All abort responses from filters still execute response filters and carry CORS headers.

---

## ⚡ Task 5: Pagination, ETag Caching, Versioning, and Sub-Resources (75 min - 25 pts)

### 🎯 Goal
1. Implement pagination using **RFC 8288 `Link` headers** (`first`, `prev`, `next`, `last`) and `X-Total-Count`.
2. Support **conditional GET caching** using `ETag` and `If-None-Match` (`304 Not Modified`).
3. Enforce **optimistic concurrency control** on `PUT` with mandatory `If-Match` (`428 Precondition Required` / `412 Precondition Failed`).
4. Support **media-type versioning** (`application/vnd.shop.v2+json`) returning structured `Money` objects.
5. Create a **sub-resource locator** at `/products/{id}/reviews`.

---

### Step 5.1: Pagination with RFC 8288 Link Headers & Total Count

Update `ProductFilter.java` with pagination parameters:

```java
    @QueryParam("page")
    @DefaultValue("0")
    @jakarta.validation.constraints.Min(value = 0, message = "page must be >= 0")
    public int page;

    @QueryParam("size")
    @DefaultValue("20")
    @jakarta.validation.constraints.Min(value = 1, message = "size must be >= 1")
    @jakarta.validation.constraints.Max(value = 100, message = "size must be <= 100")
    public int size;
```

Update `InMemoryProductRepository.java` to seed 30 additional products in its constructor so pagination can be tested:

```java
    for (int i = 4; i <= 33; i++) {
        save(new Product(0, "Item " + i, String.format("IT-%02d", i), new BigDecimal(10 + i + ".00"), 10 + i, 0));
    }
```

Implement pagination in `ProductResource.java`:

```java
    @GET
    @Compress
    public Response list(@BeanParam @Valid ProductFilter f, @Context UriInfo uriInfo) {
        List<Product> filtered = repo.findAll().stream()
                .filter(f::matches)
                .sorted(f.comparator())
                .toList();

        int total = filtered.size();
        int lastPage = Math.max(0, (total - 1) / f.size);

        List<ProductDto> pageItems = filtered.stream()
                .skip((long) f.page * f.size)
                .limit(f.size)
                .map(ProductDto::from)
                .toList();

        List<Link> links = new ArrayList<>();
        links.add(createLink(uriInfo, 0, f.size, "first"));
        if (f.page > 0) {
            links.add(createLink(uriInfo, f.page - 1, f.size, "prev"));
        }
        if (f.page < lastPage) {
            links.add(createLink(uriInfo, f.page + 1, f.size, "next"));
        }
        links.add(createLink(uriInfo, lastPage, f.size, "last"));

        return Response.ok(new GenericEntity<List<ProductDto>>(pageItems) {})
                .links(links.toArray(Link[]::new))
                .header("X-Total-Count", total)
                .build();
    }

    private static Link createLink(UriInfo uriInfo, int page, int size, String rel) {
        URI uri = uriInfo.getRequestUriBuilder()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build();
        return Link.fromUri(uri).rel(rel).build();
    }
```

---

### Step 5.2: HTTP Caching with ETag & `Cache-Control` (304 Not Modified)

In `ProductResource.java`, evaluate client caching preconditions using `Request.evaluatePreconditions(EntityTag)`:

```java
    @GET
    @Path("{id: \\d+}")
    @Produces({"application/json; qs=1.0", "application/vnd.shop.v1+json", MediaType.APPLICATION_XML})
    public Response get(@PathParam("id") long id, @Context Request request) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));

        EntityTag tag = new EntityTag(Integer.toHexString(p.version()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(tag);
        if (builder != null) {
            return builder.tag(tag).build(); // Returns 304 Not Modified
        }

        CacheControl cc = new CacheControl();
        cc.setPrivate(true);
        cc.setMaxAge(60);
        cc.setNoTransform(true);

        return Response.ok(ProductDto.from(p))
                .tag(tag)
                .cacheControl(cc)
                .build();
    }
```

---

### Step 5.3: Optimistic Concurrency Control via `If-Match` (428 / 412)

Update `PUT /products/{id}` to enforce conditional updates:

```java
    @PUT
    @Path("{id: \\d+}")
    public Response update(
            @PathParam("id") long id,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch,
            @Valid @NotNull ProductDto dto,
            @Context Request request
    ) {
        if (ifMatch == null || ifMatch.isBlank()) {
            Problem p = new Problem("about:blank", "Precondition Required", 428, "If-Match header is mandatory for updates", "/api/products/" + id);
            return Response.status(428).type(Problem.MEDIA_TYPE).entity(p).build();
        }

        Product existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));

        EntityTag currentTag = new EntityTag(Integer.toHexString(existing.version()));
        Response.ResponseBuilder precondition = request.evaluatePreconditions(currentTag);
        if (precondition != null) {
            return precondition.build(); // Returns 412 Precondition Failed
        }

        Product saved = repo.save(new Product(id, dto.name, dto.sku, dto.price, dto.stock, existing.version()));
        EntityTag newTag = new EntityTag(Integer.toHexString(saved.version()));
        return Response.ok(ProductDto.from(saved)).tag(newTag).build();
    }
```

---

### Step 5.4: Media Type Versioning (`application/vnd.shop.v2+json`)

Create `src/main/java/edu/itc/shop/api/dto/ProductV2.java`:

```java
package edu.itc.shop.api.dto;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Product;

public record ProductV2(
        long id,
        String name,
        String sku,
        Money price,
        int stock,
        boolean available
) {
    public static ProductV2 from(Product p) {
        return new ProductV2(
                p.id(),
                p.name(),
                p.sku(),
                new Money(p.price(), "EUR"),
                p.stock(),
                p.stock() > 0
        );
    }
}
```

In `ProductResource.java`, add the versioned GET method:

```java
    @GET
    @Path("{id: \\d+}")
    @Produces("application/vnd.shop.v2+json")
    public Response getV2(@PathParam("id") long id, @Context Request request) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));

        EntityTag tag = new EntityTag(Integer.toHexString(p.version()));
        Response.ResponseBuilder builder = request.evaluatePreconditions(tag);
        if (builder != null) {
            return builder.tag(tag).build();
        }

        CacheControl cc = new CacheControl();
        cc.setPrivate(true);
        cc.setMaxAge(60);
        cc.setNoTransform(true);

        return Response.ok(ProductV2.from(p))
                .tag(tag)
                .cacheControl(cc)
                .build();
    }
```

---

### Step 5.5: Hierarchical Sub-Resource Locator (`/products/{id}/reviews`)

Create `src/main/java/edu/itc/shop/domain/Review.java`:

```java
package edu.itc.shop.domain;

public record Review(long id, long productId, int stars, String author, String text) {
}
```

Create `src/main/java/edu/itc/shop/domain/ReviewRepository.java`:

```java
package edu.itc.shop.domain;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ReviewRepository {
    private final ConcurrentMap<Long, Review> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(0);

    public List<Review> findByProductId(long productId) {
        return storage.values().stream().filter(r -> r.productId() == productId).toList();
    }

    public Review save(Review r) {
        long id = idGen.incrementAndGet();
        Review created = new Review(id, r.productId(), r.stars(), r.author(), r.text());
        storage.put(id, created);
        return created;
    }
}
```

Create `src/main/java/edu/itc/shop/api/dto/CreateReviewDto.java`:

```java
package edu.itc.shop.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateReviewDto {
    @Min(1) @Max(5)
    public int stars;

    @NotBlank
    public String author;

    @Size(max = 500)
    public String text;
}
```

Create `src/main/java/edu/itc/shop/api/ReviewResource.java`. Sub-resources are instantiated directly by the locator:

```java
package edu.itc.shop.api;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import edu.itc.shop.api.dto.CreateReviewDto;
import edu.itc.shop.domain.Product;
import edu.itc.shop.domain.Review;
import edu.itc.shop.domain.ReviewRepository;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    private final Product product;
    private final ReviewRepository reviews;

    public ReviewResource(Product product, ReviewRepository reviews) {
        this.product = product;
        this.reviews = reviews;
    }

    @GET
    public List<Review> list() {
        return reviews.findByProductId(product.id());
    }

    @POST
    public Response add(@Valid @NotNull CreateReviewDto dto, @Context UriInfo uriInfo) {
        Review saved = reviews.save(new Review(0, product.id(), dto.stars, dto.author, dto.text));
        URI location = uriInfo.getAbsolutePathBuilder().path(Long.toString(saved.id())).build();
        return Response.created(location).entity(saved).build();
    }
}
```

In `ProductResource.java`, register the sub-resource locator:

```java
    @Inject
    private ReviewRepository reviewRepo;

    @Path("{id: \\d+}/reviews")
    public ReviewResource reviews(@PathParam("id") long id) {
        Product product = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
        return new ReviewResource(product, reviewRepo);
    }
```

Bind `ReviewRepository` in `Main.java`:
```java
ReviewRepository reviewRepository = new ReviewRepository();
bind(reviewRepository).to(ReviewRepository.class);
```

---

### Step 5.6: Verification Transcript (`docs/task5-http.md`)

```bash
# 1. Paged response with Link headers and X-Total-Count
curl -si 'http://localhost:8080/api/products?page=1&size=10' | grep -i -E 'link|total|^HTTP'

# 2. Retrieve product with ETag and Cache-Control
curl -si http://localhost:8080/api/products/1 | grep -i -E 'etag|cache|^HTTP'

# 3. Conditional GET returning 304 Not Modified
curl -si http://localhost:8080/api/products/1 -H 'If-None-Match: "1"'

# 4. PUT without If-Match -> 428 Precondition Required
curl -si -X PUT http://localhost:8080/api/products/1 -H 'X-Api-Key: demo-123' \
  -H 'Content-Type: application/json' -d '{"name":"Kettle XL","sku":"KT-01","price":29.90,"stock":12}'

# 5. PUT with stale ETag -> 412 Precondition Failed
curl -si -X PUT http://localhost:8080/api/products/1 -H 'X-Api-Key: demo-123' -H 'If-Match: "0"' \
  -H 'Content-Type: application/json' -d '{"name":"Kettle XL","sku":"KT-01","price":29.90,"stock":12}'

# 6. PUT with valid ETag -> 200 OK + new ETag
curl -si -X PUT http://localhost:8080/api/products/1 -H 'X-Api-Key: demo-123' -H 'If-Match: "1"' \
  -H 'Content-Type: application/json' -d '{"name":"Kettle XL","sku":"KT-01","price":29.90,"stock":12}'

# 7. Request v2 representation via Accept header
curl -s http://localhost:8080/api/products/1 -H 'Accept: application/vnd.shop.v2+json'

# 8. Add review to product -> 201 Created + Location
curl -si -X POST http://localhost:8080/api/products/1/reviews -H 'X-Api-Key: demo-123' \
  -H 'Content-Type: application/json' -d '{"stars":5,"author":"Ana","text":"Boils fast."}'

# 9. Reviews of unknown product -> 404 Not Found
curl -si http://localhost:8080/api/products/999/reviews | head -1
```

Record the output in `docs/task5-http.md`.

---

### Task 5 Checklist

- [x] Paged `list` returns validated slices with `X-Total-Count` and RFC 8288 `Link` relations.
- [x] Item endpoint emits `ETag` and `Cache-Control: private, no-transform, max-age=60`.
- [x] `If-None-Match` evaluates preconditions and returns `304 Not Modified`.
- [x] `PUT` enforces optimistic locking: `428` without `If-Match`, `412` when stale, `200` with incremented ETag on success.
- [x] Vendor media type `application/vnd.shop.v2+json` returns `ProductV2` shape with typed `Money`.
- [x] Sub-resource locator `/products/{id}/reviews` delegates to `ReviewResource` with 404 enforcement.

---

## 🌟 Challenge: Orders API, HATEOAS, Idempotency, OpenAPI, & JerseyTest (+20 pts)

### 🎯 Goal
Implement an industrial-grade orders processing workflow:
1. **Orders Sub-API**: Lifecycle state machine (`NEW`, `PAID`, `CANCELLED`) with automatic inventory reservation and replenishment.
2. **HATEOAS Controls**: Dynamic `_links` object allowing clients to navigate the order lifecycle.
3. **Race-Free Idempotency**: Cryptographic SHA-256 fingerprinting with `ConcurrentHashMap.computeIfAbsent` returning `Idempotency-Replayed: true` or `422 Unprocessable Content`.
4. **Automated OpenAPI 3.1**: Built via SmallRye plugin and served dynamically from `GET /api/openapi`.
5. **Comprehensive Integration Test Suite**: 13+ HTTP status codes covered via `JerseyTest`.

---

### Challenge 1: Orders Sub-API Domain & State Lifecycle

Create `src/main/java/edu/itc/shop/domain/OrderStatus.java`:

```java
package edu.itc.shop.domain;

public enum OrderStatus {
    NEW, PAID, CANCELLED
}
```

Create `src/main/java/edu/itc/shop/domain/Order.java`:

```java
package edu.itc.shop.domain;

import java.math.BigDecimal;
import java.util.List;

public record Order(
        String id,
        List<Line> lines,
        BigDecimal total,
        OrderStatus status
) {
    public record Line(long productId, int quantity) {}
}
```

Create `src/main/java/edu/itc/shop/domain/OrderService.java`. The service enforces atomic inventory reservations:

```java
package edu.itc.shop.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import edu.itc.shop.api.dto.PlaceOrder;

public class OrderService {

    private final ProductRepository productRepo;
    private final ConcurrentMap<String, Order> orders = new ConcurrentHashMap<>();

    @Inject
    public OrderService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    public synchronized Order place(PlaceOrder cmd) {
        BigDecimal total = BigDecimal.ZERO;
        List<Order.Line> lines = new ArrayList<>();

        // Validate all products & reserve stock atomically
        for (PlaceOrder.LineItem item : cmd.lines()) {
            Product p = productRepo.findById(item.productId())
                    .orElseThrow(() -> new NotFoundException("Product " + item.productId() + " not found"));

            if (p.stock() < item.quantity()) {
                throw new ClientErrorException("Insufficient stock for product " + p.id(), Response.Status.CONFLICT);
            }
            productRepo.save(p.withStock(p.stock() - item.quantity()));
            total = total.add(p.price().multiply(BigDecimal.valueOf(item.quantity())));
            lines.add(new Order.Line(p.id(), item.quantity()));
        }

        String orderId = "o-" + UUID.randomUUID().toString().substring(0, 8);
        Order order = new Order(orderId, lines, total, OrderStatus.NEW);
        orders.put(orderId, order);
        return order;
    }

    public Order get(String id) {
        Order o = orders.get(id);
        if (o == null) throw new NotFoundException("Order " + id + " not found");
        return o;
    }

    public synchronized Order pay(String id) {
        Order o = get(id);
        if (o.status() != OrderStatus.NEW) {
            throw new ClientErrorException("Cannot pay order in status " + o.status(), Response.Status.CONFLICT);
        }
        Order updated = new Order(o.id(), o.lines(), o.total(), OrderStatus.PAID);
        orders.put(id, updated);
        return updated;
    }

    public synchronized Order cancel(String id) {
        Order o = get(id);
        if (o.status() == OrderStatus.PAID) {
            throw new ClientErrorException("Cannot cancel paid order", Response.Status.CONFLICT);
        }
        if (o.status() == OrderStatus.CANCELLED) {
            return o;
        }

        // Restore reserved inventory
        for (Order.Line line : o.lines()) {
            productRepo.findById(line.productId()).ifPresent(p -> {
                productRepo.save(p.withStock(p.stock() + line.quantity()));
            });
        }

        Order cancelled = new Order(o.id(), o.lines(), o.total(), OrderStatus.CANCELLED);
        orders.put(id, cancelled);
        return cancelled;
    }
}
```

---

### Challenge 2: HATEOAS Hypermedia Controls

Create `src/main/java/edu/itc/shop/api/dto/OrderDto.java`:

```java
package edu.itc.shop.api.dto;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.ws.rs.core.UriInfo;

import edu.itc.shop.domain.Order;
import edu.itc.shop.domain.OrderStatus;

public record OrderDto(
        String id,
        OrderStatus status,
        BigDecimal total,
        List<Order.Line> lines,
        Map<String, String> _links
) {
    public static OrderDto from(Order o, UriInfo uriInfo) {
        String base = uriInfo.getBaseUri().toString();
        String orderUri = base + "orders/" + o.id();

        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", orderUri);

        if (o.status() == OrderStatus.NEW) {
            links.put("pay", orderUri + "/payment");
            links.put("cancel", orderUri);
        }

        for (Order.Line line : o.lines()) {
            links.put("product-" + line.productId(), base + "products/" + line.productId());
        }

        return new OrderDto(o.id(), o.status(), o.total(), o.lines(), links);
    }
}
```

---

### Challenge 3: Race-Free Idempotent POST Engine (SHA-256 Fingerprinting)

Create `src/main/java/edu/itc/shop/api/OrderResource.java`. We use a thread-safe `computeIfAbsent` call to guarantee that concurrent duplicate requests never duplicate orders or deplete stock twice:

```java
package edu.itc.shop.api;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.validation.Valid;
import jakarta.validation.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import edu.itc.shop.api.dto.OrderDto;
import edu.itc.shop.api.dto.PlaceOrder;
import edu.itc.shop.domain.Order;
import edu.itc.shop.domain.OrderService;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    public record Stored(String bodyHash, Response.StatusType status, URI location, Object body) {}

    private final OrderService orders;
    private final ConcurrentMap<String, Stored> idempotency;
    private static final Jsonb JSONB = JsonbBuilder.create();

    @Inject
    public OrderResource(OrderService orders, ConcurrentMap<String, Stored> idempotency) {
        this.orders = orders;
        this.idempotency = idempotency;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response place(
            @HeaderParam("Idempotency-Key") String key,
            @Valid @NotNull PlaceOrder cmd,
            @Context UriInfo uriInfo
    ) {
        if (key == null || key.isBlank()) {
            throw new ClientErrorException("Idempotency-Key header required", 428);
        }

        String jsonPayload = JSONB.toJson(cmd);
        String hash = sha256(jsonPayload);
        boolean[] replayed = { true };

        Stored stored = idempotency.computeIfAbsent(key, k -> {
            replayed[0] = false;
            Order order = orders.place(cmd);
            URI loc = uriInfo.getAbsolutePathBuilder().path(order.id()).build();
            return new Stored(hash, Response.Status.CREATED, loc, OrderDto.from(order, uriInfo));
        });

        if (!stored.bodyHash().equals(hash)) {
            throw new ClientErrorException("Idempotency-Key reused with a different body", 422);
        }

        return Response.status(stored.status())
                .location(stored.location())
                .header("Idempotency-Replayed", replayed[0])
                .entity(stored.body())
                .build();
    }

    @GET
    @Path("{id}")
    public OrderDto get(@PathParam("id") String id, @Context UriInfo uriInfo) {
        return OrderDto.from(orders.get(id), uriInfo);
    }

    @PUT
    @Path("{id}/payment")
    public OrderDto pay(@PathParam("id") String id, @Context UriInfo uriInfo) {
        return OrderDto.from(orders.pay(id), uriInfo);
    }

    @DELETE
    @Path("{id}")
    public OrderDto cancel(@PathParam("id") String id, @Context UriInfo uriInfo) {
        return OrderDto.from(orders.cancel(id), uriInfo);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

---

### Challenge 4: Automated OpenAPI 3.1 Generation & Serving

Create `src/main/java/edu/itc/shop/api/OpenApiResource.java` to serve the OpenAPI document generated at compile time:

```java
package edu.itc.shop.api;

import java.io.InputStream;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/openapi")
public class OpenApiResource {

    @GET
    @Produces("application/yaml")
    public Response openapi() {
        InputStream is = getClass().getResourceAsStream("/META-INF/openapi.yaml");
        if (is == null) {
            throw new NotFoundException("OpenAPI definition not found");
        }
        return Response.ok(is).build();
    }
}
```

---

### Challenge 5: Comprehensive 13+ HTTP Status Code JerseyTest Suite

Create `src/test/java/edu/itc/shop/ShopApiTest.java`. Using Jersey Test Framework over an ephemeral port, this suite verifies every HTTP status code:

```java
package edu.itc.shop;

import java.util.concurrent.ConcurrentHashMap;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.itc.shop.api.OrderResource;
import edu.itc.shop.domain.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShopApiTest extends JerseyTest {

    static {
        // Ephemeral random port for CI builds
        System.setProperty("jersey.config.test.container.port", "0");
    }

    @Override
    protected Application configure() {
        ProductRepository prodRepo = new InMemoryProductRepository();
        ReviewRepository reviewRepo = new ReviewRepository();
        OrderService orderService = new OrderService(prodRepo);
        ConcurrentHashMap<String, OrderResource.Stored> idemp = new ConcurrentHashMap<>();

        return new ResourceConfig()
                .packages("edu.itc.shop.api")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(prodRepo).to(ProductRepository.class);
                        bind(reviewRepo).to(ReviewRepository.class);
                        bind(orderService).to(OrderService.class);
                        bind(idemp).to(ConcurrentHashMap.class);
                    }
                });
    }

    @Test void test200Ok() {
        Response res = target("products/1").request().get();
        assertEquals(200, res.getStatus());
    }

    @Test void test201Created() {
        String json = "{\"name\":\"Toaster\",\"sku\":\"TS-01\",\"price\":15.00,\"stock\":5}";
        Response res = target("products").request()
                .header("X-Api-Key", "demo-123")
                .post(Entity.json(json));
        assertEquals(201, res.getStatus());
        assertNotNull(res.getLocation());
    }

    @Test void test204NoContent() {
        Response res = target("products/2").request()
                .header("X-Api-Key", "demo-123")
                .delete();
        assertEquals(204, res.getStatus());
    }

    @Test void test304NotModified() {
        Response first = target("products/1").request().get();
        String etag = first.getHeaderString(HttpHeaders.ETAG);
        Response second = target("products/1").request()
                .header(HttpHeaders.IF_NONE_MATCH, etag)
                .get();
        assertEquals(304, second.getStatus());
    }

    @Test void test400BadRequest() {
        Response res = target("products").queryParam("minPrice", "invalid").request().get();
        assertEquals(400, res.getStatus());
    }

    @Test void test401Unauthorized() {
        Response res = target("products").request()
                .post(Entity.json("{\"name\":\"Fail\",\"sku\":\"FL-01\",\"price\":1.00,\"stock\":1}"));
        assertEquals(401, res.getStatus());
    }

    @Test void test404NotFound() {
        Response res = target("products/9999").request().get();
        assertEquals(404, res.getStatus());
    }

    @Test void test406NotAcceptable() {
        Response res = target("products/1").request("application/custom-unknown").get();
        assertEquals(406, res.getStatus());
    }

    @Test void test409Conflict() {
        String json = "{\"name\":\"Kettle Duplicate\",\"sku\":\"KT-01\",\"price\":20.00,\"stock\":1}";
        Response res = target("products").request()
                .header("X-Api-Key", "demo-123")
                .post(Entity.json(json));
        assertEquals(409, res.getStatus());
    }

    @Test void test412PreconditionFailed() {
        String json = "{\"name\":\"Kettle Edit\",\"sku\":\"KT-01\",\"price\":25.00,\"stock\":10}";
        Response res = target("products/1").request()
                .header("X-Api-Key", "demo-123")
                .header(HttpHeaders.IF_MATCH, "\"invalid-etag\"")
                .put(Entity.json(json));
        assertEquals(412, res.getStatus());
    }

    @Test void test415UnsupportedMediaType() {
        Response res = target("products").request()
                .header("X-Api-Key", "demo-123")
                .post(Entity.text("plain text payload"));
        assertEquals(415, res.getStatus());
    }

    @Test void test422UnprocessableContent() {
        String bodyA = "{\"lines\":[{\"productId\":1,\"quantity\":1}]}";
        String bodyB = "{\"lines\":[{\"productId\":1,\"quantity\":2}]}";
        target("orders").request().header("X-Api-Key", "demo-123").header("Idempotency-Key", "key-422").post(Entity.json(bodyA));
        Response res = target("orders").request().header("X-Api-Key", "demo-123").header("Idempotency-Key", "key-422").post(Entity.json(bodyB));
        assertEquals(422, res.getStatus());
    }

    @Test void test428PreconditionRequired() {
        String json = "{\"lines\":[{\"productId\":1,\"quantity\":1}]}";
        Response res = target("orders").request().header("X-Api-Key", "demo-123").post(Entity.json(json));
        assertEquals(428, res.getStatus());
    }

    @Test void testIdempotencyReplay() {
        String body = "{\"lines\":[{\"productId\":1,\"quantity\":1}]}";
        Response r1 = target("orders").request().header("X-Api-Key", "demo-123").header("Idempotency-Key", "k-repeat").post(Entity.json(body));
        Response r2 = target("orders").request().header("X-Api-Key", "demo-123").header("Idempotency-Key", "k-repeat").post(Entity.json(body));
        assertEquals(201, r1.getStatus());
        assertEquals(201, r2.getStatus());
        assertEquals("true", r2.getHeaderString("Idempotency-Replayed"));
    }
}
```

Run test suite:
```bash
mvn clean test
```

---

### Challenge Checklist

- [x] Orders API supports complete lifecycle: Place (stock reservation) -> Pay -> Cancel (stock restoration).
- [x] HATEOAS hypermedia links (`self`, `pay`, `cancel`, `product-*`) returned in `_links`.
- [x] Concurrent idempotent requests guaranteed atomic via `computeIfAbsent` and SHA-256 payload hashing.
- [x] SmallRye plugin generates OpenAPI 3.1 YAML document, served at `GET /api/openapi`.
- [x] `ShopApiTest` covers 13+ HTTP status codes (`200`, `201`, `204`, `304`, `400`, `401`, `404`, `406`, `409`, `412`, `415`, `422`, `428`).

---

## ⚖️ Comprehensive Architectural Comparison: JAX-RS vs Spring MVC

### Side-by-Side Code Comparison: `GET /products/{id}`

#### Jakarta REST (JAX-RS 4.0 / Jersey):
```java
@GET
@Path("{id: \\d+}")
@Produces(MediaType.APPLICATION_JSON)
public Response getProduct(@PathParam("id") long id, @Context Request request) {
    Product product = repo.findById(id)
            .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    EntityTag tag = new EntityTag(Integer.toHexString(product.version()));
    Response.ResponseBuilder builder = request.evaluatePreconditions(tag);
    if (builder != null) {
        return builder.tag(tag).build();
    }
    return Response.ok(ProductDto.from(product)).tag(tag).build();
}
```

#### Spring Web MVC (Lab 02):
```java
@GetMapping(value = "/products/{id:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ProductDto> getProduct(@PathVariable long id, WebRequest request) {
    Product product = repo.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    String etag = "\"" + Integer.toHexString(product.version()) + "\"";
    if (request.checkNotModified(etag)) {
        return null; // Spring MVC automatically handles 304 response
    }
    return ResponseEntity.ok()
            .eTag(etag)
            .body(ProductDto.from(product));
}
```

---

### Side-by-Side Code Comparison: `POST /products`

#### Jakarta REST (JAX-RS 4.0 / Jersey):
```java
@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response createProduct(@Valid @NotNull ProductDto dto, @Context UriInfo uriInfo) {
    Product saved = repo.save(dto.toProduct(0, 0));
    URI location = uriInfo.getAbsolutePathBuilder()
            .path(Long.toString(saved.id()))
            .build();
    return Response.created(location)
            .entity(ProductDto.from(saved))
            .build();
}
```

#### Spring Web MVC (Lab 02):
```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto, UriComponentsBuilder ucb) {
    Product saved = repo.save(dto.toProduct(0, 0));
    URI location = ucb.path("/products/{id}")
            .buildAndExpand(saved.id())
            .toUri();
    return ResponseEntity.created(location)
            .body(ProductDto.from(saved));
}
```

---

### Key Differences Matrix (8 Architectural Dimensions)

| Dimension | Jakarta REST 4.0 (JAX-RS) | Spring Web MVC (Spring Boot) |
| :--- | :--- | :--- |
| **1. Primary Annotations** | Standardized Jakarta annotations: `@Path`, `@GET`, `@POST`, `@PathParam`, `@QueryParam`, `@HeaderParam`, `@Context`. | Proprietary Spring annotations: `@RestController`, `@GetMapping`, `@PostMapping`, `@PathVariable`, `@RequestParam`, `@RequestHeader`. |
| **2. Request Body Binding** | Automatic entity parameter mapping via `MessageBodyReader` without explicit annotations. | Explicit `@RequestBody` parameter annotation required. |
| **3. Response Construction** | Fluent `Response.created(uri).entity(...).build()` using contextual `UriInfo`. | Fluent `ResponseEntity.created(uri).body(...)` using injected `UriComponentsBuilder`. |
| **4. Error Handling** | Standardized `@Provider ExceptionMapper<E>` interfaces converting exceptions to `Response`. | `@ControllerAdvice` / `@RestControllerAdvice` methods annotated with `@ExceptionHandler`. |
| **5. Filters & Interceptors** | ContainerRequestFilter, ContainerResponseFilter (`@PreMatching`), WriterInterceptor. | HandlerInterceptor (`preHandle`, `postHandle`), Servlet `Filter`, ResponseBodyAdvice. |
| **6. JSON / XML Engine** | Jakarta JSON-B (Yasson) and JAXB by default; Jackson available via extension. | Jackson (`ObjectMapper`) by default; XML requires manual Jackson XML or JAXB configuration. |
| **7. Dependency Injection** | Eclipse HK2 / CDI standard (`jakarta.inject.Inject`, `AbstractBinder`). | Spring Framework IoC Container (`@Autowired`, constructor injection, `@Bean`). |
| **8. Integration Testing** | Dedicated embedded container test harness via `JerseyTest` (starts real Grizzly/Jetty). | `MockMvc` (mocked servlet environment) or `@SpringBootTest(webEnvironment = RANDOM_PORT)` with `TestRestTemplate`. |

---

## 📋 Submission Instructions & Verification Commands

### Final Verification Workflow:

1. **Run Full Test Suite:**
   ```bash
   cd shop-rest
   mvn clean verify
   ```
   *Expected output: All unit & integration tests pass with 0 failures.*

2. **Start Server:**
   ```bash
   mvn compile exec:java
   ```

3. **Verify Generated OpenAPI Document:**
   ```bash
   curl -s http://localhost:8080/api/openapi | head -n 10
   ```

4. **Verify HTTP Transcripts:**
   Ensure `docs/` contains:
   - `docs/task1-http.md`
   - `docs/task2-http.md`
   - `docs/task3-http.md`
   - `docs/task4-http.md`
   - `docs/task5-http.md`
   - `docs/challenge-http.md`

5. **Git Commit & Push:**
   ```bash
   git checkout -b lab-04
   git add .
   git commit -m "feat(lab04): complete Tasks 0-5 and extra-credit Challenge"
   git push origin lab-04
   ```
