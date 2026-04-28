# 宝宝成长记录 APP - 快速开始指南

## 📱 项目概览

**项目名称**：宝宝成长记录（BaoBao）  
**平台**：Android (Kotlin + Jetpack)  
**状态**：🚀 开发中（第一阶段 60% 完成）

---

## ⚙️ 开发环境要求

- **Android Studio** ≥ Hedgehog（2023.1.1）
- **Android SDK** API 26-35
- **Kotlin** 1.9+
- **Gradle** 8.x
- **JDK** 17+

---

## 🏗️ 项目结构

```
apps/android/
├── app/
│   ├── src/main/
│   │   ├── java/com/baobao/
│   │   │   ├── BaobaoApp.kt              # Application 类
│   │   │   ├── data/                     # 数据层
│   │   │   │   ├── local/                # 本地存储
│   │   │   │   │   ├── db/               # Room 数据库
│   │   │   │   │   │   ├── BaobaoDatabase.kt
│   │   │   │   │   │   ├── dao/          # DAO 接口
│   │   │   │   │   │   └── entity/       # 数据库实体
│   │   │   │   │   └── provider/         # 存储实现类
│   │   │   │   └── storage/              # 存储接口
│   │   │   ├── domain/                   # 业务模型
│   │   │   │   └── model/                # 数据模型
│   │   │   ├── ui/                       # UI 层
│   │   │   │   ├── page/                 # Fragment 页面
│   │   │   │   ├── adapter/              # RecyclerView Adapter
│   │   │   │   └── viewmodel/            # ViewModel 业务逻辑
│   │   │   └── di/                       # Hilt 依赖注入
│   │   └── res/
│   │       └── layout/                   # XML 布局文件
│   └── build.gradle.kts                  # Gradle 配置
└── gradle/libs.versions.toml              # 依赖版本管理
```

---

## 🚀 快速开始

### 1. 克隆项目
```bash
cd ~/Documents/repos
git clone <repo_url> baobao
cd baobao
```

### 2. 打开项目
```bash
# 使用 Android Studio 打开
open -a "Android Studio" apps/android
```

### 3. 同步 Gradle
```
Android Studio → File → Sync Now
或按 Ctrl+Alt+Y（Windows/Linux）/ Cmd+Alt+Y（Mac）
```

### 4. 运行应用
```
• 连接 Android 设备或打开模拟器
• Android Studio → Run → Run 'app'
或按 Shift+F10（Windows/Linux）/ Ctrl+R（Mac）
```

---

## 🎯 核心模块说明

### 数据层（Storage）
```
IConfigStorage       → 配置和宝宝档案存储
ILogStorage          → 育儿日志存储
IMediaStorage        → 媒体文件管理
ISyncQueueStorage    → 同步任务队列

LocalConfigStorage   → 加密文件存储
LocalLogStorage      → SQLite 存储
LocalMediaStorage    → 文件系统存储
LocalSyncQueueStorage → 队列存储
```

**特性**：
- ✅ 加密存储（AES-256-GCM）
- ✅ SQLite 数据库
- ✅ 文件系统管理
- ✅ 异步操作（Coroutines）

### 业务层（ViewModel）
```
HomeViewModel        → 首页数据（宝宝信息、时间线）
RecordViewModel      → 数据录入（喂养、睡眠等）
StatsViewModel       → 数据统计
ProfileViewModel     → 个人设置
```

### 表示层（UI）
```
HomeFragment         → 首页（成长动态）
RecordFragment       → 快速记录
StatsFragment        → 数据统计
ProfileFragment      → 我的设置

TimelineAdapter      → 时间线列表适配器
```

---

## 🔧 常见操作

### 添加新的数据类型
1. 在 `domain/model/` 中定义数据模型
2. 在 `data/local/db/entity/` 中定义 Entity
3. 在 `data/local/db/dao/` 中定义 DAO
4. 在 `data/local/provider/` 中实现存储逻辑

### 添加新的页面
1. 在 `ui/viewmodel/` 中创建 ViewModel
2. 在 `ui/page/` 中创建 Fragment
3. 在 `res/layout/` 中创建布局 XML
4. 在 `MainActivity.kt` 中注册导航

### 调试和日志
```kotlin
// 添加日志
Log.d("TAG", "消息")

// 查看日志
Android Studio → View → Tool Windows → Logcat
或按 Cmd+6（Mac）/ Alt+6（Windows）
```

---

## 📊 数据库架构

### 表结构
```sql
-- 育儿日志表
CREATE TABLE care_logs (
    id TEXT PRIMARY KEY,
    type TEXT,
    baby_id TEXT,
    recorded_at LONG,
    recorded_by TEXT,
    data TEXT,              -- JSON
    note TEXT,
    media_ids TEXT,         -- JSON array
    created_at LONG,
    updated_at LONG,
    deleted INT DEFAULT 0
)

-- 媒体项表
CREATE TABLE media_items (
    id TEXT PRIMARY KEY,
    type TEXT,
    source TEXT,
    local_raw_path TEXT,
    local_preview_path TEXT,
    cloud_raw_path TEXT,
    file_size_bytes LONG,
    duration_seconds INT,
    captured_at LONG,
    uploaded_at LONG,
    baby_id TEXT,
    taken_by TEXT,
    deleted INT DEFAULT 0
)

-- 同步任务表
CREATE TABLE sync_tasks (
    id TEXT PRIMARY KEY,
    action TEXT,
    resource_type TEXT,
    resource_id TEXT,
    local_path TEXT,
    remote_path TEXT,
    status TEXT,
    retry_count INT,
    error_message TEXT,
    created_at LONG,
    updated_at LONG
)
```

---

## 🧪 测试

### 手动测试用例
1. **启动应用**
   - [ ] 应用正常启动
   - [ ] 权限弹窗显示正确

2. **首页**
   - [ ] 显示宝宝档案
   - [ ] 显示今日数据
   - [ ] 下拉刷新工作

3. **快速记录**
   - [ ] 各记录类型能点击
   - [ ] 数据保存成功

4. **数据统计**
   - [ ] 统计数据显示正确
   - [ ] 计算逻辑无误

5. **我的设置**
   - [ ] 缓存大小显示正确
   - [ ] 清空缓存工作

---

## 🔐 安全性

### 加密配置
- ✅ 使用 Android Keystore 存储密钥
- ✅ AES-256-GCM 加密敏感配置
- ✅ 隐私数据本地存储，不上传

### 权限管理
```xml
<!-- 必需权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_MEDIA_LOCATION" />
```

---

## 🐛 常见问题

### 1. "Hilt 注入失败"
**解决**：确保 Application 类标注了 `@HiltAndroidApp`

### 2. "数据库版本不匹配"
**解决**：`Room.databaseBuilder()` 中设置 `.fallbackToDestructiveMigration()`

### 3. "编译错误：找不到类"
**解决**：执行 `Build → Clean Project` 然后 `Build → Rebuild Project`

### 4. "LiveData 观察不到更新"
**解决**：确保在主线程更新 LiveData，使用 `viewLifecycleOwner` 而非 `this`

---

## 📚 参考资源

- [Android 官方文档](https://developer.android.com/)
- [Jetpack 组件库](https://developer.android.com/jetpack)
- [Kotlin 官方文档](https://kotlinlang.org/docs/)
- [Room 数据库指南](https://developer.android.com/training/data-storage/room)
- [Hilt 依赖注入](https://developer.android.com/training/dependency-injection/hilt-android)

---

## 💡 开发提示

1. **使用 ViewModel 管理 UI 状态**
   ```kotlin
   private val viewModel: HomeViewModel by viewModels()
   ```

2. **使用 LiveData 观察数据变化**
   ```kotlin
   viewModel.babyProfile.observe(viewLifecycleOwner) { profile ->
       // 更新 UI
   }
   ```

3. **使用 Coroutines 处理异步操作**
   ```kotlin
   viewModelScope.launch {
       val data = withContext(Dispatchers.IO) {
           // 后台操作
       }
   }
   ```

4. **使用 Hilt 自动注入依赖**
   ```kotlin
   @AndroidEntryPoint
   class MyFragment : Fragment() {
       private val viewModel: MyViewModel by viewModels()
   }
   ```

---

## 🤝 贡献指南

1. 创建 feature 分支
2. 遵循项目代码规范
3. 添加单元测试
4. 提交 Pull Request

---

**最后更新**：2024-04-28  
**维护者**：开发团队
