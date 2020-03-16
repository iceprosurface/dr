## 简介

这是一个 mc 的 spigot 插件，用于完成一些简单的 hard core 模式的东西

主要包含： 死亡后禁止玩家作出以下操作

- 拣取方块
- 破坏或是与方块交互
- 攻击目标

死亡的玩家被称为英灵 ———— 他们可以自由行动，且会再一次死亡。（变相的旁观者模式）

## 配置

这里简单的作出了一些配置：

```yaml
# 复活需要的花费
ResurrectionCosts:
  # 复活需要的 hp
  Hp: 0
  # 是否需要在手中放置一件附魔物品作为祭品
  EnchantItemsInHand: false
  # 复活需要的资源列表
  # <item.id>: <int>
  ResourceList:
    AIR: 0

worlds:
  - "world"
  - "world_nether"
  - "world_the_end"

RespawnPoint:
  - 0
  - 0
  - 0

RespawnWorld: "world"

# 复活 cd
# ResurrectionCoolDown: -1

ResurrectSelf: "你不能尝试复活你自己!"
NotEnough: "你必须有足够的祭品"
NotEnoughDetail: "你缺少%count%个%name%"
NoEnchantItem: "你必须使用正确的附魔装备来复活玩家！"
NotEnoughHp: "你至少需要%hp%血来复活玩家！"
MustHaveTarget: "你必须指定一个玩家！"
NotExiled: "玩家未被放逐"
AlreadyExiled: "玩家尚未被放逐"
SuccessResurrect: "%player%已经被%from%成功复活"
OpOnly: "只有 op 能这么做"
SuccessExiled: "玩家%player%已经被成功放逐"
NotSafe: "目标所在方块不安全"
ResurrectOtherInExiled: "你不能在放逐状态下复活别人"
```

> 注意：尚未配置 placeholder 插件，所以只能使用现在我指定的变量

## 命令列表

```
dr resurrect <player>
dr exile <player>
dr config reload
dr respawn
```

## 重生

玩家可以被其他玩家重生，重生玩家将会重生在设定的世界，你可以通过指令设置，或是在配置文件中设置。

玩家重生时将会在重生点创建一道闪电

## 死亡

玩家死亡后全图都会收到死亡音效（仅非放逐模式下有效）

## 关于 ci 和 release

暂未实现，请稍等