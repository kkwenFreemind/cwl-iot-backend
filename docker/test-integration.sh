#!/bin/bash

# RAG PoC Docker 整合測試腳本

set -e  # 遇到錯誤立即退出

echo "🚀 RAG PoC Docker 整合測試開始..."

# 1. 檢查環境變數
echo "📋 檢查環境變數..."
if [[ -f "../.env" ]]; then
    echo "✅ .env 檔案存在"
    source ../.env
    if [[ -n "$GEMINI_API_KEY" ]]; then
        echo "✅ GEMINI_API_KEY 已設定: ${GEMINI_API_KEY:0:20}..."
    else
        echo "❌ GEMINI_API_KEY 未設定"
        exit 1
    fi
else
    echo "❌ .env 檔案不存在，請先複製 .env.template 為 .env"
    exit 1
fi

# 2. 啟動依賴服務
echo "🐳 啟動 Docker 服務..."
docker-compose up -d qdrant timescaledb cache

# 3. 等待服務啟動
echo "⏳ 等待服務啟動..."
sleep 10

# 4. 檢查 Qdrant 健康狀態
echo "🔍 檢查 Qdrant 健康狀態..."
max_attempts=30
attempt=0

while [ $attempt -lt $max_attempts ]; do
    if curl -f -s http://localhost:6333/collections > /dev/null 2>&1; then
        echo "✅ Qdrant 服務健康"
        break
    else
        echo "⏳ 等待 Qdrant 啟動... ($attempt/$max_attempts)"
        sleep 2
        attempt=$((attempt + 1))
    fi
done

if [ $attempt -eq $max_attempts ]; then
    echo "❌ Qdrant 服務啟動失敗"
    docker-compose logs qdrant
    exit 1
fi

# 5. 檢查 PostgreSQL
echo "🔍 檢查 PostgreSQL 連接..."
if nc -z localhost 15432; then
    echo "✅ PostgreSQL 連接正常"
else
    echo "❌ PostgreSQL 連接失敗"
    exit 1
fi

# 6. 檢查 Redis
echo "🔍 檢查 Redis 連接..."
if nc -z localhost 16379; then
    echo "✅ Redis 連接正常"
else
    echo "❌ Redis 連接失敗"
    exit 1
fi

# 7. 顯示服務狀態
echo "📊 服務狀態摘要:"
docker-compose ps

echo ""
echo "🎉 Docker 整合測試完成！"
echo ""
echo "📝 下一步:"
echo "   1. 回到專案根目錄: cd .."
echo "   2. 啟動 Spring Boot: mvn spring-boot:run"
echo "   3. 測試監控 API: curl http://localhost:8989/api/rag/monitoring/health"
echo ""
echo "🔗 服務端點:"
echo "   • Qdrant API: http://localhost:6333"
echo "   • PostgreSQL: localhost:15432"
echo "   • Redis: localhost:16379"
