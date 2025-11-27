# 箱子管理系統 (Chest Management System)

## 📑 目錄
1. [功能概述](#功能概述)
2. [快速開始](#快速開始)
3. [指令列表](#指令列表)
4. [使用工作流程](#使用工作流程)
5. [常見 Loot Table](#常見-minecraft-loot-table-id)
6. [常見指令組合](#常見指令組合)
7. [數據保存](#數據保存)
8. [技術細節](#技術細節)
9. [故障排除](#故障排除)
10. [開發者信息](#開發者信息)
11. [更新日誌](#更新日誌)

---

## 功能概述
這個模組提供了一套完整的箱子管理指令系統，讓你可以：
- ✅ 標記和記錄箱子位置
- ✅ 為箱子指定 Loot Table
- ✅ 清空並重新套用 Loot Table
- ✅ 查看所有標記的箱子
- ✅ 刪除箱子記錄
- ✅ **新增：將箱子加入組進行批量管理**
- ✅ **新增：創建和管理組**
- ✅ **新增：查看所有組和組內成員**
- ✅ **新增：批量註冊區域內的箱子**

---

## 快速開始

### 最簡單的用法（30秒上手）
```
# 1. 站在箱子上方
# 2. 執行指令
/chest add my_chest minecraft:chests/simple_dungeon

# 3. 之後想要重新生成寶藏時
/chest clear my_chest
```

### 使用組進行組織（推薦）
```
# 1. 創建組
/chest group create weapons

# 2. 站在箱子上，直接加入組
/chest add arena_chest_1 custom:pvp_weapons weapons

# 3. 查看組內所有箱子
/chest group members weapons

# 4. 比賽結束後一鍵重置所有箱子
/chest group clear weapons
```

---

## 指令列表

### 基本箱子管理

#### 1. 新增箱子
```
/chest add <名稱> <Loot Table ID> [組名1,組名2,...]
```
**說明：**
- 在你腳下的箱子上執行此命令
- `<名稱>` 是你要給這個箱子的標識名稱（不能重複）
- `<Loot Table ID>` 是 Loot Table 的完整路徑（例如：`minecraft:chests/simple_dungeon`）
- `[組名1,組名2,...]` 是可選參數，可以在新增箱子時直接將其加入一個或多個組
- 多個組名用逗號分隔（例如：`weapons,rare_loot,arena`）

**示例：**
```
/chest add dungeon_chest minecraft:chests/simple_dungeon
/chest add treasure_box custom:treasure/golden_chest weapons,rare_loot
/chest add arena_chest_1 custom:pvp_weapons arena,weapons
```

**組名格式：**
- 支援一次加入多個組，用逗號分隔
- 組名會自動去除前後空格
- 如果某個組名為空或加入失敗，會繼續處理其他組

---

#### 2. 刪除箱子記錄
```
/chest remove <名稱>
```
**說明：**
- 刪除指定名稱的箱子記錄
- 這不會破壞實際的箱子，只是移除記錄

**示例：**
```
/chest remove dungeon_chest
```

---

#### 3. 列出所有箱子
```
/chest list
```
**說明：**
- 顯示所有已標記的箱子清單
- 包括箱子名稱、座標和 Loot Table ID

**示例：**
```
/chest list
```

**輸出示例：**
```
=== 標記的箱子列表 ===
• dungeon_chest - 位置: [100, 64, 200] Loot Table: minecraft:chests/simple_dungeon
• treasure_box - 位置: [-50, 32, -100] Loot Table: custom:treasure/golden_chest
```

---

#### 4. 清空並套用 Loot Table
```
/chest clear <名稱> [seed]
```
**說明：**
- 清空指定箱子的所有內容
- 然後套用該箱子設定的 Loot Table
- `[seed]` 是可選的隨機種子（預設為 0），用來控制 Loot Table 生成結果的隨機性

**示例：**
```
/chest clear dungeon_chest
/chest clear treasure_box 12345
```

---

#### 5. 查看箱子詳細信息
```
/chest info <名稱>
```
**說明：**
- 顯示指定箱子的完整信息
- 包括名稱、座標、Loot Table、所屬組和箱子是否仍然存在

**示例：**
```
/chest info dungeon_chest
```

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

#### 6. 🆕 批量註冊區域內的箱子
```
/chest region <前綴> <Loot Table ID> <組名>
```
**說明：**
- 使用金斧頭選取3D區域後，批量註冊區域內的所有箱子
- 在創造模式下，左鍵選取第一個點，右鍵選取第二個點
- 所有箱子會使用相同的 Loot Table 並加入同一個組
- 箱子名稱格式：`<前綴>_1`, `<前綴>_2`, `<前綴>_3` ...

**使用步驟：**
1. 切換到創造模式
2. 手持金斧頭
3. 左鍵點擊第一個角落（會顯示第一點座標）
4. 右鍵點擊對角的另一個角落（會顯示選取區域資訊）
5. 執行 `/chest region` 指令

**示例：**
```
/chest region arena minecraft:chests/simple_dungeon arena_group
```

**輸出示例：**
```
成功註冊 10 個箱子到組 "arena_group"
前綴: arena, Loot Table: minecraft:chests/simple_dungeon
已清除區域選取

# 生成的箱子名稱：
# arena_1, arena_2, arena_3, ... arena_10
```

**使用場景：**
- 快速設置大型競技場的多個武器箱
- 批量配置地牢內的寶箱
- 一次性設置活動區域的獎勵箱

---

### 🆕 組管理功能

#### 7. 創建新組
```
/chest group create <組名>
```
**說明：**
- 創建一個新的空組
- 組創建後可以用來組織箱子
- 即使組內沒有箱子，組仍然存在

**示例：**
```
/chest group create weapons
/chest group create rare_loot
/chest group create arena_pvp
```

**使用場景：**
- 提前規劃箱子分組結構
- 創建組後再逐步加入箱子

---

#### 8. 列出所有組
```
/chest group list
```
**說明：**
- 顯示系統中所有存在的組
- 包括顯式創建的組和通過箱子自動創建的組
- 如果沒有任何組，會顯示相應訊息

**示例：**
```
/chest group list
```

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

#### 9. 查看組內的所有箱子
```
/chest group members <組名>
```
**說明：**
- 顯示指定組內的所有箱子成員
- 包括箱子名稱和座標位置
- 如果組為空，會顯示相應訊息

**示例：**
```
/chest group members weapons
```

**輸出示例：**
```
=== 組 "weapons" 的成員 ===
• dungeon_chest - 位置: [100, 64, 200]
• boss_chest - 位置: [250, 70, 300]
• arena_chest - 位置: [-100, 65, -150]
```

---

#### 10. 將箱子加入組
```
/chest group add <箱子名稱> <組名>
```
**說明：**
- 將指定的箱子加入某個組
- 一個箱子可以同時屬於多個組
- 如果箱子已在該組中，會顯示錯誤訊息
- 組會自動創建（如果不存在）

**示例：**
```
/chest group add dungeon_chest weapons
/chest group add treasure_box rare_loot
/chest group add boss_chest weapons
```

---

#### 11. 刪除整個組
```
/chest group remove <組名>
```
**說明：**
- 刪除指定的組
- 會自動從所有箱子中移除該組的關聯
- 如果組不存在，會顯示錯誤訊息

**示例：**
```
/chest group remove weapons
/chest group remove rare_loot
```

**注意：**
- 刪除組不會刪除箱子本身，只是移除組的標籤
- 刪除組後會顯示受影響的箱子數量

---

#### 12. 快速加入組（站在箱子上）
```
/chest group join <組名>
```
**說明：**
- 站在箱子上方執行此命令
- 自動將腳下的箱子加入指定組
- 箱子必須已經使用 `/chest add` 註冊過
- 組會自動創建（如果不存在）

**示例：**
```
/chest group join weapons
```

**使用場景：**
- 快速操作，不需要記住箱子名稱
- 適合在建造時快速分組

---

#### 13. 快速離開組（站在箱子上）
```
/chest group leave <組名>
```
**說明：**
- 站在箱子上方執行此命令
- 自動將腳下的箱子從指定組中移除

**示例：**
```
/chest group leave weapons
```

---

#### 14. 批量重置組內所有箱子
```
/chest group clear <組名> [seed]
```
**說明：**
- 一次性清空組內所有箱子並重新套用各自的 Loot Table
- 類似於 `/chest clear` 但對整個組生效
- `[seed]` 是可選的隨機種子（預設為 0），用來控制 Loot Table 生成結果的隨機性
- 會顯示成功重置的箱子數量

**示例：**
```
/chest group clear arena
/chest group clear weapons 12345
```

**輸出示例：**
```
# 全部成功
已成功重置組 "arena" 內的所有 5 個箱子

# 部分成功（有箱子被破壞）
已重置組 "arena" 內的 3/5 個箱子 (部分箱子可能已被破壞)

# 失敗
組 "arena" 不存在或沒有成員
```

**使用場景：**
- PvP 競技場比賽結束後一鍵重置所有武器箱
- 定時活動結束後批量重置獎勵箱
- 地牢副本重置

---

## 數據保存
- 所有箱子記錄和組資訊會自動保存到 `world/marked_chests.nbt` 文件
- 每次新增、刪除箱子或修改組時都會自動保存
- 顯式創建的組（使用 `/chest group create`）會被永久保存
- 服務器停止時也會保存所有數據
- 重啟服務器後，所有記錄和組關聯會自動加載

---

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
如果你安裝了其他模組，可以使用模組提供的 Loot Table：
- `tacticalguns:` 前綴的 Loot Table（如果有 Tactical Guns 模組）
- 你自己創建的自訂 Loot Table

---

## 常見指令組合
- **快速標記箱子並加入組：**
  ```
  /chest add my_chest minecraft:chests/simple_dungeon weapons
  ```

- **批量註冊區域內的箱子：**
  ```
  /chest region arena minecraft:chests/simple_dungeon arena_group
  ```

- **創建組並加入箱子：**
  ```
  /chest group create my_group
  /chest group add my_chest my_group
  ```

- **查看組內所有箱子：**
  ```
  /chest group members my_group
  ```

- **清空並重置組內所有箱子：**
  ```
  /chest group clear my_group
  ```

---

## 使用工作流程

### 場景 1：設置地牢寶箱
1. 建造地牢並放置箱子
2. 站在箱子上方執行：
   ```
   /chest add dungeon_treasure minecraft:chests/simple_dungeon
   ```
3. 之後如果想重新生成寶藏，執行：
   ```
   /chest clear dungeon_treasure
   ```

### 場景 2：製作多個自訂寶箱
1. 創建自訂 Loot Table（放在 `resourcepack` 或 datapack）
2. 標記多個箱子：
   ```
   /chest add boss_loot custom:boss_drops
   /chest add quest_reward custom:quest_rewards
   ```
3. 查看所有寶箱：
   ```
   /chest list
   ```
4. 需要時重新生成：
   ```
   /chest clear boss_loot 999
   ```

### 🆕 場景 3：使用組管理系統

#### 方法一：先創建組結構（推薦用於大型項目）
1. 提前規劃並創建所有需要的組：
   ```
   /chest group create weapons
   /chest group create rare_loot
   /chest group create arena
   /chest group create supplies
   ```

2. 查看所有組：
   ```
   /chest group list
   ```

3. 創建箱子時直接加入組：
   ```
   站在第一個箱子上：
   /chest add chest1 minecraft:chests/simple_dungeon weapons
   
   站在第二個箱子上：
   /chest add chest2 minecraft:chests/simple_dungeon weapons,rare_loot
   ```

#### 方法二：邊建造邊分組（推薦用於小型項目）
1. 使用新的一步到位語法註冊箱子並加入組：
   ```
   站在第一個箱子上：
   /chest add chest1 minecraft:chests/simple_dungeon weapons
   
   站在第二個箱子上：
   /chest add chest2 minecraft:chests/simple_dungeon weapons
   
   站在第三個箱子上：
   /chest add chest3 minecraft:chests/simple_dungeon weapons,rare_loot
   ```

2. 查看某个組的所有箱子：
   ```
   /chest group members weapons
   ```

3. 如果需要刪除整個組：
   ```
   /chest group remove weapons
   ```

4. 快速操作（站在箱子上）：
   ```
   /chest group join events
   /chest group leave weapons
   ```

### 🆕 場景 4：PvP 競技場寶箱管理

#### 使用組系統快速設置
1. 先創建競技場組：
   ```
   /chest group create arena
   /chest group create weapons
   ```

2. 建造競技場並放置多個武器箱，站在箱子上執行：
   ```
   /chest add arena_chest_1 custom:pvp_weapons arena,weapons
   /chest add arena_chest_2 custom:pvp_weapons arena,weapons
   /chest add arena_chest_3 custom:pvp_weapons arena,weapons
   ```

3. 查看競技場配置：
   ```
   /chest group list
   /chest group members arena
   /chest group members weapons
   ```

4. 每場比賽後重置所有競技場箱子（使用批量重置）：
   ```
   # 一鍵重置整個組！
   /chest group clear arena
   
   # 或者使用自訂種子
   /chest group clear arena 99999
   ```

### 🆕 場景 5：多組管理進階應用

在某些複雜場景中，你可能需要同時管理多個相互關聯的組。例如：一個 PvP 競技場既要追蹤「所有武器」，也要追蹤「各隊伍的專屬武器」。

#### 實際案例：多隊伍 PvP 競技場

**場景描述：**
- 紅隊：3 個武器箱 + 2 個補給箱
- 藍隊：3 個武器箱 + 2 個補給箱
- 中立區：2 個稀有寶箱

**設置步驟：**

1. **創建組結構：**
   ```
   /chest group create red_weapons
   /chest group create red_supplies
   /chest group create blue_weapons
   /chest group create blue_supplies
   /chest group create neutral_rare
   /chest group create all_weapons
   /chest group create all_supplies
   ```

2. **紅隊設置（站在各箱子上）：**
   ```
   /chest add red_w1 custom:pvp_weapons red_weapons,all_weapons
   /chest add red_w2 custom:pvp_weapons red_weapons,all_weapons
   /chest add red_w3 custom:pvp_weapons red_weapons,all_weapons
   /chest add red_s1 minecraft:chests/simple_dungeon red_supplies,all_supplies
   /chest add red_s2 minecraft:chests/simple_dungeon red_supplies,all_supplies
   ```

3. **藍隊設置：**
   ```
   /chest add blue_w1 custom:pvp_weapons blue_weapons,all_weapons
   /chest add blue_w2 custom:pvp_weapons blue_weapons,all_weapons
   /chest add blue_w3 custom:pvp_weapons blue_weapons,all_weapons
   /chest add blue_s1 minecraft:chests/simple_dungeon blue_supplies,all_supplies
   /chest add blue_s2 minecraft:chests/simple_dungeon blue_supplies,all_supplies
   ```

4. **中立區設置：**
   ```
   /chest add neutral_1 minecraft:chests/end_city_treasure neutral_rare
   /chest add neutral_2 minecraft:chests/end_city_treasure neutral_rare
   ```

5. **比賽前查看：**
   ```
   /chest group members red_weapons
   /chest group members all_supplies
   ```

6. **比賽流程：**
   ```
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

**優勢：**
- 一次指令重置多個隊伍的特定物品
- 清晰的組織結構便於維護
- 靈活的分類方式（可按隊伍分，也可按物品類型分）

#### 進階提示：

- **同時屬於多個組：** 一個箱子可以同時屬於 `red_weapons` 和 `all_weapons`，方便按不同維度查詢
- **組的命名約定：** 建議使用 `team_category` 的格式（如 `red_weapons`、`blue_supplies`），便於理解層級關係
- **快速組合查詢：** 用 `/chest group members` 查看任何組的詳細內容

---

## 數據保存
- 所有箱子記錄和組資訊會自動保存到 `world/marked_chests.nbt` 文件
- 每次新增、刪除箱子或修改組時都會自動保存
- 顯式創建的組（使用 `/chest group create`）會被永久保存
- 服務器停止時也會保存所有數據
- 重啟服務器後，所有記錄和組關聯會自動加載

---

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
如果你安裝了其他模組，可以使用模組提供的 Loot Table：
- `tacticalguns:` 前綴的 Loot Table（如果有 Tactical Guns 模組）
- 你自己創建的自訂 Loot Table

---

## 常見指令組合
- **快速標記箱子並加入組：**
  ```
  /chest add my_chest minecraft:chests/simple_dungeon weapons
  ```

- **批量註冊區域內的箱子：**
  ```
  /chest region arena minecraft:chests/simple_dungeon arena_group
  ```

- **創建組並加入箱子：**
  ```
  /chest group create my_group
  /chest group add my_chest my_group
  ```

- **查看組內所有箱子：**
  ```
  /chest group members my_group
  ```

- **清空並重置組內所有箱子：**
  ```
  /chest group clear my_group
  ```

---

## 💡 快速參考表

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

### 推薦命名約定

為了便於管理，建議按以下方式命名：

```
# 按位置
dungeon_chest_1, dungeon_chest_2, boss_room_chest

# 按隊伍
red_team_chest_1, blue_team_chest_1

# 按物品類型
weapon_chest, armor_chest, supply_chest

# 按難度
easy_loot, medium_loot, hard_loot

# 組名
weapons, armor, supplies, rare_items
```

### 常見工作流程速記

#### 新手模式：單個箱子
```bash
# 1. 標記
/chest add my_chest minecraft:chests/simple_dungeon

# 2. 重置（需要時）
/chest clear my_chest
```

#### 進階模式：多個箱子分組
```bash
# 1. 創建組
/chest group create weapons

# 2. 標記並加入組
/chest add chest1 minecraft:chests/simple_dungeon weapons
/chest add chest2 minecraft:chests/simple_dungeon weapons

# 3. 查看
/chest group members weapons

# 4. 一鍵重置
/chest group clear weapons
```

#### 專家模式：大型項目
```bash
# 1. 規劃組結構
/chest group create red_weapons
/chest group create blue_weapons
/chest group create all_weapons

# 2. 區域註冊（選取區域 > 執行）
/chest region red custom:pvp_weapons red_weapons

# 3. 按多維度查詢
/chest group members all_weapons
/chest group members red_weapons

# 4. 靈活重置
/chest group clear all_weapons  # 重置所有
/chest group clear red_weapons   # 只重置紅隊
```

---

## ⚙️ 進階技巧

### 技巧 1：使用隨機種子獲得不同的戰利品
```
# 每次使用不同的種子會生成不同的物品
/chest clear my_chest 123
/chest clear my_chest 456
/chest clear my_chest 789
```

### 技巧 2：為同一箱子創建多個別名
儘管系統要求箱子名稱唯一，但你可以通過多個組來實現「別名」效果：
```
# 創建不同的組名來標識同一箱子的不同用途
/chest group create arena_weapons
/chest group create rare_weapons

# 同一箱子加入多個組
/chest group add my_chest arena_weapons
/chest group add my_chest rare_weapons

# 查詢時可以按不同維度查看
/chest group members arena_weapons
/chest group members rare_weapons
```

### 技巧 3：批量重置特定類型的箱子
```
# 先查看有哪些組
/chest group list

# 然後按組重置
/chest group clear supplies
/chest group clear rare_items
```

### 技巧 4：使用區域註冊批量設置
適合大型競技場或地牢：
```bash
# 1. 手持金斧頭，切換創造模式
# 2. 左鍵點擊區域第一角
# 3. 右鍵點擊對角
# 4. 執行命令（會自動生成名稱）
/chest region arena minecraft:chests/simple_dungeon arena_pvp
```

### 技巧 5：檢查箱子是否仍然存在
有時候箱子可能被破壞了，可以用這個命令檢查：
```
/chest info my_chest
# 查看「箱子狀態」是否為「存在」
```

---

## 🎯 使用場景最佳實踐

### 場景：PvP 競技場
```bash
# 設置階段
/chest group create arena_pvp
/chest region arena custom:pvp_weapons arena_pvp

# 比賽流程
/chest list                      # 查看所有配置
/chest group members arena_pvp   # 查看競技場所有箱子
/chest group clear arena_pvp     # 比賽前重置

# 比賽中
/chest info arena_1   # 如果需要檢查單個箱子
```

### 場景：地牢副本
```bash
# 設置階段
/chest group create dungeon_treasure
/chest add boss_chest minecraft:chests/end_city_treasure dungeon_treasure
/chest add side_chest minecraft:chests/simple_dungeon dungeon_treasure

# 副本流程
/chest group clear dungeon_treasure   # 副本開始前重置
# 玩家進行副本...
/chest group clear dungeon_treasure   # 副本結束後重置（可選）
```

### 場景：活動管理
```bash
# 設置多個活動
/chest group create event_halloween
/chest group create event_christmas

# 添加不同的箱子
/chest add halloween_1 custom:halloween_loot event_halloween
/chest add christmas_1 custom:christmas_loot event_christmas

# 活動期間選擇性重置
/chest group clear event_halloween    # 萬聖節活動重置
# 不影響其他活動
```

---

## 技術細節

### 文件位置
```
world/marked_chests.nbt
```

### 支援的方塊
目前系統只支持標準的 Minecraft 箱子（`minecraft:chest`）

### 權限
- 需要 OP 權限或相應的命令權限才能執行這些指令

### 多維度支援
- 系統支援多個維度（主世界、地獄、末地）
- 每個維度的數據獨立保存

### 組功能特性
- 一個箱子可以同時屬於多個組
- 組名稱沒有限制（建議使用英文和數字）
- 刪除箱子會自動清除其所有組關聯
- 組資訊與箱子資料一起保存在 NBT 文件中
- 可以創建空組（不包含任何箱子）
- 組會在箱子加入時自動創建

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

### 🆕 問題：「該箱子未被標記」（使用 group join/leave 時）
**解決方案：**
- 先使用 `/chest add <名稱> <Loot Table>` 註冊箱子
- 確認站在正確的箱子上方

### 🆕 問題：「箱子已在該組」
**解決方案：**
- 箱子已經在這個組中，無需重複加入
- 使用 `/chest info <箱子名稱>` 查看箱子當前所屬的組

### 🆕 問題：「無法創建組，可能是因為組已存在」
**解決方案：**
- 使用 `/chest group list` 查看所有現有的組
- 選擇一個不同的組名稱

---

## 開發者信息

### 類別結構
- `ChestData.java` - 單個箱子的數據模型（包含組資訊）
- `ChestManager.java` - 核心管理邏輯（包含組管理 API）
- `ChestManagerHolder.java` - 全局 Manager 容器
- `ChestCommands.java` - 指令註冊和處理（包含組指令）
- `ChestEventListener.java` - 事件監聽器

### 事件集成
- 在 `ServerStartingEvent` 時初始化
- 在 `ServerStoppingEvent` 時保存數據
- 在 `RegisterCommandsEvent` 時註冊指令

### 組功能 API
- `ChestData.addGroup(String group)` - 加入組
- `ChestData.removeGroup(String group)` - 離開組
- `ChestData.hasGroup(String group)` - 檢查是否屬於某組
- `ChestData.getGroups()` - 取得所有組
- `ChestManager.createGroup(String groupName)` - 創建新組
- `ChestManager.getAllGroups()` - 取得所有組
- `ChestManager.groupExists(String groupName)` - 檢查組是否存在
- `ChestManager.addChestToGroup(String chestName, String group)` - 將箱子加入組
- `ChestManager.removeChestFromGroup(String chestName, String group)` - 將箱子從組移除
- `ChestManager.getChestsByGroup(String group)` - 取得組內所有箱子

---

## 更新日誌

### v1.5.0 (最新)
- ✅ **新增功能：區域批量註冊箱子**
- ✅ 新增 `/chest region <前綴> <Loot Table ID> <組名>` 指令
- ✅ 使用金斧頭選取3D區域（左鍵第一點，右鍵第二點）
- ✅ 一次性註冊區域內所有箱子並加入同一組
- ✅ 自動生成序號命名（前綴_1, 前綴_2...）
- ✅ 完美適合大型競技場、地牢等場景
- ✅ 新增 `RegionSelector` 和 `RegionSelectionListener` 類

### v1.4.0
- ✅ **新增功能：批量重置組內所有箱子**
- ✅ 新增 `/chest group clear <組名> [seed]` 指令
- ✅ 可以一鍵重置整個組內的所有箱子並重新套用 Loot Table
- ✅ 支援自訂隨機種子
- ✅ 顯示重置成功/失敗的箱子數量
- ✅ 完美適用於 PvP 競技場、活動箱子等場景

### v1.3.0
- ✅ **新增功能：創建空組**
- ✅ 新增 `/chest group create <name>` 指令
- ✅ 修改 `/chest group list` 功能為列出所有組（而非箱子的組）
- ✅ `/chest group members <group>` 保持原功能，查看指定組的成員
- ✅ 組資訊持久化保存，包括空組
- ✅ 更完善的組管理系統

### v1.2.0
- ✅ **新增功能：創建箱子時直接加入組**
- ✅ `/chest add` 指令現在支援可選的組參數
- ✅ 支援用逗號分隔一次加入多個組
- ✅ 組名會自動去除前後空格
- ✅ 簡化工作流程，無需先創建再加入組

### v1.1.0
- ✅ **新增組管理功能**
- ✅ 支援 `/chest group add/remove/list/members/join/leave` 指令
- ✅ 一個箱子可同時屬於多個組
- ✅ 組資訊自動持久化保存
- ✅ 更新 `/chest info` 顯示組資訊

### v1.0.0 (初版)
- ✅ 新增箱子管理系統
- ✅ 支援 add/remove/list/clear/info 指令
- ✅ NBT 文件保存和加載
- ✅ 多維度支援
