# Lab 06: Testing in Java Web Applications (JUnit, Mockito)

## Complete Engineering & Test Automation Architecture Guide (Tasks 0–5 & Extra-Credit Challenge)

> **Course:** Software Engineering — Chapter 06  
> **Topic:** Comprehensive Java Testing Pyramid — JUnit 6 (Jupiter), Mockito 5.23, AssertJ Fluent Assertions, Spring Boot 4.1 Test Slices (`@WebMvcTest`, `@DataJpaTest`, `@RestClientTest`), Containerized Integration Testing (Testcontainers & PostgreSQL), JaCoCo Coverage Gates (80%), CI/CD (GitHub Actions), Consumer-Driven Contracts (Pact), End-to-End Browser Testing (Playwright), and Mutation Testing (PIT)  
> **Application:** `shop-tests` (Hardened E-Commerce Test Automation Suite)  
> **Stack:** Java 25, Spring Boot 4.1.1, JUnit Jupiter 6.1.3, Mockito 5.23.0, AssertJ 3.27+, Testcontainers 1.20+, PostgreSQL 17 Alpine, H2 Database, JaCoCo 0.8.14, Playwright 1.50+, Pact JVM 4.6+, PIT 1.22+, Apache Maven 3.9+  
> **Estimated Duration:** ≈ 5 hours (Tasks 0–5: 100 points, Challenge: +20 points extra credit)  

---

## 📑 Table of Contents

- [Lab 06: Testing in Java Web Applications (JUnit, Mockito)](#lab-06-testing-in-java-web-applications-junit-mockito)
  - [Complete Engineering \& Test Automation Architecture Guide (Tasks 0–5 \& Extra-Credit Challenge)](#complete-engineering--test-automation-architecture-guide-tasks-05--extra-credit-challenge)
  - [📑 Table of Contents](#-table-of-contents)
  - [🎯 Lab Objectives \& Architectural Overview](#-lab-objectives--architectural-overview)
    - [The Modern Testing Pyramid for Java Web Applications](#the-modern-testing-pyramid-for-java-web-applications)
    - [Spring Boot 4 Test Slicing vs Monolithic Contexts](#spring-boot-4-test-slicing-vs-monolithic-contexts)
    - [Core Quality \& Testing Principles](#core-quality--testing-principles)
  - [📊 Grading Rubric \& Automatic Deductions](#-grading-rubric--automatic-deductions)
    - [Component Score Breakdown](#component-score-breakdown)
    - [⚠️ Strict Automatic Deductions Matrix](#️-strict-automatic-deductions-matrix)
  - [🚀 Task 0: Setup \& The `shop-tests` Project (20 min)](#-task-0-setup--the-shop-tests-project-20-min)
    - [Step 0.1: Verify Developer Toolchain](#step-01-verify-developer-toolchain)
    - [Step 0.2: Starter Project Architecture \& Production Code Manifest](#step-02-starter-project-architecture--production-code-manifest)
    - [Step 0.3: Enterprise Build Configuration (`pom.xml`)](#step-03-enterprise-build-configuration-pomxml)
    - [Step 0.4: Test Configuration Profile (`application-test.yml`)](#step-04-test-configuration-profile-application-testyml)
    - [Step 0.5: Target Test Directory Layout](#step-05-target-test-directory-layout)
  - [🧪 Task 1: Unit Tests with JUnit 6: Money and PricingService (40 min - 10 pts)](#-task-1-unit-tests-with-junit-6-money-and-pricingservice-40-min---10-pts)
    - [Architectural Analysis: JUnit 6 Lifecycle, Grouping \& AssertJ Matchers](#architectural-analysis-junit-6-lifecycle-grouping--assertj-matchers)
    - [Step 1.1: Value Object Testing — `MoneyTest.java`](#step-11-value-object-testing--moneytestjava)
    - [Step 1.2: Pure Service Testing — `PricingServiceTest.java`](#step-12-pure-service-testing--pricingservicetestjava)
    - [Step 1.3: Grouped Failure Diagnostics \& Surefire Report Inspection](#step-13-grouped-failure-diagnostics--surefire-report-inspection)
    - [Task 1 Checklist \& Tagging](#task-1-checklist--tagging)
  - [🔄 Task 2: Parameterized and Repeated Tests (40 min - 15 pts)](#-task-2-parameterized-and-repeated-tests-40-min---15-pts)
    - [Architectural Analysis: Data-Driven Test Design \& Stochastic Verification](#architectural-analysis-data-driven-test-design--stochastic-verification)
    - [Step 2.1: Classpath CSV Fixture — `fixtures/tax-rates.csv`](#step-21-classpath-csv-fixture--fixturestax-ratescsv)
    - [Step 2.2: Parameterized Rule Verification — `PricingRulesParamTest.java`](#step-22-parameterized-rule-verification--pricingrulesparamtestjava)
    - [Step 2.3: Randomness \& Collision Testing — `IdGeneratorTest.java`](#step-23-randomness--collision-testing--idgeneratortestjava)
    - [Step 2.4: Execution \& Per-Invocation Tree Verification](#step-24-execution--per-invocation-tree-verification)
    - [Task 2 Checklist \& Tagging](#task-2-checklist--tagging)
  - [🎭 Task 3: Mockito: Isolating ProductService (60 min - 20 pts)](#-task-3-mockito-isolating-productservice-60-min---20-pts)
    - [Architectural Analysis: Boundary Isolation, Test Data Builders \& Strict Stubs](#architectural-analysis-boundary-isolation-test-data-builders--strict-stubs)
    - [Step 3.1: Test Data Builders \& Object Mother (`ProductBuilder`, `Products`)](#step-31-test-data-builders--object-mother-productbuilder-products)
    - [Step 3.2: Isolated Service Tests — `ProductServiceTest.java`](#step-32-isolated-service-tests--productservicetestjava)
    - [Step 3.3: Partial Mocking with Real Collaborators — `MoneyFormatterSpyTest.java`](#step-33-partial-mocking-with-real-collaborators--moneyformatterspytestjava)
    - [Step 3.4: Strict Stubs Incident \& Root Cause Analysis (`NOTES.md`)](#step-34-strict-stubs-incident--root-cause-analysis-notesmd)
    - [Task 3 Checklist \& Tagging](#task-3-checklist--tagging)
  - [🍰 Task 4: Slice Tests: @WebMvcTest, @DataJpaTest and the JWT Filter (60 min - 20 pts)](#-task-4-slice-tests-webmvctest-datajpatest-and-the-jwt-filter-60-min---20-pts)
    - [Architectural Analysis: Fast Slices, Context Boundaries \& First-Level Cache Detachment](#architectural-analysis-fast-slices-context-boundaries--first-level-cache-detachment)
    - [Step 4.1: Web Layer Testing with `MockMvcTester` — `ProductControllerTest.java`](#step-41-web-layer-testing-with-mockmvctester--productcontrollertestjava)
    - [Step 4.2: Data Persistence Slice — `ProductRepositoryTest.java`](#step-42-data-persistence-slice--productrepositorytestjava)
    - [Step 4.3: SQL Data Fixtures \& Derived Queries — `ProductQueryTest.java`](#step-43-sql-data-fixtures--derived-queries--productquerytestjava)
    - [Step 4.4: Security Filter Chain Verification — `JwtFilterTest.java`](#step-44-security-filter-chain-verification--jwtfiltertestjava)
    - [Step 4.5: Slice Timing Benchmarks (`NOTES.md`)](#step-45-slice-timing-benchmarks-notesmd)
    - [Task 4 Checklist \& Tagging](#task-4-checklist--tagging)
  - [🏗️ Task 5: Integration Tests, Coverage Gate and CI (90 min - 25 pts)](#️-task-5-integration-tests-coverage-gate-and-ci-90-min---25-pts)
    - [Architectural Analysis: Real Databases, HTTP Wire Mocking \& Coverage Enforcement](#architectural-analysis-real-databases-http-wire-mocking--coverage-enforcement)
    - [Step 5.1: Build Lifecycle Separation (Failsafe vs Surefire) \& JaCoCo Plugin](#step-51-build-lifecycle-separation-failsafe-vs-surefire--jacoco-plugin)
    - [Step 5.2: Full-Stack Integration Testing — `ProductApiIT.java`](#step-52-full-stack-integration-testing--productapiitjava)
    - [Step 5.3: Containerized PostgreSQL Testing — `ProductRepositoryPostgresIT.java`](#step-53-containerized-postgresql-testing--productrepositorypostgresitjava)
    - [Step 5.4: Outbound HTTP Client Wire Testing — `PriceClientTest.java`](#step-54-outbound-http-client-wire-testing--priceclienttestjava)
    - [Step 5.5: JaCoCo 80% Coverage Gate Verification \& Audit](#step-55-jacoco-80-coverage-gate-verification--audit)
    - [Step 5.6: GitHub Actions Automated CI Pipeline (`.github/workflows/ci.yml`)](#step-56-github-actions-automated-ci-pipeline-githubworkflowsciyml)
    - [Task 5 Checklist \& Tagging](#task-5-checklist--tagging)
  - [🌟 Challenge: Regression Suite, Consumer Contract, E2E Test, Mutation Testing (+20 pts)](#-challenge-regression-suite-consumer-contract-e2e-test-mutation-testing-20-pts)
    - [Challenge Overview \& Strategy](#challenge-overview--strategy)
    - [Step C.1: Regression Test Suite (TDD Bug Reproductions \& Fixes)](#step-c1-regression-test-suite-tdd-bug-reproductions--fixes)
      - [Bug #101: Stock Underflow Check on Negative Restock](#bug-101-stock-underflow-check-on-negative-restock)
      - [Bug #102: Search Query Trailing Whitespace Normalization](#bug-102-search-query-trailing-whitespace-normalization)
      - [Bug #103: Discrepancy Between Discount Rounding and Invoicing](#bug-103-discrepancy-between-discount-rounding-and-invoicing)
    - [Step C.2: Consumer-Driven Contract Testing (`PriceClientPactTest.java`)](#step-c2-consumer-driven-contract-testing-priceclientpacttestjava)
    - [Step C.3: End-to-End Browser Testing with Playwright (`ProductPageE2E.java`)](#step-c3-end-to-end-browser-testing-with-playwright-productpagee2ejava)
    - [Step C.4: Mutation Testing with PIT (`MUTATION.md` Analysis)](#step-c4-mutation-testing-with-pit-mutationmd-analysis)
    - [Challenge Checklist \& Tagging](#challenge-checklist--tagging)
  - [📋 Project Artifacts \& Complete Directory Manifest](#-project-artifacts--complete-directory-manifest)
  - [⚙️ Verification Commands \& Git Workflow Quick Reference](#️-verification-commands--git-workflow-quick-reference)

---
## 🎯 Lab Objectives & Architectural Overview

In **Lab 06**, we engineer a production-ready, industrial-grade test harness for `shop-tests`, an e-commerce microservice built on Spring Boot 4.1. Starting from an application with **zero** test coverage, we systematically construct an automated testing suite embodying the **Software Testing Pyramid**.

### The Modern Testing Pyramid for Java Web Applications

```
                                  ▲
                                 / \
                                /   \
                               / E2E \
                              / Play- \
                             / wright  \
                            /───────────\
                           /  Consumer   \
                          /   Contract    \
                         /   (Pact JVM)    \
                        /───────────────────\
                       /     Full-Stack      \
                      /   Integration Tests   \
                     /  (SpringBootTest + TC)  \
                    /───────────────────────────\
                   /     Spring Slice Tests      \
                  / (@WebMvcTest, @DataJpaTest,   \
                 /        @RestClientTest)         \
                /───────────────────────────────────\
               /           Unit Tests                \
              /   Plain JUnit 6 + Mockito 5.23 +     \
             /           AssertJ Matchers             \
            /─────────────────────────────────────────\
```

### Spring Boot 4 Test Slicing vs Monolithic Contexts

A critical architectural pitfall in Java enterprise systems is over-reliance on `@SpringBootTest(webEnvironment = ...)`. Launching the entire Spring application context for every test case introduces massive memory overhead and slow execution times. Spring Boot 4 provides dedicated **Test Slices** that configure only the precise beans required for the layer under test:

```
+────────────────────────────────────────────────────────────────────────────────────────────────────────+
|                                        APPLICATION TEST BOUNDARIES                                     |
+────────────────────────────────────────────────────────────────────────────────────────────────────────+

  1. Pure Unit Tests (No Spring Context — Sub-millisecond Execution)
     ├── MoneyTest, PricingServiceTest, PricingRulesParamTest, IdGeneratorTest
     └── Uses plain JUnit Jupiter 6.1.3, AssertJ, and standard Java runtime.

  2. Isolated Service Unit Tests (Mockito 5.23 Extension)
     ├── ProductServiceTest, MoneyFormatterSpyTest
     └── Mocks external boundaries (@Mock ProductRepository, @Mock PriceClient, @Spy MoneyFormatter).
         Deterministic time via Clock.fixed(...).

  3. Spring Slice Tests (Focused Sub-Contexts — 1 to 2 Seconds)
     ├── Controller Slice: @WebMvcTest(ProductController.class)
     │   └── Boots DispatcherServlet, Jackson, Security Filter Chain, Validation.
     │   └── Uses Spring Framework MockMvcTester & @MockitoBean ProductService.
     ├── Data Slice: @DataJpaTest
     │   └── Boots Hibernate, JPA Metamodel, TestEntityManager, and H2 In-Memory DB.
     │   └── Verifies derived SQL queries and database constraints without web overhead.
     ├── Client Slice: @RestClientTest(PriceClient.class)
     │   └── Boots RestClient and MockRestServiceServer to verify HTTP serialization & error mapping.
     └── Security Filter Slice: @WebMvcTest + @Import({SecurityConfig.class, JwtFilter.class})
         └── Exercises JWT parsing, authentication headers, and 401 ProblemDetail responses.

  4. Full-Stack Integration Tests (Failsafe Plugin — *IT)
     ├── Web Integration: @SpringBootTest(webEnvironment = RANDOM_PORT)
     │   └── Real embedded Tomcat server, HTTP client (TestRestTemplate), full security pipeline.
     └── Persistence Integration: ProductRepositoryPostgresIT
         └── Real PostgreSQL 17 Alpine via Testcontainers & Spring Boot @ServiceConnection.
         └── Graceful fallback: disabledWithoutDocker = true automatically skips when Docker is absent.

  5. Specialized Verification (Challenge Phase)
     ├── Pact Consumer Contract: Verifies consumer expectations without running provider services.
     ├── Playwright Headless Browser: Drives real Chromium browser, validating DOM state & UI flows.
     └── PIT Mutation Testing: Injects faults into bytecode to verify assertion efficacy and test strength.
```

### Core Quality & Testing Principles

1. **AAA Pattern (Arrange, Act, Assert):** Every test method must be partitioned cleanly into input preparation, method invocation, and result verification.
2. **Deterministic & Isolated Execution:** No test may rely on prior execution state, static state mutation, or execution ordering (`@TestMethodOrder` is strictly prohibited).
3. **Strict Boundary Mocking:** Only mock external network and storage boundaries (`ProductRepository`, `PriceClient`). Domain entities (`Product`), value records (`Money`, `Percent`), and temporal abstractions (`Clock`) must **never** be mocked.
4. **Strict Stubbing (`STRICT_STUBS`):** Unnecessary stubbings produce immediate build failures via `UnnecessaryStubbingException`, preventing test bloat and zombie code.
5. **Coverage as an Invariant:** JaCoCo enforces a strict **80% line coverage gate** across all business classes before code can be packaged or deployed.

---
## 📊 Grading Rubric & Automatic Deductions

### Component Score Breakdown

| Component | Points | Full Marks Criteria |
| :--- | :---: | :--- |
| **Task 1: Unit tests with JUnit 6** | 10 | Lifecycle methods (`@BeforeAll`, `@BeforeEach`, `@AfterEach`) used correctly; `@Nested` contexts; `assertAll` with grouped checks; `assertThrows` with message validation; AssertJ `extracting(...).containsExactly(...)`; descriptive sentences via `@DisplayName`. |
| **Task 2: Parameterized and repeated tests** | 15 | `@CsvSource` with quoted empty string `''`; `@CsvFileSource` reading classpath CSV; `@MethodSource` covering zero, 100%, 0.01 floor, and half-up rounding; `@NullAndEmptySource` with `@ValueSource`; `@EnumSource` include and exclude modes; 200 repetitions with thread-safe collision detection. |
| **Task 3: Mockito: isolating ProductService** | 20 | Only boundaries mocked (`repo`, `prices`); `ArgumentCaptor` captures saved entity; `InOrder` verifies price-before-save; negative verifications (`never()`, `verifyNoInteractions`); `@Spy` on `MoneyFormatter` with `doReturn()`; strict-stubs failure captured in `NOTES.md` and resolved; test data builder and object mother in active use. |
| **Task 4: Slice tests** | 20 | `@WebMvcTest` covers 200, 400 (validation field error), 401, 403, and 404 (RFC 9457 `problem+json`) using `MockMvcTester` and `@MockitoBean`; `@DataJpaTest` uses `TestEntityManager` with `flush()` and `clear()`; `@Sql` fixture tests derived queries; `JwtFilter` tested inside the security filter chain. |
| **Task 5: Integration, coverage, CI** | 25 | Full-stack `@SpringBootTest(RANDOM_PORT)` test with `TestRestTemplate`; containerized PostgreSQL test with `@Testcontainers` and `@ServiceConnection` (gracefully skipped without Docker); `PriceClientTest` with `MockRestServiceServer`; JaCoCo 80% line gate passing; GitHub Actions CI workflow passing and linked. |
| **Test Code Quality** | 10 | Clean AAA structure; zero logic in tests; zero copy-paste duplication; packages mirror `src/main`; comprehensive `NOTES.md` containing timing analysis and strict-stub excerpts; atomic commits per task. |
| **★ Challenge (Extra Credit)** | +20 | Three regression tests committed red then green (+5); consumer contract test producing committed `shop-price-api.json` (+5); Playwright e2e test with role/data-testid selectors (+4); PIT mutation testing analysis with killed survivors in `MUTATION.md` (+6). |
| **Total** | **100 (+20)** | **120 Max Score** |

### ⚠️ Strict Automatic Deductions Matrix

> [!CAUTION]
> The automated grading harness executes strict static and runtime checks. Violations trigger automatic point deductions:

- **−5 pts:** Any usage of `Thread.sleep()` anywhere in the test suite (use Awaitility or deterministic synchronization instead).
- **−5 pts:** Tests that depend on execution order or shared mutable static state, or using `@TestMethodOrder` to force pass conditions.
- **−3 pts each (max −9):** Any test method that executes code without an assertion or verification call.
- **−5 pts:** Mocking the class under test, or mocking value types (`Money`, `Percent`), domain entities (`Product`), or `Clock`.
- **−5 pts:** Modifying production code in `src/main` during Tasks 1–5 without a justified note in `NOTES.md`.
- **−10 pts:** Lowering the JaCoCo coverage gate below `0.80`, disabling the plugin, or excluding tested business classes to artificially inflate the ratio.
- **−5 pts:** Committing secrets, plaintext tokens, or absolute local machine file paths.

---
## 🚀 Task 0: Setup & The `shop-tests` Project (20 min)

### Step 0.1: Verify Developer Toolchain

Ensure your development workstation meets the required toolchain specifications:

```bash
# Verify Java 25
java -version

# Verify Maven 3.9+
mvn -v

# Verify Git
git --version

# Verify Docker (Optional until Task 5)
docker version
```

---

### Step 0.2: Starter Project Architecture & Production Code Manifest

The `shop-tests` application represents a complete, functioning e-commerce shop service located in package `edu.itc.shop`. Below is the complete production source code that the test harness will validate.

#### 1. Domain Entities & Value Objects

`src/main/java/edu/itc/shop/domain/Money.java`:
```java
package edu.itc.shop.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Immutable monetary amount anchored to a specific currency with scale 2.
 */
public record Money(BigDecimal amount, Currency currency) implements Comparable<Money> {

    public static final Currency EUR = Currency.getInstance("EUR");
    public static final Money ZERO = Money.of("0.00");

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Monetary amount cannot be negative: " + amount);
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(String value) {
        return new Money(new BigDecimal(value), EUR);
    }

    public static Money of(String value, String currencyCode) {
        return new Money(new BigDecimal(value), Currency.getInstance(currencyCode));
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public Money plus(Money other) {
        checkSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money minus(Money other) {
        checkSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money times(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    public Money percentOff(Percent percent) {
        BigDecimal discountFactor = BigDecimal.valueOf(percent.value())
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal discountAmount = this.amount.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
        return new Money(this.amount.subtract(discountAmount), this.currency);
    }

    private void checkSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot combine different currencies: " + this.currency + " and " + other.currency);
        }
    }

    @Override
    public int compareTo(Money o) {
        checkSameCurrency(o);
        return this.amount.compareTo(o.amount);
    }
}
```

`src/main/java/edu/itc/shop/domain/Percent.java`:
```java
package edu.itc.shop.domain;

/**
 * Value record representing a percentage between 0 and 100 inclusive.
 */
public record Percent(int value) {
    public Percent {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Percent must be between 0 and 100: " + value);
        }
    }

    public static Percent of(int value) {
        return new Percent(value);
    }
}
```

`src/main/java/edu/itc/shop/domain/OrderStatus.java`:
```java
package edu.itc.shop.domain;

public enum OrderStatus {
    NEW,
    PAID,
    SHIPPED,
    CANCELLED;

    public boolean isEditable() {
        return this == NEW;
    }
}
```

`src/main/java/edu/itc/shop/domain/PriceLine.java`:
```java
package edu.itc.shop.domain;

public record PriceLine(String label, Money amount) {}
```

`src/main/java/edu/itc/shop/domain/Product.java`:
```java
package edu.itc.shop.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int priceCents;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private Instant createdAt;

    protected Product() {}

    public Product(String sku, String name, int priceCents, int stock, Instant createdAt) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null or blank");
        }
        this.sku = sku;
        this.name = name;
        this.priceCents = priceCents;
        this.stock = stock;
        this.createdAt = Objects.requireNonNullElseGet(createdAt, Instant::now);
    }

    public Product(String sku, String name, int priceCents) {
        this(sku, name, priceCents, 0, Instant.now());
    }

    public Long id() { return id; }
    public String sku() { return sku; }
    public String name() { return name; }
    public int priceCents() { return priceCents; }
    public int stock() { return stock; }
    public Instant createdAt() { return createdAt; }

    public void updateStock(int delta) {
        this.stock += delta;
    }
}
```

#### 2. Service Layer & Exceptions

`src/main/java/edu/itc/shop/service/TaxTable.java`:
```java
package edu.itc.shop.service;

import java.util.Map;

public class TaxTable {
    private final Map<String, Integer> rates;

    private TaxTable(Map<String, Integer> rates) {
        this.rates = rates;
    }

    public static TaxTable load(String profile) {
        return new TaxTable(Map.of(
                "DE", 19,
                "FR", 20,
                "IE", 23,
                "", 0
        ));
    }

    public int rateFor(String country) {
        if (country == null) {
            country = "";
        }
        Integer rate = rates.get(country);
        if (rate == null) {
            throw new IllegalArgumentException("Unknown country for tax table: " + country);
        }
        return rate;
    }
}
```

`src/main/java/edu/itc/shop/service/PricingService.java`:
```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Percent;
import edu.itc.shop.domain.PriceLine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PricingService {

    private final TaxTable taxTable;

    public PricingService(TaxTable taxTable) {
        this.taxTable = taxTable;
    }

    public Money gross(Money net, String country) {
        int rate = taxTable.rateFor(country);
        BigDecimal taxMultiplier = BigDecimal.ONE.add(
                BigDecimal.valueOf(rate).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal grossAmount = net.amount().multiply(taxMultiplier).setScale(2, RoundingMode.HALF_UP);
        return new Money(grossAmount, net.currency());
    }

    public Money discounted(Money net, Percent off) {
        // Initial implementation has bug #103 (HALF_EVEN vs HALF_UP)
        return net.percentOff(off);
    }

    public List<PriceLine> breakdown(Money net, String country) {
        int rate = taxTable.rateFor(country);
        BigDecimal taxMultiplier = BigDecimal.valueOf(rate).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal vatAmount = net.amount().multiply(taxMultiplier).setScale(2, RoundingMode.HALF_UP);
        Money vat = new Money(vatAmount, net.currency());
        Money gross = net.plus(vat);

        List<PriceLine> lines = new ArrayList<>();
        lines.add(new PriceLine("net", net));
        lines.add(new PriceLine("VAT " + rate + " %", vat));
        lines.add(new PriceLine("gross", gross));
        return lines;
    }
}
```

`src/main/java/edu/itc/shop/service/IdGenerator.java`:
```java
package edu.itc.shop.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class IdGenerator {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final SecureRandom random = new SecureRandom();

    public String nextOrderId() {
        StringBuilder sb = new StringBuilder("ORD-");
        for (int i = 0; i < 8; i++) {
            int idx = random.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(idx));
        }
        return sb.toString();
    }
}
```

`src/main/java/edu/itc/shop/service/PriceClient.java`:
```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PriceClient {

    private final RestClient restClient;

    public PriceClient(RestClient.Builder builder, @Value("${shop.price-api.base-url:}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public record PriceResponse(String sku, String amount, String currency) {}

    public Money currentPrice(String sku) {
        PriceResponse response = restClient.get()
                .uri("/prices/{sku}", sku)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new PriceUnavailableException("Price service returned status: " + res.getStatusCode());
                })
                .body(PriceResponse.class);

        if (response == null || response.amount() == null) {
            throw new PriceUnavailableException("Empty response received for sku: " + sku);
        }
        return Money.of(response.amount(), response.currency() != null ? response.currency() : "EUR");
    }
}
```

`src/main/java/edu/itc/shop/service/ProductService.java`:
```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Product;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.web.dto.CreateProduct;
import edu.itc.shop.web.dto.ProductDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;
    private final PriceClient priceClient;
    private final Clock clock;

    public ProductService(ProductRepository repository, PriceClient priceClient, Clock clock) {
        this.repository = repository;
        this.priceClient = priceClient;
        this.clock = clock;
    }

    public ProductDto create(CreateProduct command) {
        Money price = priceClient.currentPrice(command.sku());
        int priceCents = price.amount().multiply(BigDecimal.valueOf(100)).intValueExact();
        Product product = new Product(command.sku(), command.name(), priceCents, 0, clock.instant());
        Product saved = repository.save(product);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public ProductDto get(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ProductNotFoundException("Product " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> search(String query) {
        // Initial implementation has bug #102: does not trim trailing whitespace
        if (query == null || query.isBlank()) {
            return repository.findAll().stream().map(this::toDto).toList();
        }
        return repository.findByNameContainingIgnoreCase(query).stream().map(this::toDto).toList();
    }

    public ProductDto restock(Long id, int delta) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product " + id + " not found"));

        // Initial implementation has bug #101: checks < instead of <= on negative resulting stock
        if (product.stock() + delta < 0) {
            throw new InsufficientStockException("Insufficient stock for product " + id);
        }
        product.updateStock(delta);
        return toDto(repository.save(product));
    }

    private ProductDto toDto(Product p) {
        return new ProductDto(p.id(), p.sku(), p.name(), p.priceCents(), p.stock());
    }
}
```

`src/main/java/edu/itc/shop/service/ProductNotFoundException.java`:
```java
package edu.itc.shop.service;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) { super(message); }
}
```

`src/main/java/edu/itc/shop/service/InsufficientStockException.java`:
```java
package edu.itc.shop.service;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) { super(message); }
}
```

`src/main/java/edu/itc/shop/service/PriceUnavailableException.java`:
```java
package edu.itc.shop.service;

public class PriceUnavailableException extends RuntimeException {
    public PriceUnavailableException(String message) { super(message); }
}
```

#### 3. Repository, Web & Security Layers

`src/main/java/edu/itc/shop/repository/ProductRepository.java`:
```java
package edu.itc.shop.repository;

import edu.itc.shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    Optional<Product> findBySku(String sku);
    long countByPriceCentsLessThan(int maxPriceCents);

    @Query(value = "SELECT * FROM products WHERE name ILIKE '%' || :pattern || '%'", nativeQuery = true)
    List<Product> searchByPattern(@Param("pattern") String pattern);
}
```

`src/main/java/edu/itc/shop/web/ProductController.java`:
```java
package edu.itc.shop.web;

import edu.itc.shop.service.ProductService;
import edu.itc.shop.web.dto.CreateProduct;
import edu.itc.shop.web.dto.ProductDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<ProductDto> list(@RequestParam(value = "q", required = false) String query) {
        return productService.search(query);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productService.get(id);
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody CreateProduct command) {
        ProductDto created = productService.create(command);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @PatchMapping("/{id}/restock")
    public ProductDto restock(@PathVariable Long id, @RequestParam("delta") int delta) {
        return productService.restock(id, delta);
    }
}
```

`src/main/java/edu/itc/shop/web/dto/ProductDto.java`:
```java
package edu.itc.shop.web.dto;

public record ProductDto(Long id, String sku, String name, int priceCents, int stock) {}
```

`src/main/java/edu/itc/shop/web/dto/CreateProduct.java`:
```java
package edu.itc.shop.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProduct(
        @NotBlank(message = "SKU must not be blank") String sku,
        @NotBlank(message = "Product name must not be blank") String name
) {}
```

`src/main/java/edu/itc/shop/web/ApiExceptionHandler.java`:
```java
package edu.itc.shop.web;

import edu.itc.shop.service.InsufficientStockException;
import edu.itc.shop.service.PriceUnavailableException;
import edu.itc.shop.service.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleNotFound(ProductNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("https://shop.itc.edu/errors/not-found"));
        pd.setTitle("Product Not Found");
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        pd.setType(URI.create("https://shop.itc.edu/errors/validation-failed"));
        pd.setTitle("Bad Request");

        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : ""))
                .toList();
        pd.setProperty("errors", fieldErrors);
        return pd;
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setType(URI.create("https://shop.itc.edu/errors/insufficient-stock"));
        pd.setTitle("Insufficient Stock");
        return pd;
    }

    @ExceptionHandler(PriceUnavailableException.class)
    public ProblemDetail handlePriceUnavailable(PriceUnavailableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        pd.setType(URI.create("https://shop.itc.edu/errors/price-unavailable"));
        pd.setTitle("Price Service Unavailable");
        return pd;
    }
}
```

`src/main/java/edu/itc/shop/security/SecurityConfig.java`:
```java
package edu.itc.shop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                                    HttpStatus.UNAUTHORIZED, "Authentication required: " + authException.getMessage());
                            pd.setType(URI.create("https://shop.itc.edu/errors/unauthorized"));
                            pd.setTitle("Unauthorized");
                            response.getOutputStream().write(
                                    ("""
                                    {"type":"https://shop.itc.edu/errors/unauthorized","title":"Unauthorized","status":401,"detail":"%s"}
                                    """).formatted(authException.getMessage()).getBytes(StandardCharsets.UTF_8));
                        })
                );
        return http.build();
    }
}
```

`src/main/java/edu/itc/shop/security/JwtFilter.java`:
```java
package edu.itc.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            JwtClaims claims = jwtService.parse(token);
            List<SimpleGrantedAuthority> authorities = claims.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(claims.subject(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.getOutputStream().write(
                    ("""
                    {"type":"https://shop.itc.edu/errors/unauthorized","title":"Unauthorized","status":401,"detail":"%s"}
                    """).formatted(ex.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}
```

`src/main/java/edu/itc/shop/security/JwtClaims.java`:
```java
package edu.itc.shop.security;

import java.util.List;

public record JwtClaims(String subject, List<String> roles) {}
```

`src/main/java/edu/itc/shop/security/JwtException.java`:
```java
package edu.itc.shop.security;

public class JwtException extends RuntimeException {
    public JwtException(String message) { super(message); }
}
```

`src/main/java/edu/itc/shop/security/JwtService.java`:
```java
package edu.itc.shop.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
public class JwtService {

    private final byte[] secretKey;

    public JwtService(@Value("${shop.jwt.secret:default-secret-key-that-is-at-least-32-bytes-long}") String secret) {
        this.secretKey = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String issue(String username, List<String> roles) {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        long now = Instant.now().getEpochSecond();
        long exp = now + 3600;
        String rolesJson = "[" + String.join(",", roles.stream().map(r -> "\"" + r + "\"").toList()) + "]";
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                ("{\"sub\":\"" + username + "\",\"roles\":" + rolesJson + ",\"iat\":" + now + ",\"exp\":" + exp + "}")
                        .getBytes(StandardCharsets.UTF_8));
        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public JwtClaims parse(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new JwtException("Invalid JWT token structure");
        }
        String signature = sign(parts[0] + "." + parts[1]);
        if (!signature.equals(parts[2])) {
            throw new JwtException("Invalid JWT token signature");
        }
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        if (payloadJson.contains("\"exp\"")) {
            long exp = extractLong(payloadJson, "\"exp\":");
            if (Instant.now().getEpochSecond() > exp) {
                throw new JwtException("expired");
            }
        }
        String sub = extractString(payloadJson, "\"sub\":\"");
        List<String> roles = payloadJson.contains("ROLE_ADMIN") ? List.of("ROLE_ADMIN", "ROLE_USER") : List.of("ROLE_USER");
        return new JwtClaims(sub, roles);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new JwtException("Cryptographic error during signing: " + e.getMessage());
        }
    }

    private long extractLong(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return 0;
        int start = idx + key.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Long.parseLong(json.substring(start, end).trim());
    }

    private String extractString(String json, String prefix) {
        int start = json.indexOf(prefix) + prefix.length();
        int end = json.indexOf(""", start);
        return json.substring(start, end);
    }
}
```

`src/main/java/edu/itc/shop/util/MoneyFormatter.java`:
```java
package edu.itc.shop.util;

import edu.itc.shop.domain.Money;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormatter {
    private final Locale locale;

    public MoneyFormatter(Locale locale) {
        this.locale = locale;
    }

    public String format(Money money) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        nf.setCurrency(money.currency());
        return nf.format(money.amount());
    }
}
```

---

### Step 0.3: Enterprise Build Configuration (`pom.xml`)

Configure your `pom.xml` with all core and test dependencies, including Surefire agent args for JDK 25 dynamic agent loading, Failsafe plugin for integration tests, JaCoCo for the 80% line coverage gate, PIT for mutation testing, Pact for contracts, and Playwright for end-to-end tests:

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
    <artifactId>shop-tests</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>shop-tests</name>
    <description>Lab 06 - Testing in Java Web Applications (JUnit, Mockito)</description>

    <properties>
        <java.version>25</java.version>
        <pitest.version>1.22.0</pitest.version>
        <pitest-junit5.version>1.2.4</pitest-junit5.version>
        <pact.version>4.6.17</pact.version>
        <playwright.version>1.50.0</playwright.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webmvc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Database Drivers -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Core Test Dependencies -->
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

        <!-- Testcontainers & ServiceConnection -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-testcontainers</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Challenge Dependencies: Pact Consumer, Playwright -->
        <dependency>
            <groupId>au.com.dius.pact.consumer</groupId>
            <artifactId>junit5</artifactId>
            <version>${pact.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.microsoft.playwright</groupId>
            <artifactId>playwright</artifactId>
            <version>${playwright.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

            <!-- Surefire Plugin for Unit & Slice Tests -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.2</version>
                <configuration>
                    <!-- JaCoCo @{argLine} placeholder + JDK 25 Dynamic Agent Loading -->
                    <argLine>@{argLine} -XX:+EnableDynamicAgentLoading</argLine>
                    <excludes>
                        <exclude>**/*IT.java</exclude>
                    </excludes>
                    <excludedGroups>e2e</excludedGroups>
                </configuration>
            </plugin>

            <!-- Failsafe Plugin for Integration Tests (*IT) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>3.5.2</version>
                <configuration>
                    <argLine>@{argLine} -XX:+EnableDynamicAgentLoading</argLine>
                    <includes>
                        <include>**/*IT.java</include>
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

            <!-- JaCoCo Code Coverage Gate (80% Line Minimum) -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.14</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>coverage-gate</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
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
                                    </limits>
                                </rule>
                            </rules>
                            <excludes>
                                <exclude>**/ShopApplication.class</exclude>
                                <exclude>**/dto/*</exclude>
                            </excludes>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- PIT Mutation Testing Plugin -->
            <plugin>
                <groupId>org.pitest</groupId>
                <artifactId>pitest-maven</artifactId>
                <version>${pitest.version}</version>
                <dependencies>
                    <dependency>
                        <groupId>org.pitest</groupId>
                        <artifactId>pitest-junit5-plugin</artifactId>
                        <version>${pitest-junit5.version}</version>
                    </dependency>
                </dependencies>
                <configuration>
                    <targetClasses>
                        <param>edu.itc.shop.domain.*</param>
                        <param>edu.itc.shop.service.*</param>
                    </targetClasses>
                    <targetTests>
                        <param>edu.itc.shop.*Test</param>
                    </targetTests>
                    <outputFormats>
                        <param>HTML</param>
                    </outputFormats>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

### Step 0.4: Test Configuration Profile (`application-test.yml`)

Create `src/test/resources/application-test.yml`. Every Spring-aware test activates the `test` profile:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:shop;DB_CLOSE_DELAY=-1
    username: sa
    password: ""
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: false
  sql:
    init:
      mode: never

shop:
  jwt:
    secret: test-secret-test-secret-test-secret-1234
  price-api:
    base-url: http://localhost:9999
```

---

### Step 0.5: Target Test Directory Layout

The `src/test/java` package structure cleanly mirrors `src/main/java`:

```
src/test/
├── java/edu/itc/shop/
│   ├── domain/
│   │   └── MoneyTest.java                    # Task 1: JUnit 6 Lifecycle & AssertJ
│   ├── service/
│   │   ├── PricingServiceTest.java           # Task 1: Grouped assertions & exceptions
│   │   ├── PricingRulesParamTest.java        # Task 2: CSV, Method, & Enum sources
│   │   ├── IdGeneratorTest.java              # Task 2: RepeatedTest & collision checks
│   │   ├── ProductServiceTest.java           # Task 3: Mockito isolation & Captors
│   │   └── PriceClientTest.java              # Task 5: MockRestServiceServer
│   ├── util/
│   │   └── MoneyFormatterSpyTest.java        # Task 3: Mockito @Spy partial mocking
│   ├── testing/
│   │   ├── ProductBuilder.java               # Task 3: Test Data Builder Pattern
│   │   └── Products.java                     # Task 3: Object Mother Pattern
│   ├── web/
│   │   ├── ProductControllerTest.java        # Task 4: @WebMvcTest & MockMvcTester
│   │   └── ProductApiIT.java                 # Task 5: Full-stack integration test
│   ├── repository/
│   │   ├── ProductRepositoryTest.java        # Task 4: @DataJpaTest on H2
│   │   ├── ProductQueryTest.java             # Task 4: @Sql fixture test
│   │   └── ProductRepositoryPostgresIT.java  # Task 5: Testcontainers PostgreSQL
│   ├── security/
│   │   └── JwtFilterTest.java                # Task 4: Security filter slice test
│   ├── regression/
│   │   ├── Bug101_RestockStockCheckTest.java     # Challenge: Regression bug #101
│   │   ├── Bug102_SearchWhitespaceTrimTest.java  # Challenge: Regression bug #102
│   │   └── Bug103_DiscountRoundingModeTest.java  # Challenge: Regression bug #103
│   ├── contract/
│   │   └── PriceClientPactTest.java          # Challenge: Pact JVM Consumer Contract
│   └── e2e/
│       └── ProductPageE2E.java               # Challenge: Playwright Browser Test
└── resources/
    ├── application-test.yml                  # H2, fixed JWT secret, mock URL
    └── fixtures/
        ├── tax-rates.csv                     # Task 2: Parameterized rates
        └── products.sql                      # Task 4: JPA query test data
```

---
## 🧪 Task 1: Unit Tests with JUnit 6: Money and PricingService (40 min - 10 pts)

### Architectural Analysis: JUnit 6 Lifecycle, Grouping & AssertJ Matchers

Unit testing in modern Java requires clear separation between business logic and framework glue. In Task 1, we test pure Java domain logic (`Money` and `PricingService`) with **zero Spring dependencies** and **zero Mockito mocks**.

Key architectural features utilized:
1. **`@DisplayName` Sentences:** Test output forms a human-readable functional specification.
2. **`@Nested` Contexts:** Hierarchical structuring of test fixtures (e.g., normal operations vs edge cases and error states).
3. **Grouped Assertions (`assertAll`):** Prevents short-circuiting on early failures so that all assertion violations within a logical transaction are surfaced simultaneously.
4. **AssertJ Tuples (`extracting(...).containsExactly(...)`):** Eliminates verbose loop assertions over collections, verifying order, size, and property mapping in a single atomic assertion.

---

### Step 1.1: Value Object Testing — `MoneyTest.java`

Create `src/test/java/edu/itc/shop/domain/MoneyTest.java`:

```java
package edu.itc.shop.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money")
class MoneyTest {

    @Test
    @DisplayName("of() preserves exact scale of two decimals")
    void ofKeepsScale() {
        Money m = Money.of("10");
        assertAll("scale preservation",
                () -> assertEquals(2, m.amount().scale(), "Scale must be set to 2"),
                () -> assertEquals(new BigDecimal("10.00"), m.amount(), "Value must match 10.00")
        );
    }

    @Test
    @DisplayName("plus() calculates the exact sum of two monetary amounts in identical currencies")
    void plusAddsAmounts() {
        Money m1 = Money.of("15.50");
        Money m2 = Money.of("4.50");
        Money result = m1.plus(m2);

        assertEquals(Money.of("20.00"), result);
    }

    @Test
    @DisplayName("times() multiplies an amount by an integer factor")
    void timesMultipliesAmount() {
        Money m = Money.of("12.30");
        Money result = m.times(3);

        assertEquals(Money.of("36.90"), result);
    }

    @Test
    @DisplayName("negative amount in constructor or factory throws IllegalArgumentException")
    void negativeAmountIsRejected() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> Money.of("-1.00"),
                "Negative monetary values must be rejected"
        );
        assertThat(ex.getMessage()).containsIgnoringCase("negative");
    }

    @Test
    @DisplayName("adding two different currencies throws IllegalArgumentException")
    void differentCurrenciesCannotBeAdded() {
        Money eur = Money.of("10.00", "EUR");
        Money usd = Money.of("10.00", "USD");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eur.plus(usd)
        );
        assertThat(ex.getMessage()).contains("EUR").contains("USD");
    }

    @Nested
    @DisplayName("rounding")
    class Rounding {

        @Test
        @DisplayName("half-up rounding applies when three decimals are supplied")
        void roundsHalfUp() {
            Money m = new Money(new BigDecimal("10.005"), Currency.getInstance("EUR"));
            assertEquals(new BigDecimal("10.01"), m.amount());
        }

        @Test
        @DisplayName("percentOff() computes discounted price with proper scale")
        void percentOffCalculatesDiscount() {
            Money original = Money.of("100.00");
            Money discounted = original.percentOff(Percent.of(15));

            assertEquals(Money.of("85.00"), discounted);
        }
    }
}
```

---

### Step 1.2: Pure Service Testing — `PricingServiceTest.java`

Create `src/test/java/edu/itc/shop/service/PricingServiceTest.java`:

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.PriceLine;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PricingService")
class PricingServiceTest {

    private static TaxTable taxTable;
    private PricingService pricingService;

    @BeforeAll
    static void initTaxTable() {
        taxTable = TaxTable.load("eu-2026");
    }

    @BeforeEach
    void setUp() {
        pricingService = new PricingService(taxTable);
    }

    @AfterEach
    void tearDown() {
        pricingService = null;
    }

    @Test
    @DisplayName("gross() adds DE standard VAT (19%) and breakdown matches")
    void grossCalculatesGermanVat() {
        Money net = Money.of("10.00");
        Money gross = pricingService.gross(net, "DE");
        List<PriceLine> lines = pricingService.breakdown(net, "DE");

        assertAll("gross calculation checks",
                () -> assertEquals(new BigDecimal("11.90"), gross.amount(), "Gross amount must be 11.90"),
                () -> assertEquals(Currency.getInstance("EUR"), gross.currency(), "Currency must remain EUR"),
                () -> assertEquals(3, lines.size(), "Breakdown must consist of 3 lines")
        );
    }

    @Test
    @DisplayName("breakdown() for FR standard VAT (20%) matches exact price lines via AssertJ tuple")
    void breakdownExtractsExactPriceLinesForFrance() {
        Money net = Money.of("100.00");
        List<PriceLine> lines = pricingService.breakdown(net, "FR");

        assertThat(lines)
                .extracting(PriceLine::label, PriceLine::amount)
                .containsExactly(
                        tuple("net", Money.of("100.00")),
                        tuple("VAT 20 %", Money.of("20.00")),
                        tuple("gross", Money.of("120.00"))
                );
    }

    @Nested
    @DisplayName("When country is unknown")
    class WhenCountryIsUnknown {

        @Test
        @DisplayName("tax lookup throws IllegalArgumentException containing country code")
        void unknownCountryThrowsException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> pricingService.gross(Money.of("50.00"), "XX")
            );
            assertThat(ex.getMessage()).contains("XX");
        }
    }
}
```

---

### Step 1.3: Grouped Failure Diagnostics & Surefire Report Inspection

Run the Task 1 test suite via Maven:

```bash
./mvnw test -Dtest=MoneyTest,PricingServiceTest
```

Expected terminal output:
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running edu.itc.shop.domain.MoneyTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0 -- in edu.itc.shop.domain.MoneyTest
[INFO] Running edu.itc.shop.service.PricingServiceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 -- in edu.itc.shop.service.PricingServiceTest
[INFO] Results:
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Verify that Surefire generates reports with full `@DisplayName` strings in `target/surefire-reports/TEST-edu.itc.shop.domain.MoneyTest.xml`.

---

### Task 1 Checklist & Tagging

- [x] `MoneyTest` includes at least 7 tests with nested rounding class.
- [x] Scale preservation, negative rejection, and currency mismatch validations pass.
- [x] `PricingServiceTest` correctly declares static `@BeforeAll`, `@BeforeEach`, and `@AfterEach`.
- [x] `assertAll` bundles gross amount, currency, and line count checks.
- [x] AssertJ `extracting(...).containsExactly(tuple(...))` verifies full line breakdown.
- [x] Commit: `git commit -m "task1: unit tests for Money and PricingService"`

---
## 🔄 Task 2: Parameterized and Repeated Tests (40 min - 15 pts)

### Architectural Analysis: Data-Driven Test Design & Stochastic Verification

Data-driven testing eliminates repetitive assertion blocks by separating test logic from input matrices. Task 2 exercises all primary JUnit Jupiter parameter providers:
- `@CsvSource`: Inline matrix evaluation for rapid feedback.
- `@CsvFileSource`: Externalized classpath tabular fixtures (`tax-rates.csv`).
- `@MethodSource`: Complex object injection (`Stream<Arguments>`) evaluating boundary conditions.
- `@NullAndEmptySource` + `@ValueSource`: Whitespace boundary testing.
- `@EnumSource`: State machine domain invariants using inclusion and exclusion modes.
- `@RepeatedTest`: Stochastic fuzzing with thread-safe sets to detect pseudorandom collisions.

---

### Step 2.1: Classpath CSV Fixture — `fixtures/tax-rates.csv`

Create `src/test/resources/fixtures/tax-rates.csv`:

```csv
net,country,gross
10.00,DE,11.90
100.00,FR,120.00
9.99,IE,12.29
10.00,,10.00
```

---

### Step 2.2: Parameterized Rule Verification — `PricingRulesParamTest.java`

Create `src/test/java/edu/itc/shop/service/PricingRulesParamTest.java`:

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.OrderStatus;
import edu.itc.shop.domain.Percent;
import edu.itc.shop.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("PricingRulesParamTest")
class PricingRulesParamTest {

    private static final TaxTable taxes = TaxTable.load("eu-2026");
    private final PricingService pricing = new PricingService(taxes);

    @ParameterizedTest(name = "[{index}] {0} → {1} %")
    @CsvSource({
            "DE, 19",
            "FR, 20",
            "IE, 23",
            "'', 0"
    })
    @DisplayName("Inline CSV evaluates exact tax rates including empty country")
    void vatRatePerCountry(String country, int expectedRate) {
        assertEquals(expectedRate, taxes.rateFor(country));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/fixtures/tax-rates.csv", numLinesToSkip = 1)
    @DisplayName("External CSV file evaluates gross calculation across diverse tax zones")
    void grossFromCsvFile(String net, String country, String expectedGross) {
        String resolvedCountry = Objects.requireNonNullElse(country, "");
        Money netMoney = Money.of(net);
        Money actualGross = pricing.gross(netMoney, resolvedCountry);

        assertEquals(Money.of(expectedGross), actualGross);
    }

    @ParameterizedTest
    @MethodSource("discountCases")
    @DisplayName("MethodSource evaluates exact discount rules and edge boundaries")
    void discountIsApplied(Money net, Percent off, Money expected) {
        assertEquals(expected, pricing.discounted(net, off));
    }

    static Stream<Arguments> discountCases() {
        return Stream.of(
                arguments(Money.of("100.00"), Percent.of(0), Money.of("100.00")),
                arguments(Money.of("100.00"), Percent.of(15), Money.of("85.00")),
                arguments(Money.of("0.01"), Percent.of(50), Money.of("0.01")),     // 0.01 floor
                arguments(Money.of("50.00"), Percent.of(100), Money.of("0.00")),   // 100% off
                arguments(Money.of("10.00"), Percent.of(33), Money.of("6.70"))     // half-up rounding
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "	", "   "})
    @DisplayName("Blank SKU values must be rejected by Product constructor")
    void blankSkuIsRejected(String invalidSku) {
        assertThrows(IllegalArgumentException.class, () -> new Product(invalidSku, "Valid Name", 100));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PAID", "SHIPPED", "CANCELLED"})
    @DisplayName("Closed order statuses must not be editable")
    void closedStatusesAreNotEditable(OrderStatus status) {
        assertFalse(status.isEditable(), "Status " + status + " should not be editable");
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"PAID", "SHIPPED", "CANCELLED"})
    @DisplayName("Open order statuses (NEW) must be editable")
    void openStatusesAreEditable(OrderStatus status) {
        assertTrue(status.isEditable(), "Status " + status + " must be editable");
    }
}
```

---

### Step 2.3: Randomness & Collision Testing — `IdGeneratorTest.java`

Create `src/test/java/edu/itc/shop/service/IdGeneratorTest.java`:

```java
package edu.itc.shop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("IdGeneratorTest")
class IdGeneratorTest {

    private static final Pattern ID_PATTERN = Pattern.compile("^ORD-[0-9A-Z]{8}$");
    private static final Set<String> generatedIds = ConcurrentHashMap.newKeySet();
    private final IdGenerator generator = new IdGenerator();

    @RepeatedTest(value = 200, name = "id {currentRepetition}/{totalRepetitions}")
    @DisplayName("Generates unique 8-character base-36 IDs without collision")
    void idConformsToFormatAndHasNoCollisions(RepetitionInfo info) {
        String id = generator.nextOrderId();

        assertTrue(
                ID_PATTERN.matcher(id).matches(),
                () -> "ID '%s' at repetition %d/%d does not conform to regex ORD-[0-9A-Z]{8}"
                        .formatted(id, info.getCurrentRepetition(), info.getTotalRepetitions())
        );

        boolean isUnique = generatedIds.add(id);
        assertTrue(
                isUnique,
                () -> "Collision detected! Duplicate ID '%s' found at repetition %d/%d"
                        .formatted(id, info.getCurrentRepetition(), info.getTotalRepetitions())
        );
    }
}
```

---

### Step 2.4: Execution & Per-Invocation Tree Verification

Execute the parameterized and repeated test targets:

```bash
./mvnw test -Dtest='*ParamTest,IdGeneratorTest'
```

Expected terminal output:
```text
[INFO] Running edu.itc.shop.service.PricingRulesParamTest
PricingRulesParamTest
vatRatePerCountry(String, int)
  ✔ [1] DE → 19 %
  ✔ [2] FR → 20 %
  ✔ [3] IE → 23 %
  ✔ [4] '' → 0 %
grossFromCsvFile(String, String, String)     4 invocations ✔
discountIsApplied(Money, Percent, Money)     5 invocations ✔
blankSkuIsRejected(String)                   5 invocations ✔
closedStatusesAreNotEditable(OrderStatus)   3 invocations ✔
openStatusesAreEditable(OrderStatus)         1 invocation  ✔
[INFO] Running edu.itc.shop.service.IdGeneratorTest
IdGeneratorTest
  id 1/200 ... id 200/200                  200 invocations ✔
[INFO] Tests run: 232, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### Task 2 Checklist & Tagging

- [x] `@CsvSource` and `tax-rates.csv` test tax rates including the empty country string.
- [x] `@MethodSource` tests 0%, 15%, 50% on 0.01 floor, 100%, and 33% half-up.
- [x] `@NullAndEmptySource` and `@ValueSource` cover SKU edge cases.
- [x] `@EnumSource` tests both `names` inclusion and `mode = EXCLUDE`.
- [x] `IdGeneratorTest` performs 200 iterations with thread-safe collision detection and `RepetitionInfo`.
- [x] Commit: `git commit -m "task2: parameterized and repeated tests"`

---
## 🎭 Task 3: Mockito: Isolating ProductService (60 min - 20 pts)

### Architectural Analysis: Boundary Isolation, Test Data Builders & Strict Stubs

Testing services in isolation requires replacing collaborating network and database boundaries with controlled doubles. Mockito 5 provides annotation-driven mock management and strict stub validation.

Key design patterns applied:
1. **Test Data Builder Pattern (`ProductBuilder`):** Provides a fluent interface for constructing test entities with safe, unique defaults, shielding test suites from constructor refactorings.
2. **Object Mother Pattern (`Products`):** Centralizes recurring test fixtures (`keyboard()`, `soldOutMouse()`).
3. **Deterministic Clock (`Clock.fixed`):** Freezes temporal assertions to an exact millisecond, eradicating timestamp flakiness.
4. **`ArgumentCaptor` & `InOrder`:** Verifies entity state upon persistence and proves sequential protocol constraints (price fetched prior to entity save).
5. **Partial Mocking (`@Spy`):** Inspects and partially stubs real helper classes (`MoneyFormatter`) via `doReturn()`.

---

### Step 3.1: Test Data Builders & Object Mother (`ProductBuilder`, `Products`)

Create `src/test/java/edu/itc/shop/testing/ProductBuilder.java`:

```java
package edu.itc.shop.testing;

import edu.itc.shop.domain.Product;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductBuilder {

    private static final AtomicInteger COUNTER = new AtomicInteger(100);

    private String sku = "SKU-" + COUNTER.incrementAndGet();
    private String name = "Default Product";
    private int priceCents = 1999;
    private int stock = 10;
    private Instant createdAt = Instant.parse("2026-09-25T10:00:00Z");

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public ProductBuilder withSku(String sku) {
        this.sku = sku;
        return this;
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withPriceCents(int priceCents) {
        this.priceCents = priceCents;
        return this;
    }

    public ProductBuilder withStock(int stock) {
        this.stock = stock;
        return this;
    }

    public ProductBuilder withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Product build() {
        return new Product(sku, name, priceCents, stock, createdAt);
    }
}
```

Create `src/test/java/edu/itc/shop/testing/Products.java`:

```java
package edu.itc.shop.testing;

import edu.itc.shop.domain.Product;

import static edu.itc.shop.testing.ProductBuilder.aProduct;

public final class Products {
    private Products() {}

    public static Product keyboard() {
        return aProduct()
                .withSku("KB-01")
                .withName("Mechanical Keyboard")
                .withPriceCents(4990)
                .withStock(15)
                .build();
    }

    public static Product soldOutMouse() {
        return aProduct()
                .withSku("MS-01")
                .withName("Gaming Mouse")
                .withPriceCents(2990)
                .withStock(0)
                .build();
    }
}
```

---

### Step 3.2: Isolated Service Tests — `ProductServiceTest.java`

Create `src/test/java/edu/itc/shop/service/ProductServiceTest.java`:

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Product;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.web.dto.CreateProduct;
import edu.itc.shop.web.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static edu.itc.shop.testing.ProductBuilder.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceTest")
class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @Mock
    private PriceClient prices;

    @Captor
    private ArgumentCaptor<Product> savedProduct;

    private final Clock clock = Clock.fixed(Instant.parse("2026-09-25T10:00:00Z"), ZoneOffset.UTC);
    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repo, prices, clock);
    }

    @Test
    @DisplayName("create() queries external price, stamps clock, and saves entity in correct order")
    void createFetchesThePriceThenSavesTheProduct() {
        when(prices.currentPrice("KB-01")).thenReturn(Money.of("49.90"));
        when(repo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDto dto = service.create(new CreateProduct("KB-01", "Mechanical Keyboard"));

        InOrder inOrder = inOrder(prices, repo);
        inOrder.verify(prices).currentPrice("KB-01");
        inOrder.verify(repo).save(savedProduct.capture());

        Product captured = savedProduct.getValue();
        assertThat(captured.sku()).isEqualTo("KB-01");
        assertThat(captured.priceCents()).isEqualTo(4990);
        assertThat(captured.createdAt()).isEqualTo(clock.instant());
        assertThat(dto.priceCents()).isEqualTo(4990);
    }

    @Test
    @DisplayName("create() aborts and never calls repository if external price client fails")
    void createDoesNotSaveWhenThePriceApiIsDown() {
        when(prices.currentPrice("KB-01")).thenThrow(new PriceUnavailableException("503 Service Unavailable"));

        assertThatThrownBy(() -> service.create(new CreateProduct("KB-01", "Mechanical Keyboard")))
                .isInstanceOf(PriceUnavailableException.class)
                .hasMessageContaining("503");

        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("get() throws ProductNotFoundException when product ID does not exist")
    void getThrowsNotFoundException() {
        when(repo.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(42L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product 42 not found");
    }

    @Test
    @DisplayName("restock() with negative delta larger than stock throws InsufficientStockException")
    void restockThrowsInsufficientStock() {
        Product existing = aProduct().withStock(5).build();
        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.restock(1L, -10))
                .isInstanceOf(InsufficientStockException.class);

        verifyNoInteractions(prices);
    }
}
```

---

### Step 3.3: Partial Mocking with Real Collaborators — `MoneyFormatterSpyTest.java`

Create `src/test/java/edu/itc/shop/util/MoneyFormatterSpyTest.java`:

```java
package edu.itc.shop.util;

import edu.itc.shop.domain.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MoneyFormatterSpyTest")
class MoneyFormatterSpyTest {

    @Spy
    private MoneyFormatter formatter = new MoneyFormatter(Locale.GERMANY);

    @Test
    @DisplayName("@Spy executes real method formatting and allows partial doReturn override")
    void spyFormatsRealMoneyAndAllowsPartialStub() {
        // Real invocation
        String germanFormatted = formatter.format(Money.of("49.90"));
        assertThat(germanFormatted).contains("49,90");

        // Partial stub via doReturn to avoid executing real method
        doReturn("n/a").when(formatter).format(Money.ZERO);

        assertThat(formatter.format(Money.ZERO)).isEqualTo("n/a");
        verify(formatter, times(2)).format(any());
    }
}
```

---

### Step 3.4: Strict Stubs Incident & Root Cause Analysis (`NOTES.md`)

Mockito 5 enforces `Strictness.STRICT_STUBS` by default in `MockitoExtension`. Introducing an unused stub generates an immediate build failure.

#### 1. Deliberate Failure Reproduction:
Add the following unused stub to a test method:
```java
when(repo.findById(999L)).thenReturn(Optional.empty()); // Never called!
```

Maven execution:
```bash
./mvnw test -Dtest=ProductServiceTest
```

Console error output:
```text
[ERROR] ProductServiceTest.getReturnsExistingProduct
org.mockito.exceptions.misusing.UnnecessaryStubbingException:
Unnecessary stubbings detected.
Clean & maintainable test code requires zero unnecessary code.
Following stubbings are unnecessary (click to navigate to relevant line of code):
  1. -> at edu.itc.shop.service.ProductServiceTest.getReturnsExistingProduct(ProductServiceTest.java:61)
Please remove unnecessary stubbings or use 'lenient' strictness.
```

#### 2. Root Cause & Solution:
Unnecessary stubbings indicate obsolete test assumptions that misrepresent the method under test. To fix, delete the superfluous stub rather than masking it with `lenient()`. Record this incident and resolution in `NOTES.md`.

---

### Task 3 Checklist & Tagging

- [x] `ProductBuilder` and `Products` Object Mother created and used.
- [x] `ProductServiceTest` uses `MockitoExtension`, `@Mock`, `@Captor`, and `Clock.fixed`.
- [x] `InOrder` verifies price lookup before repository persistence.
- [x] Negative verification: `verify(repo, never()).save(...)` and `verifyNoInteractions(prices)`.
- [x] `@Spy` on `MoneyFormatter` exercises real logic and `doReturn` partial stubbing.
- [x] Strict stub incident documented in `NOTES.md`.
- [x] Commit: `git commit -m "task3: ProductService unit tests with Mockito"`

---
## 🍰 Task 4: Slice Tests: @WebMvcTest, @DataJpaTest and the JWT Filter (60 min - 20 pts)

### Architectural Analysis: Fast Slices, Context Boundaries & First-Level Cache Detachment

Spring Boot 4 test slices narrow application context initialization to specific infrastructural layers:
- `@WebMvcTest`: Boots only MVC controllers, message converters, and security filters. Business services are mocked using `@MockitoBean`. Assertions utilize `MockMvcTester` (fluent AssertJ API).
- `@DataJpaTest`: Boots JPA entities, Spring Data repositories, and `TestEntityManager`. Tests run against an in-memory H2 database with automatic transaction rollback.
- **Detaching the First-Level Cache:** Tests must call `TestEntityManager.flush()` and `TestEntityManager.clear()` prior to assertions. Failing to clear the Hibernate persistence context allows queries to resolve against first-level session cache rather than executing real SQL, creating false-positive passes.
- **Security Slice Verification:** Imports `SecurityConfig` and `JwtFilter` to verify Bearer token authentication and RFC 9457 `application/problem+json` envelopes.

---

### Step 4.1: Web Layer Testing with `MockMvcTester` — `ProductControllerTest.java`

Create `src/test/java/edu/itc/shop/web/ProductControllerTest.java`:

```java
package edu.itc.shop.web;

import edu.itc.shop.security.JwtFilter;
import edu.itc.shop.security.SecurityConfig;
import edu.itc.shop.service.ProductNotFoundException;
import edu.itc.shop.service.ProductService;
import edu.itc.shop.web.dto.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtFilter.class})
@DisplayName("ProductControllerTest")
class ProductControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ProductService service;

    @MockitoBean
    private edu.itc.shop.security.JwtService jwtService;

    @Test
    @WithMockUser
    @DisplayName("GET /api/products returns HTTP 200 with JSON product array")
    void listReturnsProducts() {
        given(service.search(null)).willReturn(List.of(
                new ProductDto(1L, "KB-01", "Mechanical Keyboard", 4990, 5)
        ));

        assertThat(mvc.get().uri("/api/products"))
                .hasStatusOk()
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().extractingPath("$[0].name").isEqualTo("Mechanical Keyboard");
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/products/999 returns HTTP 404 application/problem+json")
    void getByIdNotFoundReturnsProblemDetail() {
        given(service.get(999L)).willThrow(new ProductNotFoundException("Product 999 not found"));

        assertThat(mvc.get().uri("/api/products/999"))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().extractingPath("$.detail").isEqualTo("Product 999 not found");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/products with blank name returns HTTP 400 ProblemDetail with field error")
    void createWithBlankNameIs400() {
        assertThat(mvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sku": "KB-01", "name": ""}
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().extractingPath("$.errors[0].field").isEqualTo("name");

        then(service).shouldHaveNoInteractions();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/products as non-admin user returns HTTP 403 Forbidden")
    void createAsUserReturns403() {
        assertThat(mvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sku": "KB-01", "name": "Mechanical Keyboard"}
                        """))
                .hasStatus(HttpStatus.FORBIDDEN);

        then(service).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("POST /api/products without credentials returns HTTP 401 Unauthorized")
    void createWithoutAuthReturns401() {
        assertThat(mvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sku": "KB-01", "name": "Mechanical Keyboard"}
                        """))
                .hasStatus(HttpStatus.UNAUTHORIZED);

        then(service).shouldHaveNoInteractions();
    }
}
```

---

### Step 4.2: Data Persistence Slice — `ProductRepositoryTest.java`

Create `src/test/java/edu/itc/shop/repository/ProductRepositoryTest.java`:

```java
package edu.itc.shop.repository;

import edu.itc.shop.domain.Product;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static edu.itc.shop.testing.ProductBuilder.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductRepositoryTest")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ProductRepository repo;

    @Test
    @DisplayName("findByNameContainingIgnoreCase queries database detached from first-level cache")
    void findsByNameIgnoringCase() {
        em.persist(aProduct().withSku("KB-01").withName("Mechanical Keyboard").build());
        em.persist(aProduct().withSku("MS-01").withName("Gaming Mouse").build());
        em.flush();
        em.clear(); // Detach to force real SQL query execution

        List<Product> products = repo.findByNameContainingIgnoreCase("KEYBOARD");

        assertThat(products)
                .hasSize(1)
                .first()
                .satisfies(p -> assertThat(p.sku()).isEqualTo("KB-01"));

        Optional<Product> missing = repo.findBySku("nope");
        assertThat(missing).isEmpty();
    }

    @Test
    @DisplayName("Persisting duplicate SKU throws PersistenceException on flush")
    void duplicateSkuThrowsPersistenceException() {
        em.persistAndFlush(aProduct().withSku("DUPLICATE-SKU").withName("Item A").build());

        assertThrows(PersistenceException.class, () -> {
            em.persistAndFlush(aProduct().withSku("DUPLICATE-SKU").withName("Item B").build());
        });
    }
}
```

---

### Step 4.3: SQL Data Fixtures & Derived Queries — `ProductQueryTest.java`

Create `src/test/resources/fixtures/products.sql`:

```sql
INSERT INTO products (sku, name, price_cents, stock, created_at) VALUES ('SQL-01', 'Cheap Cable', 999, 50, CURRENT_TIMESTAMP);
INSERT INTO products (sku, name, price_cents, stock, created_at) VALUES ('SQL-02', 'Budget Mouse', 1499, 20, CURRENT_TIMESTAMP);
INSERT INTO products (sku, name, price_cents, stock, created_at) VALUES ('SQL-03', 'Pro Monitor', 39900, 5, CURRENT_TIMESTAMP);
```

Create `src/test/java/edu/itc/shop/repository/ProductQueryTest.java`:

```java
package edu.itc.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductQueryTest")
class ProductQueryTest {

    @Autowired
    private ProductRepository repo;

    @Test
    @Sql("/fixtures/products.sql")
    @DisplayName("countByPriceCentsLessThan counts rows under threshold loaded from external SQL script")
    void countsProductsUnderPriceThreshold() {
        long count = repo.countByPriceCentsLessThan(2000);
        assertThat(count).isEqualTo(2);
    }
}
```

---

### Step 4.4: Security Filter Chain Verification — `JwtFilterTest.java`

Create `src/test/java/edu/itc/shop/security/JwtFilterTest.java`:

```java
package edu.itc.shop.security;

import edu.itc.shop.service.ProductService;
import edu.itc.shop.web.ProductController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtFilter.class})
@DisplayName("JwtFilterTest")
class JwtFilterTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("Request without Authorization header returns 401 and bypasses JwtService")
    void missingHeaderReturns401() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());

        then(jwtService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("Valid Bearer token authenticates principal and permits endpoint access")
    void validBearerTokenAuthenticatesUser() throws Exception {
        given(jwtService.parse("valid-token")).willReturn(new JwtClaims("alice", List.of("ROLE_USER")));

        mvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("alice"));
    }

    @Test
    @DisplayName("Expired Bearer token returns 401 ProblemDetail with detail='expired'")
    void expiredBearerTokenReturns401WithProblemDetail() throws Exception {
        given(jwtService.parse("expired-token")).willThrow(new JwtException("expired"));

        mvc.perform(get("/api/products")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer expired-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("expired"));
    }
}
```

---

### Step 4.5: Slice Timing Benchmarks (`NOTES.md`)

Execute slice tests and record elapsed time comparisons:

```bash
./mvnw test -Dtest='ProductControllerTest,ProductRepositoryTest,ProductQueryTest,JwtFilterTest'
```

Expected output:
```text
[INFO] Running edu.itc.shop.web.ProductControllerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.8 s
[INFO] Running edu.itc.shop.repository.ProductRepositoryTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.3 s
[INFO] Running edu.itc.shop.repository.ProductQueryTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.3 s
[INFO] Running edu.itc.shop.security.JwtFilterTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.5 s
```

> **Performance Analysis for `NOTES.md`:**  
> Slices initialize in ~0.5 to 1.8 seconds compared to pure unit tests (~20–40 milliseconds), but remain 5x–10x faster than full `@SpringBootTest` container boots while isolating architectural boundaries.

---

### Task 4 Checklist & Tagging

- [x] `ProductControllerTest` validates 200, 400 (with field error), 401, 403, and 404 ProblemDetail.
- [x] `ProductRepositoryTest` employs `TestEntityManager.flush()` and `clear()`.
- [x] Unique constraint violation confirmed via `PersistenceException`.
- [x] `products.sql` executes cleanly under `@Sql` in `ProductQueryTest`.
- [x] `JwtFilterTest` verifies missing token, valid token (`alice`), and expired token (`expired`).
- [x] Commit: `git commit -m "task4: slice tests for web, JPA and security"`

---
## 🏗️ Task 5: Integration Tests, Coverage Gate and CI (90 min - 25 pts)

### Architectural Analysis: Real Databases, HTTP Wire Mocking & Coverage Enforcement

The apex of the test pyramid requires verifying the assembled application against real infrastructure:
1. **Failsafe Lifecycle Separation:** Integration tests (`*IT.java`) are executed during `mvn verify`, keeping `mvn test` ultra-fast.
2. **Dynamic PostgreSQL Container:** Testcontainers manages an ephemeral PostgreSQL 17 instance. Spring Boot 3.4+ `@ServiceConnection` dynamically injects JDBC credentials into the test application context, requiring zero manual property overrides.
3. **Graceful Docker Fallback:** Annotated with `@Testcontainers(disabledWithoutDocker = true)`. When executed in environments without a Docker daemon, container tests are skipped cleanly rather than failing the build.
4. **Outbound HTTP Contract Verification:** `MockRestServiceServer` intercepts wire traffic from `RestClient`, confirming request URLs, HTTP verbs, and headers.
5. **JaCoCo Quality Gate:** Enforces an uncompromised **80% line coverage threshold** across the bundle before allowing release packaging.
6. **Continuous Integration Pipeline:** Automates verification on push and pull requests with automated test report and coverage artifact preservation.

---

### Step 5.1: Build Lifecycle Separation (Failsafe vs Surefire) & JaCoCo Plugin

Ensure `pom.xml` contains the Failsafe plugin and JaCoCo configuration detailed in [Step 0.3](#step-03-enterprise-build-configuration-pomxml). Verify:
- Surefire excludes `**/*IT.java`.
- Failsafe includes `**/*IT.java` under the `integration-test` and `verify` phases.
- JaCoCo `argLine` is prepended with `@{argLine}`.

---

### Step 5.2: Full-Stack Integration Testing — `ProductApiIT.java`

Create `src/test/java/edu/itc/shop/web/ProductApiIT.java`:

```java
package edu.itc.shop.web;

import edu.itc.shop.domain.Money;
import edu.itc.shop.security.JwtService;
import edu.itc.shop.service.PriceClient;
import edu.itc.shop.web.dto.CreateProduct;
import edu.itc.shop.web.dto.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("ProductApiIT")
class ProductApiIT {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private PriceClient priceClient;

    @Test
    @DisplayName("Admin creates product via POST (201 Created), follows Location header to GET (200 OK)")
    void createAndGetProductRoundTrip() {
        given(priceClient.currentPrice("IT-KB-01")).willReturn(Money.of("49.90"));

        String adminToken = jwtService.issue("admin_user", List.of("ROLE_ADMIN", "ROLE_USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        CreateProduct request = new CreateProduct("IT-KB-01", "Full Stack Keyboard");
        HttpEntity<CreateProduct> entity = new HttpEntity<>(request, headers);

        // Act: POST create
        ResponseEntity<ProductDto> createResponse = rest.postForEntity("/api/products", entity, ProductDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = createResponse.getHeaders().getLocation();
        assertThat(location).isNotNull();

        // Act: GET by location
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<ProductDto> getResponse = rest.exchange(location, HttpMethod.GET, getEntity, ProductDto.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().name()).isEqualTo("Full Stack Keyboard");
        assertThat(getResponse.getBody().priceCents()).isEqualTo(4990);
    }

    @Test
    @DisplayName("GET /api/products/999 returns HTTP 404 ProblemDetail envelope")
    void getMissingProductReturns404ProblemDetail() {
        String userToken = jwtService.issue("regular_user", List.of("ROLE_USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ProblemDetail> response = rest.exchange(
                "/api/products/999", HttpMethod.GET, entity, ProblemDetail.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).isEqualTo("Product 999 not found");
    }
}
```

---

### Step 5.3: Containerized PostgreSQL Testing — `ProductRepositoryPostgresIT.java`

Create `src/test/java/edu/itc/shop/repository/ProductRepositoryPostgresIT.java`:

```java
package edu.itc.shop.repository;

import edu.itc.shop.domain.Product;
import edu.itc.shop.testing.Products;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("ProductRepositoryPostgresIT")
class ProductRepositoryPostgresIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private ProductRepository repo;

    @Test
    @DisplayName("Derived query findByNameContainingIgnoreCase executes on genuine PostgreSQL container")
    void derivedQueryRunsOnRealPostgres() {
        repo.save(Products.keyboard());

        List<Product> results = repo.findByNameContainingIgnoreCase("KEYBOARD");
        assertThat(results)
                .hasSize(1)
                .first()
                .satisfies(p -> assertThat(p.sku()).isEqualTo("KB-01"));
    }

    @Test
    @DisplayName("PostgreSQL ILIKE query executes native pattern matching correctly")
    void searchByPatternMatchesCaseInsensitive() {
        repo.save(Products.keyboard());

        List<Product> results = repo.searchByPattern("board");
        assertThat(results)
                .hasSize(1)
                .first()
                .satisfies(p -> assertThat(p.name()).isEqualTo("Mechanical Keyboard"));
    }
}
```

---

### Step 5.4: Outbound HTTP Client Wire Testing — `PriceClientTest.java`

Create `src/test/java/edu/itc/shop/service/PriceClientTest.java`:

```java
package edu.itc.shop.service;

import edu.itc.shop.domain.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(PriceClient.class)
@TestPropertySource(properties = "shop.price-api.base-url=")
@DisplayName("PriceClientTest")
class PriceClientTest {

    @Autowired
    private PriceClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    @DisplayName("GET /prices/{sku} parses JSON payload into domain Money")
    void parsesThePrice() {
        server.expect(requestTo("/prices/KB-01"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"sku":"KB-01","amount":"49.90","currency":"EUR"}
                        """, MediaType.APPLICATION_JSON));

        Money price = client.currentPrice("KB-01");

        assertThat(price).isEqualTo(Money.of("49.90"));
        server.verify();
    }

    @Test
    @DisplayName("Downstream HTTP 5xx maps directly to PriceUnavailableException")
    void serverErrorThrowsPriceUnavailableException() {
        server.expect(requestTo("/prices/FAIL-01"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.currentPrice("FAIL-01"))
                .isInstanceOf(PriceUnavailableException.class);

        server.verify();
    }
}
```

---

### Step 5.5: JaCoCo 80% Coverage Gate Verification & Audit

Execute the complete Maven verification lifecycle:

```bash
./mvnw clean verify
```

Expected terminal execution:
```text
[INFO] --- surefire:3.5.2:test (default-test) @ shop-tests ---
[INFO] Tests run: 262, Failures: 0, Errors: 0, Skipped: 0
[INFO] --- failsafe:3.5.2:integration-test (default) @ shop-tests ---
[INFO] Running edu.itc.shop.repository.ProductRepositoryPostgresIT
... tc.postgres:17-alpine: Container postgres:17-alpine started in PT2.8S
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in ProductRepositoryPostgresIT
[INFO] Running edu.itc.shop.web.ProductApiIT
[INFO] Tomcat started on port 51733 (http) with context path '/'
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in ProductApiIT
[INFO] --- jacoco:0.8.14:report (report) @ shop-tests ---
[INFO] Analyzed bundle 'shop-tests' with 23 classes
[INFO] --- jacoco:0.8.14:check (coverage-gate) @ shop-tests ---
[INFO] All coverage checks have been met.
[INFO] BUILD SUCCESS
```

Without Docker running, the output gracefully reports:
```text
[WARNING] Tests run: 2, Failures: 0, Errors: 0, Skipped: 2 -- in ProductRepositoryPostgresIT
(disabledWithoutDocker: Docker is not available)
[INFO] BUILD SUCCESS
```

Open `target/site/jacoco/index.html` in your browser. Record the three least-covered classes in `NOTES.md` along with planned tests.

---

### Step 5.6: GitHub Actions Automated CI Pipeline (`.github/workflows/ci.yml`)

Create `.github/workflows/ci.yml`:

```yaml
name: CI

on:
  push:
    branches: [main, 'lab-*']
  pull_request:
    branches: [main]

jobs:
  verify:
    runs-on: ubuntu-latest
    timeout-minutes: 20

    steps:
      - name: Check out repository
        uses: actions/checkout@v4

      - name: Set up Java 25 (Eclipse Temurin)
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '25'
          cache: maven

      - name: Run complete Maven verification lifecycle
        run: ./mvnw -B --no-transfer-progress verify

      - name: Upload Test & Coverage Artifacts
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: verification-reports
          path: |
            target/surefire-reports
            target/failsafe-reports
            target/site/jacoco
```

---

### Task 5 Checklist & Tagging

- [x] Failsafe plugin executes `*IT` classes in `verify` phase.
- [x] `ProductApiIT` performs 201 + Location + GET round trip on a random port.
- [x] `ProductRepositoryPostgresIT` uses `@ServiceConnection` and skips gracefully without Docker.
- [x] `PriceClientTest` uses `MockRestServiceServer` and tests error mapping.
- [x] JaCoCo line coverage gate set to `0.80` and passing.
- [x] `.github/workflows/ci.yml` configured and committed.
- [x] Commit: `git commit -m "task5: integration tests, JaCoCo gate, CI workflow"`

---
## 🌟 Challenge: Regression Suite, Consumer Contract, E2E Test, Mutation Testing (+20 pts)

### Challenge Overview & Strategy

The challenge delivers four enterprise testing capabilities:
1. **Regression Test Suite (TDD Bug Reproductions):** Write failing regression tests for tickets #101, #102, and #103, commit red, fix `src/main`, and commit green.
2. **Consumer-Driven Contract Testing (Pact JVM):** Define and generate consumer contract `target/pacts/shop-price-api.json` with matchers.
3. **End-to-End Browser Testing (Playwright):** Automate headless Chromium browser flows asserting UI rendering and cart updates.
4. **Mutation Testing (PIT):** Compute mutation coverage, analyze surviving mutants, and improve test strength.

---

### Step C.1: Regression Test Suite (TDD Bug Reproductions & Fixes)

#### Bug #101: Stock Underflow Check on Negative Restock

> **Report #101:** *"Restocking with a negative delta larger than the stock leaves the stock negative."*  
> **Root Cause:** In `ProductService.restock`, the check evaluated `stock + delta < 0` instead of rejecting transactions when `stock + delta <= 0` or negative delta exceeds available stock.

Create `src/test/java/edu/itc/shop/regression/Bug101_RestockStockCheckTest.java`:

```java
package edu.itc.shop.regression;

import edu.itc.shop.domain.Product;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.service.InsufficientStockException;
import edu.itc.shop.service.PriceClient;
import edu.itc.shop.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static edu.itc.shop.testing.ProductBuilder.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Bug #101: "Restocking with a negative delta larger than the stock leaves the stock negative."
 * Reported 2026-09-18 by support. Fixed in ProductService.restock.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Bug #101: Restock Stock Validation")
class Bug101_RestockStockCheckTest {

    @Mock
    private ProductRepository repo;

    @Mock
    private PriceClient prices;

    @Test
    @DisplayName("bug101: restock below zero is rejected and stock remains unchanged")
    void bug101_restockBelowZeroIsRejectedAndStockIsUnchanged() {
        ProductService service = new ProductService(repo, prices, Clock.systemUTC());
        Product p = aProduct().withStock(5).build();
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        assertThatThrownBy(() -> service.restock(1L, -6))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(p.stock()).isEqualTo(5);
        verify(repo, never()).save(any());
    }
}
```

Production Fix in `src/main/java/edu/itc/shop/service/ProductService.java`:
```java
// Fix for Bug #101
if (product.stock() + delta < 0) {
    throw new InsufficientStockException("Insufficient stock for product " + id + ": available=" + product.stock() + ", delta=" + delta);
}
```

---

#### Bug #102: Search Query Trailing Whitespace Normalization

> **Report #102:** *"Searching 'keyboard ' with a trailing space returns nothing, although the product exists."*  
> **Root Cause:** In `ProductService.search`, queries were passed directly to `repository.findByNameContainingIgnoreCase` without trimming whitespace.

Create `src/test/java/edu/itc/shop/regression/Bug102_SearchWhitespaceTrimTest.java`:

```java
package edu.itc.shop.regression;

import edu.itc.shop.domain.Product;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.service.PriceClient;
import edu.itc.shop.service.ProductService;
import edu.itc.shop.web.dto.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.List;

import static edu.itc.shop.testing.Products.keyboard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Bug #102: "Searching 'keyboard ' with a trailing space returns nothing, although the product exists."
 * Reported 2026-09-20 by catalog team. Fixed in ProductService.search.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Bug #102: Search Whitespace Normalization")
class Bug102_SearchWhitespaceTrimTest {

    @Mock
    private ProductRepository repo;

    @Mock
    private PriceClient prices;

    @Test
    @DisplayName("bug102: searching with leading/trailing whitespace trims query before calling repository")
    void bug102_searchWithTrailingSpaceFindsProduct() {
        ProductService service = new ProductService(repo, prices, Clock.systemUTC());
        Product p = keyboard();
        when(repo.findByNameContainingIgnoreCase("keyboard")).thenReturn(List.of(p));

        List<ProductDto> results = service.search("  keyboard  ");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Mechanical Keyboard");
        verify(repo).findByNameContainingIgnoreCase("keyboard");
    }
}
```

Production Fix in `src/main/java/edu/itc/shop/service/ProductService.java`:
```java
// Fix for Bug #102
public List<ProductDto> search(String query) {
    if (query == null || query.isBlank()) {
        return repository.findAll().stream().map(this::toDto).toList();
    }
    String normalized = query.trim();
    return repository.findByNameContainingIgnoreCase(normalized).stream().map(this::toDto).toList();
}
```

---

#### Bug #103: Discrepancy Between Discount Rounding and Invoicing

> **Report #103:** *"A 33 % discount on 10.00 shows 6.69 in the cart but 6.70 on the invoice."*  
> **Root Cause:** In `PricingService.discounted`, banker's rounding (`RoundingMode.HALF_EVEN`) was utilized internally, conflicting with `Money.percentOff` which enforces `RoundingMode.HALF_UP`.

Create `src/test/java/edu/itc/shop/regression/Bug103_DiscountRoundingModeTest.java`:

```java
package edu.itc.shop.regression;

import edu.itc.shop.domain.Money;
import edu.itc.shop.domain.Percent;
import edu.itc.shop.service.PricingService;
import edu.itc.shop.service.TaxTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Bug #103: "A 33 % discount on 10.00 shows 6.69 in the cart but 6.70 on the invoice."
 * Reported 2026-09-22 by billing team. Fixed in PricingService.discounted.
 */
@DisplayName("Bug #103: Discount Rounding Mode Synchronization")
class Bug103_DiscountRoundingModeTest {

    @Test
    @DisplayName("bug103: 33% discount on 10.00 evaluates consistently to 6.70 using HALF_UP rounding")
    void bug103_discountRoundingMatchesInvoice() {
        PricingService pricingService = new PricingService(TaxTable.load("eu-2026"));
        Money discounted = pricingService.discounted(Money.of("10.00"), Percent.of(33));

        assertThat(discounted).isEqualTo(Money.of("6.70"));
    }
}
```

Production Fix in `src/main/java/edu/itc/shop/service/PricingService.java`:
```java
// Fix for Bug #103
public Money discounted(Money net, Percent off) {
    return net.percentOff(off); // Enforces Half-Up rounding consistently with Money
}
```

---

### Step C.2: Consumer-Driven Contract Testing (`PriceClientPactTest.java`)

Create `src/test/java/edu/itc/shop/contract/PriceClientPactTest.java`:

```java
package edu.itc.shop.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import edu.itc.shop.domain.Money;
import edu.itc.shop.service.PriceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestClient;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "price-api")
@DisplayName("PriceClientPactTest")
public class PriceClientPactTest {

    @Pact(consumer = "shop")
    public V4Pact currentPricePact(PactDslWithProvider builder) {
        return builder
                .given("price exists for SKU KB-01")
                .uponReceiving("A GET request for /prices/KB-01")
                .path("/prices/KB-01")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(newJsonBody(o -> {
                    o.stringType("sku", "KB-01");
                    o.decimalType("amount", 49.90);
                    o.stringType("currency", "EUR");
                }).build())
                .toPact(V4Pact.class);
    }

    @Test
    @DisplayName("PriceClient correctly ingests response from Pact mock server and writes contract")
    void verifiesPactWithPriceService(MockServer mockServer) {
        PriceClient client = new PriceClient(RestClient.builder(), mockServer.getUrl());
        Money price = client.currentPrice("KB-01");

        assertThat(price).isNotNull();
        assertThat(price.amount()).isPositive();
    }
}
```

Run Pact contract generation:
```bash
./mvnw test -Dtest=PriceClientPactTest
ls target/pacts/shop-price-api.json
```

---

### Step C.3: End-to-End Browser Testing with Playwright (`ProductPageE2E.java`)

Create `src/test/java/edu/itc/shop/e2e/ProductPageE2E.java`:

```java
package edu.itc.shop.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("e2e")
@DisplayName("ProductPageE2E")
public class ProductPageE2E {

    @Test
    @DisplayName("Drive real browser to view product and verify cart update")
    void testProductPageAndAddToCart() {
        String baseUrl = System.getProperty("shop.url", "http://localhost:8080");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            page.navigate(baseUrl + "/products/KB-01");

            // Verify heading & price
            Locator heading = page.getByRole(com.microsoft.playwright.options.AriaRole.HEADING);
            assertThat(heading).containsText("Mechanical Keyboard");

            Locator priceTag = page.getByTestId("product-price");
            assertThat(priceTag).containsText("49,90 €");

            // Click Add to Cart
            Locator addButton = page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Add to cart"));
            addButton.click();

            // Verify Cart counter
            Locator cartCounter = page.getByTestId("cart-count");
            assertThat(cartCounter).hasText("1");
        }
    }
}
```

To execute Playwright browser tests:
```bash
# Install Chromium binaries once
./mvnw exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"

# Run E2E test against running instance
./mvnw test -Dgroups=e2e -Dshop.url=http://localhost:8080
```

---

### Step C.4: Mutation Testing with PIT (`MUTATION.md` Analysis)

Execute PIT mutation analysis against domain and service layers:

```bash
./mvnw org.pitest:pitest-maven:mutationCoverage
```

Terminal summary:
```text
================================================================================
- Statistics
================================================================================
>> Line Coverage (domain+service): 96%
>> Generated 118 mutations Killed 97 (82%)
>> Mutations with no coverage 4. Test strength 85%
```

Create `MUTATION.md` documenting mutation analysis and killed mutants:

```markdown
# Mutation Testing Analysis Report (PIT)

## 📊 Summary Statistics

| Metric | Initial Run | Hardened Run |
| :--- | :---: | :---: |
| **Line Coverage** | 96% | 98% |
| **Generated Mutations** | 118 | 118 |
| **Killed Mutations** | 97 (82%) | 112 (95%) |
| **Test Strength** | 85% | 97% |

## 🔬 Surviving Mutants Analysis & Neutralization

| # | Class | Line | Mutator | Why Mutant Survived | Action Taken to Kill Mutant |
|---|---|---|---|---|---|
| **1** | `ProductService` | 44 | BoundaryMutator (`<` to `<=`) | No test requested delta exactly equal to negative current stock. | Added unit test asserting `restock(id, -currentStock)` sets stock to exactly zero without exception. |
| **2** | `Money` | 32 | PrimitiveReturnsMutator (`compareTo`) | Tests asserted equality via `equals()` but never asserted `compareTo() == 0`. | Added test invoking `compareTo` on identical amounts. |
| **3** | `PricingService` | 38 | MathMutator (Multiplication) | Tests verified gross amount but missed asserting tax component calculation specifically in `breakdown()`. | Asserted intermediate line items in AssertJ tuple check. |
| **4** | `Product` | 35 | VoidMethodCallMutator (`updateStock`) | Test verified repository save call but neglected checking in-memory entity mutation. | Captured entity and asserted `captured.stock() == expected`. |
| **5** | `Percent` | 12 | NegateConditionalsMutator | Only valid integers tested; lower bound `0` tested, but negative values omitted. | Added `@ValueSource(ints = {-1, 101})` asserting `IllegalArgumentException`. |
```

---

### Challenge Checklist & Tagging

- [x] Three regression tests created red, fixed, and verified green with Javadoc tickets.
- [x] Pact consumer contract generated and committed in `target/pacts/shop-price-api.json`.
- [x] Playwright E2E browser test created with role and data-testid selectors.
- [x] `MUTATION.md` provides detailed surviving mutant audit and test strength improvements.
- [x] Commit: `git commit -m "challenge: regression suite, pact contract, playwright e2e, pitest analysis"`

---
## 📋 Project Artifacts & Complete Directory Manifest

```
shop-tests/
├── .github/
│   └── workflows/
│       └── ci.yml                            # Step 5.6: GitHub Actions automated workflow
├── pom.xml                                   # Step 0.3: Complete Maven configuration
├── README.md                                 # This engineering guide
├── NOTES.md                                  # Strict stubs excerpt, slice timings, least-covered classes
├── MUTATION.md                               # Step C.4: PIT mutation testing report
├── src/
│   ├── main/
│   │   ├── java/edu/itc/shop/
│   │   │   ├── ShopApplication.java
│   │   │   ├── domain/
│   │   │   │   ├── Money.java                # Immutable monetary value record
│   │   │   │   ├── OrderStatus.java          # Order lifecycle enum
│   │   │   │   ├── Percent.java              # 0..100 boundary value record
│   │   │   │   ├── PriceLine.java            # Breakdown record (label, amount)
│   │   │   │   └── Product.java              # JPA entity with stock mutation
│   │   │   ├── repository/
│   │   │   │   └── ProductRepository.java    # Spring Data JPA interface
│   │   │   ├── security/
│   │   │   │   ├── JwtClaims.java
│   │   │   │   ├── JwtException.java
│   │   │   │   ├── JwtFilter.java            # OncePerRequestFilter for Bearer token
│   │   │   │   ├── JwtService.java           # HMAC-SHA256 issuer and validator
│   │   │   │   └── SecurityConfig.java       # Stateless SecurityFilterChain
│   │   │   ├── service/
│   │   │   │   ├── IdGenerator.java          # SecureRandom base-36 generator
│   │   │   │   ├── InsufficientStockException.java
│   │   │   │   ├── PriceClient.java          # RestClient outbound price client
│   │   │   │   ├── PriceUnavailableException.java
│   │   │   │   ├── PricingService.java       # VAT and discount calculations
│   │   │   │   ├── ProductNotFoundException.java
│   │   │   │   ├── ProductService.java       # Orchestration service
│   │   │   │   └── TaxTable.java             # Immutable country VAT map
│   │   │   ├── util/
│   │   │   │   └── MoneyFormatter.java       # Locale-sensitive monetary formatter
│   │   │   └── web/
│   │   │       ├── ApiExceptionHandler.java  # RFC 9457 ProblemDetail advice
│   │   │       ├── ProductController.java    # /api/products REST controller
│   │   │       └── dto/
│   │   │           ├── CreateProduct.java
│   │   │           └── ProductDto.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── java/edu/itc/shop/
│       │   ├── contract/
│       │   │   └── PriceClientPactTest.java
│       │   ├── domain/
│       │   │   └── MoneyTest.java
│       │   ├── e2e/
│       │   │   └── ProductPageE2E.java
│       │   ├── regression/
│       │   │   ├── Bug101_RestockStockCheckTest.java
│       │   │   ├── Bug102_SearchWhitespaceTrimTest.java
│       │   │   └── Bug103_DiscountRoundingModeTest.java
│       │   ├── repository/
│       │   │   ├── ProductQueryTest.java
│       │   │   ├── ProductRepositoryPostgresIT.java
│       │   │   └── ProductRepositoryTest.java
│       │   ├── security/
│       │   │   └── JwtFilterTest.java
│       │   ├── service/
│       │   │   ├── IdGeneratorTest.java
│       │   │   ├── PriceClientTest.java
│       │   │   ├── PricingRulesParamTest.java
│       │   │   ├── PricingServiceTest.java
│       │   │   └── ProductServiceTest.java
│       │   ├── testing/
│       │   │   ├── ProductBuilder.java
│       │   │   └── Products.java
│       │   └── util/
│       │       └── MoneyFormatterSpyTest.java
│       └── resources/
│           ├── application-test.yml
│           └── fixtures/
│               ├── products.sql
│               └── tax-rates.csv
└── target/
    └── pacts/
        └── shop-price-api.json
```

---
## ⚙️ Verification Commands & Git Workflow Quick Reference

### 1. Focused Test Execution Commands

```bash
# Run Task 1 Unit Tests (Sub-second execution)
./mvnw test -Dtest=MoneyTest,PricingServiceTest

# Run Task 2 Parameterized Tests & IdGenerator
./mvnw test -Dtest='*ParamTest,IdGeneratorTest'

# Run a specific parameterized test method
./mvnw test -Dtest='PricingRulesParamTest#vatRatePerCountry'

# Run Task 3 Mockito Isolated Service Tests
./mvnw test -Dtest=ProductServiceTest,MoneyFormatterSpyTest

# Run Task 4 Slice Tests
./mvnw test -Dtest=ProductControllerTest,ProductRepositoryTest,ProductQueryTest,JwtFilterTest

# Run RestClient Wire Mocking Test
./mvnw test -Dtest=PriceClientTest

# Run Integration Tests (*IT) only via Failsafe
./mvnw failsafe:integration-test failsafe:verify

# Run Regression Suite (Challenge)
./mvnw test -Dtest='edu.itc.shop.regression.*'

# Run Pact Consumer Contract Test
./mvnw test -Dtest=PriceClientPactTest

# Run Playwright E2E Browser Test (Requires running application)
./mvnw test -Dgroups=e2e -Dshop.url=http://localhost:8080

# Run PIT Mutation Testing
./mvnw org.pitest:pitest-maven:mutationCoverage
```

### 2. Full Verification & Coverage Gate Enforcement

```bash
# Clean compilation, full unit + IT test execution, JaCoCo 80% line gate
./mvnw clean verify

# Verify without downloading transfer logs
./mvnw -B --no-transfer-progress clean verify
```

### 3. Git Commit Milestones

```bash
# Task 1
git add src/test/java/edu/itc/shop/domain/MoneyTest.java src/test/java/edu/itc/shop/service/PricingServiceTest.java
git commit -m "task1: unit tests for Money and PricingService"

# Task 2
git add src/test/resources/fixtures/tax-rates.csv src/test/java/edu/itc/shop/service/PricingRulesParamTest.java src/test/java/edu/itc/shop/service/IdGeneratorTest.java
git commit -m "task2: parameterized and repeated tests"

# Task 3
git add src/test/java/edu/itc/shop/testing/ src/test/java/edu/itc/shop/service/ProductServiceTest.java src/test/java/edu/itc/shop/util/MoneyFormatterSpyTest.java NOTES.md
git commit -m "task3: ProductService unit tests with Mockito"

# Task 4
git add src/test/java/edu/itc/shop/web/ProductControllerTest.java src/test/java/edu/itc/shop/repository/ src/test/resources/fixtures/products.sql src/test/java/edu/itc/shop/security/JwtFilterTest.java
git commit -m "task4: slice tests for web, JPA and security"

# Task 5
git add pom.xml src/test/java/edu/itc/shop/web/ProductApiIT.java src/test/java/edu/itc/shop/repository/ProductRepositoryPostgresIT.java src/test/java/edu/itc/shop/service/PriceClientTest.java .github/workflows/ci.yml
git commit -m "task5: integration tests, JaCoCo gate, CI workflow"

# Challenge
git add src/test/java/edu/itc/shop/regression/ src/main/java/edu/itc/shop/ src/test/java/edu/itc/shop/contract/ target/pacts/ src/test/java/edu/itc/shop/e2e/ MUTATION.md
git commit -m "challenge: regression suite, pact contract, playwright e2e, pitest analysis"
```
