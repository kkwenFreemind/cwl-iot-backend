# Qdrant RAG環境部署指南

## 🚀 快速啟動

### 1. 啟動Qdrant服務
```bash
cd /Users/kevinchang/Documents/MyCode/ai-interface-backend

# 啟動Qdrant
docker-compose -f docker/qdrant-compose.yml up -d

# 檢查服務狀態
docker-compose -f docker/qdrant-compose.yml ps

# 查看日誌
docker-compose -f docker/qdrant-compose.yml logs -f qdrant
```

### 2. 驗證安裝
```bash
# 檢查Qdrant健康狀態
curl http://localhost:6333/health

# 查看集群信息
curl http://localhost:6333/cluster

# 列出所有collection
curl http://localhost:6333/collections
```

預期回應：
```json
{
  "result": {
    "collections": []
  },
  "status": "ok",
  "time": 0.000123
}
```

## 📊 Web UI訪問

Qdrant提供了Web控制台：
- **URL**: http://localhost:6333/dashboard
- **功能**: 可視化管理collections、查看數據、執行搜索等

## 🔧 Java依賴配置

在您的`pom.xml`中添加：

```xml
<!-- Qdrant Java Client -->
<dependency>
    <groupId>io.qdrant</groupId>
    <artifactId>client</artifactId>
    <version>1.7.0</version>
</dependency>

<!-- HTTP客戶端 (Qdrant使用) -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<!-- JSON處理 -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

## ⚙️ Spring Boot配置

在`application.yml`中添加：

```yaml
# Qdrant配置
qdrant:
  host: localhost
  port: 6333
  api-key: # 生產環境時設定
  use-tls: false
  
# RAG相關配置
rag:
  embedding:
    model: text-embedding-3-small  # 或使用其他embedding模型
    dimension: 1536
  vector-db:
    collection-name: ai-knowledge-base
    distance-metric: cosine
```

## 🧪 基礎測試

創建一個簡單的測試collection：

```bash
# 創建測試collection
curl -X PUT "http://localhost:6333/collections/test" \
  -H "Content-Type: application/json" \
  -d '{
    "vectors": {
      "size": 384,
      "distance": "Cosine"
    }
  }'

# 插入測試數據
curl -X PUT "http://localhost:6333/collections/test/points" \
  -H "Content-Type: application/json" \
  -d '{
    "points": [
      {
        "id": 1,
        "vector": [0.1, 0.2, 0.3, ..., 0.384],
        "payload": {
          "text": "這是一個測試文檔",
          "department": "hr",
          "access_level": "internal"
        }
      }
    ]
  }'
```

## 📁 資料持久化

資料會自動保存在：
- **本機路徑**: `./qdrant-data/`
- **容器內路徑**: `/qdrant/storage/`

## 🛠️ 管理指令

```bash
# 停止服務
docker-compose -f docker/qdrant-compose.yml down

# 停止並清除資料
docker-compose -f docker/qdrant-compose.yml down -v

# 重啟服務
docker-compose -f docker/qdrant-compose.yml restart

# 查看資源使用
docker stats ai-qdrant
```

## 🔍 故障排除

### 常見問題

1. **端口衝突**
   ```bash
   # 檢查端口使用
   netstat -an | grep 6333
   # 或修改docker-compose.yml中的端口映射
   ```

2. **權限問題**
   ```bash
   # 檢查資料目錄權限
   ls -la ./qdrant-data/
   
   # 修復權限 (如需要)
   sudo chown -R $USER:$USER ./qdrant-data/
   ```

3. **記憶體不足**
   ```yaml
   # 在docker-compose.yml中限制記憶體
   services:
     qdrant:
       deploy:
         resources:
           limits:
             memory: 2G
   ```

## 🎯 下一步

環境準備完成後，可以開始：
1. 創建RAG專用的collection
2. 實現文檔向量化服務
3. 開發權限過濾查詢邏輯

---

**注意**: 這是開發環境配置，生產環境請考慮：
- 啟用API密鑰驗證
- 設定適當的資源限制
- 配置監控和備份策略
