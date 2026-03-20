# Portfolio Website

A full-stack personal portfolio built with Spring Boot MVC and Thymeleaf.
All content is managed through a custom-built CMS admin panel — no hardcoded data.

🔗 **Live Demo**: https://portfolio-e8wp.onrender.com/public/

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.3.5, Spring Security |
| Frontend | Thymeleaf, Bootstrap 5, vanilla JS |
| Database | MySQL (local) / Railway MySQL (production) |
| Auth | Spring Security form login, BCrypt, role-based access |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Deploy | Railway |

---

## Features

**Public**
- Homepage with dynamic sections (About, Projects, Skills, Experience)
- Projects page with image gallery and video embed support
- Skills page with categorized tech stack
- Experience timeline — newest first
- Lab page for experimental tools and side projects (currently restricted)
- Light / dark theme toggle
- Responsive layout with mobile nav

**Admin CMS** (`/admin/**`, requires `ROLE_ADMIN`)
- Manage homepage sections with multiple layout types (Single, Two-column, Two-one, Timeline, Skill-row)
- Add / edit / delete projects with image / video embed and tag support
- Manage skills, experience timeline, and lab items
- Content block editor for page-level text

**Auth**
- Login / Register
- Change password
- Role-based route protection (USER / ADMIN)

---

## Project Structure

```
src/main/
├── java/com/arauta/portfolio/
│   ├── config/          # SecurityConfig
│   ├── controller/      # HomeController, DrawerControllerAdvice, AdminHomepageController, ...
│   ├── dto/             # RegisterForm
│   ├── model/           # AppUser, Section, Project, LabEntry, Experience, ...
│   ├── repo/            # JPA repositories
│   ├── service/         # Business logic layer
│   └── util/            # HtmlUtils, PageNames
└── resources/
    ├── templates/
    │   ├── fragments/   # nav, drawer, footer
    │   ├── public/      # index, projects, skills, experience, lab
    │   └── admin/       # CMS pages
    └── static/
        ├── css/         # variables, base, layout, components, nav, admin
        └── js/          # main.js
```

---

## Local Development

**Prerequisites:** Java 21, Maven, MySQL

1. Clone the repo
```bash
git clone https://github.com/h2322623226/portfolio.git
cd portfolio
```

2. Create a local database
```sql
CREATE DATABASE portfolio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Create `src/main/resources/application-dev.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/portfolio_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Taipei&characterEncoding=utf8
    username: your_username
    password: your_password
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

4. Run
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

5. Open `http://localhost:8080/public/`

---

## Deployment

Deployed on [Railway](https://railway.app) with Railway MySQL.
Environment variables required:

| Key | Description |
|---|---|
| `DB_URL` | JDBC connection string |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `PORT` | Auto-injected by Railway |

---

## License

This project is for personal portfolio use.
Feel free to explore — always a work in progress.
