# MerlinLib — Usage

[English](#english) | [中文](#中文)

> Configuration files, commands and where the visual configuration screen comes from.
> For the Java API see [`DEV.md`](DEV.md); for what can and cannot be changed from a file see
> [`LIMITS.md`](LIMITS.md).

---

## English

### Where everything lives

The file that holds a given switch depends on the loader, because each line hands the switches to that
loader's own configuration system:

| Line | Client switches | Server switches |
|---|---|---|
| NeoForge | `config/merlinlib-client.toml` | `config/merlinlib-common.toml` |
| Fabric | `config/MerlinLib/client.toml` | `config/MerlinLib/server.toml` |
| Forge | `config/MerlinLib/client.toml` | `config/MerlinLib/server.toml` |

A `config/MerlinLib/client.toml` left by an earlier build is read once and renamed to
`client.toml.imported`; the content definitions (`enchantments.json`, `effects.json`) and the
`config/MerlinLib/*.json` folders stay where they were.

| File | Purpose | How a change applies |
|---|---|---|
| `client.toml` | client switches and display settings | saving is enough |
| `server.toml` | authoritative server switches and ceilings | saving is enough |
| `enchantments.json` | enchantment definitions | **at the next start** |
| `effects.json` | effect recolouring / disabling | **at the next start** |

You may add more `.json` files next to `enchantments.json`; they are merged in file name order.

**Content is read once, at startup.** Enchantments are a code registry here — there is no data driven
enchantment registry to inject into — so editing `enchantments.json` or `effects.json` and running
`/reload` changes nothing about what is registered. Restart the game. `/reload` is still worth having:
it reloads loot tables and re-reads the switch files.

`/merlinlib content` prints what is registered and whether it came from the files on disk, so a
mismatch between what you edited and what is running is one command away.

The values in `client.toml` and `server.toml` are read as they are used, so a switch takes effect as
soon as the file is saved.

The configuration screen shows both files: the client sections first, then `content` and `notices` from
the server file, in the order this guide lists them. The join notice page is a page of its own — one
switch per mod — reachable from the screen or from `/merlinlib notices`.

### `client.toml`

| Key | Default | Meaning |
|---|---|---|
| `particles.limit_enabled` / `particles.limit` | `true` / `512` | cap on how many particles this client draws per 1/20 second and how many one server send may carry |
| `general.config_version` | current | file layout marker, written by the mod; not meant to be edited, and never part of the server file |

### `server.toml`

| Key | Default | Meaning |
|---|---|---|
| `content.disable_generated_enchantments` | `false` | emergency switch: none of the enchantments in `config/MerlinLib/*.json` are registered |
| `notices.enabled` | `true` | show the join notices business mods registered through MerlinLib |

`server.toml` is authoritative: the client only shows what the server allows and every write is
validated on the server. In single player the integrated server shares the process, so a change applies
at once.

### `enchantments.json`

Adding enchantments from a file is the core of MerlinLib. The field names match the vanilla
enchantment data format one to one, and MerlinLib turns them into registered enchantments at startup.

```json
{
  "enchantments": [
    {
      "id": "frostbite",
      "translation_key": "enchantment.mymod.frostbite",
      "weight": 5,
      "max_level": 3,
      "min_cost": { "base": 10, "per_level_above_first": 9 },
      "max_cost": { "base": 40, "per_level_above_first": 9 },
      "anvil_cost": 2,
      "slots": ["mainhand"],
      "supported_items": "#minecraft:enchantable/weapon",
      "primary_items": "#minecraft:enchantable/melee_weapon",
      "exclusive_set": "#minecraft:exclusive_set/damage",
      "acquisition": {
        "enchanting_table": true,
        "villager_trade": true,
        "fishing": false,
        "loot_chest": true,
        "treasure_only": false,
        "curse": false
      },
      "effects": {
        "minecraft:damage": [
          {
            "effect": {
              "type": "minecraft:add",
              "value": { "type": "minecraft:linear", "base": 1.0, "per_level_above_first": 0.5 }
            }
          }
        ]
      }
    }
  ]
}
```

| Field | Meaning |
|---|---|
| `id` | required; `frostbite` is completed to `merlinlib:frostbite`, a full `mymod:frostbite` is used as is |
| `enabled` | `false` disables it. Disabling wins over everything, so it can switch off a same-named enchantment from another source |
| `translation_key` / `description` | either one; when both are missing the key is derived from `id` as `enchantment.<namespace>.<path>` |
| `weight` | `1..1024` |
| `max_level` | `1..255` |
| `min_cost` / `max_cost` | an integer, or `{base, per_level_above_first}` |
| `anvil_cost` | non-negative |
| `slots` | `mainhand` / `offhand` / `hand` / `armor` / `head` / `chest` / `legs` / `feet` / `body` / `any` |
| `supported_items` | an item id, a `#tag` or an array of either |
| `primary_items` | optional, same forms |
| `exclusive_set` | mutual exclusion: a `#tag`, an id or an array |
| `effects` | the vanilla effect components, passed through unchanged |
| `acquisition` | how it is obtained — see below |

Anything that fails to parse is reported as **file + JSON path + expected + found** and the entry is
skipped; the rest of the file still loads.

#### `acquisition` (MerlinLib's own field)

Vanilla has no such field. MerlinLib uses the six switches to drive the enchantment tags
(`in_enchanting_table`, `tradeable`, `on_random_loot`, `treasure`, `curse`) *by name*, and translates
them onto the equivalent tags and item classes of a version that has no such tag layer, so a definition
written for a newer format keeps working here.

| Field | Effect |
|---|---|
| `enchanting_table` | the enchanting table may offer it |
| `villager_trade` | librarian trades may sell it |
| `fishing` / `loot_chest` | it may appear in random loot |
| `treasure_only` | it is treasure, so it never appears on the enchanting table |
| `curse` | it is a curse |

#### `#minecraft:enchantable/*` names

Those tags arrive with the data driven enchantment registry and are not present on every version.
MerlinLib translates the family onto the tags and item classes of the older format —
`enchantable/sharp_weapon` becomes "swords and axes", `enchantable/head_armor` becomes "armour worn on
the head", and so on. A name that does not resolve matches nothing and says so once in the log rather
than quietly accepting every item.

### `effects.json`

```json
{
  "effects": [
    { "id": "minecraft:slowness", "enabled": true, "color": "0x5A6ACF" },
    { "id": "minecraft:poison", "enabled": false }
  ]
}
```

- `color` accepts `"0xRRGGBB"`, `"#RRGGBB"`, `"RRGGBB"`, a decimal number or a palette name
  (`red`, `cyan`, `pink`, ...).
- `enabled: false` makes the effect do nothing at all and stops potions from being generated for it.

### Commands

| Command | What it does |
|---|---|
| `/merlinlib list` | every non-vanilla enchantment in the registry, not only the ones this library manages |
| `/merlinlib content` | what is registered and whether it matches the files on disk |
| `/merlinlib info <id>` | one enchantment: whether it is in the registry, its maximum level and weight |
| `/merlinlib book <id> [level]` | hands out the enchanted book (operators) |
| `/merlinlib layers <effect> <layers> <seconds>` | stacks an effect on yourself: one level per layer, one more layer per second |
| `/merlinlib pack` | state of the in-memory resource pack built from the files: whether it is available, selected, and how many files it holds |
| `/merlinlib validate` | re-checks the definitions and prints what it finds wrong |

`/merlinlib pack` and `/merlinlib validate` belong to the lines that build that pack; elsewhere
`/merlinlib content` answers the same question. Either way the answer is about what is running, not
about what you meant to load.

### Client-side commands

| Command | What it does |
|---|---|
| `/merlinlib config` | opens the configuration screen |
| `/merlinlib notices` | opens the join notice switches: one row for MerlinLib and one for every mod that registered a notice |

The same page is reachable from the configuration screen itself: the native screen, the Cloth screen and
the Jupiter screen each carry an entry that opens it.

### The configuration screen

The screen is the loader's, not MerlinLib's:

| Line | How the screen is reached | Without it |
|---|---|---|
| NeoForge | **Config** in the mods list — NeoForge's own screen, built from the declared switches | always available |
| Fabric | `/merlinlib config` or the mod menu entry, drawn by **Cloth Config** or **Jupiter** | edit `config/MerlinLib/*.toml`, use the commands |
| Forge | **Config** in the mods list, drawn by **Cloth Config** or **Jupiter** | edit `config/MerlinLib/*.toml`, use the commands |

Cloth Config and Jupiter are both optional, and on Forge and Fabric either one is enough: both screens
are built from the same list of switches, so the two libraries cannot disagree about what exists. With
neither installed the switches live in the files and the commands still read and write them, so nothing
is lost except the window.

---

## 中文

### 文件都在哪

开关落在哪个文件取决于加载器——每条线都把开关交给该加载器自己的配置系统：

| 线 | 客户端开关 | 服务端开关 |
|---|---|---|
| NeoForge | `config/merlinlib-client.toml` | `config/merlinlib-common.toml` |
| Fabric | `config/MerlinLib/client.toml` | `config/MerlinLib/server.toml` |
| Forge | `config/MerlinLib/client.toml` | `config/MerlinLib/server.toml` |

旧版留下的 `config/MerlinLib/client.toml` 只读一次，随后改名为 `client.toml.imported`；内容定义
（`enchantments.json`、`effects.json`）与 `config/MerlinLib/*.json` 目录原地不动。

| 文件 | 作用 | 生效方式 |
|---|---|---|
| `client.toml` | 客户端开关与显示参数 | 保存即可 |
| `server.toml` | 服务端权威开关与上限 | 保存即可 |
| `enchantments.json` | 附魔定义 | **下次启动** |
| `effects.json` | 效果改色 / 禁用 | **下次启动** |

你可以在 `enchantments.json` 旁边新建更多 `.json` 文件（按文件名排序合并）。

**内容只在启动时读一次。** 附魔走的是代码注册表，没有可注入的数据驱动注册表，因此改完
`enchantments.json` 或 `effects.json` 再执行 `/reload` 什么都不会变——**要重启游戏**。
`/reload` 仍然有用：它会重读战利品表与两个开关文件。

`/merlinlib content` 会打印当前注册了什么、以及是否与磁盘上的文件一致，改完文件想确认"跑的到底是哪份"
只需一条指令。

`client.toml` 与 `server.toml` 的值是**用的时候才读**，所以开关一保存就生效。

配置界面把两个文件都列出来：先是客户端各段，然后是服务端文件的 `content` 与 `notices`，
顺序与本文一致。进世界通知是单独一页——每个模组一个开关——从配置界面或
`/merlinlib notices` 进入。

### `client.toml`

| 键 | 默认 | 说明 |
|---|---|---|
| `particles.limit_enabled` / `particles.limit` | `true` / `512` | 粒子上限：既是客户端每 1/20 秒的绘制上限，也是服务端单次发送上限 |
| `general.config_version` | 当前值 | 文件布局标记，由模组写入，不必手改；它只存在于客户端文件 |

### `server.toml`

| 键 | 默认 | 说明 |
|---|---|---|
| `content.disable_generated_enchantments` | `false` | 应急开关：不再注册 `config/MerlinLib/*.json` 里的附魔 |
| `notices.enabled` | `true` | 是否显示业务模组注册的进服聊天提示 |

`server.toml` 是权威：客户端只展示服务端允许的范围，每次写入都由服务端校验。单人游戏下集成服务端
与本进程共享，改动立即生效。

### `enchantments.json`

用文件新增附魔是 MerlinLib 的核心能力。字段名与原生附魔数据格式一一对应，MerlinLib 在启动时把它们
变成注册好的附魔。

字段含义见英文部分的两张表（`id` / `enabled` / `translation_key` / `weight` / `max_level` /
`min_cost` / `max_cost` / `anvil_cost` / `slots` / `supported_items` / `primary_items` /
`exclusive_set` / `effects` / `acquisition`）。

解析失败会给出**文件名 + JSON 路径 + 期望值 + 实际值**并跳过该条，文件其余部分照常加载。

#### `acquisition`（MerlinLib 自有字段）

原生附魔没有这个字段。六个开关对应附魔标签（`in_enchanting_table`、`tradeable`、
`on_random_loot`、`treasure`、`curse`）；在没有这层标签的版本上，MerlinLib 会按名字把它们翻译到
旧格式的等价标签与物品类别上，所以照新版写的定义在这里照样能用。

#### `#minecraft:enchantable/*` 这类名字

它们随数据驱动附魔注册表一起出现，并非每个版本都有。MerlinLib 会把这一族翻译到旧格式的标签与物品类上
——`enchantable/sharp_weapon` 变成"剑与斧"，`enchantable/head_armor` 变成"戴在头上的护甲"，以此类推。
解析不出来的名字匹配不到任何物品，并会在日志里说一次，而不是悄悄接受所有物品。

### `effects.json`

- `color` 支持 `"0xRRGGBB"`、`"#RRGGBB"`、`"RRGGBB"`、十进制数字或调色板名（`red`、`cyan`、`pink` …）。
- `enabled: false` 会让该效果**停止产生任何效果**，并且不再为它生成药水。

### 指令

| 指令 | 作用 |
|---|---|
| `/merlinlib list` | 列出注册表里所有非原版附魔（不只本库管理的） |
| `/merlinlib content` | 当前注册了什么，以及是否与磁盘上的文件一致 |
| `/merlinlib info <id>` | 单个附魔：是否在注册表、最大等级、权重 |
| `/merlinlib book <id> [等级]` | 发放对应附魔书（需要 OP） |
| `/merlinlib layers <效果> <层数> <秒数>` | 给自己叠效果：一层一等级，每多一秒再加一层 |
| `/merlinlib pack` | 按文件在内存里生成的那份资源包的状态：是否可用、是否被选中、含多少个文件 |
| `/merlinlib validate` | 重新检查全部定义并打印发现的问题 |

`/merlinlib pack` 与 `/merlinlib validate` 属于会生成那份资源包的线；不生成它的线上，
`/merlinlib content` 回答同一个问题。两种情况下答案说的都是"正在跑的是什么"，不是"你打算加载什么"。

### 客户端指令

| 指令 | 作用 |
|---|---|
| `/merlinlib config` | 打开配置界面 |
| `/merlinlib notices` | 打开进服提示开关页：MerlinLib 一行，每个登记过提示的模组各一行 |

这一页也能从配置界面本身进入：原生界面、Cloth 界面、Jupiter 界面各有一个入口。

### 配置界面

界面由加载器提供，不是本模组画的：

| 线 | 打开方式 | 没装时 |
|---|---|---|
| NeoForge | 模组列表里的 **配置（Config）**，NeoForge 自己的界面，按声明的开关生成 | 始终可用 |
| Fabric | `/merlinlib config` 或模组菜单入口，由 **Cloth Config** 或 **Jupiter** 绘制 | 手改 `config/MerlinLib/*.toml`，或用指令 |
| Forge | 模组列表里的 **配置**，由 **Cloth Config** 或 **Jupiter** 绘制 | 手改 `config/MerlinLib/*.toml`，或用指令 |

Cloth Config 与 Jupiter 都是可选的，且在 Forge 与 Fabric 上**装其中任意一个就够**：两套界面都由同一份
开关清单生成，不会出现「这边有这个开关、那边没有」。两个都没装时开关仍在文件里、指令照样读写，
除了窗口不会少任何东西。
