# GitHub Actions 快速构建指南

**最快5分钟获得APK！**

---

## 🚀 最简单的方法（推荐）

### 一键构建，无需签名

#### 步骤 1: 推送代码到GitHub

```bash
git add .
git commit -m "Ready to build"
git push origin master
```

#### 步骤 2: 在GitHub上触发构建

1. 打开你的GitHub仓库
2. 点击 **Actions** 标签
3. 选择 **Build APK (Unsigned - Debug)** 工作流
4. 点击 **Run workflow** 按钮
5. 选择 `release` 或 `debug`
6. 点击绿色的 **Run workflow**

#### 步骤 3: 下载APK

1. 等待5-10分钟（会自动刷新）
2. 点击构建任务
3. 向下滚动到 **Artifacts** 部分
4. 点击 **aw-apk-release** 下载
5. 解压ZIP文件，得到APK

---

## 📱 安装到手机

### Android 8.0 及以上

1. 将APK传到手机
2. 直接安装（可能需要允许"未知来源"）

### Android 7.0

1. 先安装Debug版本（无需签名）
2. 或使用签名版本

---

## ⚙️ 如果需要签名版本

### 方法：使用现有工作流

1. **生成签名密钥**
```bash
keytool -genkey -v -keystore aw.keystore -alias aw -keyalg RSA -keysize 2048 -validity 10000
```

2. **转换为Base64**
```bash
base64 -i aw.keystore | pbcopy  # Mac
# 或
base64 -w 0 aw.keystore  # Linux
```

3. **配置GitHub Secrets**
   - 进入仓库 Settings → Secrets → Actions
   - 添加4个Secrets:
     - `APP_KEYSTORE_BASE64`: 粘贴Base64字符串
     - `APP_KEYSTORE_PASSWORD`: keystore密码
     - `APP_KEYSTORE_ALIAS`: `aw`
     - `APP_KEY_PASSWORD`: 密钥密码（通常与keystore密码相同）

4. **触发签名构建**
   - Actions → Build APK → Run workflow

---

## 📊 构建时间

| 构建类型 | 时间 |
|----------|------|
| Debug | 3-5分钟 |
| Release | 5-8分钟 |

---

## 🔍 常见问题

**Q: 构建失败怎么办？**
- 点击失败的步骤查看详细日志
- 检查代码是否正确推送

**Q: APK安装失败？**
- Debug版本可以直接安装
- 确保Android版本 ≥ 7.0 (API 24)
- 如果是Release版本，需要签名

**Q: 构建太慢？**
- 首次构建较慢，后续会使用缓存
- 选择Debug构建更快

---

## 📚 更多信息

查看完整文档: [GITHUB_ACTIONS_BUILD_GUIDE.md](GITHUB_ACTIONS_BUILD_GUIDE.md)

---

**就这么简单！🎉**
