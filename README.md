# OctServerGunfight 資源控制系統 - 完整使用指南

## 📑 目錄

### 第一部分：箱子管理系統
1. [箱子管理功能概述](#箱子管理功能概述)
2. [快速開始](#快速開始)
3. [基本指令參考](#基本指令參考)
4. [組管理功能](#組管理功能)
5. [快速參考表](#快速參考表)

### 第二部分：自定義 Loot Table
6. [自定義 Loot Table 功能](#自定義-loot-table-功能)
7. [自定義 Loot Table 指令](#自定義-loot-table-指令)
8. [權重系統詳解](#權重系統詳解)

### 第三部分：實踐應用
9. [使用場景和工作流程](#使用場景和工作流程)
10. [常見指令組合](#常見指令組合)
11. [進階技巧](#進階技巧)

### 第四部分：參考資料
12. [常見 Minecraft Loot Table ID](#常見-minecraft-loot-table-id)
13. [技術細節](#技術細節)
14. [故障排除](#故障排除)
15. [開發者信息](#開發者信息)
16. [更新日誌](#更新日誌)

---

# 第一部分：箱子管理系統

## 箱子管理功能概述

這個模組提供了一套完整的箱子管理指令系統，讓你可以：
- ✅ 標記和記錄箱子位置
- ✅ 為箱子指定 Loot Table（原生或自定義）
- ✅ 清空並重新套用 Loot Table
- ✅ 查看所有標記的箱子
- ✅ 刪除箱子記錄
- ✅ 將箱子加入組進行批量管理
- ✅ 創建和管理組
- ✅ 查看所有組和組內成員
- ✅ 批量註冊區域內的箱子
- ✅ 從背包創建自定義 Loot Table
- ✅ 為每個物品設置掉落權重

---

## 快速開始

### 最簡單的用法（30秒上手）

```bash
# 1. 站在箱子上方
# 2. 執行指令標記箱子
/chest add my_chest minecraft:chests/simple_dungeon

# 3. 之後想要重新生成寶藏時
/chest clear my_chest
```

### 使用組進行組織（推薦）

```bash
# 1. 創建組
/chest group create weapons

# 2. 站在箱子上，直接加入組
/chest add arena_chest_1 custom:pvp_weapons weapons

# 3. 查看組內所有箱子
/chest group members weapons

# 4. 比賽結束後一鍵重置所有箱子
/chest group clear weapons
```

### 使用自定義 Loot Table（新功能）

```bash
# 1. 在背包前27格放入想要的物品

# 2. 創建自定義 Loot Table
/customloot create my_loot

# 3. 創建箱子
/chest add treasure_box minecraft:chests/simple_dungeon

# 4. 應用自定義 Loot Table
/customloot apply treasure_box my_loot

# 5. 測試效果
/chest clear treasure_box
```

---

# 第一部分詳細指令

## 基本指令參考

### 基本箱子管理

#### 1️⃣ 新增箱子
```
/chest add <名稱> <Loot Table ID> [組名1,組名2,...]
```
- 在你腳下的箱子上執行此命令
- `<名稱>` - 箱子的標識名稱（不能重複）
- `<Loot Table ID>` - Loot Table 的完整路徑
- `[組名...]` - 可選，可在新增時直接加入組

**示例：**
```bash
/chest add dungeon_chest minecraft:chests/simple_dungeon
/chest add treasure_box custom:treasure/golden_chest weapons,rare_loot
/chest add arena_chest_1 custom:pvp_weapons arena,weapons
```

---

#### 2️⃣ 刪除箱子記錄
```
/chest remove <名稱>
```
- 刪除指定名稱的箱子記錄
- 不會破壞實際的箱子，只是移除記錄

**示例：**
```bash
/chest remove dungeon_chest
```

---

#### 3️⃣ 列出所有箱子
```
/chest list
```
- 顯示所有已標記的箱子清單
- 包括箱子名稱、座標和 Loot Table ID

**輸出示例：**
```
=== 標記的箱子列表 ===
• dungeon_chest - 位置: [100, 64, 200] Loot Table: minecraft:chests/simple_dungeon
• treasure_box - 位置: [-50, 32, -100] Loot Table: custom:treasure/golden_chest
```

---

#### 4️⃣ 清空並套用 Loot Table
```
/chest clear <名稱> [seed]
```
- 清空指定箱子的所有內容
- 然後套用該箱子設定的 Loot Table
- `[seed]` - 可選的隨機種子（預設為 0）

**示例：**
```bash
/chest clear dungeon_chest
/chest clear treasure_box 12345
```

---

#### 5️⃣ 查看箱子詳細信息
```
/chest info <名稱>
```
- 顯示指定箱子的完整信息
- 包括名稱、座標、Loot Table、所屬組和箱子狀態

**輸出示例：**
```
=== 箱子信息 ===
名稱: dungeon_chest
位置: [100, 64, 200]
Loot Table: minecraft:chests/simple_dungeon
組: weapons, rare_loot
箱子狀態: 存在
```

---

#### 6️⃣ 批量註冊區域內的箱子
```
/chest region <前綴> <Loot Table ID> <組名>
```
- 使用金斧頭選取3D區域後，批量註冊區域內的所有箱子
- 在創造模式下，左鍵選取第一個點，右鍵選取第二個點
- 箱子名稱格式：`<前綴>_1`, `<前綴>_2`, `<前綴>_3` ...

**使用步驟：**
1. 切換到創造模式
2. 手持金斧頭
3. 左鍵點擊第一個角落（會顯示第一點座標）
4. 右鍵點擊對角的另一個角落
5. 執行 `/chest region` 指令

**示例：**
```bash
/chest region arena minecraft:chests/simple_dungeon arena_group
```

**輸出示例：**
```
成功註冊 10 個箱子到組 "arena_group"
前綴: arena, Loot Table: minecraft:chests/simple_dungeon
已清除區域選取
```

**使用場景：**
- 快速設置大型競技場的多個武器箱
- 批量配置地牢內的寶箱
- 一次性設置活動區域的獎勵箱

---

## 組管理功能

#### 7️⃣ 創建新組
```
/chest group create <組名>
```
- 創建一個新的空組
- 即使組內沒有箱子，組仍然存在

**示例：**
```bash
/chest group create weapons
/chest group create rare_loot
/chest group create arena_pvp
```

---

#### 8️⃣ 列出所有組
```
/chest group list
```
- 顯示系統中所有存在的組
- 包括顯式創建的組和通過箱子自動創建的組

**輸出示例：**
```
=== 所有組列表 ===
• weapons
• rare_loot
• arena
• pvp
• supplies
```

---

#### 9️⃣ 查看組內的所有箱子
```
/chest group members <組名>
```
- 顯示指定組內的所有箱子成員
- 包括箱子名稱和座標位置

**輸出示例：**
```
=== 組 "weapons" 的成員 ===
• dungeon_chest - 位置: [100, 64, 200]
• boss_chest - 位置: [250, 70, 300]
• arena_chest - 位置: [-100, 65, -150]
```

---

#### 🔟 將箱子加入組
```
/chest group add <箱子名稱> <組名>
```
- 將指定的箱子加入某個組
- 一個箱子可以同時屬於多個組
- 組會自動創建（如果不存在）

**示例：**
```bash
/chest group add dungeon_chest weapons
/chest group add treasure_box rare_loot
/chest group add boss_chest weapons
```

---

#### 1️⃣1️⃣ 刪除整個組
```
/chest group remove <組名>
```
- 刪除指定的組
- 會自動從所有箱子中移除該組的關聯
- 注意：不會刪除箱子本身，只是移除組的標籤

**示例：**
```bash
/chest group remove weapons
/chest group remove rare_loot
```

---

#### 1️⃣2️⃣ 快速加入組（站在箱子上）
```
/chest group join <組名>
```
- 站在箱子上方執行此命令
- 自動將腳下的箱子加入指定組
- 箱子必須已經使用 `/chest add` 註冊過

**示例：**
```bash
/chest group join weapons
```

---

#### 1️⃣3️⃣ 快速離開組（站在箱子上）
```
/chest group leave <組名>
```
- 站在箱子上方執行此命令
- 自動將腳下的箱子從指定組中移除

**示例：**
```bash
/chest group leave weapons
```

---

#### 1️⃣4️⃣ 批量重置組內所有箱子
```
/chest group clear <組名> [seed]
```
- 一次性清空組內所有箱子並重新套用各自的 Loot Table
- `[seed]` - 可選的隨機種子（預設為 0）
- 會顯示成功重置的箱子數量

**示例：**
```bash
/chest group clear arena
/chest group clear weapons 12345
```

**輸出示例：**
```
# 全部成功
已成功重置組 "arena" 內的所有 5 個箱子

# 部分成功（有箱子被破壞）
已重置組 "arena" 內的 3/5 個箱子 (部分箱子可能已被破壞)
```

---

## 快速參考表

### 最常用的指令速查

| 用途 | 指令 |
|------|------|
| **基礎操作** |
| 標記箱子 | `/chest add <名稱> <Loot Table>` |
| 標記+加組 | `/chest add <名稱> <Loot Table> <組名>` |
| 刪除記錄 | `/chest remove <名稱>` |
| 查看所有箱子 | `/chest list` |
| 查看箱子詳情 | `/chest info <名稱>` |
| 重置箱子 | `/chest clear <名稱>` |
| **組管理** |
| 創建組 | `/chest group create <組名>` |
| 查看所有組 | `/chest group list` |
| 查看組內成員 | `/chest group members <組名>` |
| 將箱子加入組 | `/chest group add <箱子> <組名>` |
| 刪除組 | `/chest group remove <組名>` |
| 重置組內所有箱子 | `/chest group clear <組名>` |
| **快速操作（站在箱子上）** |
| 加入組 | `/chest group join <組名>` |
| 離開組 | `/chest group leave <組名>` |
| **批量操作** |
| 區域批量註冊 | `/chest region <前綴> <Loot Table> <組名>` |

---

# 第二部分：自定義 Loot Table

## 自定義 Loot Table 功能

### 功能概述

自定義 Loot Table 功能允許你直接使用背包內的物品來創建自訂的戰利品表。每個物品可以設置不同的掉落權重，系統會根據權重隨機選擇物品放入箱子中。

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
```bash
# 最簡單：所有物品權重都是 1
/customloot create my_loot

# 指定權重：第 1 個物品權重 10，第 2 個權重 5
/customloot create my_loot 10,5

# 完整：為所有 27 格都指定權重
/customloot create treasure 100,50,50,25,25,10,10,5,5,3,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
```

#### 步驟 3：查看已創建的 Loot Table
```
/customloot list
```

**輸出示例：**
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

**輸出示例：**
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

---

## 自定義 Loot Table 指令

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

## 權重系統詳解

### 權重如何工作

權重決定了物品在隨機選擇時被選中的相對機率。

**例子：**
- 物品 A 權重 10
- 物品 B 權重 5
- 物品 C 權重 1
- 總權重 = 16

每次選擇時：
- 物品 A 被選中的機率 = 10/16 ≈ **62.5%**
- 物品 B 被選中的機率 = 5/16 ≈ **31.25%**
- 物品 C 被選中的機率 = 1/16 ≈ **6.25%**

### 常見權重配置

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

# 第三部分：實踐應用

## 使用場景和工作流程

### 場景 1：設置簡單的戰利品箱

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

---

### 場景 2：Boss 戰利品

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

---

### 場景 3：多層難度系統

```bash
# 簡單難度戰利品
/customloot create easy_loot    # 普通物品
/customloot create normal_loot  # 更好的物品
/customloot create hard_loot    # 稀有物品

# 根據難度應用不同的 Loot Table
/customloot apply dungeon_easy easy_loot
/customloot apply dungeon_normal normal_loot
/customloot apply dungeon_hard hard_loot
```

---

### 場景 4：活動獎勵箱

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

### 場景 5：PvP 競技場寶箱管理

#### 使用組系統快速設置

1. 先創建競技場組：
   ```bash
   /chest group create arena
   /chest group create weapons
   ```

2. 建造競技場並放置多個武器箱，站在箱子上執行：
   ```bash
   /chest add arena_chest_1 custom:pvp_weapons arena,weapons
   /chest add arena_chest_2 custom:pvp_weapons arena,weapons
   /chest add arena_chest_3 custom:pvp_weapons arena,weapons
   ```

3. 每場比賽後重置所有競技場箱子：
   ```bash
   # 一鍵重置整個組！
   /chest group clear arena
   
   # 或者使用自訂種子
   /chest group clear arena 99999
   ```

---

### 場景 6：多隊伍 PvP 競技場（進階）

**場景描述：**
- 紅隊：3 個武器箱 + 2 個補給箱
- 藍隊：3 個武器箱 + 2 個補給箱
- 中立區：2 個稀有寶箱

**設置步驟：**

1. **創建組結構：**
   ```bash
   /chest group create red_weapons
   /chest group create red_supplies
   /chest group create blue_weapons
   /chest group create blue_supplies
   /chest group create neutral_rare
   /chest group create all_weapons
   /chest group create all_supplies
   ```

2. **紅隊設置（站在各箱子上）：**
   ```bash
   /chest add red_w1 custom:pvp_weapons red_weapons,all_weapons
   /chest add red_w2 custom:pvp_weapons red_weapons,all_weapons
   /chest add red_w3 custom:pvp_weapons red_weapons,all_weapons
   /chest add red_s1 minecraft:chests/simple_dungeon red_supplies,all_supplies
   /chest add red_s2 minecraft:chests/simple_dungeon red_supplies,all_supplies
   ```

3. **藍隊設置：**
   ```bash
   /chest add blue_w1 custom:pvp_weapons blue_weapons,all_weapons
   /chest add blue_w2 custom:pvp_weapons blue_weapons,all_weapons
   /chest add blue_w3 custom:pvp_weapons blue_weapons,all_weapons
   /chest add blue_s1 minecraft:chests/simple_dungeon blue_supplies,all_supplies
   /chest add blue_s2 minecraft:chests/simple_dungeon blue_supplies,all_supplies
   ```

4. **中立區設置：**
   ```bash
   /chest add neutral_1 minecraft:chests/end_city_treasure neutral_rare
   /chest add neutral_2 minecraft:chests/end_city_treasure neutral_rare
   ```

5. **比賽流程：**
   ```bash
   # 比賽開始時重置所有武器
   /chest group clear all_weapons
   
   # 中場更新補給
   /chest group clear red_supplies
   /chest group clear blue_supplies
   
   # 比賽結束後重置一切
   /chest group clear all_weapons
   /chest group clear all_supplies
   /chest group clear neutral_rare
   ```

---

## 常見指令組合

```bash
# 快速標記箱子並加入組
/chest add my_chest minecraft:chests/simple_dungeon weapons

# 批量註冊區域內的箱子
/chest region arena minecraft:chests/simple_dungeon arena_group

# 創建組並加入箱子
/chest group create my_group
/chest group add my_chest my_group

# 查看組內所有箱子
/chest group members my_group

# 清空並重置組內所有箱子
/chest group clear my_group

# 創建自定義 Loot Table 並應用
/customloot create my_loot
/customloot apply my_chest my_loot

# 為整個組應用相同的 Loot Table
/customloot create dungeon_loot
/customloot apply dungeon_chest_1 dungeon_loot
/customloot apply dungeon_chest_2 dungeon_loot
/customloot apply dungeon_chest_3 dungeon_loot
/chest group clear dungeon_chests
```

---

## 進階技巧

### 技巧 1：使用隨機種子獲得不同的戰利品
```bash
# 每次使用不同的種子會生成不同的物品
/chest clear my_chest 123
/chest clear my_chest 456
/chest clear my_chest 789
```

### 技巧 2：使用物品堆疊創建加權效果
```bash
# 背包配置：
# 格 1: 鑽石劍 x1
# 格 2: 金錠 x64  (堆疊)

/customloot create test
```

### 技巧 3：為同一箱子創建多個別名
```bash
# 創建不同的組名來標識同一箱子的不同用途
/chest group create arena_weapons
/chest group create rare_weapons

# 同一箱子加入多個組
/chest group add my_chest arena_weapons
/chest group add my_chest rare_weapons
```

### 技巧 4：批量重置特定類型的箱子
```bash
# 先查看有哪些組
/chest group list

# 然後按組重置
/chest group clear supplies
/chest group clear rare_items
```

### 技巧 5：使用區域註冊批量設置
```bash
# 1. 手持金斧頭，切換創造模式
# 2. 左鍵點擊區域第一角
# 3. 右鍵點擊對角
# 4. 執行命令（會自動生成名稱）
/chest region arena minecraft:chests/simple_dungeon arena_pvp
```

### 技巧 6：檢查箱子是否仍然存在
```bash
/chest info my_chest
# 查看「箱子狀態」是否為「存在」
```

### 技巧 7：頻繁更新 Loot Table
```bash
# 刪除舊 Loot Table
/customloot delete old_loot

# 重新準備背包

# 創建新 Loot Table（相同名稱）
/customloot create old_loot
```

### 技巧 8：快速測試權重效果
```bash
# 應用 Loot Table 到箱子
/customloot apply test_chest my_table

# 多次清空並查看結果
/chest clear test_chest 1
/chest clear test_chest 2
/chest clear test_chest 3
```

---

# 第四部分：參考資料

## 常見 Minecraft Loot Table ID

### 原版 Loot Table
- `minecraft:chests/simple_dungeon` - 簡單地牢
- `minecraft:chests/abandoned_mineshaft` - 廢棄礦井
- `minecraft:chests/desert_pyramid` - 沙漠金字塔
- `minecraft:chests/end_city_treasure` - 末地城寶藏
- `minecraft:chests/nether_bridge` - 地獄橋
- `minecraft:chests/stronghold_corridor` - 要塞走廊
- `minecraft:chests/stronghold_crossing` - 要塞交叉口
- `minecraft:chests/stronghold_library` - 要塞圖書館
- `minecraft:chests/woodland_mansion` - 林地大宅

### 自訂 Loot Table
- `tacticalguns:` 前綴的 Loot Table（如果有 Tactical Guns 模組）
- 自己創建的自訂 Loot Table
- `CUSTOM_LOOT:<表名稱>` - 自定義 Loot Table 格式

---

## 技術細節

### 文件位置
```
world/marked_chests.nbt          # 箱子和組的數據
world/custom_loot_tables.nbt     # 自定義 Loot Table 的數據
```

### 支援的方塊
目前系統只支持標準的 Minecraft 箱子（`minecraft:chest`）

### 權限
- 需要 OP 權限或相應的命令權限才能執行這些指令

### 多維度支援
- 系統支援多個維度（主世界、地獄、末地）
- 每個維度的數據獨立保存

### 箱子功能特性
- 一個箱子可以同時屬於多個組
- 箱子名稱唯一性（每個箱子需要不同的名稱）
- 刪除箱子會自動清除其所有組關聯
- 箱子位置被破壞時會標記為「已破壞」

### 組功能特性
- 組名稱沒有限制（建議使用英文和數字）
- 可以創建空組（不包含任何箱子）
- 組會在箱子加入時自動創建
- 刪除組不會刪除箱子，只是移除組的標籤

### 自定義 Loot Table 特性
- 最多支持 27 種不同的物品
- 權重必須是正整數（1 或以上）
- 支持物品堆疊（堆疊大小會被記錄）
- 多個箱子可以指向同一個 Loot Table

---

## 故障排除

### 問題：「你下方沒有箱子」
**解決方案：**
- 確保你站在箱子的正上方
- 確保下方的方塊是 Minecraft 標準箱子

### 問題：「箱子已存在」
**解決方案：**
- 使用不同的名稱
- 或先用 `/chest remove <名稱>` 刪除舊記錄

### 問題：「無法套用 Loot Table」
**解決方案：**
- 檢查 Loot Table ID 是否正確
- 確保 Loot Table 文件存在於對應的 datapack 或 resourcepack 中

### 問題：「找不到箱子」
**解決方案：**
- 使用 `/chest list` 查看所有已標記的箱子
- 檢查箱子名稱是否正確（區分大小寫）

### 自定義 Loot Table 常見問題

#### Q: 為什麼創建自定義 Loot Table 失敗？
**A:** 
- 背包前 27 格沒有任何物品
- 自定義 Loot Table 名稱已存在
- 權重格式不正確（應為逗號分隔的數字）

#### Q: 物品會重複出現嗎？
**A:** 不會。系統從背包中複製物品信息，原背包中的物品不會被消耗或移動。

#### Q: 權重可以是小數嗎？
**A:** 不行，權重必須是正整數（1, 2, 3...）。

#### Q: 能否同時在多個箱子中使用同一個 Loot Table？
**A:** 當然可以！多個箱子可以指向同一個 Loot Table。

---

## 開發者信息

### 類別結構
- `ChestData.java` - 單個箱子的數據模型（包含組資訊）
- `ChestManager.java` - 箱子和組的核心管理邏輯
- `CustomLootTable.java` - 自定義 Loot Table 數據模型
- `CustomLootTableManager.java` - 自定義 Loot Table 管理邏輯
- `ChestManagerHolder.java` - 全局 Manager 容器
- `ChestCommands.java` - 所有指令的註冊和處理
- `ChestEventListener.java` - 服務器事件監聽器
- `RegionSelector.java` - 區域選擇工具
- `RegionSelectionListener.java` - 區域選擇事件監聽器

### 事件集成
- 在 `ServerStartingEvent` 時初始化
- 在 `ServerStoppingEvent` 時保存數據
- 在 `RegisterCommandsEvent` 時註冊指令

### 箱子管理 API
- `ChestManager.createGroup(String groupName)` - 創建新組
- `ChestManager.addChestToGroup(String chestName, String group)` - 將箱子加入組
- `ChestManager.removeChestFromGroup(String chestName, String group)` - 將箱子從組移除
- `ChestManager.getChestsByGroup(String group)` - 取得組內所有箱子

### 自定義 Loot Table API
- `CustomLootTable.addItem(ItemStack itemStack, long weight)` - 添加物品和權重
- `CustomLootTable.getRandomItem(Random random)` - 隨機選擇物品
- `CustomLootTableManager.createCustomLootTable(String name, CustomLootTable table)` - 創建 Loot Table
- `CustomLootTableManager.getCustomLootTable(String name)` - 獲取 Loot Table

---

## 更新日誌

### v1.6.0 (最新)
- ✅ **新增功能：自定義 Loot Table**
- ✅ 支持从背包的 27 格物品创建 Loot Table
- ✅ 支持为每个物品设置权重
- ✅ 新增 5 个自定義 Loot Table 相關指令
- ✅ 修改 ChestManager 支持自定义 Loot Table

### v1.5.0
- ✅ **新增功能：區域批量註冊箱子**
- ✅ 使用金斧頭選取3D區域
- ✅ 自動生成序號命名

### v1.4.0
- ✅ **新增功能：批量重置組內所有箱子**
- ✅ 新增 `/chest group clear` 指令

### v1.3.0
- ✅ **新增功能：創建空組**

### v1.2.0
- ✅ **新增功能：創建箱子時直接加入組**

### v1.1.0
- ✅ **新增組管理功能**

### v1.0.0 (初版)
- ✅ 新增箱子管理系統

---

**文檔完成日期：2025-11-28**
**模組版本：v1.6.0**
**Minecraft 版本：1.20.1**
**Forge 版本：47.4.12**

---

