#!/bin/bash

# 构建状态监控脚本
# 使用方法: GITHUB_TOKEN=your_token bash check_build_status.sh

REPO="awlei/aw2"
RUN_ID="21432474952"
TOKEN="${GITHUB_TOKEN:-}"

echo "🔍 检查构建状态..."
echo ""

# 获取构建信息
RUN_INFO=$(curl -s -H "Authorization: token $TOKEN" \
  "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID")

# 提取状态
STATUS=$(echo "$RUN_INFO" | grep '"status"' | head -1 | grep -o '"status": "[^"]*"' | grep -o ':"[^"]*"' | cut -c3-)
CONCLUSION=$(echo "$RUN_INFO" | grep '"conclusion"' | head -1 | grep -o '"conclusion": "[^"]*"' | grep -o ':"[^"]*"' | cut -c3-)

# 显示信息
echo "📦 构建信息"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "仓库: $REPO"
echo "构建ID: $RUN_ID"
echo "状态: $STATUS"
echo "结论: ${CONCLUSION:-未完成}"
echo ""

# 构建URL
ACTIONS_URL="https://github.com/$REPO/actions/runs/$RUN_ID"
echo "🔗 查看构建详情"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "$ACTIONS_URL"
echo ""

# 根据状态显示不同的信息
case $STATUS in
  "queued")
    echo "⏳ 构建已在队列中，等待开始..."
    ;;
  "in_progress")
    echo "🔄 构建进行中，请耐心等待..."
    echo "预计完成时间: 10-15分钟"
    ;;
  "completed")
    if [ "$CONCLUSION" == "success" ]; then
      echo "✅ 构建成功！"
      echo ""
      echo "📱 下载APK"
      echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
      echo "1. 访问: $ACTIONS_URL"
      echo "2. 向下滚动到 Artifacts 部分"
      echo "3. 点击 'aw-apk-release' 下载"
    elif [ "$CONCLUSION" == "failure" ]; then
      echo "❌ 构建失败！"
      echo ""
      echo "🔍 查看错误日志"
      echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
      echo "访问: $ACTIONS_URL"
    else
      echo "⚠️ 构建状态: $CONCLUSION"
    fi
    ;;
  *)
    echo "❓ 未知状态: $STATUS"
    ;;
esac

echo ""
echo "💡 提示"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "如需持续监控，请运行:"
echo "watch -n 10 'bash check_build_status.sh'"

