<div align="center">

<img src="assets/banner.svg" alt="CoreProtectModFix" width="100%">

# CoreProtectModFix

**CoreProtect Mod 交互记录补全** —— 让 CoreProtect 记住 Mod 方块的交互：谁在什么时候动了你的机器，`/comod` 一查便知。

![Platform](https://img.shields.io/badge/Platform-Spigot%20%2F%20Paper-62bda8?style=flat-square&logo=minecraft&logoColor=white)
![Minecraft](https://img.shields.io/badge/MC-1.20.x-8a9a5b?style=flat-square)
![Java](https://img.shields.io/badge/Java-17-f89820?style=flat-square&logo=openjdk&logoColor=white)
![Depends](https://img.shields.io/badge/depend-CoreProtect-e2b90f?style=flat-square)

[功能](#-功能) · [命令](#-命令) · [配置](#️-配置)

</div>

## ✨ 功能

- **Mod 方块审计**：`BlockInteractionListener` 监听 `tracked-blocks` 列出的 Mod 方块（如 Mekanism 生物发电机）的交互，经 CoreProtect API 落库
- **独立存储**：`DataStorage` 维护自己的记录层，`index` 控制检索深度
- **查询命令**：`/comod` 直接查询某方块最近的交互者，输出带 `🔍 交互查询系统` 前缀

## 📜 命令

| 命令 | 说明 |
|------|------|
| `/comod` | 查询受追踪 Mod 方块的交互记录 |

## ⚙️ 配置

```yaml
index: 5
Prefix: '§b🔍 交互查询系统 '
debug: false
tracked-blocks:
  - MEKANISMGENERATORS_BIO_GENERATOR
```

## 🔨 构建

```bash
./gradlew build
```
