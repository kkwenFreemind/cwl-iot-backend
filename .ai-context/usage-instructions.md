# .ai-context 使用指南

## 📂 目錄結構

```
.ai-context/
├── README.md                    # 主要上下文概覽 (快速參考)
├── spring-boot-expert.md        # 完整技術專家指南 (詳細參考)
├── project-guidelines.md        # 項目特定開發指南
└── usage-instructions.md        # 本文件 - 使用說明
```

## 🚀 AI 助手使用方法

### 方法 1: 簡單引用 (推薦)

在對話開始時說：

```
請載入 .ai-context 中的項目上下文，繼續 CWL-IoT 項目開發。
```

### 方法 2: 具體文件引用

```
請參考以下文件進行開發：
- .ai-context/README.md (項目概覽)
- .ai-context/spring-boot-expert.md (技術指南)
- doc/plan/phase1/phase1_plan.md (當前階段計劃)
```

### 方法 3: 直接說明當前工作

```
我正在進行 CWL-IoT 項目的 Phase 1 開發，當前在 Task Group 1.5 進行 API 驗證。
請按照項目標準提供技術支援。
```

## 📋 核心參考文件索引

### 項目狀態文件
- **當前進度**: `doc/plan/phase1/phase1_plan.md`
- **項目概覽**: `README.md`
- **開發標準**: `.ai-context/README.md`

### 技術文件
- **詳細技術指南**: `.ai-context/spring-boot-expert.md`
- **API 文檔**: 各模組 Controller 的 JavaDoc
- **數據庫架構**: `sql/postgresql/sourcedb_v1.sql`

## 🎯 快速上下文關鍵詞

使用這些關鍵詞可以快速讓 AI 助手理解你的需求：

- **"CWL-IoT"** - 識別項目背景
- **"Phase 1"** - 當前開發階段
- **"Task Group 1.5"** - 當前具體任務
- **"Spring Boot 3 + JPA"** - 技術棧
- **"API 驗證"** - 當前重點工作
- **"雙語文檔"** - 文檔標準
- **"AI-Enhanced"** - 開發模式

## 💡 最佳實踐建議

### 文件維護
1. **定期更新**: 隨著項目進展更新 README.md 中的狀態
2. **版本同步**: 確保技術指南與實際使用的技術版本一致
3. **文檔一致性**: 保持 .ai-context 與項目其他文檔的一致性

### 使用效率
1. **簡化引用**: 通常只需要說 "載入項目上下文" 即可
2. **具體問題**: 在上下文基礎上提出具體的技術問題
3. **進度更新**: 完成任務後及時更新項目計劃文件

## 🔄 更新流程

當項目有重大變更時，按以下順序更新：

1. **更新項目計劃** (`doc/plan/phase1/phase1_plan.md`)
2. **更新快速參考** (`.ai-context/README.md`)
3. **必要時更新技術指南** (`.ai-context/spring-boot-expert.md`)
4. **提交 git 變更**

這樣可以確保 AI 助手始終獲得最新、最準確的項目上下文信息。