#!/bin/bash

# 持续监控构建状态
# 使用方法: bash monitor_build.sh

TOKEN="${GITHUB_TOKEN:-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}"
REPO="awlei/aw2"

echo "🔍 持续监控构建状态..."
echo "按 Ctrl+C 停止监控"
echo ""

while true; do
    # 获取最新的构建信息
    RUN_INFO=$(curl -s -H "Authorization: token $TOKEN" \
      "https://api.github.com/repos/$REPO/actions/runs?per_page=1")

    # 提取关键信息
    RUN_ID=$(echo "$RUN_INFO" | grep -o '"id": [0-9]*' | head -1 | grep -o '[0-9]*')
    STATUS=$(echo "$RUN_INFO" | grep '"status"' | head -1 | grep -o '"status": "[^"]*"' | grep -o ':"[^"]*"' | cut -c3-)
    CONCLUSION=$(echo "$RUN_INFO" | grep '"conclusion"' | head -1 | grep -o '"conclusion": "[^"]*"' | grep -o ':"[^"]*"' | cut -c3-)

    # 显示当前状态
    timestamp=$(date "+%H:%M:%S")
    echo "[$timestamp] 构建ID: $RUN_ID | 状态: $STATUS${CONCLUSION:+ | 结论: $CONCLUSION}"

    # 根据状态决定下一步
    if [ "$STATUS" = "completed" ]; then
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        if [ "$CONCLUSION" = "success" ]; then
            echo "✅ 构建成功！"
            echo ""
            echo "📱 下载APK"
            echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            echo "1. 访问: https://github.com/$REPO/actions/runs/$RUN_ID"
            echo "2. 向下滚动到 Artifacts 部分"
            echo "3. 点击 'aw-apk-release' 下载"
        else
            echo "❌ 构建失败！"
            echo ""
            echo "🔍 查看错误日志"
            echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
            echo "访问: https://github.com/$REPO/actions/runs/$RUN_ID"
        fi
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        break
    fi

    # 等待30秒
    sleep 30
done
