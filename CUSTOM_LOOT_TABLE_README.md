##  自訂義 Loot Table 功能使用指南

### 功能概述

自定義 Loot Table 功能允許你直接使用背包內的物品來創建自訂的戰利品表。每個物品可以設置不同的掉落權重，系統會根據權重隨機選擇物品放入箱子中。

---

### 快速開始

#### 步驟 1：準備背包物品
1. 打開背包（默認是 E 鍵）
2. 在背包的**前 27 格**放入你想要作為戰利品的物品
   - 物品可以堆疊，系統會記錄物品類型和數量
   - 空格會被忽略
   - 最多支援 27 種不同的物品

#### 步驟 2：創建自定義 Loot Table
```
/customloot create <名稱> [權重1,權重2,權重3,...]
```

**例子：**

```
# 最簡單：所有物品權重都是 1
/customloot create my_loot

# 指定權重：第 1 個物品權重 10，第 2 個權重 5，其他都是 1
/customloot create my_loot 10,5

# 完整：為所有 27 格都指定權重
/customloot create treasure 100,50,50,25,25,10,10,5,5,3,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
```

**說明：**
- `<名稱>` - 自定義 Loot Table 的名稱（不能重複）
- `[權重...]` - 可選的逗號分隔的權重列表
  - 權重對應背包的每一格（從左到右，從上到下）
  - 如果不指定權重，所有物品的權重預設為 1
  - 權重越高，該物品被選中的機率越大
  - 權重必須是正整數

#### 步驟 3：查看已創建的 Loot Table
```
/customloot list
```

輸出示例：
```
=== 自定義 Loot Table 列表 ===
• my_loot (物品數: 5, 總權重: 20)
• treasure (物品數: 3, 總權重: 35)
• common_drops (物品數: 2, 總權重: 2)
```

#### 步驟 4：查看詳細信息
```
/customloot info <名稱>
```

輸出示例：
```
[自定义 Loot Table] my_loot
總權重: 20
  1. 鑽石 x1 (權重: 10)
  2. 金錠 x64 (權重: 5)
  3. 綠寶石 x1 (權重: 3)
  4. 附魔書 x1 (權重: 2)
```

#### 步驟 5：將 Loot Table 應用到箱子
```
/customloot apply <箱子名稱> <Loot Table 名稱>
```

**例子：**
```bash
# 先建立一個箱子
/chest add boss_loot minecraft:chests/simple_dungeon

# 然後應用自定義 Loot Table
/customloot apply boss_loot treasure
```

#### 步驟 6：測試效果
```
/chest clear <箱子名稱>
```

站在箱子旁查看其中的物品。每次執行此命令會根據權重重新隨機填充箱子。

---

### 完整指令參考

#### 創建自定義 Loot Table
```
/customloot create <名稱> [權重1,權重2,...]
```
- 從玩家背包的前 27 格創建自定義 Loot Table
- 權重為可選參數，默認所有物品權重為 1

#### 列出所有 Loot Table
```
/customloot list
```
- 顯示所有已創建的自定義 Loot Table 及其物品數和總權重

#### 查看 Loot Table 詳情
```
/customloot info <名稱>
```
- 顯示指定 Loot Table 的所有物品和權重

#### 刪除 Loot Table
```
/customloot delete <名稱>
```
- 永久刪除指定的 Loot Table
- 注意：這不會影響已應用此 Loot Table 的箱子，只是刪除了表本身

#### 將 Loot Table 應用到箱子
```
/customloot apply <箱子名稱> <Loot Table 名稱>
```
- 將自定義 Loot Table 應用到指定的箱子
- 箱子必須先使用 `/chest add` 創建
- 應用後，箱子的 Loot Table 會指向自定義表

---

### 權重系統詳解

#### 權重如何工作

權重決定了物品在隨機選擇時被選中的相對機率。

**例子：**
- 物品 A 權重 10
- 物品 B 權重 5
- 物品 C 權重 1
- 總權重 = 16

每次選擇時：
- 物品 A 被選中的機率 = 10/16 ≈ 62.5%
- 物品 B 被選中的機率 = 5/16 ≈ 31.25%
- 物品 C 被選中的機率 = 1/16 ≈ 6.25%

#### 常見權重配置

**普通戰利品（均勻分佈）：**
```bash
/customloot create common_loot 1,1,1,1,1
```
每個物品等概率出現

**稀有戰利品（金字塔式）：**
```bash
/customloot create rare_loot 100,50,20,5,1
```
第一個物品最常見，後面的逐漸稀有

**Boss 戰利品（集中式）：**
```bash
/customloot create boss_loot 10,10,10,1,1,1,1,1,1
```
前三個物品較常見，其他為稀有掉落

---

### 使用場景

#### 場景 1：設置簡單的戰利品箱
```bash
# 1. 準備背包：放入木劍、石頭、泥土各 1 個
# 2. 創建 Loot Table
/customloot create starter_loot

# 3. 創建箱子
/chest add starter_chest minecraft:chests/simple_dungeon

# 4. 應用 Loot Table
/customloot apply starter_chest starter_loot

# 5. 測試
/chest clear starter_chest
```

#### 場景 2：Boss 戰利品
```bash
# 1. 準備背包：
#    - 鑽石劍（附魔）x1
#    - 鑽石 x2
#    - 綠寶石 x1
#    - 經驗瓶 x8

# 2. 創建高價值 Loot Table
/customloot create boss_treasure 100,50,50,10,2

# 3. 設置箱子
/chest add boss_room_chest minecraft:chests/end_city_treasure
/customloot apply boss_room_chest boss_treasure

# 4. 使用隨機種子多次重置測試
/chest clear boss_room_chest 12345
/chest clear boss_room_chest 67890
```

#### 場景 3：多層難度系統
```bash
# 簡單難度戰利品
/customloot create easy_loot    # 普通物品
/customloot create normal_loot  # 更好的物品
/customloot create hard_loot    # 稀有物品

# 根據難度應用不同的 Loot Table
/customloot apply dungeon_easy normal_loot
/customloot apply dungeon_hard hard_loot
```

#### 場景 4：活動獎勵箱
```bash
# 聖誕活動
/customloot create christmas_loot
/customloot apply event_chest_1 christmas_loot
/customloot apply event_chest_2 christmas_loot

# 萬聖節活動（稍後替換）
/customloot delete christmas_loot
/customloot create halloween_loot
/customloot apply event_chest_1 halloween_loot
```

---

### 與組功能結合使用

#### 為整個組應用相同的 Loot Table
```bash
# 1. 創建自定義 Loot Table
/customloot create dungeon_loot

# 2. 創建組
/chest group create dungeon_chests

# 3. 為組內所有箱子設置相同的 Loot Table
/customloot apply dungeon_chest_1 dungeon_loot
/customloot apply dungeon_chest_2 dungeon_loot
/customloot apply dungeon_chest_3 dungeon_loot

# 4. 一鍵重置整個組
/chest group clear dungeon_chests
```

---

### 進階技巧

#### 技巧 1：使用物品堆疊創建加權效果
如果背包中某格放置了堆疊的物品，系統會同時記錄堆疊大小。

```bash
# 背包配置：
# 格 1: 鑽石劍 x1
# 格 2: 金錠 x64  (堆疊)

/customloot create test
```

當隨機選擇時，如果選中第 2 格，會獲得完整的 64 個金錠！

#### 技巧 2：頻繁更新 Loot Table
```bash
# 刪除舊 Loot Table
/customloot delete old_loot

# 重新準備背包
# ...

# 創建新 Loot Table（相同名稱）
/customloot create old_loot
```

#### 技巧 3：查詢當前配置
```bash
# 查看所有 Loot Table
/customloot list

# 查看每個 Loot Table 的詳細物品
/customloot info table_name

# 查看箱子當前使用的 Loot Table
/chest info chest_name
```

---

### 常見問題

#### Q: 為什麼創建失敗？
**A:** 
- 背包前 27 格沒有任何物品
- 自定義 Loot Table 名稱已存在
- 權重格式不正確（應為逗號分隔的數字）

#### Q: 物品會重複出現嗎？
**A:** 不會。系統從背包中複製物品信息，原背包中的物品不會被消耗或移動。

#### Q: 權重可以是小數嗎？
**A:** 不行，權重必須是正整數（1, 2, 3...）。

#### Q: 如何快速測試權重效果？
**A:** 
```bash
# 應用 Loot Table 到箱子
/customloot apply test_chest my_table

# 多次清空並查看結果
/chest clear test_chest 1
/chest clear test_chest 2
/chest clear test_chest 3
```

#### Q: 能否同時在多個箱子中使用同一個 Loot Table？
**A:** 當然可以！多個箱子可以指向同一個 Loot Table。每次清空時都會獨立隨機選擇。

---

### 數據保存

自定義 Loot Table 會自動保存到：
```
world/custom_loot_tables.nbt
```

- 每次創建/刪除 Loot Table 都會自動保存
- 服務器重啟後數據會自動加載
- 備份世界文件即可備份所有 Loot Table

---

### 完整工作流程示例

**目標：創建一個地牢，有普通箱子和 Boss 箱子，各有不同的戰利品**

```bash
# 步驟 1：準備普通戰利品
# 背包：石頭、泥土、木板、鐵錠、金錠
/customloot create dungeon_common

# 步驟 2：準備 Boss 戰利品
# 背包：鑽石、綠寶石、附魔金蘋果、經驗瓶 x8
/customloot create dungeon_boss 50,30,10,2

# 步驟 3：創建箱子
/chest add dungeon_common minecraft:chests/simple_dungeon common_room
/chest add boss_room minecraft:chests/end_city_treasure boss_room

# 步驟 4：應用 Loot Table
/customloot apply dungeon_common dungeon_common
/customloot apply boss_room dungeon_boss

# 步驟 5：測試
/chest clear dungeon_common
/chest clear boss_room

# 步驟 6：查詢配置
/customloot list
/customloot info dungeon_common
/customloot info dungeon_boss
```

---

### 與原有 Loot Table 的區別

| 特性 | 自定義 Loot Table | 原生 Loot Table |
|------|------------------|-----------------|
| 創建方式 | 使用背包物品 | Minecraft JSON 文件 |
| 權重設定 | 通過指令參數 | JSON 中配置 |
| 實時修改 | 支持（刪除並重建） | 需要重啟服務器 |
| 複雜邏輯 | 不支持 | 支持（條件、數量範圍等） |
| 易用性 | ⭐⭐⭐⭐⭐ | ⭐⭐ |

---

### 提示和最佳實踐

✅ **推薦做法**
- 為不同類型的箱子創建不同的 Loot Table
- 使用有意義的名稱（如 `boss_rare_loot` 而不是 `loot1`）
- 定期使用 `/customloot list` 檢查現有配置
- 使用備份來保存重要的 Loot Table 配置

❌ **避免的做法**
- 不要給所有箱子使用相同的 Loot Table（降低遊戲多樣性）
- 不要過度堆疊權重（會導致單一物品過度出現）
- 不要刪除活跃使用中的 Loot Table（物品會無法生成）

---

