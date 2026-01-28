# aw

A V2Ray client for Android, support [Xray core](https://github.com/XTLS/Xray-core) and [v2fly core](https://github.com/v2fly/v2ray-core)

[![API](https://img.shields.io/badge/API-24%2B-yellow.svg?style=flat)](https://developer.android.com/about/versions/lollipop)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg)](https://kotlinlang.org)
[![GitHub Actions](https://github.com/awlei/aw2/workflows/Build%20APK%20%28Unsigned%20-%20Debug%29/badge.svg)](https://github.com/awlei/aw2/actions)
[![Security](https://img.shields.io/badge/Security-Safe-brightgreen.svg)](SECURITY_AUDIT_REPORT.md)
[![Chat on Telegram](https://img.shields.io/badge/Chat%20on-Telegram-brightgreen.svg)](https://t.me/v2rayn)

## 🔒 Security Status

✅ **All security issues fixed!** - No malware, no data collection, fully audited.

- [x] No hidden links or malware detected
- [x] Network security optimized
- [x] All dependencies are secure
- [x] See [Security Audit Report](SECURITY_AUDIT_REPORT.md) for details
- [x] See [Security Fix Summary](SECURITY_FIX_SUMMARY.md) for latest fixes

## 🚀 Quick Start with GitHub Actions

### Build APK in 5 Minutes (No Setup Required)

1. **Click Actions tab** in this repository
2. **Select "Build APK (Unsigned - Debug)"** workflow
3. **Click "Run workflow"** button
4. **Choose build type** (release or debug)
5. **Wait 5-10 minutes** and download the APK

**Detailed Guide**: [GitHub Actions Build Guide](GITHUB_ACTIONS_BUILD_GUIDE.md) | [Quick Start](QUICK_BUILD_GUIDE.md)

### Manual Build

Android project under V2rayNG folder can be compiled directly in Android Studio, or using Gradle wrapper.

## 📖 Usage

### Geoip and Geosite
- geoip.dat and geosite.dat files are in `Android/data/com.v2ray.ang/files/assets` (path may differ on some Android device)
- download feature will get enhanced version in this [repo](https://github.com/Loyalsoldier/v2ray-rules-dat) (Note it need a working proxy)
- latest official [domain list](https://github.com/Loyalsoldier/v2ray-rules-dat) and [ip list](https://github.com/Loyalsoldier/geoip) can be imported manually
- possible to use third party dat file in the same folder, like [h2y](https://guide.v2fly.org/routing/sitedata.html#%E5%A4%96%E7%BD%AE%E7%9A%84%E5%9F%9F%E5%90%8D%E6%96%87%E4%BB%B6)

## 📚 Documentation

- [GitHub Actions Build Guide](GITHUB_ACTIONS_BUILD_GUIDE.md) - Complete CI/CD setup guide
- [Quick Build Guide](QUICK_BUILD_GUIDE.md) - 5-minute APK build tutorial
- [Build Checklist](BUILD_CHECKLIST.md) - Step-by-step verification checklist
- [Security Audit Report](SECURITY_AUDIT_REPORT.md) - Security analysis and recommendations
- [Security Fix Summary](SECURITY_FIX_SUMMARY.md) - Latest security fixes
- [Optimization Report](OPTIMIZATION_REPORT.md) - Code quality improvements

### More in our [wiki](https://github.com/2dust/v2rayNG/wiki)

## 🔧 Development guide

The aar can be compiled from the Golang project [AndroidLibV2rayLite](https://github.com/2dust/AndroidLibV2rayLite) or [AndroidLibXrayLite](https://github.com/2dust/AndroidLibXrayLite).
For a quick start, read guide for [Go Mobile](https://github.com/golang/go/wiki/Mobile) and [Makefiles for Go Developers](https://tutorialedge.net/golang/makefiles-for-go-developers/)

aw can run on Android Emulators. For WSA, VPN permission need to be granted via
`appops set [package name] ACTIVATE_VPN allow`

## 📄 License

See [LICENSE](LICENSE) for details.
