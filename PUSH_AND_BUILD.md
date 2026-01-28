# 推送代码到GitHub并构建APK

**当前状态**: ✅ 代码已提交到本地Git仓库
**下一步**: 推送到GitHub并触发构建

---

## 📝 当前Git状态

```bash
Branch: master
Latest commit: 3fe35016
Message: feat: 重命名项目为aw并配置GitHub Actions自动化构建
Files changed: 31 files (+3609, -68)
```

---

## 🚀 推送代码到GitHub

### 方法1: 使用HTTPS推送（推荐）

```bash
cd /workspace/projects/aw2-vpn
git push origin master
```

**如果需要认证**:
```bash
# GitHub会提示输入用户名和密码
# 用户名: 你的GitHub用户名
# 密码: 使用Personal Access Token (不是GitHub密码)
```

### 方法2: 使用SSH推送

如果你已经配置了SSH密钥：

```bash
cd /workspace/projects/aw2-vpn
git remote set-url origin git@github.com:awlei/aw2.git
git push origin master
```

---

## 🔐 获取GitHub Personal Access Token

### 1. 生成Token

1. 访问 https://github.com/settings/tokens
2. 点击 "Generate new token" → "Generate new token (classic)"
3. 设置权限：
   - `repo` (完整仓库访问权限)
   - `workflow` (允许GitHub Actions)
4. 点击 "Generate token"
5. **重要**: 复制token（只显示一次！）

### 2. 使用Token推送

```bash
# 当Git提示输入密码时，粘贴Token
Password: <你的Personal Access Token>
```

### 3. 保存Token（可选）

```bash
# 使用Git Credential Helper
git config --global credential.helper store
git push origin master
# 输入一次后，Token会被保存
```

---

## ⚙️ 推送后自动构建

推送代码后，GitHub Actions会**自动触发**构建：

### 自动构建触发条件

```yaml
# .github/workflows/build-unsigned.yml
on:
  push:
    branches:
      - master    # ✅ 推送到master自动构建
      - main
      - develop
```

### 预期流程

```
1. 执行 git push origin master
   ↓
2. 代码推送到GitHub
   ↓
3. GitHub自动检测到推送
   ↓
4. 触发 "Build APK (Unsigned - Debug)" 工作流
   ↓
5. 开始自动构建（5-10分钟）
   ↓
6. 构建完成后上传Artifacts
   ↓
7. 可下载APK
```

---

## 📊 监控构建进度

### 1. 查看构建状态

```bash
# 进入GitHub仓库
https://github.com/awlei/aw2

# 点击 "Actions" 标签
```

### 2. 查看具体构建

- Actions → 点击最新的构建任务
- 可以看到每个步骤的进度
- 绿色✓ = 成功
- 红色✗ = 失败
- 蓝色🔄 = 进行中

### 3. 查看详细日志

点击任意步骤可以查看详细日志：
- `Checkout code` - 代码检出
- `Setup Android SDK` - 安装SDK
- `Build APK` - 构建APK
- `Upload all APKs` - 上传结果

---

## ⏱️ 预计构建时间

| 步骤 | 预计时间 |
|------|----------|
| Checkout | 30秒 |
| Setup SDK | 1-2分钟 |
| Install NDK | 1-2分钟 |
| Build libhevtun | 1-2分钟（首次）/ 30秒（缓存） |
| Download libv2ray | 30秒 |
| Build APK | 3-5分钟（Debug）/ 5-7分钟（Release） |
| Upload Artifacts | 30秒 |
| **总计** | **5-10分钟** |

---

## 📥 下载APK

### 1. 构建完成后

1. 进入 Actions → 点击完成的构建任务
2. 向下滚动到 **Artifacts** 部分
3. 点击 `aw-apk-release` 或 `aw-apk-debug`
4. 解压下载的ZIP文件
5. 得到APK文件

### 2. APK文件列表

```bash
aw_release_arm64-v8a.apk      # 64位ARM设备（推荐）
aw_release_armeabi-v7a.apk    # 32位ARM设备
aw_release_x86.apk            # x86模拟器
aw_release_x86_64.apk         # x86_64模拟器
```

---

## 🎯 快速命令参考

### 推送并监控

```bash
# 1. 推送代码
cd /workspace/projects/aw2-vpn
git push origin master

# 2. 在浏览器中查看
# https://github.com/awlei/aw2/actions

# 3. 等待5-10分钟
# 4. 下载APK
```

### 查看状态

```bash
# 查看当前分支
git branch

# 查看提交历史
git log --oneline -5

# 查看远程仓库
git remote -v

# 查看未推送的提交
git log origin/master..HEAD
```

---

## ❓ 常见问题

### Q1: 推送时提示认证失败

**解决**:
1. 使用Personal Access Token（不是密码）
2. 确保Token有 `repo` 和 `workflow` 权限
3. 检查用户名是否正确

### Q2: 推送成功但没有触发构建

**检查**:
1. GitHub Actions是否启用
   - Settings → Actions → General
   - 勾选 "Allow all actions"
2. 分支名称是否为 master/main/develop
3. 工作流文件是否在 `.github/workflows/` 目录

### Q3: 构建失败

**解决**:
1. 点击失败的步骤查看详细日志
2. 检查代码语法错误
3. 查看安全审计报告中的建议

### Q4: 如何触发新的构建

**方法1**: 推送新代码
```bash
# 任意修改并推送
echo "# test" >> README.md
git add . && git commit -m "Trigger build"
git push origin master
```

**方法2**: 手动触发
1. GitHub → Actions → 选择工作流
2. 点击 "Run workflow"
3. 选择构建类型（release/debug）

---

## 📞 获取帮助

如果遇到问题：

1. **查看GitHub Actions日志**
   - Actions → 点击失败的构建

2. **阅读文档**
   - [GITHUB_ACTIONS_BUILD_GUIDE.md](GITHUB_ACTIONS_BUILD_GUIDE.md)
   - [QUICK_BUILD_GUIDE.md](QUICK_BUILD_GUIDE.md)

3. **检查状态**
   ```bash
   git status
   git remote -v
   ```

---

## ✅ 检查清单

推送前检查：

- [ ] 代码已提交
- [ ] 提交信息清晰
- [ ] 工作流文件正确
- [ ] 分支名称正确

推送后检查：

- [ ] 代码已推送到GitHub
- [ ] GitHub Actions已触发
- [ ] 构建正在运行
- [ ] 等待构建完成（5-10分钟）
- [ ] 下载APK
- [ ] 安装测试

---

**准备好了吗？运行以下命令：**

```bash
cd /workspace/projects/aw2-vpn
git push origin master
```

**然后等待5-10分钟，APK就构建好了！🎉**

---

**下一步**:
1. 推送代码
2. 访问 https://github.com/awlei/aw2/actions
3. 查看构建进度
4. 下载APK并测试
