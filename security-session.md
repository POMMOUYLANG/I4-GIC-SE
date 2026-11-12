# Security Session Transcripts — `shop-secure`

## Task 1: Default Security & First Filter Chain

### 1.1 Default Auto-Configuration Observation (`--debug`, before `SecurityConfig`)

When no custom `SecurityFilterChain` bean is defined, `SecurityAutoConfiguration` and `SpringBootWebSecurityConfiguration` register a default filter chain protecting every route (`anyRequest().authenticated()`) with HTTP Basic, Form Login, and stateful `JSESSIONID` cookies.

Notice that in this project, because `spring-boot-starter-security-oauth2-resource-server` (`OpaqueTokenIntrospector`) is already on the classpath, Spring Boot's `UserDetailsServiceAutoConfiguration` skips generating the default `user` + UUID password (`MissingAlternativeUserDetailsManagerOrUserPropertiesConfigured` did not match), whereas without the OAuth2 Resource Server starter on the classpath, `UserDetailsServiceAutoConfiguration` prints `Using generated security password: <uuid>` and authenticates `-u user:<password>`.

```http
$ curl -i -s http://localhost:8080/api/products
HTTP/1.1 401 
Set-Cookie: JSESSIONID=CFD7832D2C9DF9B949A196BF8EF01061; Path=/; HttpOnly
WWW-Authenticate: Basic realm="Realm", charset="UTF-8"
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Length: 0
Date: Thu, 12 Nov 2026 06:32:29 GMT
```

### 1.2 Custom `SecurityFilterChain` Verification

1. **Anonymous `GET /api/products` → `200 OK`**

```http
$ curl -i -s http://localhost:8080/api/products
HTTP/1.1 200 
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Content-Length: 175
Date: Thu, 12 Nov 2026 06:32:08 GMT

[{"name":"USB-C cable","price":7.90,"stock":25,"id":1},{"name":"Ergonomic Keyboard","price":89.00,"stock":12,"id":2},{"name":"Wireless Mouse","price":29.50,"stock":40,"id":3}]
```

2. **Anonymous `POST /api/products` → `401 Unauthorized` (`application/problem+json`)**

```http
$ curl -i -s -X POST http://localhost:8080/api/products -H 'Content-Type: application/json' -d '{"name":"Mouse","price":19.9,"stock":5}'
HTTP/1.1 401 
WWW-Authenticate: Basic realm="Realm"
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/problem+json
Content-Length: 99
Date: Thu, 12 Nov 2026 06:32:08 GMT

{"detail":"Authentication required","instance":"/api/products","status":401,"title":"Unauthorized"}
```

3. **Customer (`alice`) `DELETE /api/products/1` → `403 Forbidden` (`application/problem+json`)**

```http
$ curl -i -s -u alice:alice -X DELETE http://localhost:8080/api/products/1
HTTP/1.1 403 
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/problem+json
Content-Length: 88
Date: Thu, 12 Nov 2026 06:32:09 GMT

{"detail":"Access denied","instance":"/api/products/1","status":403,"title":"Forbidden"}
```

4. **Admin (`admin`) `DELETE /api/products/1` → `204 No Content`**

```http
$ curl -i -s -u admin:admin -X DELETE http://localhost:8080/api/products/1
HTTP/1.1 204 
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Date: Thu, 12 Nov 2026 06:32:09 GMT
```
