# TEST PLAN — Portfolio Demo

**專案**：portfolio-demo
**框架**：Spring Boot 3.3.5 / Java 21 / Maven
**安全性**：Spring Security 6（ROLE_USER / ROLE_ADMIN）
**資料庫**：MySQL（生產）/ H2 In-Memory（測試）
**模板引擎**：Thymeleaf（SSR）
**撰寫日期**：2026-04-02
**最後更新**：2026-04-16
**版本**：v1.3

---

## 目錄

1. [測試策略](#1-測試策略)
2. [測試覆蓋統計](#2-測試覆蓋統計)
3. [工具類測試](#3-工具類測試)
4. [服務層測試](#4-服務層測試)
5. [安全性測試](#5-安全性測試)
6. [控制器測試](#6-控制器測試)
7. [整合測試](#7-整合測試)
8. [設計決策紀錄](#8-設計決策紀錄)

---

## 1. 測試策略

### 1.1 分層架構

本專案採用四層測試架構，每層各司其職，避免重複驗證：

```
┌─────────────────────────────────────────┐
│  Tier 4：Integration Tests              │  @SpringBootTest + 真實 H2
│  驗證跨層的完整業務流程與資料庫互動        │
├─────────────────────────────────────────┤
│  Tier 3：Security Tests                 │  @SpringBootTest + MockMvc
│  驗證所有路由的存取規則、CSRF 保護        │
├─────────────────────────────────────────┤
│  Tier 2：Controller Tests               │  @WebMvcTest（僅切片）
│  驗證 HTTP 輸入輸出、View 名稱、Model    │
├─────────────────────────────────────────┤
│  Tier 1：Unit Tests                     │  JUnit 5 + Mockito
│  驗證業務邏輯、邊界條件（無 Spring 上下文）│
└─────────────────────────────────────────┘
```

### 1.2 設計原則

- **責任分離**：安全規則集中在 `SecurityConfigTest`（@SpringBootTest），Controller Tests 只測邏輯
- **最小化依賴**：能用 Mockito 解決的不啟動 Spring Context，提升執行速度
- **測試隔離**：Integration Tests 全部加 `@Transactional`，測試完自動 rollback，不汙染資料

### 1.3 使用工具

| 工具 | 用途 |
|---|---|
| JUnit 5 | 測試執行框架 |
| Mockito | 服務層 Mock |
| Spring MockMvc | HTTP 層模擬 |
| Spring Security Test | 認證情境模擬（`@WithMockUser`）|
| H2 In-Memory DB | 取代 MySQL 用於測試環境 |

---

## 2. 測試覆蓋統計

| 層次 | 測試類 | 測試方法數 |
|---|---|---|
| 工具類 | HtmlUtilsTest | 16 |
| 服務層 | UserServiceTest | 8 |
| 服務層 | PortfolioUserDetailsServiceTest | 7 |
| 服務層 | ProjectServiceTest | 12 |
| 服務層 | SectionServiceTest | 11 |
| 服務層 | ExperienceServiceTest | 7 |
| 服務層 | LabServiceTest | 11 |
| 服務層 | ContentServiceTest | 6 |
| 安全性 | SecurityConfigTest | 26 |
| Repository | ExperienceRepoTest | 3 |
| Repository | ContentRepoTest | 3 |
| Repository | SectionRepoTest | 3 |
| 控制器 | AuthControllerTest | 16 |
| 控制器 | HomeControllerTest | 8 |
| 控制器 | AdminProjectControllerTest | 12 |
| 控制器 | AdminHomepageControllerTest | 9 |
| 控制器 | AdminSkillsControllerTest | 8 |
| 控制器 | AdminExperienceControllerTest | 13 |
| 控制器 | AdminLabControllerTest | 10 |
| 控制器 | DrawerControllerAdviceTest | 4 |
| 整合測試 | AuthFlowIntegrationTest | 4 |
| 整合測試 | AdminProjectFlowIntegrationTest | 3 |
| 整合測試 | PublicEndpointIntegrationTest | 11 |
| 其他 | PortfolioApplicationTests | 1 |
| **總計** | **24 個測試類** | **212 個方法** |

---

## 3. 工具類測試

### 3.1 HtmlUtils — `nl2br()` 方法

**測試類**：`HtmlUtilsTest`
**測試方式**：純 JUnit 5（無 Spring Context）
**測試目標**：驗證換行轉換與 XSS 防護邏輯

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-UTIL-001 | null 輸入不拋 NPE，回傳空字串 | 負向 | 錯誤猜測 | 輸入 null | 回傳 `""` | `nl2br_nullInput_returnsEmpty` |
| TC-UTIL-002 | 空字串輸入回傳空字串 | 負向 | 等價分割 | 輸入 `""` | 回傳 `""` | `nl2br_emptyString_returnsEmpty` |
| TC-UTIL-003 | 純空白字串回傳空字串 | 負向 | 等價分割 | 輸入 `"   "` | 回傳 `""` | `nl2br_blankString_returnsEmpty` |
| TC-UTIL-004 | Unix 換行（`\n`）替換為 `<br>` | 正向 | 等價分割 | 輸入含 `\n` | 輸出含 `<br>` | `nl2br_unixNewline_replacedWithBrTag` |
| TC-UTIL-005 | Windows 換行（`\r\n`）只產生一個 `<br>` | 正向 | 邊界值分析 | 輸入含 `\r\n` | 輸出單一 `<br>` | `nl2br_windowsNewline_replacedWithSingleBrTag` |
| TC-UTIL-006 | 舊 Mac 換行（`\r`）替換為 `<br>` | 正向 | 等價分割 | 輸入含 `\r` | 輸出含 `<br>` | `nl2br_oldMacNewline_replacedWithBrTag` |
| TC-UTIL-007 | 多個換行全部被替換 | 正向 | 邊界值分析 | 輸入含多個 `\n` | 每個 `\n` 都轉為 `<br>` | `nl2br_multipleNewlines_allReplaced` |
| TC-UTIL-008 | 無換行的純文字不被修改 | 正向 | 等價分割 | 輸入純文字 | 輸出與輸入相同 | `nl2br_noNewlines_stringReturnedUnchanged` |
| TC-UTIL-009 | `&` 符號被 escape 為 `&amp;` | 安全性 | 錯誤猜測 | 輸入含 `&` | 輸出 `&amp;` | `nl2br_ampersand_isEscaped` |
| TC-UTIL-010 | `<` 符號被 escape 為 `&lt;` | 安全性 | 錯誤猜測 | 輸入含 `<` | 輸出 `&lt;` | `nl2br_lessThanSign_isEscaped` |
| TC-UTIL-011 | `>` 符號被 escape 為 `&gt;` | 安全性 | 錯誤猜測 | 輸入含 `>` | 輸出 `&gt;` | `nl2br_greaterThanSign_isEscaped` |
| TC-UTIL-012 | `"` 符號被 escape 為 `&quot;` | 安全性 | 錯誤猜測 | 輸入含 `"` | 輸出 `&quot;` | `nl2br_doubleQuote_isEscaped` |
| TC-UTIL-013 | `'` 符號被 escape 為 `&#39;` | 安全性 | 錯誤猜測 | 輸入含 `'` | 輸出 `&#39;` | `nl2br_singleQuote_isEscaped` |
| TC-UTIL-014 | XSS Script Tag 被完整 escape | 安全性 | 錯誤猜測 | 輸入 `<script>alert(1)</script>` | 輸出不含原始 tag | `nl2br_scriptTagXssAttempt_isFullyEscaped` |
| TC-UTIL-015 | XSS 內容含換行，escape 後換行轉 `<br>` | 安全性 | 決策表 | 輸入含 XSS + `\n` | 先 escape 再轉換 | `nl2br_xssWithNewline_escapedAndNewlineConverted` |
| TC-UTIL-016 | 驗證 escape 先於換行替換發生（執行順序） | 安全性 | 決策表 | 輸入 `<br>\n` | `<br>` 被 escape，`\n` 轉為 `<br>` | `nl2br_escapeHappensBeforeNewlineReplacement` |

---

## 4. 服務層測試

### 4.1 UserService — 使用者管理

**測試類**：`UserServiceTest`
**測試方式**：`@ExtendWith(MockitoExtension.class)`，Mock `UserRepo`、`PasswordEncoder`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-US-001 | 新帳號註冊，密碼被 BCrypt 加密後儲存 | 正向 | 等價分割 | 帳號不存在於 DB | User 被 save，密碼為 encoded | `register_newUsername_savesUserWithEncodedPassword` |
| TC-US-002 | 重複帳號註冊，拋出 UsernameAlreadyExistsException | 負向 | 錯誤猜測 | 帳號已存在 | 拋出例外，不執行 save | `register_existingUsername_throwsUsernameAlreadyExistsException` |
| TC-US-003 | 新用戶預設角色為 ROLE_USER，不可為 ROLE_ADMIN | 安全性 | 錯誤猜測 | 無 | saved User 的 role = `ROLE_USER` | `register_newUser_defaultRoleIsUser_notAdmin` |
| TC-US-004 | 明文密碼不直接儲存至 DB | 安全性 | 錯誤猜測 | 無 | saved User 的密碼 ≠ 輸入的明文 | `register_rawPasswordNeverStoredDirectly` |
| TC-US-005 | 正確舊密碼，密碼更新成功 | 正向 | 等價分割 | 用戶存在，舊密碼正確 | 新密碼被 encoded 後儲存 | `changePassword_correctOldPassword_updatesSuccessfully` |
| TC-US-006 | 錯誤舊密碼，拋出 WrongPasswordException | 負向 | 等價分割 | 用戶存在，舊密碼錯誤 | 拋出例外，不更新 | `changePassword_wrongOldPassword_throwsWrongPasswordException` |
| TC-US-007 | 新密碼必須經 BCrypt 加密，不得為明文 | 安全性 | 錯誤猜測 | 舊密碼正確 | 儲存的新密碼 ≠ 輸入的明文 | `changePassword_newPasswordIsEncoded_notStoredAsPlaintext` |
| TC-US-008 | 修改不存在用戶的密碼，拋出 IllegalArgumentException | 負向 | 錯誤猜測 | 帳號不存在 | 拋出 IllegalArgumentException | `changePassword_userNotFound_throwsIllegalArgument` |

---

### 4.2 PortfolioUserDetailsService — Spring Security 整合

**測試類**：`PortfolioUserDetailsServiceTest`
**測試方式**：`@ExtendWith(MockitoExtension.class)`，Mock `UserRepo`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-UDS-001 | 正常載入存在用戶，回傳 UserDetails | 正向 | 等價分割 | 用戶存在 | 回傳 UserDetails 物件 | `loadUserByUsername_existingUser_returnsUserDetails` |
| TC-UDS-002 | ROLE_USER 的 authorities 包含 ROLE_USER | 正向 | 等價分割 | 用戶 role = ROLE_USER | authorities 含 ROLE_USER | `loadUserByUsername_userWithRoleUser_hasUserAuthority` |
| TC-UDS-003 | ROLE_ADMIN 的 authorities 包含 ROLE_ADMIN | 正向 | 等價分割 | 用戶 role = ROLE_ADMIN | authorities 含 ROLE_ADMIN | `loadUserByUsername_userWithRoleAdmin_hasAdminAuthority` |
| TC-UDS-004 | 回傳密碼為 Hash，不為明文 | 安全性 | 錯誤猜測 | 用戶密碼已 encoded | 回傳密碼 ≠ 明文 | `loadUserByUsername_returnedPassword_isHashNotPlaintext` |
| TC-UDS-005 | 回傳 UserDetails 的 enabled 狀態為 true | 正向 | 等價分割 | 無 | `isEnabled()` = true | `loadUserByUsername_enabledUser_isEnabled` |
| TC-UDS-006 | 不存在的帳號拋出 UsernameNotFoundException（Spring Security 合約） | 負向 | 錯誤猜測 | 帳號不存在 | 拋出 UsernameNotFoundException | `loadUserByUsername_nonExistentUser_throwsUsernameNotFoundException` |
| TC-UDS-007 | Exception message 包含查詢的 username | 負向 | 錯誤猜測 | 帳號不存在 | 例外訊息含 username | `loadUserByUsername_usernameInExceptionMessage` |

---

### 4.3 ProjectService — 專案管理

**測試類**：`ProjectServiceTest`
**測試方式**：`@ExtendWith(MockitoExtension.class)`，Mock `ProjectRepo`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-PS-001 | 有效輸入建立專案，執行 save | 正向 | 等價分割 | 無 | repo.save 被呼叫 | `createWithTags_validInput_projectIsSaved` |
| TC-PS-002 | 剛好 5 個 tag，全部被儲存（邊界值） | 邊界值 | 邊界值分析 | tags.size() = 5 | 5 個 tag 全部儲存 | `createWithTags_fiveTags_allFiveTagsSaved` |
| TC-PS-003 | 6 個 tag，只儲存前 5 個（截斷，不拋例外） | 邊界值 | 邊界值分析 | tags.size() = 6 | 只儲存 5 個 tag | `createWithTags_sixTags_onlyFirstFiveSaved` |
| TC-PS-004 | 0 個 tag，儲存空 tag 列表 | 邊界值 | 邊界值分析 | tags.size() = 0 | tag 列表為空 | `createWithTags_zeroTags_savedWithNoTags` |
| TC-PS-005 | content 為 null，儲存為空字串 | 負向 | 錯誤猜測 | content = null | 儲存的 content = `""` | `createWithTags_nullContent_savedAsEmptyString` |
| TC-PS-006 | 空白或 null 的 tag 值被略過 | 負向 | 錯誤猜測 | tags 含 `""`, `null`, `"  "` | 這些 tag 不被儲存 | `createWithTags_blankOrNullTagValues_areSkipped` |
| TC-PS-007 | 更新專案時，現有 tag 全部被新 tag 取代 | 正向 | 等價分割 | 專案已有 2 個 tag | 儲存後只剩新 tag | `saveWithTags_replacesExistingTags` |
| TC-PS-008 | tag 值前後的空白被 trim | 正向 | 錯誤猜測 | tag = `"  Java  "` | 儲存為 `"Java"` | `saveWithTags_tagValuesAreTrimmed` |
| TC-PS-009 | 以存在的 ID 查詢，回傳對應專案 | 正向 | 等價分割 | 專案存在 | 回傳該專案 | `getById_existingProject_returnsProject` |
| TC-PS-010 | 以不存在的 ID 查詢，拋出 IllegalArgumentException | 負向 | 錯誤猜測 | 專案不存在 | 拋出 IllegalArgumentException | `getById_nonExistentId_throwsIllegalArgumentException` |
| TC-PS-011 | getAll 委派至 Repository | 正向 | 等價分割 | 無 | repo.findAll 被呼叫 | `getAll_delegatesToRepository` |
| TC-PS-012 | deleteById 呼叫 repo.deleteById | 正向 | 等價分割 | 無 | repo.deleteById 被呼叫 | `deleteById_callsRepositoryDeleteById` |

---

### 4.4 ExperienceService — 工作經歷管理

**測試類**：`ExperienceServiceTest`
**測試方式**：`@ExtendWith(MockitoExtension.class)`，Mock `ExperienceRepo`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-ES-001 | 無任何資料時，nextSortOrder 回傳 0 | 邊界值 | 邊界值分析 | DB 為空 | 回傳 0 | `nextSortOrder_noExistingEntries_returnsZero` |
| TC-ES-002 | 有資料時，nextSortOrder 回傳最後 sortOrder + 1 | 正向 | 等價分割 | 最後一筆 sortOrder = 2 | 回傳 3 | `nextSortOrder_existingEntries_returnsLastPlusOne` |
| TC-ES-003 | getAll 委派至 repo 並回傳有序列表 | 正向 | 等價分割 | 無 | 依 sortOrder 排序的列表 | `getAll_delegatesToRepositoryOrderedQuery` |
| TC-ES-004 | 以存在的 ID 查詢，回傳對應 Experience | 正向 | 等價分割 | Experience 存在 | 回傳該 Experience | `getById_existingId_returnsExperience` |
| TC-ES-005 | 以不存在的 ID 查詢，拋出 IllegalArgumentException | 負向 | 錯誤猜測 | Experience 不存在 | 拋出 IllegalArgumentException | `getById_nonExistentId_throwsIllegalArgument` |
| TC-ES-006 | save 委派至 Repository | 正向 | 等價分割 | 無 | repo.save 被呼叫 | `save_delegatesToRepository` |
| TC-ES-007 | deleteById 呼叫 repo.deleteById | 正向 | 等價分割 | 無 | repo.deleteById 被呼叫 | `deleteById_callsRepositoryDelete` |

---

### 4.5 SectionService — 技能分組管理

**測試類**：`SectionServiceTest`
**測試方式**：`@ExtendWith(MockitoExtension.class)`，Mock `SectionRepo`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-SS-001 | getGroupedSections 依 key 分組回傳 Map | 正向 | 等價分割 | 多個 Section 存在 | 回傳以 key 為索引的 Map | `getGroupedSections_returnsSectionsGroupedByKey` |
| TC-SS-002 | 空頁面的 getGroupedSections 回傳空 Map | 邊界值 | 邊界值分析 | 無 Section | 回傳空 Map | `getGroupedSections_emptyPage_returnsEmptyMap` |
| TC-SS-003 | LinkedHashMap 保持插入順序 | 正向 | 等價分割 | 多個 Section | 順序與插入一致 | `getGroupedSections_preservesInsertionOrder` |
| TC-SS-004 | 以存在的 ID 查詢，回傳對應 Section | 正向 | 等價分割 | Section 存在 | 回傳該 Section | `getById_existingId_returnsSection` |
| TC-SS-005 | 以不存在的 ID 查詢，拋出 IllegalArgumentException | 負向 | 錯誤猜測 | Section 不存在 | 拋出 IllegalArgumentException | `getById_nonExistentId_throwsIllegalArgument` |
| TC-SS-006 | 無資料時新增 card，sortOrder 為 0 | 邊界值 | 邊界值分析 | DB 為空 | sortOrder = 0 | `addCard_noExisting_sortOrderIsZero` |
| TC-SS-007 | 有資料時新增 card，sortOrder 為最後 + 1 | 正向 | 等價分割 | 最後 sortOrder = 1 | sortOrder = 2 | `addCard_existingEntries_sortOrderIsLastPlusOne` |
| TC-SS-008 | 5 個 tag 全部儲存（邊界值） | 邊界值 | 邊界值分析 | tags.size() = 5 | 5 個 tag 全部儲存 | `saveWithTags_fiveTags_allSaved` |
| TC-SS-009 | 6 個 tag 只儲存 5 個（上限截斷） | 邊界值 | 邊界值分析 | tags.size() = 6 | 只儲存 5 個 tag | `saveWithTags_sixTags_onlyFiveSaved` |
| TC-SS-010 | 更新時，現有 tag 被新 tag 完整取代 | 正向 | 等價分割 | Section 已有 tag | 儲存後只剩新 tag | `saveWithTags_replacesExistingTags` |
| TC-SS-011 | deleteById 呼叫 repo.deleteById | 正向 | 等價分割 | 無 | repo.deleteById 被呼叫 | `deleteById_callsRepository` |

---

### 4.6 LabService — 技術工具管理

**測試類**：`LabServiceTest`
**測試方式**：`@ExtendWith(MockitoExtension.class)`，Mock `LabRepo`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-LS-001 | 無資料時 prepareNew，sortOrder 為 0 | 邊界值 | 邊界值分析 | DB 為空 | sortOrder = 0 | `prepareNew_noExistingEntries_sortOrderIsZero` |
| TC-LS-002 | 有資料時 prepareNew，sortOrder 為最後 + 1 | 正向 | 等價分割 | 最後 sortOrder = 3 | sortOrder = 4 | `prepareNew_existingEntries_sortOrderIsLastPlusOne` |
| TC-LS-003 | prepareNew 所有欄位被正確設定 | 正向 | 等價分割 | 無 | name / tags / sortOrder 正確 | `prepareNew_fieldsAreSetCorrectly` |
| TC-LS-004 | 5 個 tag 全部儲存（邊界值） | 邊界值 | 邊界值分析 | tags.size() = 5 | 5 個 tag 全部儲存 | `saveWithTags_fiveTags_allSaved` |
| TC-LS-005 | 6 個 tag 只儲存 5 個（上限截斷） | 邊界值 | 邊界值分析 | tags.size() = 6 | 只儲存 5 個 tag | `saveWithTags_sixTags_onlyFiveSaved` |
| TC-LS-006 | null tag 列表，儲存無 tag 的 Entry | 負向 | 錯誤猜測 | tags = null | tag 列表為空 | `saveWithTags_nullList_savedWithNoTags` |
| TC-LS-007 | 更新時，現有 tag 被新 tag 完整取代 | 正向 | 等價分割 | Entry 已有 tag | 儲存後只剩新 tag | `saveWithTags_replacesExistingTags` |
| TC-LS-008 | 以存在的 ID 查詢，回傳對應 LabEntry | 正向 | 等價分割 | Entry 存在 | 回傳該 Entry | `getById_existingId_returnsEntry` |
| TC-LS-009 | 以不存在的 ID 查詢，拋出 IllegalArgumentException | 負向 | 錯誤猜測 | Entry 不存在 | 拋出 IllegalArgumentException | `getById_nonExistentId_throwsIllegalArgument` |
| TC-LS-010 | getAll 委派至 Repository | 正向 | 等價分割 | 無 | repo.findAll 被呼叫 | `getAll_delegatesToRepository` |
| TC-LS-011 | deleteById 呼叫 repo.deleteById | 正向 | 等價分割 | 無 | repo.deleteById 被呼叫 | `deleteById_callsRepository` |

---

### 4.7 ContentService — 頁面內容管理

**測試類**：`ContentServiceTest`
**測試方式**：`@ExtendWith(MockitoExtension.class)`，Mock `ContentRepo`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-CS-001 | upsert 新 Block，執行 create 並儲存 | 正向 | 等價分割 | Block 不存在 | Block 被建立並 save | `upsert_newBlock_createsAndSaves` |
| TC-CS-002 | upsert 已存在的 Block，更新內容（不重複建立） | 正向 | 等價分割 | Block 已存在 | 內容被更新，無重複 Block | `upsert_existingBlock_updatesContentWithoutDuplicate` |
| TC-CS-003 | getPageContent 回傳以 blockKey 為索引的 Map | 正向 | 等價分割 | 多個 Block 存在 | 回傳 Map\<key, Block\> | `getPageContent_returnsMapKeyedByBlockKey` |
| TC-CS-004 | 空頁面的 getPageContent 回傳空 Map | 邊界值 | 邊界值分析 | 無 Block | 回傳空 Map | `getPageContent_emptyPage_returnsEmptyMap` |
| TC-CS-005 | getDrawerContent 只回傳 `rail.` 前綴的 Block | 正向 | 等價分割 | 混合 key 前綴的 Block | 只回傳 `rail.` 開頭的 | `getDrawerContent_returnsOnlyRailPrefixedBlocks` |
| TC-CS-006 | updatePageContent 對每個 entry 呼叫 upsert | 正向 | 等價分割 | 多個 entry | 每個 entry 都被 upsert | `updatePageContent_callsUpsertForEachEntry` |

---

## 5. 安全性測試

### 5.1 SecurityConfig — 路由存取規則

**測試類**：`SecurityConfigTest`
**測試方式**：`@SpringBootTest` + `@AutoConfigureMockMvc`（載入完整 Security 規則）

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 認證狀態 | 目標路由 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|---|
| TC-SEC-001 | 首頁公開，未認證可訪問 | 安全性 | 決策表 | 未認證 | `GET /public/` | 200 | `publicHome_unauthenticated_returns200` |
| TC-SEC-002 | 專案頁公開，未認證可訪問 | 安全性 | 決策表 | 未認證 | `GET /public/projects` | 200 | `publicProjects_unauthenticated_returns200` |
| TC-SEC-003 | 技能頁公開，未認證可訪問 | 安全性 | 決策表 | 未認證 | `GET /public/skills` | 200 | `publicSkills_unauthenticated_returns200` |
| TC-SEC-004 | 經歷頁公開，未認證可訪問 | 安全性 | 決策表 | 未認證 | `GET /public/experience` | 200 | `publicExperience_unauthenticated_returns200` |
| TC-SEC-005 | Lab 頁公開，未認證可訪問 | 安全性 | 決策表 | 未認證 | `GET /public/lab` | 200 | `publicLab_unauthenticated_returns200` |
| TC-SEC-006 | 登入頁公開，未認證可訪問 | 安全性 | 決策表 | 未認證 | `GET /login` | 200 | `loginPage_unauthenticated_returns200` |
| TC-SEC-007 | 註冊頁公開，未認證可訪問 | 安全性 | 決策表 | 未認證 | `GET /register` | 200 | `registerPage_unauthenticated_returns200` |
| TC-SEC-008 | 改密碼需登入，未認證跳轉至 `/login` | 安全性 | 決策表 | 未認證 | `GET /change-password` | 3xx → /login | `changePassword_unauthenticated_redirectsToLogin` |
| TC-SEC-009 | 已認證可訪問改密碼頁 | 安全性 | 決策表 | 已認證（任意 role） | `GET /change-password` | 200 | `changePassword_authenticated_returns200` |
| TC-SEC-010 | Admin 專案頁，未認證跳轉至 `/login` | 安全性 | 決策表 | 未認證 | `GET /admin/project` | 3xx → /login | `adminProject_unauthenticated_redirectsToLogin` |
| TC-SEC-011 | Admin 專案頁，ROLE_USER 被拒（403） | 安全性 | 決策表 | ROLE_USER | `GET /admin/project` | 403 | `adminProject_asRoleUser_returns403` |
| TC-SEC-012 | Admin 專案頁，ROLE_ADMIN 可訪問 | 安全性 | 決策表 | ROLE_ADMIN | `GET /admin/project` | 200 | `adminProject_asRoleAdmin_returns200` |
| TC-SEC-013 | Admin 首頁，未認證跳轉至 `/login` | 安全性 | 決策表 | 未認證 | `GET /admin/` | 3xx → /login | `adminHomepage_unauthenticated_redirectsToLogin` |
| TC-SEC-014 | Admin 首頁，ROLE_USER 被拒（403） | 安全性 | 決策表 | ROLE_USER | `GET /admin/` | 403 | `adminHomepage_asRoleUser_returns403` |
| TC-SEC-015 | Admin 首頁，ROLE_ADMIN 可訪問 | 安全性 | 決策表 | ROLE_ADMIN | `GET /admin/` | 200 | `adminHomepage_asRoleAdmin_returns200` |
| TC-SEC-016 | Admin 經歷頁，未認證跳轉至 `/login` | 安全性 | 決策表 | 未認證 | `GET /admin/experience` | 3xx → /login | `adminExperience_unauthenticated_redirectsToLogin` |
| TC-SEC-017 | Admin 經歷頁，ROLE_USER 被拒（403） | 安全性 | 決策表 | ROLE_USER | `GET /admin/experience` | 403 | `adminExperience_asRoleUser_returns403` |
| TC-SEC-018 | Admin 經歷頁，ROLE_ADMIN 可訪問 | 安全性 | 決策表 | ROLE_ADMIN | `GET /admin/experience` | 200 | `adminExperience_asRoleAdmin_returns200` |
| TC-SEC-019 | Admin 技能頁，未認證跳轉至 `/login` | 安全性 | 決策表 | 未認證 | `GET /admin/skills` | 3xx → /login | `adminSkills_unauthenticated_redirectsToLogin` |
| TC-SEC-020 | Admin 技能頁，ROLE_USER 被拒（403） | 安全性 | 決策表 | ROLE_USER | `GET /admin/skills` | 403 | `adminSkills_asRoleUser_returns403` |
| TC-SEC-021 | Admin 技能頁，ROLE_ADMIN 可訪問 | 安全性 | 決策表 | ROLE_ADMIN | `GET /admin/skills` | 200 | `adminSkills_asRoleAdmin_returns200` |
| TC-SEC-022 | Admin Lab 頁，未認證跳轉至 `/login` | 安全性 | 決策表 | 未認證 | `GET /admin/lab` | 3xx → /login | `adminLab_unauthenticated_redirectsToLogin` |
| TC-SEC-023 | Admin Lab 頁，ROLE_USER 被拒（403） | 安全性 | 決策表 | ROLE_USER | `GET /admin/lab` | 403 | `adminLab_asRoleUser_returns403` |
| TC-SEC-024 | Admin Lab 頁，ROLE_ADMIN 可訪問 | 安全性 | 決策表 | ROLE_ADMIN | `GET /admin/lab` | 200 | `adminLab_asRoleAdmin_returns200` |
| TC-SEC-025 | POST 不帶 CSRF Token，回傳 403 | 安全性 | 錯誤猜測 | 任意 | 任意 POST | 403 | `postWithoutCsrf_returns403` |
| TC-SEC-026 | POST 帶 CSRF Token（ADMIN），不回傳 403 | 安全性 | 錯誤猜測 | ROLE_ADMIN | 任意 POST | ≠ 403 | `postWithCsrf_asAdmin_doesNotReturn403` |

---

## 6. 控制器測試

> **說明**：Controller Tests 使用 `@WebMvcTest`，**不載入** SecurityConfig，安全規則由 Tier 3 的 `SecurityConfigTest` 統一驗證。Controller Tests 只驗證：HTTP 回應碼、View 名稱、Model 屬性、Service 呼叫行為。

### 6.1 AuthController — 認證相關頁面

**測試類**：`AuthControllerTest`
**測試方式**：`@WebMvcTest(AuthController.class)` + `@Import(SecurityConfig.class)` + `@WithMockUser`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-AC-001 | GET /login 回傳 200 與 login view | 正向 | 等價分割 | 無 | status=200, view=login | `getLoginPage_returns200AndLoginView` |
| TC-AC-002 | GET /register 回傳 200 與 register view | 正向 | 等價分割 | 無 | status=200, model 含 form | `getRegisterPage_returns200AndRegisterView` |
| TC-AC-003 | POST /register 有效資料，跳轉至 /login | 正向 | 等價分割 | 帳號不存在 | 3xx → /login | `postRegister_validNewUser_redirectsToLogin` |
| TC-AC-004 | POST /register 重複帳號，返回 register view 含 usernameExists=true | 負向 | 等價分割 | 帳號已存在 | view=register, usernameExists=true | `postRegister_existingUsername_returnsRegisterViewWithFlag` |
| TC-AC-005 | POST /register 密碼不一致，返回 register view 含 passwordMismatch=true | 負向 | 等價分割 | 無 | view=register, passwordMismatch=true | `postRegister_passwordMismatch_returnsRegisterViewWithFlag` |
| TC-AC-006 | POST /register 空白帳號，Bean Validation 攔截，model 含 errors | 負向 | 邊界值分析 | username = `""` | view=register, model 有 errors | `postRegister_blankUsername_failsValidation` |
| TC-AC-007 | POST /register 密碼過短，Bean Validation 攔截 | 負向 | 邊界值分析 | password 長度 < 最小值 | view=register, model 有 errors | `postRegister_shortPassword_failsValidation` |
| TC-AC-008 | POST /register 帳號過短，Bean Validation 攔截 | 負向 | 邊界值分析 | username 長度 < 最小值 | view=register, model 有 errors | `postRegister_tooShortUsername_failsValidation` |
| TC-AC-009 | GET /change-password 已認證，回傳 200 | 正向 | 等價分割 | 已認證 | status=200 | `getChangePassword_authenticated_returns200` |
| TC-AC-010 | POST /change-password 正確舊密碼，跳轉成功 | 正向 | 等價分割 | 舊密碼正確 | 3xx 含 ?success | `postChangePassword_correctPassword_redirectsWithSuccess` |
| TC-AC-011 | POST /change-password 錯誤舊密碼，返回 view 含 wrongPassword=true | 負向 | 等價分割 | 舊密碼錯誤 | view=change-password, wrongPassword=true | `postChangePassword_wrongCurrentPassword_returnsViewWithFlag` |
| TC-AC-012 | POST /change-password 新密碼不一致，返回 view 含 confirmMismatch=true | 負向 | 等價分割 | 新密碼不一致 | view=change-password, confirmMismatch=true | `postChangePassword_newPasswordMismatch_returnsViewWithFlag` |
| TC-AC-013 | POST /register 帳號超過上限（21 字），Bean Validation 攔截 | 邊界值 | 邊界值分析 | username 長度 > 最大值 | view=register, username FieldError | `postRegister_usernameTooLong_failsValidation` |
| TC-AC-014 | POST /register 密碼超過上限（21 字），Bean Validation 攔截 | 邊界值 | 邊界值分析 | password 長度 > 最大值 | view=register, password FieldError | `postRegister_passwordTooLong_failsValidation` |
| TC-AC-015 | POST /change-password 新密碼過短（6 字），Bean Validation 攔截 | 邊界值 | 邊界值分析 | newPassword 長度 < 最小值 | model 含 FieldError | `postChangePassword_newPasswordTooShort_failsValidation` |
| TC-AC-016 | POST /change-password 新密碼超過上限（21 字），Bean Validation 攔截 | 邊界值 | 邊界值分析 | newPassword 長度 > 最大值 | model 含 FieldError | `postChangePassword_newPasswordTooLong_failsValidation` |

---

### 6.2 HomeController — 公開頁面

**測試類**：`HomeControllerTest`
**測試方式**：`@WebMvcTest(HomeController.class)` + `@WithMockUser`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-HC-001 | GET /public/ 回傳 200 與 public/index view | 正向 | 等價分割 | 無 | status=200, view=public/index | `getHome_returns200AndIndexView` |
| TC-HC-002 | GET /public/projects 回傳 200，model 含 projects | 正向 | 等價分割 | 無 | status=200, model 含 projects | `getProjects_returns200AndProjectsView` |
| TC-HC-003 | GET /public/projects 空列表，正常渲染不崩潰 | 邊界值 | 邊界值分析 | projects = [] | status=200, projects 為空 list | `getProjects_emptyList_rendersWithoutError` |
| TC-HC-004 | GET /public/skills 回傳 200，model 含 groupedSections | 正向 | 等價分割 | 無 | status=200, model 含 groupedSections | `getSkills_returns200AndSkillsView` |
| TC-HC-005 | GET /public/experience 回傳 200，model 含 experienceList | 正向 | 等價分割 | 無 | status=200, model 含 experienceList | `getExperience_returns200AndExperienceView` |
| TC-HC-006 | GET /public/experience model 中的列表為反轉排序 | 正向 | 等價分割 | 多筆 experience | 列表順序被反轉 | `getExperience_modelContainsReversedList` |
| TC-HC-007 | GET /public/lab 回傳 200，model 含 labEntries | 正向 | 等價分割 | 無 | status=200, model 含 labEntries | `getLab_returns200AndLabView` |
| TC-HC-008 | DrawerControllerAdvice 注入 drawer 內容至 model | 正向 | 等價分割 | ContentService 有資料 | model 含 drawerContent | `drawerContent_isInjectedIntoModel` |

---

### 6.3 AdminProjectController — 專案後台

**測試類**：`AdminProjectControllerTest`
**測試方式**：`@WebMvcTest(AdminProjectController.class)` + `@WithMockUser(roles="ADMIN")`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 前置條件 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|---|
| TC-APC-001 | GET /admin/project ADMIN 回傳 200，model 含 projects | 正向 | 等價分割 | ROLE_ADMIN | status=200, model 含 projects | `getAdminProjects_asAdmin_returns200WithProjectList` |
| TC-APC-002 | GET /admin/project/{id}/edit 存在的 ID，回傳 200 | 正向 | 等價分割 | 專案存在 | status=200, model 含 project | `getEditProject_asAdmin_existingProject_returns200` |
| TC-APC-003 | GET /admin/project/{id}/edit 不存在的 ID，拋出 IllegalArgumentException | 負向 | 錯誤猜測 | 專案不存在 | 拋出 IllegalArgumentException | `getEditProject_nonExistentProject_throwsIllegalArgument` |
| TC-APC-004 | POST /admin/project/save 有效資料，跳轉至列表頁 | 正向 | 等價分割 | ROLE_ADMIN | 3xx → /admin/project?saved | `postSaveProject_asAdmin_validData_redirectsToList` |
| TC-APC-005 | POST /admin/project/save 無 CSRF，回傳 403 | 安全性 | 錯誤猜測 | 無 CSRF Token | status=403 | `postSaveProject_withoutCsrf_returns403` |
| TC-APC-006 | POST /admin/project/{id}/save 更新，跳轉至列表頁 | 正向 | 等價分割 | ROLE_ADMIN | 3xx → /admin/project?saved | `postUpdateProject_asAdmin_validData_redirectsToList` |
| TC-APC-007 | POST /admin/project/{id}/delete ADMIN，跳轉並呼叫 deleteById | 正向 | 等價分割 | ROLE_ADMIN | 3xx → /admin/project?deleted | `postDeleteProject_asAdmin_redirectsToList` |
| TC-APC-008 | POST /admin/project/{id}/delete 無 CSRF，回傳 403，deleteById 不被呼叫 | 安全性 | 錯誤猜測 | 無 CSRF Token | status=403, deleteById 未被呼叫 | `postDeleteProject_withoutCsrf_returns403` |
| TC-APC-009 | POST /admin/project/save 正確傳遞 tags 至 Service | 正向 | 等價分割 | ROLE_ADMIN | saveWithTags 被呼叫，tags 正確 | `postSaveProject_passesTagsToService` |
| TC-APC-V01 | POST /admin/project/save 空白 title，Bean Validation 攔截 | 負向 | 邊界值分析 | title = `""` | view=project-list, title FieldError | `postSaveProject_blankTitle_returnsFormWithErrors` |
| TC-APC-V02 | POST /admin/project/save title 超過上限（256 字），Bean Validation 攔截 | 邊界值 | 邊界值分析 | title 長度 > 255 | view=project-list, model 含 errors | `postSaveProject_titleTooLong_returnsFormWithErrors` |
| TC-APC-V03 | POST /admin/project/{id}/save 更新時空白 title，返回編輯頁含 FieldError | 負向 | 邊界值分析 | title = `""` | view=project-edit, title FieldError | `postUpdateProject_blankTitle_returnsEditFormWithErrors` |

---

### 6.4 AdminHomepageController — 首頁後台

**測試類**：`AdminHomepageControllerTest`
**測試方式**：`@WebMvcTest(AdminHomepageController.class)` + `@WithMockUser(roles="ADMIN")`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|
| TC-AHC-001 | GET /admin/ 回傳 200，model 含必要屬性 | 正向 | 等價分割 | status=200, model 含屬性 | `getAdminHomepage_asAdmin_returns200` |
| TC-AHC-002 | GET /admin/section/{id}/edit 回傳 200 | 正向 | 等價分割 | status=200 | `getEditSection_asAdmin_existingSection_returns200` |
| TC-AHC-003 | POST /admin/section/create 跳轉至編輯頁 | 正向 | 等價分割 | 3xx → edit page | `postCreateSection_asAdmin_redirectsToEditPage` |
| TC-AHC-004 | POST /admin/section/{id}/save 跳轉至首頁 | 正向 | 等價分割 | 3xx → /admin/ | `postSaveSection_asAdmin_validData_redirectsToHomepage` |
| TC-AHC-005 | POST /admin/section/{id}/save 無 CSRF，回傳 403 | 安全性 | 錯誤猜測 | status=403 | `postSaveSection_withoutCsrf_returns403` |
| TC-AHC-006 | POST /admin/section/{id}/delete 跳轉至首頁 | 正向 | 等價分割 | 3xx → /admin/ | `postDeleteSection_asAdmin_redirectsToHomepage` |
| TC-AHC-007 | POST /admin/rail/save 跳轉，updatePageContent 被呼叫 | 正向 | 等價分割 | 3xx → /admin/?saved | `postSaveRail_asAdmin_redirectsWithSaved` |
| TC-AHC-V01 | POST /admin/section/{id}/save title 超過上限（201 字），返回編輯頁含 FieldError | 邊界值 | 邊界值分析 | view=section-edit, title FieldError | `postSaveSection_titleTooLong_returnsEditFormWithErrors` |
| TC-AHC-V02 | POST /admin/section/{id}/save groupKey 超過上限（51 字），返回編輯頁含 FieldError | 邊界值 | 邊界值分析 | view=section-edit, groupKey FieldError | `postSaveSection_groupKeyTooLong_returnsEditFormWithErrors` |

---

### 6.5 AdminSkillsController — 技能後台

**測試類**：`AdminSkillsControllerTest`
**測試方式**：`@WebMvcTest(AdminSkillsController.class)` + `@WithMockUser(roles="ADMIN")`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|
| TC-ASC-001 | GET /admin/skills 回傳 200，model 含 groupedSections | 正向 | 等價分割 | status=200 | `getAdminSkills_asAdmin_returns200` |
| TC-ASC-002 | POST /admin/skills/section/create 跳轉至編輯頁 | 正向 | 等價分割 | 3xx → edit page | `postCreateSection_asAdmin_redirectsToEditPage` |
| TC-ASC-003 | GET /admin/skills/section/{id}/edit 回傳 200 | 正向 | 等價分割 | status=200 | `getEditSection_asAdmin_returns200` |
| TC-ASC-004 | POST /admin/skills/section/{id}/save 跳轉至技能頁 | 正向 | 等價分割 | 3xx → /admin/skills | `postSaveSection_asAdmin_redirectsToSkills` |
| TC-ASC-005 | POST /admin/skills/section/{id}/delete 跳轉，deleteById 被呼叫 | 正向 | 等價分割 | 3xx → /admin/skills | `postDeleteSection_asAdmin_redirectsToSkills` |
| TC-ASC-006 | POST 無 CSRF，回傳 403 | 安全性 | 錯誤猜測 | status=403 | `postDeleteSection_withoutCsrf_returns403` |
| TC-ASC-V01 | POST /admin/skills/section/{id}/save title 超過上限（201 字），返回編輯頁含 FieldError | 邊界值 | 邊界值分析 | view=section-edit, title FieldError | `postSaveSection_titleTooLong_returnsEditFormWithErrors` |
| TC-ASC-V02 | POST /admin/skills/section/{id}/save groupKey 超過上限（51 字），返回編輯頁含 FieldError | 邊界值 | 邊界值分析 | view=section-edit, groupKey FieldError | `postSaveSection_groupKeyTooLong_returnsEditFormWithErrors` |

---

### 6.6 AdminExperienceController — 經歷後台

**測試類**：`AdminExperienceControllerTest`
**測試方式**：`@WebMvcTest(AdminExperienceController.class)` + `@WithMockUser(roles="ADMIN")`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|
| TC-AEC-001 | GET /admin/experience 回傳 200，model 含 experienceList | 正向 | 等價分割 | status=200 | `getAdminExperience_asAdmin_returns200` |
| TC-AEC-002 | GET /admin/experience/{id}/edit 回傳 200 | 正向 | 等價分割 | status=200 | `getEditExperience_asAdmin_returns200` |
| TC-AEC-003 | POST /admin/experience/create 跳轉至列表頁 | 正向 | 等價分割 | 3xx → /admin/experience?saved | `postCreateExperience_asAdmin_redirectsToList` |
| TC-AEC-004 | POST /admin/experience/create 無 CSRF，回傳 403 | 安全性 | 錯誤猜測 | status=403 | `postCreateExperience_withoutCsrf_returns403` |
| TC-AEC-005 | POST /admin/experience/{id}/save 跳轉至列表頁 | 正向 | 等價分割 | 3xx → /admin/experience?saved | `postSaveExperience_asAdmin_redirectsToList` |
| TC-AEC-006 | POST /admin/experience/{id}/delete 跳轉，deleteById 被呼叫 | 正向 | 等價分割 | 3xx → /admin/experience?deleted | `postDeleteExperience_asAdmin_redirectsToList` |
| TC-AEC-007 | POST /admin/experience/{id}/delete 無 CSRF，回傳 403 | 安全性 | 錯誤猜測 | status=403 | `postDeleteExperience_withoutCsrf_returns403` |
| TC-AEC-008 | POST /admin/experience/create 使用 nextSortOrder 作為排序值 | 正向 | 等價分割 | nextSortOrder() 被呼叫 | `postCreateExperience_usesNextSortOrderFromService` |
| TC-AEC-V01 | POST /admin/experience/create 空白 year，Bean Validation 攔截 | 負向 | 邊界值分析 | view=experience, year FieldError | `postCreateExperience_blankYear_returnsFormWithErrors` |
| TC-AEC-V02 | POST /admin/experience/create 空白 title，Bean Validation 攔截 | 負向 | 邊界值分析 | view=experience, title FieldError | `postCreateExperience_blankTitle_returnsFormWithErrors` |
| TC-AEC-V03 | POST /admin/experience/{id}/save 更新時空白 title，返回編輯頁含 FieldError | 負向 | 邊界值分析 | view=experience-edit, title FieldError | `postSaveExperience_blankTitle_returnsEditFormWithErrors` |
| TC-AEC-V04 | POST /admin/experience/create year 超過上限（5 字），Bean Validation 攔截 | 邊界值 | 邊界值分析 | year 長度 > 4 | view=experience, year FieldError | `postCreateExperience_yearTooLong_returnsFormWithErrors` |
| TC-AEC-V05 | POST /admin/experience/create title 超過上限（201 字），Bean Validation 攔截 | 邊界值 | 邊界值分析 | title 長度 > 200 | view=experience, title FieldError | `postCreateExperience_titleTooLong_returnsFormWithErrors` |

---

### 6.7 AdminLabController — Lab 後台

**測試類**：`AdminLabControllerTest`
**測試方式**：`@WebMvcTest(AdminLabController.class)` + `@WithMockUser(roles="ADMIN")`

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|
| TC-ALC-001 | GET /admin/lab 回傳 200，model 含 labEntries | 正向 | 等價分割 | status=200 | `getAdminLab_asAdmin_returns200` |
| TC-ALC-002 | GET /admin/lab/{id}/edit 回傳 200 | 正向 | 等價分割 | status=200 | `getEditLab_asAdmin_returns200` |
| TC-ALC-003 | POST /admin/lab/save 建立 Lab 項目，跳轉至列表頁 | 正向 | 等價分割 | 3xx → /admin/lab?saved | `postSaveLab_asAdmin_redirectsToList` |
| TC-ALC-004 | POST /admin/lab/save 無 CSRF，回傳 403 | 安全性 | 錯誤猜測 | status=403 | `postSaveLab_withoutCsrf_returns403` |
| TC-ALC-005 | POST /admin/lab/{id}/save 更新，跳轉至列表頁 | 正向 | 等價分割 | 3xx → /admin/lab?saved | `postUpdateLab_asAdmin_redirectsToList` |
| TC-ALC-006 | POST /admin/lab/{id}/delete 跳轉，deleteById 被呼叫 | 正向 | 等價分割 | 3xx → /admin/lab?deleted | `postDeleteLab_asAdmin_redirectsToList` |
| TC-ALC-007 | POST /admin/lab/{id}/delete 無 CSRF，回傳 403 | 安全性 | 錯誤猜測 | status=403 | `postDeleteLab_withoutCsrf_returns403` |
| TC-ALC-V01 | POST /admin/lab/save 空白 name，Bean Validation 攔截 | 負向 | 邊界值分析 | view=lab, name FieldError | `postSaveLab_blankName_returnsFormWithErrors` |
| TC-ALC-V02 | POST /admin/lab/{id}/save 更新時空白 name，返回編輯頁含 FieldError | 負向 | 邊界值分析 | view=lab-edit, name FieldError | `postUpdateLab_blankName_returnsEditFormWithErrors` |
| TC-ALC-V03 | POST /admin/lab/save name 超過上限（101 字），Bean Validation 攔截 | 邊界值 | 邊界值分析 | name 長度 > 100 | view=lab, name FieldError | `postSaveLab_nameTooLong_returnsFormWithErrors` |

---

### 6.8 DrawerControllerAdvice — 全域 Drawer 注入

**測試類**：`DrawerControllerAdviceTest`
**測試方式**：`@WebMvcTest(HomeController.class)`（Advice 只作用於 HomeController）

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|
| TC-DCA-001 | 訪問公開首頁，drawer 內容被注入至 model | 正向 | 等價分割 | model 含 drawerContent | `drawerContent_isInjectedIntoEveryPublicPage` |
| TC-DCA-002 | 訪問 /public/projects，drawer 內容被注入 | 正向 | 等價分割 | model 含 drawerContent | `drawerContent_isInjectedOnProjectsPage` |
| TC-DCA-003 | 訪問 /public/experience，drawer 內容被注入 | 正向 | 等價分割 | model 含 drawerContent | `drawerContent_isInjectedOnExperiencePage` |
| TC-DCA-004 | ContentService 回傳空 Map，頁面仍正常渲染 | 邊界值 | 邊界值分析 | drawerContent = {} | status=200 | `drawerContent_emptyMap_pageStillRenders` |

---

## 7. 整合測試

> **說明**：所有整合測試使用 `@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional`。
> `@Transactional` 確保每個測試結束後自動 rollback，測試之間不互相汙染。

### 7.1 AuthFlowIntegrationTest — 認證完整流程

| TC 編號 | 測試案例名稱 | 測試類型 | 流程說明 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|
| TC-INT-001 | 註冊後以相同帳密登入，成功跳轉 | 正向 | POST /register → POST /login | 登入成功，跳轉至目標頁 | `register_thenLoginWithSameCredentials_succeeds` |
| TC-INT-002 | 註冊後以錯誤密碼登入，跳轉至 /login?error | 負向 | POST /register → POST /login（錯誤密碼） | 跳轉至 /login?error | `register_thenLoginWithWrongPassword_fails` |
| TC-INT-003 | 重複帳號註冊，返回 register 頁面含 usernameExists=true | 負向 | POST /register（帳號重複） | usernameExists=true | `register_duplicateUsername_showsError` |
| TC-INT-004 | 空白帳號註冊，Bean Validation 攔截 | 負向 | POST /register（空白帳號） | model 含 errors | `register_withBlankUsername_failsValidation` |

---

### 7.2 AdminProjectFlowIntegrationTest — 後台專案完整流程

| TC 編號 | 測試案例名稱 | 測試類型 | 流程說明 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|
| TC-INT-005 | 建立專案後，公開頁面可見該專案 | 正向 | POST /admin/project/save → GET /public/projects | 公開頁面顯示專案 | `createProject_thenVisibleOnPublicProjectsPage` |
| TC-INT-006 | 建立含 5 個 tag 的專案，後台列表正常顯示 | 正向 | POST /admin/project/save（5 tags） | 後台列表 status=200 | `createProject_withFiveTags_allTagsPersisted` |
| TC-INT-007 | 建立後刪除專案，後台列表正常顯示 | 正向 | POST save → POST delete | 後台列表 status=200 | `createThenDelete_projectNoLongerOnPublicPage` |

---

### 7.3 PublicEndpointIntegrationTest — 公開端點驗證

| TC 編號 | 測試案例名稱 | 測試類型 | 設計方法 | 預期結果 | 對應自動化測試 |
|---|---|---|---|---|---|
| TC-INT-008 | 所有公開端點（5 個），未認證皆回傳 200（參數化） | 安全性 | 等價分割 | 全部 status=200 | `allPublicEndpoints_unauthenticated_return200` |
| TC-INT-009 | 含 XSS Payload 的專案名稱，公開頁面正確 escape script tag | 安全性 | 錯誤猜測 | 輸出不含原始 `<script>` | `createProjectWithXssPayload_publicPageEscapesScript` |
| TC-INT-010 | 首頁回傳 200，Content-Type 為 text/html | 正向 | 等價分割 | status=200, content-type=text/html | `homePage_returns200AndHasHtmlStructure` |
| TC-INT-011 | 空 DB 時專案頁面正常渲染，不崩潰 | 邊界值 | 邊界值分析 | status=200 | `projectsPage_emptyDb_renders200WithoutError` |
| TC-INT-012 | 空 DB 時經歷頁面正常渲染，不崩潰 | 邊界值 | 邊界值分析 | status=200 | `experiencePage_emptyDb_renders200WithoutError` |
| TC-INT-013 | 空 DB 時技能頁面正常渲染，不崩潰 | 邊界值 | 邊界值分析 | status=200 | `skillsPage_emptyDb_renders200WithoutError` |
| TC-INT-014 | 空 DB 時 Lab 頁面正常渲染，不崩潰 | 邊界值 | 邊界值分析 | status=200 | `labPage_emptyDb_renders200WithoutError` |

---

## 8. 設計決策紀錄

### 8.1 Form DTO 與 Validation 設計

所有後台 Controller 的 POST 方法均透過 Form DTO 接收輸入，搭配 Bean Validation（`@NotBlank`、`@Size`）在 Controller 層攔截非法輸入，驗證失敗時返回表單頁面而非拋出 500。

| DTO | 對應 Controller | 驗證欄位 |
|---|---|---|
| `RegisterForm` | AuthController | `username`：NotBlank、Size(min=3, max=20)；`password`：NotBlank、Size(min=8, max=20) |
| `ChangePasswordForm` | AuthController | `newPassword`：Size(min=8, max=20)，與 RegisterForm 保持一致 |
| `ProjectForm` | AdminProjectController | `title`：NotBlank、Size(max=255) |
| `ExperienceForm` | AdminExperienceController | `year`：NotBlank、Size(max=4)；`title`：NotBlank、Size(max=200) |
| `LabForm` | AdminLabController | `name`：NotBlank、Size(max=100) |
| `SectionForm` | AdminHomepageController、AdminSkillsController | `title`：Size(max=200)；`groupKey`：Size(max=50)；`sectionLabel`：Size(max=100)（均選填，Section 內容設計為彈性輸入） |

### 8.2 Repository 層測試技術決策

`@DataJpaTest` 預設會自建一個 H2 實例，不使用 `src/test/resources/application.yml` 的設定。加上 `@AutoConfigureTestDatabase(replace = NONE)` 後，測試才會使用已設定的 H2 DataSource，確保 `ddl-auto: create-drop` 生效並建立完整的 schema。

`Experience.year` 欄位原本未明確指定 DB 欄位名稱，Hibernate 預設使用 `year`，與 H2 保留字衝突。改以 `@Column(name = "exp_year", length = 4)` 明確指定，從根本解決問題，不依賴 `NON_KEYWORDS` 設定。

### 8.3 UI 測試

UI 自動化測試不在本計劃範圍內。

---

*TEST_PLAN.md — portfolio-demo v1.0*
