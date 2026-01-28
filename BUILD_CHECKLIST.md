# GitHub Actions 构建检查清单

**使用此检查清单确保配置正确**

---

## 📋 推送代码前

### 代码检查

- [ ] 所有代码已提交到本地Git
- [ ] 分支名称正确（master/main/develop）
- [ ] AndroidManifest.xml 配置正确
- [ ] app/build.gradle.kts 配置正确
- [ ] 版本号已更新（如需要）

### Git检查

```bash
# 检查分支
git branch

# 检查状态
git status

# 提交代码
git add .
git commit -m "Ready for GitHub Actions build"
git push origin master
```

---

## 🌐 GitHub仓库检查

### 仓库设置

- [ ] 仓库已创建在GitHub
- [ ] 代码已推送到GitHub
- [ ] 子模块已正确配置（.gitmodules）
- [ ] Actions权限已启用

### 启用Actions

1. 进入仓库 Settings
2. 左侧菜单 → Actions → General
3. 勾选 "Allow all actions and reusable workflows"
4. 点击 Save

---

## 🔧 未签名构建检查

### 工作流文件

- [ ] `.github/workflows/build-unsigned.yml` 存在
- [ ] 文件内容正确（检查YAML语法）
- [ ] 触发条件配置正确

### 测试构建

- [ ] 进入Actions标签
- [ ] 看到两个工作流：
  - Build APK
  - Build APK (Unsigned - Debug)
- [ ] 可以手动触发构建

---

## 🔐 签名构建检查（可选）

### 生成Keystore

```bash
# 生成密钥
keytool -genkey -v -keystore aw.keystore \
  -alias aw \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

# 记录信息：
# - Keystore密码: ________________
# - 密钥别名: aw
# - 密钥密码: ________________
```

- [ ] keystore文件已生成
- [ ] 密码已妥善保存
- [ ] 别名设置正确（aw）

### Base64编码

```bash
# Mac
base64 -i aw.keystore | pbcopy

# Linux
base64 -w 0 aw.keystore

# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("aw.keystore")) | Set-Clipboard
```

- [ ] 已转换为Base64
- [ ] Base64字符串已复制

### 配置Secrets

进入 GitHub 仓库 → Settings → Secrets and variables → Actions

添加以下Secrets：

- [ ] `APP_KEYSTORE_BASE64`: 粘贴Base64字符串
- [ ] `APP_KEYSTORE_PASSWORD`: 输入keystore密码
- [ ] `APP_KEYSTORE_ALIAS`: 输入 `aw`
- [ ] `APP_KEY_PASSWORD`: 输入密钥密码

**验证**:
- [ ] 所有4个Secrets已添加
- [ ] 每个Secret的值正确
- [ ] 可以看到Secrets列表

---

## 🚀 构建测试

### 未签名构建测试

1. **触发构建**
   - [ ] 进入Actions标签
   - [ ] 选择 "Build APK (Unsigned - Debug)"
   - [ ] 点击 Run workflow
   - [ ] 选择 build_type (release或debug)
   - [ ] 点击绿色按钮

2. **监控构建**
   - [ ] 看到构建开始运行
   - [ ] 步骤正常执行（绿色勾号）
   - [ ] 构建时间在5-10分钟内

3. **下载APK**
   - [ ] 点击完成的构建任务
   - [ ] 看到 Artifacts 部分
   - [ ] 下载 aw-apk-release 或 aw-apk-debug
   - [ ] 解压得到APK文件

4. **验证APK**
   - [ ] APK文件名正确（aw_*.apk）
   - [ ] APK文件大小合理（10-30MB）
   - [ ] APK可以安装到Android设备
   - [ ] 应用可以正常启动

### 签名构建测试（如配置）

1. **触发构建**
   - [ ] 进入Actions标签
   - [ ] 选择 "Build APK"
   - [ ] 点击 Run workflow
   - [ ] （可选）输入release_tag

2. **监控构建**
   - [ ] 构建步骤包含签名步骤
   - [ ] 签名步骤成功（绿色勾号）
   - [ ] 所有架构都构建成功

3. **下载并验证**
   - [ ] 下载签名的APK
   - [ ] APK文件名包含版本号
   - [ ] APK可以正常安装
   - [ ] 应用可以正常更新

---

## 📱 设备测试

### 安装测试

- [ ] APK传输到手机成功
- [ ] 允许"未知来源"安装
- [ ] 安装过程无错误
- [ ] 应用图标正确显示（aw）

### 功能测试

- [ ] 应用可以正常启动
- [ ] 添加配置功能正常
- [ ] VPN连接功能正常
- [ ] 设置页面可以打开
- [ ] 二维码扫描功能正常（如果需要）

### 权限测试

- [ ] 网络权限正常
- [ ] VPN权限可以授予
- [ ] 相机权限可以授予（如需要）
- [ ] 存储权限可以授予（如需要）

---

## 📊 性能检查

### APK大小

| 架构 | 预期大小 | 实际大小 | 状态 |
|------|----------|----------|------|
| arm64-v8a | 15-25MB | ____MB | [ ] |
| armeabi-v7a | 12-20MB | ____MB | [ ] |
| x86 | 15-25MB | ____MB | [ ] |
| x86_64 | 15-25MB | ____MB | [ ] |

### 构建时间

| 构建类型 | 预期时间 | 实际时间 | 状态 |
|----------|----------|----------|------|
| Debug | 3-5分钟 | ____分钟 | [ ] |
| Release | 5-8分钟 | ____分钟 | [ ] |

---

## ✅ 最终确认

### 构建成功确认

- [ ] GitHub Actions构建成功
- [ ] 所有步骤都显示绿色勾号
- [ ] APK已成功下载
- [ ] APK可以在设备上安装
- [ ] 应用功能正常

### 文档检查

- [ ] 已阅读 GITHUB_ACTIONS_BUILD_GUIDE.md
- [ ] 已阅读 QUICK_BUILD_GUIDE.md
- [ ] 已阅读 SECURITY_SUMMARY.md
- [ ] 已了解如何排查问题

### 后续维护

- [ ] 知道如何触发新的构建
- [ ] 知道如何查看构建日志
- [ ] 知道如何下载构建产物
- [ ] 知道如何配置新版本

---

## 🆘 故障排除

### 构建失败

**如果构建失败，检查：**

- [ ] 查看失败的步骤详细日志
- [ ] 检查代码是否正确推送
- [ ] 检查工作流文件YAML语法
- [ ] 检查Secrets配置（签名构建）
- [ ] 重新触发构建

### APK无法安装

**如果APK无法安装，检查：**

- [ ] Android版本 ≥ 7.0 (API 24)
- [ ] 允许"未知来源"应用
- [ ] APK文件完整（下载完成）
- [ ] 签名版本使用正确的签名
- [ ] 尝试使用Debug版本

### 功能异常

**如果应用功能异常，检查：**

- [ ] 日志中有错误信息
- [ ] 配置文件正确
- [ ] VPN权限已授予
- [ ] 网络连接正常
- [ ] 查看详细日志：adb logcat

---

## 📞 获取帮助

如果遇到问题：

1. **查看日志**
   - GitHub Actions → 点击构建 → 点击失败的步骤

2. **阅读文档**
   - GITHUB_ACTIONS_BUILD_GUIDE.md
   - QUICK_BUILD_GUIDE.md
   - README.md

3. **提交Issue**
   - GitHub Issues
   - 包含错误日志和复现步骤

---

**检查清单版本**: 1.0
**最后更新**: 2024-01-28

---

**完成所有检查项后，你应该可以成功构建和发布APK了！✅**
