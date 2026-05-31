# 倒数日 App - 项目说明书

## 一、项目概述

- **项目名称**：CountdownApp（倒数日）
- **项目路径**：`E:\CountdownApp`
- **技术栈**：Kotlin + Jetpack Compose + Room
- **目标**：开发一个安卓倒数日应用，支持添加事件、展示倒计时、纪念日计算、自定义卡片背景等功能

---

## 二、项目结构

```
E:\CountdownApp\
├── app\
│   ├── src\main\
│   │   ├── java\com\countdownapp\
│   │   │   ├── data\
│   │   │   │   ├── db\            # Room 数据库
│   │   │   │   ├── entity\        # 数据实体
│   │   │   │   └── repository\    # 仓库类
│   │   │   ├── ui\
│   │   │   │   ├── theme\         # 主题配置
│   │   │   │   ├── components\    # 可复用组件
│   │   │   │   ├── screens\       # 页面
│   │   │   │   └── viewmodel\     # ViewModel
│   │   │   └── util\              # 工具类
│   │   ├── res\
│   │   │   ├── values\            # 字符串/颜色/主题
│   │   │   ├── drawable\
│   │   │   └── mipmap\
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle\
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 三、数据模型

### Event 实体（Room Entity）

```kotlin
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetDate: Long,
    val backgroundImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
```

### 颜色生成规则

按事件名称通过哈希算法生成稳定颜色。

---

## 四、页面划分

1. **首页（MainScreen）**：事件卡片列表 + FAB添加 + 左滑删除
2. **详情页（EventDetailScreen）**：大卡片 + 背景虚化 + 更换背景

---

## 五、技术栈

- Kotlin + Jetpack Compose
- Room 数据库
- Coil 图片加载
- Navigation Compose