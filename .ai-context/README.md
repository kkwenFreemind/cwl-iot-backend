# CWL-IoT Backend AI Assistant Context

> **Always load this context at the start of conversations**  
> 包含完整專案背景、技術棧、開發標準與 AI 協作指南

## 🎯 專案概述

**專案名稱**: Community Water Level IoT Backend (社區水位監測 IoT 系統)  
**當前階段**: Phase 1 - Project Architecture Optimization  
**技術棧**: Spring Boot 3.5+, Java 17+, PostgreSQL, Spring Data JPA, Spring Security 6  
**開發模式**: AI-Enhanced Development (AI 輔助開發)

## 📋 當前狀態

**進度摘要**: Task Group 1 Complete ✅, Task Group 1.5 In Progress 🟡  
**最近完成**: 
- MyBatis → Spring Data JPA 完整遷移
- Profile API 修復 (deptName, roleNames 字段補全)
- Dictionary API 資料庫架構問題修復

**當前重點**:
- API 驗證與測試 (Task Group 1.5)
- 確保 JPA 實體與資料庫架構一致性
- 系統管理模組 API 全面驗證

## 📄 核心參考文件

1. **詳細技術指南**: `.ai-context/spring-boot-expert.md` - 完整的 Spring Boot 3 專家指南
2. **項目計劃**: `doc/plan/phase1/phase1_plan.md` - 當前開發階段詳細計劃
3. **項目概述**: `README.md` - 專案總覽與設置指南

## 🔧 AI 協作指南

### 開發標準
- **文檔**: 專業雙語 JavaDoc (中文 + English)
- **代碼**: Spring Boot 3 最佳實踐 + Java 17+ 現代語法
- **架構**: JPA 優先於 MyBatis，IoT 特化設計
- **安全**: Spring Security 6 + JWT token 管理
- **數據權限**: 基於角色和部門的數據隔離控制

### 數據權限控制原則 🔐

#### 核心實現架構
```java
// 數據權限枚舉定義 (DataScopeEnum)
ALL(1, "All data"),              // 全部數據權限 (系統管理員)
DEPT_AND_SUB(2, "Dept + Sub"),   // 部門及子部門數據權限
DEPT(3, "Department data"),      // 本部門數據權限 (社區管理員)
SELF(4, "Self data");            // 僅個人數據權限 (普通用戶)
```

#### 標準實現模式
所有涉及數據查詢的服務方法必須實現數據權限過濾：

```java
/**
 * 數據權限過濾標準實現模式
 * 在所有分頁查詢和列表查詢中應用此模式
 */
private void applyDataPermissionFilter(List<Predicate> predicates, 
                                     Root<EntityJpa> root, 
                                     CriteriaBuilder criteriaBuilder) {
    // 1. 檢查是否為超級管理員，如果是則跳過權限控制
    if (SecurityUtils.isRoot()) {
        return;
    }

    // 2. 獲取當前用戶的數據權限範圍
    Integer dataScope = SecurityUtils.getDataScope();
    Long currentUserId = SecurityUtils.getUserId();
    Long currentDeptId = SecurityUtils.getDeptId();

    if (dataScope != null && currentUserId != null) {
        switch (dataScope) {
            case 1: // ALL - 全部數據權限
                // 系統管理員無需添加過濾條件
                break;
                
            case 2: // DEPT_AND_SUB - 部門及子部門數據權限
                if (currentDeptId != null) {
                    // 查找當前部門及所有子部門
                    List<DeptJpa> subDepts = deptJpaRepository.findByTreePathContaining("," + currentDeptId + ",");
                    List<Long> deptIds = subDepts.stream().map(DeptJpa::getId).collect(Collectors.toList());
                    deptIds.add(currentDeptId);
                    predicates.add(root.get("deptId").in(deptIds));
                }
                break;
                
            case 3: // DEPT - 本部門數據權限 (社區管理員標準)
                if (currentDeptId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("deptId"), currentDeptId));
                }
                break;
                
            case 4: // SELF - 僅個人數據權限
                predicates.add(criteriaBuilder.equal(root.get("createBy"), currentUserId));
                break;
                
            default:
                // 默認最嚴格權限：僅個人數據
                predicates.add(criteriaBuilder.equal(root.get("createBy"), currentUserId));
                break;
        }
    }
}
```

#### 應用場景與模組
- **用戶管理**: 社區管理員只能管理本社區用戶 ✅ (已實現)
- **設備管理**: 按社區隔離水位感測器數據
- **告警記錄**: 社區管理員只查看本社區告警
- **報表數據**: 按權限範圍顯示統計資料
- **系統日誌**: 按操作權限過濾日誌訪問

#### 角色權限配置標準
```sql
-- 系統管理員：全部數據權限
INSERT INTO sys_role (name, code, data_scope) VALUES ('System Administrator', 'System_Admin', 1);

-- 社區管理員：本部門數據權限 (CWL-IoT 標準)
INSERT INTO sys_role (name, code, data_scope) VALUES ('Community Admin', 'Community_Admin', 3);

-- 社區用戶：僅個人數據權限
INSERT INTO sys_role (name, code, data_scope) VALUES ('Community User', 'Community_User', 4);
```

#### 實現檢查清單
- [ ] 在 Specification 查詢中調用 `applyDataPermissionFilter()`
- [ ] 確保實體有 `deptId` 或 `createBy` 字段用於權限過濾
- [ ] 在服務方法 JavaDoc 中標明數據權限控制
- [ ] 添加相應的單元測試驗證不同角色的數據隔離
- [ ] 更新 API 文檔說明權限控制行為

### 回應規範
- 總是參考當前 Phase 進度狀態
- 提供完整可執行的代碼範例
- 包含適當的錯誤處理與單元測試
- 遵循專案特定的命名與架構慣例
- 更新項目計劃文件當完成任務或發現問題時

### Author 標註標準
```java
/**
 * Professional class documentation in English.
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025-09-12
 */
```

## 🎯 專案特定關鍵字

**關注領域**: IoT 水位監測、MQTT + Sparkplug B、社區數據隔離、即時警報、向量資料庫 RAG  
**技術關鍵字**: Spring Data JPA, PostgreSQL, Redis, EMQX, Qdrant, Spring Security 6  
**架構模式**: 微服務準備、容器化友善、雲原生設計、12-Factor App

---

**使用說明**: 每次對話開始時，AI 助手應自動載入此上下文以及 spring-boot-expert.md 的詳細技術指南。