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

#### `/customloot info <名稱> [頁碼]` ⭐ **已增強**
查看 Loot Table 的詳細信息（分頁顯示）。

**新功能：**
- ✅ 顯示每個物品的索引編號（從 1 開始）
- ✅ 顯示每個物品的權重
- ✅ **自動計算並顯示每個物品的掉落概率百分比**
- ✅ 支援分頁顯示（每頁 10 個物品）
- ✅ 根據稀有度用不同顏色標示（綠色=常見，紫色=超稀有）

**例子：**
```bash
/customloot info my_loot        # 查看第 1 頁
/customloot info my_loot 2      # 查看第 2 頁
```

**顯示格式：**
```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  my_loot (頁 1/3)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
總共 25 種物品 | 總權重: 1000 | 填充: 3-5 格

[  1] 鑽石劍 x1
    └ 權重: 100 | 概率: 10.00%
[  2] 鑽石 x64
    └ 權重: 50 | 概率: 5.00%
[  3] 金錠 x32
    └ 權重: 200 | 概率: 20.00%
...
```

#### `/customloot view <名稱>` ⭐ **新增**
查看詳細格式的 Loot Table（可交互、可點擊）。

**新功能：**
- ✅ 更美觀的顯示格式
- ✅ 將鼠標懸停在物品上可查看完整詳情
- ✅ 點擊物品可複製索引號到聊天框
- ✅ 完整顯示所有物品（無分頁）

**適用場景：** 當你需要編輯 Loot Table 時使用此指令，可以輕鬆複製索引號進行編輯。

**例子：**
```bash
/customloot view my_loot
```

**顯示效果：**
- 可以點擊物品名稱來複製索引號
- 懸停顯示完整的物品信息（索引、名稱、數量、權重、概率）
- 根據掉落概率用顏色標示稀有度

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

| 物品 | 權重 | 概率（自動計算） |
|------|------|------------------|
| 鑽石 | 10 | 10/20 = 50% |
| 金錠 | 5 | 5/20 = 25% |
| 翠玉 | 3 | 3/20 = 15% |
| 鐵錠 | 2 | 2/20 = 10% |

**計算結果：**
- 總權重 = 10 + 5 + 3 + 2 = 20
- 鑽石掉落概率 = 10/20 = 50% §a(常見 - 綠色)
- 金錠掉落概率 = 5/20 = 25% §e(普通 - 黃色)
- 翠玉掉落概率 = 3/20 = 15% §6(稀有 - 金色)
- 鐵錠掉落概率 = 2/20 = 10% §c(非常稀有 - 紅色)

**💡 提示：** 使用 `/customloot info` 或 `/customloot view` 指令可以自動計算並顯示所有物品的概率！

### 稀有度顏色標示

系統會根據掉落概率自動用不同顏色標示物品：

| 概率範圍 | 顏色 | 稀有度 |
|---------|------|--------|
| ≥ 30% | §a綠色 | 常見 |
| 15% - 30% | §e黃色 | 普通 |
| 5% - 15% | §6金色 | 稀有 |
| 1% - 5% | §c紅色 | 非常稀有 |
| < 1% | §5紫色 | 超稀有 |

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

### 場景 1：PvP 競技場設置

**需求：** 為 PvP 競技場設置武器箱和補給箱，每場比賽後自動重置。

**步驟：**

```bash
# 1. 創建武器組
/chest group create arena_weapons

# 2. 創建補給組
/chest group create arena_supplies

# 3. 創建自定義武器 Loot Table
/customloot new weapon_box
# 手持鑽石劍添加
/customloot additem weapon_box 100
# 手持鐵劍添加
/customloot additem weapon_box 200
# 手持弓添加
/customloot additem weapon_box 150

# 4. 設置填充格數（武器箱只填充 2-3 格）
/customloot slots weapon_box 2 3

# 5. 批量註冊區域內的武器箱
# 先用金斧頭選取競技場區域
/chest region "minecraft:chests/simple_dungeon" arena_weapons

# 6. 應用自定義 Loot Table 到所有武器箱
/customloot apply region_chest_1 weapon_box
/customloot apply region_chest_2 weapon_box
# ... 對每個箱子執行

# 7. 比賽結束後一鍵重置
/chest group clear arena_weapons
/chest group clear arena_supplies
```

---

### 場景 2：RPG 地城系統

**需求：** 創建不同難度的地城，每個地城有不同的戰利品配置。

**步驟：**

```bash
# 1. 創建簡單地城組
/chest group create dungeon_easy

# 2. 創建困難地城組
/chest group create dungeon_hard

# 3. 創建簡單地城戰利品表
/customloot new easy_loot
# 添加常見物品（高權重）
/customloot additem easy_loot 500  # 麵包
/customloot additem easy_loot 300  # 鐵錠
/customloot additem easy_loot 100  # 金錠

# 4. 創建困難地城戰利品表
/customloot new hard_loot
# 添加稀有物品（低權重）
/customloot additem hard_loot 50   # 鑽石
/customloot additem hard_loot 20   # 附魔書
/customloot additem hard_loot 10   # 下界之星

# 5. 設置填充範圍
/customloot slots easy_loot 5 10   # 簡單地城：5-10 格
/customloot slots hard_loot 3 5    # 困難地城：3-5 格（少但精）

# 6. 標記箱子
/chest add easy_treasure_1 "minecraft:chests/simple_dungeon" dungeon_easy
/chest add hard_treasure_1 "minecraft:chests/simple_dungeon" dungeon_hard

# 7. 應用戰利品表
/customloot apply easy_treasure_1 easy_loot
/customloot apply hard_treasure_1 hard_loot

# 8. 填充箱子
/chest clear easy_treasure_1
/chest clear hard_treasure_1
```

---

### 場景 3：每日挑戰獎勵

**需求：** 設置每日挑戰箱，使用固定 seed 確保公平性。

**步驟：**

```bash
# 1. 創建每日挑戰組
/chest group create daily_challenge

# 2. 創建獎勵戰利品表
/customloot new daily_reward
# 添加各種獎勵
/customloot additem daily_reward 100  # 經驗瓶
/customloot additem daily_reward 80   # 鑽石
/customloot additem daily_reward 50   # 附魔書

# 3. 標記獎勵箱
/chest add daily_box "minecraft:chests/simple_dungeon" daily_challenge

# 4. 應用獎勵表
/customloot apply daily_box daily_reward

# 5. 每天使用相同的 seed（日期）重置
# 例如：2025年11月29日 = seed 20251129
/chest clear daily_box 20251129
```

---

### 場景 4：隨機事件箱

**需求：** 在遊戲世界中設置隨機事件箱，定期刷新隨機戰利品。

**步驟：**

```bash
# 1. 創建事件箱組
/chest group create random_events

# 2. 創建多個不同的戰利品表
/customloot new event_common
/customloot new event_rare
/customloot new event_legendary

# 3. 配置不同稀有度的戰利品
# 常見事件
/customloot additem event_common 500  # 食物
/customloot additem event_common 300  # 基礎材料

# 稀有事件
/customloot additem event_rare 100    # 附魔裝備
/customloot additem event_rare 50     # 鑽石

# 傳說事件
/customloot additem event_legendary 20  # 下界之星
/customloot additem event_legendary 10  # 鞘翅

# 4. 批量註冊事件箱
/chest region "minecraft:chests/simple_dungeon" random_events

# 5. 隨機應用不同的戰利品表到不同箱子
/customloot apply region_chest_1 event_common
/customloot apply region_chest_2 event_rare
/customloot apply region_chest_3 event_legendary

# 6. 定期重置（不使用 seed 保持隨機性）
/chest group clear random_events
```

---

## 進階技巧

### 技巧 1：組合使用原生和自定義 Loot Table

**應用：** 在同一個區域混合使用 Minecraft 原生戰利品表和自定義戰利品表。

```bash
# 創建混合組
/chest group create mixed_loot

# 部分箱子使用原生表
/chest add chest1 "minecraft:chests/simple_dungeon" mixed_loot

# 部分箱子使用自定義表
/chest add chest2 "minecraft:chests/simple_dungeon" mixed_loot
/customloot apply chest2 my_custom_loot

# 一鍵重置所有
/chest group clear mixed_loot
```

---

### 技巧 2：動態調整戰利品權重

**應用：** 根據遊戲進程調整物品掉落概率。

```bash
# 查看當前配置
/customloot info my_loot

# 假設第 1 個物品（鑽石）太常見了
# 降低其權重
/customloot setweight my_loot 1 50

# 假設第 3 個物品（附魔書）太稀有了
# 提高其權重
/customloot setweight my_loot 3 200

# 查看調整後的概率
/customloot view my_loot
```

---

### 技巧 3：使用 seed 進行測試

**應用：** 在設置戰利品表時，使用固定 seed 測試配置是否合理。

```bash
# 使用固定 seed 測試
/chest clear test_chest 999

# 如果不滿意，調整權重後再次測試（相同 seed 會得到相同結果）
/customloot setweight my_loot 1 100
/chest clear test_chest 999

# 滿意後，使用隨機 seed 正式啟用
/chest clear test_chest
```

---

### 技巧 4：創建階層式戰利品系統

**應用：** 創建多層次的戰利品系統（青銅、白銀、黃金、鑽石）。

```bash
# 創建四個等級的戰利品表
/customloot new tier_bronze
/customloot new tier_silver
/customloot new tier_gold
/customloot new tier_diamond

# 配置青銅級（常見物品，大量格數）
/customloot slots tier_bronze 10 15
/customloot additem tier_bronze 500  # 麵包
/customloot additem tier_bronze 300  # 木材

# 配置白銀級（普通物品，中等格數）
/customloot slots tier_silver 8 12
/customloot additem tier_silver 200  # 鐵錠
/customloot additem tier_silver 150  # 金錠

# 配置黃金級（稀有物品，少量格數）
/customloot slots tier_gold 5 8
/customloot additem tier_gold 100   # 鑽石
/customloot additem tier_gold 80    # 附魔書

# 配置鑽石級（超稀有物品，極少格數）
/customloot slots tier_diamond 2 4
/customloot additem tier_diamond 50  # 下界之星
/customloot additem tier_diamond 30  # 鞘翅

# 創建對應的組
/chest group create tier_bronze
/chest group create tier_silver
/chest group create tier_gold
/chest group create tier_diamond
```

---

### 技巧 5：批量管理大量箱子

**應用：** 使用組和區域選擇功能高效管理大型地圖。

```bash
# 1. 用金斧頭選擇整個城市區域
# 2. 批量註冊所有箱子
/chest region "minecraft:chests/simple_dungeon" city_chests

# 3. 查看註冊了多少個箱子
/chest group members city_chests

# 4. 如果需要，可以將箱子分配到不同的子組
/chest group create city_north
/chest group create city_south

/chest group add region_chest_1 city_north
/chest group add region_chest_2 city_north
/chest group add region_chest_10 city_south
# ...

# 5. 分區域重置
/chest group clear city_north
/chest group clear city_south
```

---

### 技巧 6：備份和還原戰利品配置

**應用：** 雖然沒有直接的備份指令，但可以通過查看信息來記錄配置。

```bash
# 查看並記錄配置
/customloot view my_important_loot

# 記錄下所有物品的索引、權重、概率
# 如果需要重建，按照記錄重新創建

# 創建新表
/customloot new my_important_loot_backup

# 按照記錄添加物品
/customloot additem my_important_loot_backup 100
/customloot additem my_important_loot_backup 200
# ...
```

---

### 技巧 7：無限物品戰利品表

**應用：** 利用新的 `additem` 和 `import` 指令突破 27 種物品限制。

```bash
# 創建空表
/customloot new mega_loot

# 第一批：從背包導入 27 種物品
# （在背包放滿 27 格）
/customloot import mega_loot

# 第二批：清空背包，放入另外 27 種物品
# （在背包放滿另外 27 格）
/customloot import mega_loot

# 第三批：手持逐個添加
/customloot additem mega_loot 50
/customloot additem mega_loot 100
# ... 可以無限添加

# 查看總共添加了多少種物品
/customloot info mega_loot
```

---

# 第四部分：參考資料

## 常見 Loot Table ID

### Minecraft 原生 Loot Table（箱子類）

| Loot Table ID | 說明 | 常見位置 |
|--------------|------|---------|
| `minecraft:chests/simple_dungeon` | 簡單地牢箱 | 地牢 |
| `minecraft:chests/abandoned_mineshaft` | 廢棄礦井箱 | 廢棄礦井 |
| `minecraft:chests/desert_pyramid` | 沙漠神殿箱 | 沙漠神殿 |
| `minecraft:chests/jungle_temple` | 叢林神廟箱 | 叢林神廟 |
| `minecraft:chests/stronghold_corridor` | 要塞走廊箱 | 要塞 |
| `minecraft:chests/stronghold_crossing` | 要塞交叉口箱 | 要塞 |
| `minecraft:chests/stronghold_library` | 要塞圖書館箱 | 要塞圖書館 |
| `minecraft:chests/igloo_chest` | 冰屋箱 | 冰屋 |
| `minecraft:chests/woodland_mansion` | 林地府邸箱 | 林地府邸 |
| `minecraft:chests/end_city_treasure` | 終界城寶藏箱 | 終界城 |
| `minecraft:chests/nether_bridge` | 地獄要塞箱 | 地獄要塞 |
| `minecraft:chests/bastion_treasure` | 堡壘遺跡寶藏箱 | 堡壘遺跡 |
| `minecraft:chests/bastion_other` | 堡壘遺跡普通箱 | 堡壘遺跡 |
| `minecraft:chests/village/village_weaponsmith` | 村莊武器匠箱 | 村莊 |
| `minecraft:chests/village/village_toolsmith` | 村莊工具匠箱 | 村莊 |
| `minecraft:chests/village/village_armorer` | 村莊盔甲匠箱 | 村莊 |
| `minecraft:chests/shipwreck_treasure` | 沉船寶藏箱 | 沉船 |
| `minecraft:chests/buried_treasure` | 埋藏寶藏箱 | 海洋 |
| `minecraft:chests/underwater_ruin_small` | 小型水下廢墟箱 | 水下廢墟 |
| `minecraft:chests/underwater_ruin_big` | 大型水下廢墟箱 | 水下廢墟 |
| `minecraft:chests/pillager_outpost` | 掠奪者前哨站箱 | 掠奪者前哨站 |

### 使用建議

| 用途 | 推薦 Loot Table | 理由 |
|------|----------------|------|
| PvP 競技場武器箱 | 自定義 | 可精確控制武器類型和數量 |
| 新手村補給箱 | `minecraft:chests/village/village_toolsmith` | 基礎工具和材料 |
| 中級冒險箱 | `minecraft:chests/simple_dungeon` | 平衡的戰利品 |
| 高級挑戰箱 | `minecraft:chests/end_city_treasure` | 高級戰利品 |
| 隨機寶藏箱 | `minecraft:chests/buried_treasure` | 豐富的隨機寶藏 |

---

## 故障排除

### 問題 1：箱子沒有被填充

**症狀：** 執行 `/chest clear` 後箱子仍然是空的。

**可能原因：**
1. Loot Table ID 錯誤
2. 箱子位置記錄不正確
3. 自定義 Loot Table 為空

**解決方法：**
```bash
# 檢查箱子信息
/chest info <箱子名>

# 檢查自定義 Loot Table 內容
/customloot info <表名>

# 確認箱子是否存在
# 站在箱子上重新註冊
/chest remove <箱子名>
/chest add <箱子名> <正確的Loot Table>
```

---

### 問題 2：權重設置後概率不正確

**症狀：** 設置的權重似乎沒有生效。

**可能原因：**
1. 總權重計算錯誤
2. 權重設置到錯誤的索引

**解決方法：**
```bash
# 查看詳細信息確認概率
/customloot view <表名>

# 確認索引號（從 1 開始，不是 0）
/customloot info <表名>

# 重新設置正確的權重
/customloot setweight <表名> <正確索引> <權重>
```

---

### 問題 3：區域選擇失效

**症狀：** 使用 `/chest region` 指令後沒有註冊任何箱子。

**可能原因：**
1. 沒有使用金斧頭選擇區域
2. 選擇的區域內沒有箱子
3. 區域太大

**解決方法：**
```bash
# 確保手持金斧頭
# 左鍵點擊第一個角落（會有提示訊息）
# 右鍵點擊對角的另一個角落（會有提示訊息）
# 確認區域已選擇後執行指令

# 如果區域太大，分批選擇
```

---

### 問題 4：組操作無效

**症狀：** 執行 `/chest group clear` 後部分箱子沒有重置。

**可能原因：**
1. 箱子沒有正確加入組
2. 箱子被移除或破壞

**解決方法：**
```bash
# 查看組內成員
/chest group members <組名>

# 確認所有箱子都在列表中
# 如果有遺漏，手動添加
/chest group add <箱子名> <組名>

# 檢查每個箱子是否存在
/chest info <箱子名>
```

---

### 問題 5：自定義 Loot Table 物品遺失

**症狀：** 創建的自定義 Loot Table 中部分物品不見了。

**可能原因：**
1. 物品 NBT 數據損壞
2. 伺服器重啟導致數據遺失

**解決方法：**
```bash
# 查看當前內容
/customloot info <表名>

# 重新添加遺失的物品
/customloot additem <表名> <權重>

# 建議定期記錄配置
/customloot view <表名>
```

---

### 問題 6：填充格數不正確

**症狀：** 箱子填充的物品數量與設置不符。

**可能原因：**
1. 沒有設置 slots 範圍
2. Loot Table 物品種類太少

**解決方法：**
```bash
# 查看 Loot Table 信息
/customloot info <表名>

# 設置合理的填充範圍
/customloot slots <表名> <最小格數> <最大格數>

# 確保物品種類足夠
# 如果只有 3 種物品，無法填充 10 格不重複的物品
```

---

## 技術細節

### 數據存儲

所有箱子和 Loot Table 數據都存儲在：
```
world/data/osgrc/
├── chests.dat           # 箱子記錄
├── groups.dat           # 組記錄
└── custom_loots.dat     # 自定義 Loot Table
```

### NBT 數據結構

#### 箱子數據
```nbt
{
  name: "chest_name",
  pos: {x: 100, y: 64, z: 200},
  dimension: "minecraft:overworld",
  lootTable: "minecraft:chests/simple_dungeon",
  customLootTable: "my_custom_loot",
  groups: ["group1", "group2"]
}
```

#### 自定義 Loot Table 數據
```nbt
{
  name: "my_loot",
  totalWeight: 1000L,
  minSlots: 5,
  maxSlots: 10,
  items: [
    {
      item: {id: "minecraft:diamond_sword", Count: 1b, ...},
      weight: 100L
    },
    ...
  ]
}
```

### 權重計算算法

```java
// 隨機選擇物品的算法
long roll = random.nextLong() % totalWeight;
long current = 0;

for (LootItem item : items) {
    current += item.weight;
    if (roll < current) {
        return item.itemStack.copy();
    }
}
```

### 填充算法

```java
// 箱子填充算法
int slotsToFill = random.nextInt(maxSlots - minSlots + 1) + minSlots;

for (int i = 0; i < slotsToFill; i++) {
    int randomSlot = random.nextInt(27);
    ItemStack randomItem = lootTable.getRandomItem(random);
    chest.setItem(randomSlot, randomItem);
}
```

### 概率計算公式

```
單個物品掉落概率 = 物品權重 / 總權重 × 100%

至少獲得一次該物品的概率（填充 n 格）:
P(至少一次) = 1 - (1 - 單次概率)^n

例如：
- 物品權重 = 100
- 總權重 = 1000
- 單次概率 = 10%
- 填充 5 格
- 至少獲得一次 = 1 - (1 - 0.1)^5 = 1 - 0.59049 ≈ 40.95%
```

### 性能優化建議

1. **避免過大的區域選擇**
   - 一次最多註冊 100 個箱子
   - 大範圍分批處理

2. **合理設置權重**
   - 使用整數權重
   - 避免過大的權重值（建議 < 10000）

3. **定期清理無效箱子**
   - 檢查已破壞的箱子記錄
   - 刪除不再使用的記錄

4. **組織結構建議**
   ```
   總組
   ├── 區域組（北區、南區）
   │   ├── 類型組（武器箱、補給箱）
   │   │   └── 個別箱子
   ```

---

## 版本歷史

### v1.0.0
- ✅ 基本箱子管理功能
- ✅ 組管理系統
- ✅ 區域批量註冊
- ✅ 自定義 Loot Table（27 種物品限制）

### v1.1.0（當前版本）
- ⭐ 新增無限物品支援
- ⭐ 新增 `additem` 指令
- ⭐ 新增 `import` 指令
- ⭐ 新增 `removeitem` 指令
- ⭐ 新增 `setweight` 指令
- ⭐ 新增 `view` 指令（可交互顯示）
- ⭐ 增強 `info` 指令（顯示索引和概率）
- ⭐ 新增填充格數控制
- ⭐ 新增稀有度顏色標示
- ⭐ 新增分頁顯示支援

---

## 致謝

感謝所有測試人員和貢獻者！

---

## 授權

本專案採用 MIT 授權。

---

## 聯絡方式

如有問題或建議，請聯繫開發團隊。

---

**祝你遊戲愉快！🎮**
