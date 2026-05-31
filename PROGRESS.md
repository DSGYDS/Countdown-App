# 倒数日 App 开发进度 - 2026-05-30

## 项目路径
- 项目目录：`E:\CountdownApp`
- 最新备份：`C:\Users\SilverWhite\CountdownApp_backup_20260530_2358`

---

## 当前完成状态

### P0 模块（核心滑动交互）✅ 已完成

| 模块 | 状态 | 说明 |
|------|------|------|
| 模块一：列表基础 | ✅ 已有 | 普通卡片+置顶卡片📌，高度未区分 |
| 模块二：左滑删除 | ✅ 已有 | 红按钮+直接删除，阈值40dp |
| 模块三：右滑置顶 | ✅ 已有 | 天蓝按钮+置顶/取消置顶，5个上限 |
| 模块四：展开交互 | ✅ 已有 | 点击非按钮只关滑动不开详情 |
| 模块五：多选编辑模式 | ✅ 已有 | 编辑按钮+复选框+全选+批量删除 |

### P1 模块（农历+样式）

| 模块 | 状态 | 说明 |
|------|------|------|
| 模块六：农历支持 | ⚠️ 框架完成 | 库已装(lunar 1.7.4)，Dialog有Tab切换，日期双向转换未集成 |
| 模块七：置顶大卡片样式 | ⚠️ 基础完成 | 📌图标有，2倍高度有，显示更多信息有 |

### P2 模块

| 模块 | 状态 | 说明 |
|------|------|------|
| 模块八：导出图片 | ❌ 未做 | 无 |

---

## 当前代码关键文件

### 数据层
- `data/entity/Event.kt` — Event实体，字段：id/name/targetDate/backgroundImagePath/createdAt/recurringMonth/recurringDay/isPinned/pinnedTime/calendarType/lunarYear/lunarMonth/lunarDay/isLeapMonth/isRepeatYearly
- `data/db/AppDatabase.kt` — v2，fallbackToDestructiveMigration()
- `data/db/EventDao.kt` — getAllEvents(含排序)/insert/delete/deleteEventsByIds/updatePinStatus/getPinnedCount/getPinnedEventIds
- `data/repository/EventRepository.kt` — 对应DAO方法封装

### UI层
- `ui/components/SwipeToDelete.kt` — 核心滑动组件
  - 左滑（dragOffset<0）→ 右边露红按钮 → 文字"删除" → 点击直接删
  - 右滑（dragOffset>0）→ 左边露天蓝按钮 → 文字"置顶/取消置顶" → 点击置顶
  - 阈值40dp，按钮宽度80dp
  - `content: @Composable (Event, Boolean, (Event) -> Unit) -> Unit`
- `ui/components/EventCard.kt` — 事件卡片，置顶卡片2倍高(180dp)，普通80dp，📌图标
- `ui/components/AddEventDialog.kt` — 添加弹窗，阳历/农历Tab切换，年/月/日滚轮选择器

### ViewModel
- `ui/viewmodel/MainViewModel.kt` — 全部事件Flow，editMode状态(selectedEventIds)，enterEditMode/exitEditMode/toggleSelection/selectAll/deselectAll/deleteSelectedEvents/togglePin/updateBackground/updateTargetDate

### Activity
- `MainActivity.kt` — CountdownAppContent主界面，编辑模式进入/退出，滑动互斥(openedEventId)，pin limit toast

---

## SwipeToDelete 当前逻辑（已验证正确）

```
手势：手指向左滑动 → cardOffset < 0 → 右边露出红色"删除"按钮 → 点击直接删
手势：手指向右滑动 → cardOffset > 0 → 左边露出天蓝"置顶"按钮 → 点击置顶

isOpen = true 时：
  - 点击卡片其他区域 → 只关闭滑动
  - 点击空白处 → 只关闭滑动
  - 滚动列表 → 关闭滑动
  - 打开另一个滑动 → 当前关闭
```

---

## 待完成

1. **模块六（农历）**：lunar库集成，AddEventDialog的农历日期格式化显示，EventCard农历显示
2. **模块七（置顶大卡片）**：置顶卡片2倍高度样式细节优化
3. **模块八（导出图片）**：EventDetailScreen导出为图片保存本地

---

## 下次开始

从 `T6-2：农历显示格式化` 开始，接续今天进度。
需要集成 `cn.6tail:lunar:1.7.4` 库到 AddEventDialog 和 EventCard 的农历日期显示中。