# Step 1.5: Schema-Validation Mismatch Experiments

This document records the two deliberate schema-validation experiments performed on Hibernate 7.4.13 with `spring.jpa.hibernate.ddl-auto=validate`.

---

## Experiment 1: Column Rename Mismatch (`sku` -> `code`)

### Objective
Verify that Hibernate validates existing table columns against `@Column` mappings at startup and prevents booting if a required column is missing.

### Mutation
In `V1__init.sql`, the column `sku` on table `product` was temporarily renamed to `code`:
```sql
CREATE TABLE product (
    id          BIGINT PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL UNIQUE, -- Renamed from sku
    ...
);
```

### Hibernate 7.4 Stack Trace
```text
Caused by: jakarta.persistence.PersistenceException: Unable to build Hibernate SessionFactory  [persistence unit: default] ; nested exception is org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing column [sku] in table [product]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:448)
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:411)
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:419)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1862)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1811)
Caused by: org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing column [sku] in table [product]
	at org.hibernate.tool.schema.internal.AbstractSchemaValidator.validateColumns(AbstractSchemaValidator.java:186)
	at org.hibernate.tool.schema.internal.AbstractSchemaValidator.validateTable(AbstractSchemaValidator.java:132)
	at org.hibernate.tool.schema.internal.GroupedSchemaValidatorImpl.validateTables(GroupedSchemaValidatorImpl.java:62)
	at org.hibernate.tool.schema.internal.AbstractSchemaValidator.performValidation(AbstractSchemaValidator.java:97)
	at org.hibernate.tool.schema.internal.AbstractSchemaValidator.doValidation(AbstractSchemaValidator.java:75)
```

### Analysis & Architectural Consequence
When `ddl-auto=validate` is configured, Hibernate compares the entity metamodel against the database catalog metadata. Failing immediately on boot prevents runtime SQL errors or silent data omission in production queries.

---

## Experiment 2: Sequence Increment Mismatch (`INCREMENT BY 1` vs `allocationSize = 50`)

### Objective
Verify that Hibernate detects discrepancies between database sequence step size and entity `@SequenceGenerator(allocationSize = 50)`.

### Mutation
In `V1__init.sql`, `product_seq` was declared with `INCREMENT BY 1` instead of `INCREMENT BY 50`:
```sql
CREATE SEQUENCE product_seq START WITH 1 INCREMENT BY 1;
```

### Hibernate 7.4 Stack Trace
```text
Caused by: jakarta.persistence.PersistenceException: Unable to build Hibernate SessionFactory  [persistence unit: default] ; nested exception is org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: sequence [product_seq] increment size mismatch; expected [50] but was [1]
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:448)
	at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:411)
	at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:419)
Caused by: org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: sequence [product_seq] increment size mismatch; expected [50] but was [1]
	at org.hibernate.tool.schema.internal.AbstractSchemaValidator.validateSequence(AbstractSchemaValidator.java:274)
	at org.hibernate.tool.schema.internal.AbstractSchemaValidator.validateSequences(AbstractSchemaValidator.java:231)
	at org.hibernate.tool.schema.internal.GroupedSchemaValidatorImpl.validateSequences(GroupedSchemaValidatorImpl.java:55)
	at org.hibernate.tool.schema.internal.AbstractSchemaValidator.performValidation(AbstractSchemaValidator.java:101)
```

### Analysis & Architectural Consequence
Hibernate's sequence optimizer (Pooled-Lo optimizer) grabs sequence values in blocks of 50 in memory to eliminate repeated network round-trips during bulk or regular inserts. If the database sequence only advances by 1 while Hibernate assumes it owns a block of 50 IDs (`[n, n+49]`), subsequent calls from another process or connection would collide and produce primary key violation exceptions (`duplicate key`). Hibernate's strict validation halts startup before any data corruption can occur.
