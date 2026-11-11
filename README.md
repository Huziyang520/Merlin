# MerlinLib

[![Issues](https://img.shields.io/github/issues/Huziyang520/Merlin?label=issues)](https://github.com/Huziyang520/Merlin/issues)
[![Releases](https://img.shields.io/github/v/release/Huziyang520/Merlin?label=release)](https://github.com/Huziyang520/Merlin/releases)
[![License](https://img.shields.io/badge/license-CC0--1.0-blue)](https://github.com/Huziyang520/Merlin/blob/master/LICENSE)

A library mod for mod authors: three registration lines — **enchantments, effects and potions** —
plus the parts a content mod ends up needing anyway: enchantment events, loot injection, join notices,
a server lifecycle and mob behaviour hooks.

面向模组开发者的前置库：**附魔 / 效果 / 药水** 三条注册主线，外加内容模组迟早都要用到的部分——
附魔事件、战利品注入、进服提示、服务器生命周期与生物行为挂钩。

## What you get / 提供什么

Everything is reached through one entry point, `com.huziyang520.merlinlib.api.MerlinApi`:

| Capability | 能力 | What it is for / 用途 |
|---|---|---|
| Enchantments | 附魔 | Register one from code, or describe it in `config/MerlinLib/*.json` and let the library register it. |
| Effects | 效果 | Register a mob effect; recolour or disable an existing one from the config. |
| Potions | 药水 | Register potions, including the normal / long / strong variants of one effect. |
| Enchantment events | 附魔事件 | Eight event types with the equipment scan done for you, matched by enchantment id. |
| Loot injection | 战利品注入 | Put enchanted books or items into vanilla chests and mob drops. |
| Join notices | 进服提示 | A chat message shown when a player arrives, with the per-mod switch handled for you. |
| Server lifecycle | 服务器生命周期 | Starting / started / stopping and player join, the two start moments kept distinct. |
| Mob behaviour hooks | 生物行为挂钩 | Veto a mob's target or make it keep its distance — ordinary mobs and brain mobs such as the piglin family. |
| Interface animation | 界面动画 | Opening curves and an intro a mod can drive its own screens with. |
| Colours and helpers | 颜色与工具 | Colour parsing and gradients, grant counters, smelting lookups. |

## Using it / 使用

Add the jar as a dependency and call `MerlinApi`. The library is client side too — its configuration
screen and the join notice page ship in the same file — so a mod that only wants the registration API
still installs one jar. It must be present on both sides.

把 jar 作为依赖引入并调用 `MerlinApi`。本库同时也是客户端模组：配置界面与进服提示页都在同一个 jar 里，
所以只想要注册 API 的模组也只装一个文件；两端都必须安装。

## Configuration / 配置

Content is described in `config/MerlinLib/*.json` and registered at startup; the client switches live
in `config/MerlinLib/client.toml`. The configuration screen opens from the mod list and applies on save.

内容写在 `config/MerlinLib/*.json` 里，启动时注册；客户端开关在 `config/MerlinLib/client.toml`。
配置界面从模组列表打开，保存即生效。

## Links / 链接

- Source, releases and issue tracker: <https://github.com/Huziyang520/Merlin>
- Backup feedback address: <https://issue.mengcai.online/>

## Licence / 许可证

CC0 1.0 Universal — do whatever you want with it. Copyright (c) 2026 Huziyang520.
