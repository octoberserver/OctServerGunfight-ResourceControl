# OctServerGunfight 資源控制系統 - 完整使用指南

## 📑 目錄

### 第一部分：箱子管理系統
1. [功能概述](#功能概述)
2. [快速開始](#快速開始)
3. [基本指令](#基本指令)
4. [組管理](#組管理)

### 第二部分：自定義 Loot Table
5. [自定義 Loot Table 功能](#自定義-loot-table-功能)
6. [自定義 Loot Table 指令](#自定義-loot-table-指令)
7. [權重系統詳解](#權重系統詳解)

### 第三部分：實踐應用
8. [使用場景](#使用場景)
9. [進階技巧](#進階技巧)

### 第四部分：參考資料
10. [常見 Loot Table ID](#常見-loot-table-id)
11. [故障排除](#故障排除)
12. [技術細節](#技術細節)

---

# 第一部分：箱子管理系統

## 功能概述

這個模組提供了一套完整的箱子管理指令系統，讓你可以：
- ✅ 標記和記錄箱子位置
- ✅ 為箱子指定 Loot Table（原生或自定義）
- ✅ 清空並重新套用 Loot Table
- ✅ 查看所有標記的箱子
- ✅ 刪除箱子記錄
- ✅ 將箱子加入組進行批量管理
- ✅ 創建和管理組
- ✅ 查看所有組和組內成員
- ✅ 批量註冊區域內的箱子（自動命名）
- ✅ 從背包創建自定義 Loot Table
- ✅ 為每個物品設置掉落權重
- ✅ 設置箱子填充的最小和最大格數

---

## 快速開始

### 最簡單的用法（30秒上手）

```bash
# 1. 站在箱子上方
# 2. 執行指令標記箱子
/chest add my_chest "minecraft:chests/simple_dungeon"

# 3. 之後想要重新生成寶藏時
/chest clear my_chest
```

### 使用組進行組織（推薦）

```bash
# 1. 創建組
/chest group create weapons

# 2. 標記箱子並加入組
/chest add arena_chest_1 "minecraft:chests/simple_dungeon" weapons

# 3. 查看組內所有箱子
/chest group members weapons

# 4. 比賽結束後一鍵重置所有箱子
/chest group clear weapons
```

### 使用自定義 Loot Table（推薦）

```bash
# 1. 在背包前27格放入想要的物品

# 2. 創建自定義 Loot Table
/customloot create my_loot

# 3. 設置格數範圍（可選）
/customloot slots my_loot 5 10

# 4. 標記箱子
/chest add treasure_box "minecraft:chests/simple_dungeon"

# 5. 應用自定義 Loot Table
/customloot apply treasure_box my_loot

# 6. 填充箱子
/chest clear treasure_box
```

---

## 基本指令

### 箱子操作

#### `/chest add <名稱> <Loot Table> [組名...]`
在你腳下的箱子上執行，標記並記錄該箱子。

**例子：**
```bash
/chest add dungeon_chest "minecraft:chests/simple_dungeon"
/chest add treasure_box "minecraft:chests/simple_dungeon" weapons,rare_loot
```

#### `/chest remove <名稱>`
刪除指定箱子的記錄（不會破壞實際的箱子）。

#### `/chest list`
列出所有已標記的箱子及其位置和 Loot Table。

#### `/chest info <名稱>`
查看箱子的詳細信息，包括位置、Loot Table、所屬組。

#### `/chest clear <名稱> [seed]`
清空箱子內容並重新套用 Loot Table。如果不提供 seed，系統會自動生成隨機 seed，確保每次生成的戰利品都不同。

**例子：**
```bash
/chest clear dungeon_chest              # 使用隨機 seed
/chest clear treasure_box 12345         # 使用指定 seed (可重現相同結果)
```

#### `/chest region <Loot Table> <組名>`
批量標記選定區域內的所有箱子，系統會自動為每個箱子生成名稱（region_chest_1, region_chest_2...）。需要先用金斧頭選取區域。

**使用步驟：**
1. 手持金斧頭
2. 左鍵點擊第一個角落
3. 右鍵點擊對角的另一個角落
4. 執行 `/chest region <Loot Table> <組名>` 指令

**例子：**
```bash
/chest region "minecraft:chests/simple_dungeon" arena_chests
```

---

## 組管理

### 組操作指令

#### `/chest group create <組名>`
創建新組。

#### `/chest group list`
列出所有組。

#### `/chest group members <組名>`
查看組內的所有箱子。

#### `/chest group add <箱子名> <組名>`
將箱子加入組。

#### `/chest group remove <組名>`
刪除整個組（會從所有箱子中移除該組標籤）。

#### `/chest group clear <組名> [seed]`
一鍵重置組內所有箱子。

#### `/chest group join <組名>`
站在箱子上執行，將該箱子加入組。

#### `/chest group leave <組名>`
站在箱子上執行，將該箱子從組移除。

---

# 第二部分：自定義 Loot Table

## 自定義 Loot Table 功能

自定義 Loot Table 功能允許你直接使用背包內的物品來創建自訂的戰利品表。

### 如何創建

1. **準備背包物品**
   - 在背包的前 27 格放入想要的物品
   - 空格會被忽略
   - 最多支援 27 種不同的物品

2. **執行指令**
   ```bash
   /customloot create <名稱> [權重1,權重2,...]
   ```

3. **例子**
   ```bash
   # 最簡單：所有物品權重都是 1
   /customloot create my_loot
   
   # 指定權重
   /customloot create my_loot 10,5,3,2
   ```

---

## 自定義 Loot Table 指令

#### `/customloot create <名稱> [權重...]`
從背包的前 27 格創建自定義 Loot Table（舊方法，有 27 種物品限制）。

#### `/customloot new <名稱>` ⭐ **新增**
創建一個空的 Loot Table，可以逐步添加無限數量的物品。

#### `/customloot additem <表名> <權重>` ⭐ **新增**
添加手持物品到 Loot Table。可以重複執行來添加更多物品，**突破 27 種物品的限制**。

**例子：**
```bash
# 手持鑽石劍
/customloot additem my_loot 100

# 手持鑽石胸甲
/customloot additem my_loot 50

# 可以無限添加...
```

#### `/customloot import <表名> [權重...]` ⭐ **新增**
批量導入背包物品到已存在的 Loot Table（可多次執行）。

**例子：**
```bash
/customloot import my_loot          # 導入背包所有物品，權重為 1
/customloot import my_loot 10,5,20  # 導入背包物品，指定權重
```

#### `/customloot removeitem <表名> <索引>` ⭐ **新增**
移除 Loot Table 中的物品（索引從 1 開始）。

**例子：**
```bash
/customloot removeitem my_loot 3    # 移除第 3 個物品
```

#### `/customloot clear <表名>` ⭐ **新增**
清空 Loot Table 的所有物品（但保留表本身）。

#### `/customloot setweight <表名> <索引> <新權重>` ⭐ **新增**
修改指定物品的權重。

**例子：**
```bash
/customloot setweight my_loot 1 500  # 將第 1 個物品的權重改為 500
```

#### `/customloot list`
列出所有自定義 Loot Table。

#### `/customloot info <名稱>`
查看 Loot Table 的詳細信息，包括物品、權重和格數設置。

#### `/customloot delete <名稱>`
刪除自定義 Loot Table。

#### `/customloot apply <箱子名> <表名>`
將自定義 Loot Table 應用到箱子。

#### `/customloot slots <表名> <最小格數> <最大格數>`
設置該 Loot Table 填充箱子的最小和最大格數（1-27）。

**例子：**
```bash
/customloot slots rare_drops 3 5      # 填充 3-5 格
/customloot slots common_loot 8 15    # 填充 8-15 格
/customloot slots fixed_loot 15 15    # 固定填充 15 格
```

---

## 權重系統詳解

### 什麼是權重？

權重決定物品掉落的概率。權重越高，物品被選中的概率越大。

### 計算公式

```
物品被選中的概率 = 該物品的權重 / 所有物品的總權重 × 100%
```

### 實例

假設你有這樣的配置：

| 物品 | 權重 |
|------|------|
| 鑽石 | 10 |
| 金錠 | 5 |
| 翠玉 | 3 |
| 鐵錠 | 2 |

**計算結果：**
- 總權重 = 10 + 5 + 3 + 2 = 20
- 鑽石掉落概率 = 10/20 = 50%
- 金錠掉落概率 = 5/20 = 25%
- 翠玉掉落概率 = 3/20 = 15%
- 鐵錠掉落概率 = 2/20 = 10%

### 權重設置建議

| 用途 | 權重範圍 | 說明 |
|------|---------|------|
| 超稀有 | 1 | 極低概率 |
| 稀有 | 5-20 | 低概率 |
| 普通 | 50-100 | 中等概率 |
| 常見 | 200+ | 高概率 |

---

# 第三部分：實踐應用

## 使用場景

### 場景 1：PvP 競技場

**目標：** 設置公平的 PvP 競技場，所有參賽者初始裝備相同。

```bash
# 1. 創建組
/chest group create arena_weapons

# 2. 準備背包（PvP 裝備）
# - 鑽石劍 (權重 100)
# - 鑽石胸甲 (權重 50)
# - 鑽石褲 (權重 50)
# - 鑽石靴 (權重 50)
# - 鑽石頭盔 (權重 50)

# 3. 創建 Loot Table
/customloot create pvp_gear 100,50,50,50,50

# 4. 設置為固定 3 格
/customloot slots pvp_gear 3 3

# 5. 批量標記箱子
/chest region arena "minecraft:chests/simple_dungeon" arena_weapons

# 6. 應用 Loot Table
/customloot apply arena_chest_1 pvp_gear
/customloot apply arena_chest_2 pvp_gear

# 7. 比賽開始，重置所有箱子
/chest group clear arena_weapons
```

### 場景 2：地牢寶藏

**目標：** 創建隨機但平衡的地牢寶藏系統。

```bash
# 1. 準備背包
# - 鑽石 (權重 50)
# - 綠寶石 (權重 30)
# - 金錠 x64 (權重 100)
# - 鐵錠 x64 (權重 100)
# - 木板 x64 (權重 200)

# 2. 創建 Loot Table
/customloot create dungeon_treasure 50,30,100,100,200

# 3. 設置格數（5-12 格）
/customloot slots dungeon_treasure 5 12

# 4. 標記箱子
/chest group create dungeon
/chest add chest_1 "minecraft:chests/simple_dungeon" dungeon
/chest add chest_2 "minecraft:chests/simple_dungeon" dungeon

# 5. 應用 Loot Table
/customloot apply chest_1 dungeon_treasure
/customloot apply chest_2 dungeon_treasure

# 6. 首次填充
/chest group clear dungeon
```

### 場景 3：活動獎勵箱

**目標：** 設置不同難度的獎勵箱。

```bash
# 1. 創建多個 Loot Table
/customloot create reward_easy
/customloot create reward_normal
/customloot create reward_hard

# 2. 設置格數
/customloot slots reward_easy 5 10
/customloot slots reward_normal 3 8
/customloot slots reward_hard 2 5

# 3. 應用到箱子
/customloot apply reward_easy_box reward_easy
/customloot apply reward_normal_box reward_normal
/customloot apply reward_hard_box reward_hard

# 4. 填充
/chest clear reward_easy_box
/chest clear reward_normal_box
/chest clear reward_hard_box
```

---

## 進階技巧

### 技巧 1：使用隨機種子控制戰利品

```bash
# 不提供 seed - 每次都隨機生成不同的戰利品（推薦）
/chest clear my_chest

# 提供 seed - 可重現相同的戰利品
/chest clear my_chest 12345
/chest clear my_chest 12345  # 完全相同的寶藏

# 組清空 - 不提供 seed 時每個箱子都不同
/chest group clear arena      # 每個箱子隨機
/chest group clear arena 999  # 所有箱子相同
```

### 技巧 2：快速設置流程

```bash
# 3 步完成整個流程
/customloot create my_loot
/customloot apply my_chest my_loot
/chest clear my_chest
```

### 技巧 3：為不同難度創建多套 Loot Table

```bash
/customloot create diff_easy
/customloot slots diff_easy 8 15

/customloot create diff_normal
/customloot slots diff_normal 5 10

/customloot create diff_hard
/customloot slots diff_hard 2 5
```

### 技巧 4：創建超大獎池（無限物品）⭐ **新增**

```bash
# 方法 1：從空表開始逐步添加
/customloot new super_rare
/customloot additem super_rare 100   # 手持物品1
/customloot additem super_rare 80    # 手持物品2
# ... 重複添加任意數量的物品

# 方法 2：批量導入 + 繼續添加
/customloot new mixed_pool
/customloot import mixed_pool        # 導入當前背包27格
# 換一批物品到背包
/customloot import mixed_pool        # 再次導入，累加物品
# 可以重複多次

# 方法 3：組合使用
/customloot new ultimate_pool
/customloot import ultimate_pool 100,50,30  # 批量導入
/customloot additem ultimate_pool 500       # 額外添加稀有物品
/customloot additem ultimate_pool 1000      # 添加超稀有物品
```

### 技巧 5：動態調整 Loot Table

```bash
# 查看當前配置
/customloot info my_loot

# 調整權重
/customloot setweight my_loot 3 200   # 提高某物品掉率

# 移除不需要的物品
/customloot removeitem my_loot 7

# 添加新物品
/customloot additem my_loot 150

# 重新應用到箱子即可生效
/chest clear my_chest
```

