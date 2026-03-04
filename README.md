# security-demo-pack

A minimal Spring Boot 3 + Thymeleaf + Spring Security + JPA (H2) demo.

## Routes
- GET /            public
- GET /projects    public
- GET /login       login page
- POST /login      handled by Spring Security
- POST /logout     logout
- GET /secret      protected (login required)

## Test accounts
- user / user123
- admin / admin123

## Run
```bash
mvn spring-boot:run
```

Then open:
- http://localhost:8080/
- http://localhost:8080/secret (will redirect to /login)
