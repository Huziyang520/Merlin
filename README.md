# 📚 MerlinLib

[![GitHub](https://img.shields.io/badge/GitHub-Merlin-181717?logo=github)](https://github.com/Huziyang520/Merlin)
[![Issues](https://img.shields.io/github/issues/Huziyang520/Merlin?color=2ea043)](https://github.com/Huziyang520/Merlin/issues)
[![License](https://img.shields.io/badge/license-CC0--1.0-4c9be8)](https://github.com/Huziyang520/Merlin/blob/master/LICENSE)

[English](#english) | [中文](#中文)

---

## English

A library mod for mod authors: three registration lines — enchantments, effects and potions — plus the parts a content mod ends up needing anyway: enchantment events, loot injection, join notices, a server lifecycle and mob behaviour hooks.

### ✨ What you get

Everything is reached through one entry point, `com.huziyang520.merlinlib.api.MerlinApi`:

| Capability | What it is for |
|---|---|
| Enchantments | Register one from code, or describe it in `config/MerlinLib/*.json` and let the library register it. |
| Effects | Register a mob effect; recolour or disable an existing one from the config. |
| Potions | Register potions, including the normal / long / strong variants of one effect. |
| Enchantment events | Eight event types with the equipment scan done for you, matched by enchantment id. |
| Loot injection | Put enchanted books or items into vanilla chests and mob drops. |
| Join notices | A chat message shown when a player arrives, with the per-mod switch handled for you. |
| Server lifecycle | Starting / started / stopping and player join, the two start moments kept distinct. |
| Mob behaviour hooks | Veto a mob's target or make it keep its distance — ordinary mobs and brain mobs such as the piglin family. |
| Interface animation | Opening curves and an intro a mod can drive its own screens with. |
| Colours and helpers | Colour parsing and gradients, grant counters, smelting lookups. |

### 📥 Install

Add the jar as a dependency and call `MerlinApi`. It must be present on both sides: the library is client side too — its configuration screen and the join notice page ship in the same file — so a mod that only wants the registration API still installs one jar.

### ⚙️ Configuration

Content is described in `config/MerlinLib/*.json` and registered at startup; the client switches live in `config/MerlinLib/client.toml`. The configuration screen opens from the mod list and applies on save.

### 🔗 Links

- Source and issue tracker: <https://github.com/Huziyang520/Merlin>
- Backup feedback address: <https://issue.mengcai.online/>

### 📜 Licence

CC0 1.0 Universal — do whatever you want with it. Copyright (c) 2026 Huziyang520.

---

## 中文

面向模组开发者的前置库：附魔 / 效果 / 药水三条注册主线，外加内容模组迟早都要用到的部分——附魔事件、战利品注入、进服提示、服务器生命周期与生物行为挂钩。

### ✨ 提供什么

统一入口是 `com.huziyang520.merlinlib.api.MerlinApi`：

| 能力 | 用途 |
|---|---|
| 附魔 | 代码注册，或在 `config/MerlinLib/*.json` 里写一份定义、由库完成注册。 |
| 效果 | 注册状态效果；已有效果可以改色或禁用。 |
| 药水 | 注册药水，含单一效果的普通 / 长效 / 强力三连。 |
| 附魔事件 | 八种事件类型，装备扫描由库完成，按附魔 id 匹配。 |
| 战利品注入 | 往原版箱子与生物掉落里塞附魔书或物品。 |
| 进服提示 | 玩家进入世界时的聊天消息，逐模组开关由库处理。 |
| 服务器生命周期 | 启动中 / 已启动 / 停止与玩家进入，两个"启动"时机严格区分。 |
| 生物行为挂钩 | 否决生物的寻敌目标、让它主动远离——普通生物与 Brain 生物（如猪灵族）都覆盖。 |
| 界面动画 | 库自己的界面带动画，任何模组都能拿到同一套曲线与入场动画。 |
| 颜色与工具 | 颜色解析与渐变、授予记账、熔炼查询。 |

### 📥 安装

把 jar 作为依赖引入并调用 `MerlinApi`。两端都必须安装：本库同时也是客户端模组，配置界面与进服提示页都在同一个 jar 里，所以只想要注册 API 的模组也只装一个文件。

### ⚙️ 配置

内容写在 `config/MerlinLib/*.json` 里，启动时注册；客户端开关在 `config/MerlinLib/client.toml`。配置界面从模组列表打开，保存即生效。

### 🔗 链接

- 源码与问题反馈：<https://github.com/Huziyang520/Merlin>
- 备用反馈地址：<https://issue.mengcai.online/>

### 📜 许可证

本项目以 CC0 1.0 协议开源，可自由使用。Copyright (c) 2026 Huziyang520.
