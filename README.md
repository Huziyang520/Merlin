# MerlinLib

[English](#english) | [中文](#中文)

---

## English

MerlinLib is a Minecraft **library mod** for mod authors: it gives you three registration lines —
**enchantments, effects and potions** — plus the parts every content mod ends up needing anyway:
enchantment events, loot injection, join notices, a server lifecycle and mob behaviour hooks.

- Sides: **client and server** — it must be installed on both
- Author: Huziyang520 · Licence: **CC0 1.0**
- Repository: <https://github.com/Huziyang520/Merlin> · Issues: <https://github.com/Huziyang520/Merlin/issues>

### Supported versions

| Branch | Minecraft | Loader |
|---|---|---|
| [`master`](https://github.com/Huziyang520/Merlin/tree/master) | 26.X | NeoForge, Fabric |
| [`1.20.1`](https://github.com/Huziyang520/Merlin/tree/1.20.1) | 1.20.1 | Forge |

A branch that covers a range of game versions ships one jar for the whole range.

### What it gives a developer

Everything is reached through one entry point, `com.huziyang520.merlinlib.api.MerlinApi`:

| Capability | What it is for |
|---|---|
| **Enchantments** | register one from code, or describe it in `config/MerlinLib/*.json` and let the library register it |
| **Effects** | register a mob effect, recolour or disable an existing one from the config |
| **Potions** | register potions, including the normal / long / strong variants of one effect |
| **Enchantment events** | eight event types with the equipment scan done for you, matched by enchantment id |
| **Loot injection** | put enchanted books or items into vanilla chests and mob drops |
| **Join notices** | a chat message shown when a player arrives, with the per-mod switch handled for you |
| **Server lifecycle** | starting / started / stopping and player join, with the two start moments kept distinct |
| **Mob behaviour hooks** | veto a mob's target, make it keep its distance — ordinary mobs **and** brain mobs such as the piglin family |
| **Interface animation** | opening curves and an intro a mod can drive its own screens with |
| **Colours and helpers** | colour parsing and gradients, grant counters, smelting lookups |

The library is **client side too**: its configuration screen and the join notice page live in the same
jar, so a mod that only wants the registration API still installs one file.

### Documentation

| Document | What is in it |
|---|---|
| [`docs/USAGE.md`](docs/USAGE.md) | the config files, the commands, where the configuration screen comes from |
| [`docs/DEV.md`](docs/DEV.md) | the Java API, and how to depend on MerlinLib |
| [`docs/LIMITS.md`](docs/LIMITS.md) | what a configuration file can and cannot do |

### Licence

Released under **CC0 1.0**, see [LICENSE](LICENSE).

```
Copyright (c) 2026 Huziyang520
```

---

## 中文

MerlinLib 是一款 Minecraft **前置库模组**：为模组开发者提供**附魔 / 效果 / 药水**三条注册主线，
外加内容模组迟早都要用到的部分：附魔事件、战利品注入、进服提示、服务器生命周期与生物行为挂钩。

- 部署属性：**客户端 + 服务端模组**，两端都必须安装
- 作者：Huziyang520 · 许可证：**CC0 1.0**
- 仓库：<https://github.com/Huziyang520/Merlin> · 问题反馈：<https://github.com/Huziyang520/Merlin/issues>

### 支持版本

| 分支 | Minecraft | 加载器 |
|---|---|---|
| [`master`](https://github.com/Huziyang520/Merlin/tree/master) | 26.X | NeoForge, Fabric |
| [`1.20.1`](https://github.com/Huziyang520/Merlin/tree/1.20.1) | 1.20.1 | Forge |

覆盖一段游戏版本的分支，为整段版本提供同一个 jar。

### 给开发者提供什么

统一入口是 `com.huziyang520.merlinlib.api.MerlinApi`：

| 能力 | 用途 |
|---|---|
| **附魔** | 代码注册，或在 `config/MerlinLib/*.json` 里写一份定义、由库完成注册 |
| **效果** | 注册状态效果；已有效果可以改色或禁用 |
| **药水** | 注册药水，含单一效果的 普通 / 长效 / 强力 三连 |
| **附魔事件** | 八种事件类型，装备扫描由库完成，按附魔 id 匹配 |
| **战利品注入** | 往原版箱子与生物掉落里塞附魔书或物品 |
| **进服提示** | 玩家进入世界的聊天消息，逐模组开关由库处理 |
| **服务器生命周期** | 启动中 / 已启动 / 停止与玩家进入，两个"启动"时机严格区分 |
| **生物行为挂钩** | 否决生物的寻敌目标、让它主动远离——普通生物**与** Brain 生物（如猪灵族）都覆盖 |
| **界面动画** | 库自己的界面带动画，任何模组都能拿到同一套曲线与入场动画 |
| **颜色与工具** | 颜色解析与渐变、授予记账、熔炼查询 |

本库**同时也是客户端模组**：配置界面与进服提示页都在同一个 jar 里，所以只想要注册 API 的模组也只装一个文件。

### 文档

| 文档 | 内容 |
|---|---|
| [`docs/USAGE.md`](docs/USAGE.md) | 配置文件、指令、配置界面的来源 |
| [`docs/DEV.md`](docs/DEV.md) | Java API 与依赖方式 |
| [`docs/LIMITS.md`](docs/LIMITS.md) | 配置文件能做什么、不能做什么 |

### 许可证

本项目以 **CC0 1.0** 协议开源，协议全文见 [LICENSE](LICENSE)。

```
Copyright (c) 2026 Huziyang520
```
