# Portfolio Website｜個人作品集網站

使用 Spring Boot MVC + Thymeleaf 打造的全端個人作品集，包含公開展示頁面與後台內容管理系統（CMS）。

🔗 **Live Demo（線上展示）**: https://portfolio-e8wp.onrender.com/public/

![CI](https://github.com/h2322623226/portfolio/actions/workflows/ci.yml/badge.svg)

---

## Tech Stack｜技術架構

| 層次 | 技術 | 說明 |
|---|---|---|
| Backend（後端） | Java 21, Spring Boot 3.3.5 | 主框架 |
| Security（安全性） | Spring Security 6 | 表單登入、BCrypt 加密、角色存取控制 |
| Frontend（前端） | Thymeleaf, Bootstrap 5, vanilla JS | 伺服器端渲染（SSR） |
| Database（資料庫） | MySQL（生產）/ H2 In-Memory（測試） | ORM 使用 Spring Data JPA / Hibernate |
| Build（建置） | Maven | 依賴管理與測試執行 |
| Deploy（部署） | Render（應用程式）+ Aiven（MySQL） | 雲端部署 |

---

## Features｜功能說明

**Public Pages（公開頁面）**
- 首頁：動態區塊內容（About、Projects、Skills、Experience）
- 作品頁：圖片展示與影片嵌入支援
- 技能頁：分類技術清單
- 經歷頁：時間軸顯示，最新在前
- Lab 頁：實驗性工具與側專案
- 淺色 / 深色主題切換、響應式行動裝置導覽列

**Admin CMS（後台管理，需 `ROLE_ADMIN`）**
- 管理首頁區塊，支援多種版型（單欄、雙欄、時間軸、技能列）
- 新增 / 編輯 / 刪除作品（含圖片、影片嵌入、標籤）
- 管理技能、經歷時間軸、Lab 項目
- 頁面級文字內容區塊編輯器

**Auth（認證）**
- 登入 / 註冊 / 修改密碼
- 角色存取控制（ROLE_USER / ROLE_ADMIN）
- CSRF 保護

---

## Testing｜測試

本專案採用四層測試架構，共 **212 個自動化測試**，使用 H2 In-Memory 資料庫，與生產環境完全隔離。

```
整合測試（Integration Tests）  ← @SpringBootTest，跨層流程驗證
安全性測試（Security Tests）    ← 路由規則、CSRF、角色存取
控制器測試（Controller Tests）  ← @WebMvcTest，HTTP 輸入輸出驗證
Repository 測試                ← @DataJpaTest，實際 SQL 查詢驗證
服務層測試（Service Tests）     ← Mockito，業務邏輯與邊界條件
工具類測試（Util Tests）        ← 純 JUnit 5，XSS 防護驗證
```

詳細測試案例請參考：[TEST_PLAN.md](./TEST_PLAN.md)

---

## Project Structure｜專案結構

```
src/main/
├── java/com/arauta/portfolio/
│   ├── config/          # SecurityConfig（安全性設定）
│   ├── controller/      # 公開頁面、後台各分頁 Controller
│   ├── dto/             # 表單 DTO（RegisterForm、ProjectForm、ExperienceForm、
│   │                    #   LabForm、SectionForm、ChangePasswordForm）
│   ├── model/           # Entity（AppUser、Section、Project、LabEntry、Experience…）
│   ├── repo/            # JPA Repository 介面
│   ├── service/         # 業務邏輯層
│   └── util/            # HtmlUtils（XSS 防護）、PageNames
└── resources/
    ├── templates/
    │   ├── fragments/   # 共用元件：nav、drawer、footer
    │   ├── public/      # 公開頁面：index、projects、skills、experience、lab
    │   └── admin/       # 後台 CMS 頁面
    └── static/
        ├── css/         # 樣式：variables、base、layout、components、nav、admin
        └── js/          # main.js
```

---

## Local Development｜本地開發

**環境需求：** Java 21、Maven、MySQL

1. Clone 專案
```bash
git clone https://github.com/h2322623226/portfolio.git
cd portfolio
```

2. 建立本地資料庫
```sql
CREATE DATABASE portfolio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 建立 `src/main/resources/application-dev.yml`
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

4. 啟動
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

5. 開啟 `http://localhost:8080/public/`

---

## Run Tests｜執行測試

```bash
mvn test
```

測試使用 H2 In-Memory 資料庫，**不需要** MySQL 連線即可執行。

---

## Deployment｜部署說明

應用程式部署於 [Render](https://render.com)，資料庫使用 [Aiven](https://aiven.io) 托管的 MySQL。

所需環境變數：

| 變數名稱 | 說明 |
|---|---|
| `DB_URL` | JDBC 連線字串 |
| `DB_USERNAME` | 資料庫帳號 |
| `DB_PASSWORD` | 資料庫密碼 |

---

## License

個人作品集專案，歡迎參考，持續更新中。
