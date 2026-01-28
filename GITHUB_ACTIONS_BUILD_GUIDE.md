# GitHub Actions APK 构建指南

**项目**: aw (V2Ray Client)
**文档版本**: 1.0
**更新日期**: 2024-01-28

---

## 📋 目录

1. [快速开始](#快速开始)
2. [构建流程说明](#构建流程说明)
3. [配置详解](#配置详解)
4. [签名配置](#签名配置)
5. [自动发布](#自动发布)
6. [常见问题](#常见问题)

---

## 🚀 快速开始

### 方法一：使用工作流手动触发（推荐，无需签名）

#### 1. 推送代码到GitHub
```bash
git add .
git commit -m "Initial commit"
git push origin master
```

#### 2. 在GitHub上触发构建

1. 进入GitHub仓库
2. 点击 **Actions** 标签
3. 选择 **Build APK (Unsigned - Debug)** 工作流
4. 点击 **Run workflow**
5. 选择构建类型：
   - `debug` - 调试版本（更快）
   - `release` - 发布版本（优化过）

#### 3. 下载APK

1. 等待构建完成（约5-10分钟）
2. 点击构建任务
3. 在 **Artifacts** 部分下载 APK
4. 文件名格式：`aw_debug_arm64-v8a.apk` 或 `aw_release_arm64-v8a.apk`

---

## 📦 构建流程说明

### 自动构建流程

```
代码推送 → 检出代码 → 安装SDK → 编译NDK → 下载依赖 → 构建APK → 上传Artifacts → 发布Release
```

### 详细步骤

1. **检出代码**
   - 克隆仓库
   - 检出子模块

2. **安装Android SDK**
   - Android SDK 36.1
   - Build Tools 36.1.0
   - Platform Tools

3. **安装NDK**
   - NDK 28.2.13676358
   - 用于编译hevtun库

4. **构建hevtun库**
   - 编译WireGuard内核
   - 使用缓存加速

5. **下载libv2ray**
   - 从AndroidLibXrayLite获取最新版
   - 用于V2Ray核心

6. **构建APK**
   - Debug或Release
   - 多架构支持

7. **上传结果**
   - Artifacts保存30天
   - 可自动发布到Release

---

## ⚙️ 配置详解

### 工作流文件

项目包含两个工作流：

1. **build.yml** - 签名版本（需要配置密钥）
2. **build-unsigned.yml** - 未签名版本（开箱即用）

### build-unsigned.yml 配置

```yaml
name: Build APK (Unsigned - Debug)

on:
  workflow_dispatch:
    inputs:
      build_type:
        type: choice
        options: [debug, release]
  push:
    branches: [master, main, develop]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      # ... 构建步骤
```

**触发条件**:
- 手动触发（workflow_dispatch）
- 推送到 master/main/develop 分支

---

## 🔐 签名配置

### 生成签名密钥

#### 1. 生成Keystore文件

```bash
# 在本地生成
keytool -genkey -v -keystore aw-release.keystore \
  -alias aw \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

**输入信息示例**:
```
Keystore password: [设置密码]
Re-enter new password: [重复密码]
What is your first and last name?
  [Unknown]:  aw
What is the name of your organizational unit?
  [Unknown]:  Development
What is the name of your organization?
  [Unknown]:  aw VPN
What is the name of your City or Locality?
  [Unknown]:  [城市]
What is the name of your State or Province?
  [Unknown]:  [省份]
What is the two-letter country code for this unit?
  [Unknown]:  CN
Is CN=aw, OU=Development, O=aw VPN, L=[城市], ST=[省份], C=CN correct?
  [no]:  yes
Enter key password for <aw>
        (RETURN if same as keystore password): [直接回车]
```

#### 2. 转换为Base64

**Linux/Mac**:
```bash
base64 -i aw-release.keystore | pbcopy
```

**Windows (PowerShell)**:
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("aw-release.keystore")) | Set-Clipboard
```

### 配置GitHub Secrets

#### 1. 进入仓库设置

1. GitHub仓库 → **Settings**
2. 左侧菜单 → **Secrets and variables** → **Actions**
3. 点击 **New repository secret**

#### 2. 添加以下Secrets

| Secret名称 | 值 | 说明 |
|------------|---|------|
| `APP_KEYSTORE_BASE64` | Base64编码的keystore文件 | 签名文件 |
| `APP_KEYSTORE_PASSWORD` | Keystore密码 | 第一步设置的密码 |
| `APP_KEYSTORE_ALIAS` | 签名别名 | 示例: `aw` |
| `APP_KEY_PASSWORD` | 密钥密码 | 与Keystore密码相同（可直接回车） |

#### 3. 示例配置

```yaml
# 在Settings → Secrets中添加：
APP_KEYSTORE_BASE64: uG90...（很长的Base64字符串）
APP_KEYSTORE_PASSWORD: your_keystore_password
APP_KEYSTORE_ALIAS: aw
APP_KEY_PASSWORD: your_key_password
```

### 使用签名构建

配置好Secrets后，使用 `build.yml` 工作流：

```bash
# 手动触发带签名的构建
1. GitHub → Actions → Build APK
2. 点击 Run workflow
3. 等待完成，下载签名后的APK
```

---

## 📤 自动发布

### 自动发布到GitHub Release

### build-unsigned.yml 自动发布

当使用 `build-unsigned.yml` 并选择 `release` 构建时：

1. 自动创建GitHub Release
2. Tag格式: `v{run_number}`
3. 标题: `aw v{run_number}`
4. 包含所有架构的APK

### 自定义发布版本

修改工作流中的发布部分：

```yaml
- name: Create Release
  if: github.event_name == 'workflow_dispatch'
  uses: softprops/action-gh-release@v2
  with:
    tag_name: v1.0.0  # 自定义版本号
    name: aw v1.0.0
    body: |
      ## 更新内容
      - 修复已知问题
      - 优化性能
    draft: false
    prerelease: false
    files: aw_*.apk
```

---

## 🛠️ 构建优化

### 使用缓存加速

工作流已配置缓存：

1. **NDK构建缓存**
   - 缓存编译好的hevtun库
   - 减少构建时间

2. **Gradle缓存**
   - 缓存依赖
   - 加速后续构建

### 多架构构建

默认构建4种架构：

| 架构 | 设备支持 |
|------|----------|
| `arm64-v8a` | 64位ARM设备（推荐） |
| `armeabi-v7a` | 32位ARM设备 |
| `x86` | x86模拟器/设备 |
| `x86_64` | x86_64模拟器/设备 |

**优化：只构建需要的架构**

修改 `app/build.gradle.kts`:

```kotlin
splits {
    abi {
        isEnable = true
        reset()
        include("arm64-v8a")  // 只构建64位ARM
    }
}
```

---

## 📊 构建时间估算

| 构建类型 | 首次构建 | 缓存构建 |
|----------|----------|----------|
| Debug | 5-8分钟 | 3-5分钟 |
| Release | 8-12分钟 | 5-7分钟 |

**影响因素**:
- NDK编译（最耗时）
- 依赖下载
- 代码混淆（Release）

---

## ❓ 常见问题

### Q1: 构建失败，提示签名错误

**原因**: Secrets配置不正确或过期

**解决**:
1. 检查Secrets是否正确
2. 确认密码没有拼写错误
3. 重新生成keystore和Secrets

### Q2: 构建很慢

**原因**: NDK编译缓存未命中

**解决**:
1. 等待首次完成，后续会使用缓存
2. 检查 `compile-hevtun.sh` 文件是否变化
3. 使用Debug构建更快

### Q3: APK安装失败

**原因**: 未签名或签名问题

**解决**:
1. Debug版本可直接安装
2. Release版本需要签名
3. 确保Android版本兼容（API 24+）

### Q4: 如何只构建一个架构？

**方法1**: 修改build.gradle.kts
```kotlin
splits {
    abi {
        include("arm64-v8a")
    }
}
```

**方法2**: 使用productFlavors

### Q5: 如何自定义APK文件名？

修改 `app/build.gradle.kts`:

```kotlin
variant.outputs.forEach { output ->
    output.outputFileName = "aw_v${versionName}_${output.filterName}.apk"
}
```

### Q6: 如何查看构建日志？

1. GitHub → Actions
2. 点击具体的构建任务
3. 点击任意步骤查看日志

---

## 🔍 调试技巧

### 1. 查看详细日志

在构建步骤中添加调试输出：

```yaml
- name: Debug Info
  run: |
    echo "Android SDK: $ANDROID_HOME"
    echo "NDK Home: $NDK_HOME"
    ls -la ${{ github.workspace }}/V2rayNG/app/libs
```

### 2. 保留失败构建

在工作流中添加：

```yaml
- name: Upload Logs on Failure
  if: failure()
  uses: actions/upload-artifact@v6
  with:
    name: build-logs
    path: |
      **/*.log
      **/build/reports/
```

### 3. 本地测试

使用act在本地测试GitHub Actions：

```bash
# 安装act
brew install act  # Mac
# 或
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# 运行工作流
act -W .github/workflows/build-unsigned.yml
```

---

## 📚 参考资源

### 官方文档
- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Android Gradle Plugin](https://developer.android.com/studio/build)
- [应用签名](https://developer.android.com/studio/publish/app-signing)

### 相关工具
- [Android Actions Setup](https://github.com/android-actions/setup-android)
- [Upload Artifact](https://github.com/actions/upload-artifact)
- [Create Release](https://github.com/softprops/action-gh-release)

---

## ✅ 检查清单

### 首次构建前检查

- [ ] 代码已推送到GitHub
- [ ] 子模块已正确初始化
- [ ] AndroidManifest.xml 配置正确
- [ ] app/build.gradle.kts 配置正确

### 签名构建前检查

- [ ] 已生成keystore文件
- [ ] 已配置GitHub Secrets
- [ ] 已测试密码是否正确

### 发布前检查

- [ ] 版本号已更新
- [ ] ChangeLog已更新
- [ ] APK已测试通过
- [ ] Release说明已填写

---

## 🎯 最佳实践

### 1. 版本管理

```kotlin
// app/build.gradle.kts
defaultConfig {
    versionCode = 706
    versionName = "2.0.6"
}
```

### 2. 发布流程

```bash
# 1. 更新版本号
git commit -am "Bump version to 2.0.7"
git tag v2.0.7
git push && git push --tags

# 2. 触发GitHub Actions构建
# 3. 下载测试APK
# 4. 创建正式Release
```

### 3. 持续集成

```yaml
# 添加代码质量检查
- name: Run Tests
  run: ./gradlew test

- name: Lint Check
  run: ./gradlew lint
```

---

## 📞 支持

遇到问题？

1. 查看 [GitHub Actions 日志](https://github.com/your-repo/actions)
2. 检查 [常见问题](#常见问题)
3. 提交 [Issue](https://github.com/your-repo/issues)

---

**文档版本**: 1.0
**最后更新**: 2024-01-28
**维护者**: aw Team
