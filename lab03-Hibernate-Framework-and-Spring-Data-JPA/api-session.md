# Lab 03: API Session Transcripts

This document contains live cURL transcripts and HTTP responses for the three primary endpoints in Task 3, running against Spring Boot 4.1.1 and Hibernate 7.4.7 with `spring.profiles.active=dev` enabled.

---

## 1. Paged Orders by Status (`GET /api/orders`)

### Request
```bash
curl -s "http://localhost:8080/api/orders?status=PAID&page=0&size=2&sort=placedAt,desc" | jq .
```

### Response
```json
{
  "content": [
    {
      "id": 151,
      "status": "PAID",
      "placedAt": "2026-09-24T09:12:44.120Z",
      "customer": {
        "name": "Ada Lovelace"
      }
    },
    {
      "id": 108,
      "status": "PAID",
      "placedAt": "2026-09-23T17:03:10.551Z",
      "customer": {
        "name": "Grace Hopper"
      }
    }
  ],
  "page": {
    "size": 2,
    "number": 0,
    "totalElements": 11,
    "totalPages": 6
  }
}
```

### Hibernate SQL Execution Log (Closed Interface Projection)
```sql
DEBUG org.hibernate.SQL : 
    select
        o1_0.id,
        o1_0.status,
        o1_0.placed_at,
        c1_0.name 
    from
        orders o1_0 
    join
        customer c1_0 
            on c1_0.id=o1_0.customer_id 
    where
        o1_0.status=? 
    order by
        o1_0.placed_at desc 
    offset ? rows fetch first ? rows only
DEBUG org.hibernate.SQL : 
    select
        count(o1_0.id) 
    from
        orders o1_0 
    where
        o1_0.status=?
```

---

## 2. Specification-Filtered Products Search (`GET /api/products`)

### Request
```bash
curl -s "http://localhost:8080/api/products?q=key&category=HARDWARE&maxPrice=100.00&inStock=true&page=0&size=5" | jq .
```

### Response
```json
{
  "content": [
    {
      "id": 1,
      "sku": "KB-1",
      "name": "Mechanical Keyboard RGB",
      "amount": 89.90,
      "stock": 25
    },
    {
      "id": 7,
      "sku": "KB-7",
      "name": "Ergonomic Split Keyboard",
      "amount": 75.00,
      "stock": 4
    }
  ],
  "page": {
    "size": 5,
    "number": 0,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

### Hibernate SQL Execution Log (Combined Predicates)
```sql
DEBUG org.hibernate.SQL : 
    select
        p1_0.id,
        p1_0.sku,
        p1_0.name,
        p1_0.amount,
        p1_0.currency,
        p1_0.category,
        p1_0.stock 
    from
        product p1_0 
    where
        lower(p1_0.name) like ? escape '\' 
        and p1_0.category=? 
        and p1_0.amount<=? 
        and p1_0.stock>0 
    offset ? rows fetch first ? rows only
```

---

## 3. Customer Orders with Eager Fetch Join (`GET /api/orders/by-customer/{email}`)

### Request
```bash
curl -s "http://localhost:8080/api/orders/by-customer/ada@example.com" | jq .
```

### Response
```json
[
  {
    "id": 151,
    "status": "PAID",
    "placedAt": "2026-09-24T09:12:44.120Z",
    "total": {
      "amount": 179.80,
      "currency": "EUR"
    },
    "lines": [
      {
        "id": 301,
        "productSku": "KB-1",
        "productName": "Mechanical Keyboard RGB",
        "quantity": 2,
        "unitPrice": {
          "amount": 89.90,
          "currency": "EUR"
        }
      }
    ]
  }
]
```

### Hibernate SQL Execution Log (Single Fetch Join Query)
```sql
DEBUG org.hibernate.SQL : 
    select
        distinct o1_0.id,
        o1_0.customer_id,
        o1_0.placed_at,
        o1_0.status,
        o1_0.version,
        l1_0.order_id,
        l1_0.id,
        l1_0.amount,
        l1_0.currency,
        l1_0.product_id,
        l1_0.quantity,
        p1_0.id,
        p1_0.amount,
        p1_0.currency,
        p1_0.category,
        p1_0.name,
        p1_0.sku,
        p1_0.stock 
    from
        orders o1_0 
    join
        order_line l1_0 
            on o1_0.id=l1_0.order_id 
    join
        product p1_0 
            on p1_0.id=l1_0.product_id 
    join
        customer c1_0 
            on c1_0.id=o1_0.customer_id 
    where
        c1_0.email=? 
    order by
        o1_0.placed_at desc
```
