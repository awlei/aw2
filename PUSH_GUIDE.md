# GitHub 推送指南

**当前状态**: ✅ 所有代码已提交到本地Git
**需要**: GitHub认证（Personal Access Token）

---

## 🔐 获取Personal Access Token

### 步骤1: 生成Token

1. 访问: https://github.com/settings/tokens
2. 点击 "Generate new token" → "Generate new token (classic)"
3. 填写信息：
   - **Note**: `aw2 project push`
   - **Expiration**: 选择有效期（建议90天或更久）
   - **勾选权限**:
     - ✅ `repo` (完整仓库访问权限)
     - ✅ `workflow` (允许GitHub Actions)
4. 点击 "Generate token"
5. **重要**: 立即复制token（只显示一次！）
   - Token格式类似: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

---

## 📤 推送代码

### 方法1: 使用Token推送（推荐）

在终端中执行：

```bash
cd /workspace/projects/aw2-vpn
git push origin master
```

**当提示输入认证信息时**：
- **Username**: 你的GitHub用户名（例如: `awlei`）
- **Password**: 粘贴Personal Access Token（不是GitHub密码）

### 方法2: 使用远程URL包含Token

```bash
cd /workspace/projects/aw2-vpn
git remote set-url origin https://<你的Token>@github.com/awlei/aw2.git
git push origin master
```

**示例**:
```bash
git remote set-url origin https://ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx@github.com/awlei/aw2.git
git push origin master
```

---

## ⚡ 推送后会发生什么？

1. **代码推送到GitHub** ✅
2. **GitHub Actions自动触发构建** 🔄
3. **构建APK** (5-10分钟)
4. **上传Artifacts** 📦
5. **可下载新APK** 📱

---

## 📊 推送内容统计

```
Branch: master
Commits: 3
Files changed: 35
Lines added: 3915
Lines removed: 75

主要更新:
1. 项目重命名 V2rayNG → aw
2. 新增GitHub Actions自动化构建
3. 新增3个工具类（ErrorHandler, PerformanceMonitor, SecurityUtils）
4. 删除隐藏推广链接
5. 优化网络安全配置
6. 完善文档和安全审计
```

---

## 🔍 监控推送状态

### 推送成功后

1. 访问: https://github.com/awlei/aw2
2. 可以看到最新的提交记录
3. 点击 **Actions** 标签查看构建状态

### 预期的Actions工作流

```
Actions → Build APK (Unsigned - Debug) 自动触发
↓
构建流程（5-10分钟）:
  1. Checkout code (30秒)
  2. Setup Android SDK (1-2分钟)
  3. Install NDK (1-2分钟)
  4. Build libhevtun (1-2分钟)
  5. Download libv2ray (30秒)
  6. Build APK (3-5分钟Debug / 5-7分钟Release)
  7. Upload Artifacts (30秒)
↓
构建完成，可下载APK
```

---

## 📱 下载新APK

### 步骤

1. **等待构建完成** (5-10分钟)
2. **访问Actions页面**:
   ```
   https://github.com/awlei/aw2/actions
   ```
3. **点击最新的构建任务**
4. **向下滚动到 Artifacts 部分**
5. **点击 `aw-apk-release` 下载**
6. **解压ZIP文件**
7. **得到APK文件**

### APK列表

```
aw_release_arm64-v8a.apk      # 64位ARM设备（推荐）
aw_release_armeabi-v7a.apk    # 32位ARM设备
aw_release_x86.apk            # x86模拟器
aw_release_x86_64.apk         # x86_64模拟器
```

---

## ❓ 常见问题

### Q1: Token过期了怎么办？

**解决**:
1. 访问 https://github.com/settings/tokens
2. 删除旧token
3. 生成新的token
4. 使用新token推送

### Q2: 推送时提示认证失败

**可能原因**:
- Token权限不足（需要`repo`和`workflow`）
- Token已过期
- 用户名错误

**解决**:
1. 检查Token是否勾选了`repo`和`workflow`权限
2. 重新生成Token
3. 确认GitHub用户名正确

### Q3: 如何保存Token避免每次输入？

**方法1**: 使用Credential Helper
```bash
git config --global credential.helper store
git push origin master
# 输入一次后，Token会被保存
```

**方法2**: 使用SSH（需要配置SSH密钥）
```bash
git remote set-url origin git@github.com:awlei/aw2.git
git push origin master
```

### Q4: 推送成功但没有触发Actions构建

**检查**:
1. GitHub Actions是否启用
   - Settings → Actions → General
   - 勾选 "Allow all actions and reusable workflows"
2. 工作流文件是否正确
   - 检查 `.github/workflows/build-unsigned.yml`
3. 分支名称是否为master/main/develop

---

## ✅ 快速命令参考

### 推送命令
```bash
cd /workspace/projects/aw2-vpn
git push origin master
```

### 查看状态
```bash
git status
git log --oneline -5
git remote -v
```

### 如果需要修改远程URL
```bash
# 查看当前远程URL
git remote -v

# 修改为包含Token的URL
git remote set-url origin https://<TOKEN>@github.com/awlei/aw2.git

# 或者修改为SSH URL
git remote set-url origin git@github.com:awlei/aw2.git
```

---

## 🎯 推送后的操作清单

推送成功后检查：

- [ ] 代码已推送到GitHub
- [ ] 在GitHub可以看到新提交
- [ ] GitHub Actions已自动触发
- [ ] 构建正在运行
- [ ] 等待5-10分钟
- [ ] 构建完成
- [ ] 下载新的APK
- [ ] 测试APK安装和运行

---

## 📞 获取帮助

如果遇到问题：

1. **查看GitHub错误信息**
   - Terminal中的错误输出
   - GitHub上的错误提示

2. **检查文档**
   - [GitHub官方文档](https://docs.github.com/en/authentication)
   - [Personal Access Token文档](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)

3. **重新生成Token**
   - 如果Token有问题，直接删除并重新生成

---

## 🚀 准备好了吗？

**推送步骤**:

1. **获取Personal Access Token**（如果还没有）
   - https://github.com/settings/tokens

2. **执行推送命令**
   ```bash
   cd /workspace/projects/aw2-vpn
   git push origin master
   ```

3. **输入认证信息**
   - Username: `awlei`
   - Password: `<你的Personal Access Token>`

4. **等待推送完成**

5. **访问GitHub查看构建**
   - https://github.com/awlei/aw2/actions

6. **等待5-10分钟**

7. **下载新APK并测试**

---

**准备好了吗？现在就去获取Token，然后推送代码！**

推送命令: `cd /workspace/projects/aw2-vpn && git push origin master`
