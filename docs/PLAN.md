# Vibe Weather · 设计规划

> 状态：规划定稿（2026-11-22），三项决策已确认，未开始编码。
> 本文是本项目唯一权威规划；改方向先改本文，再改代码。

## 0. 一句话本质

天气 App = **一组缓慢过期的数字 + 一个你喜欢的窗口**。

全部架构由这一句推出：

- 数据只读、随时间过期 → 它是**缓存**，不是数据库
- 使用是「打开 → 扫一眼 → 关掉」 → **单屏**、无导航、缓存优先渲染
- 自用、无广告无账号 → **零后端**、零第三方 SDK
- 唯一可变的是数据源 → 数据源做成**一条缝**（接口），可换、可并存

## 1. 需求还原

| 原始诉求 | 本质 |
|---|---|
| 看我定位这一周天气 | 只读预报，无输入无账号 |
| 未来 20 小时下雨概率 | 逐小时降雨概率 = 主信息（界面主角） |
| 未来 7 天晴/阴/雨 | 一周概览列表 |
| 精简、无广告、界面自己喜欢 | 精简是系统能力：包小、秒开、零跟踪，不只是界面好看 |

精简的判定法：每个功能先回答「不加会怎样」，答案必须是「没有损失」才保留。

## 2. 已定决策（2026-11-22）

| 决策点 | 结论 |
|---|---|
| 技术栈 | Kotlin + Jetpack Compose（原生） |
| 视觉风格 | 天气沉浸式：背景与动效随天气变化 |
| 数据源 | 不二选一：统一 WeatherProvider 抽象，v1 同时接 Open-Meteo 与和风天气，App 内可切换对比 |

## 3. 信息架构：一块屏幕

```
主屏（唯一一屏，无导航无设置页）
├─ 沉浸背景   天色渐变 + 粒子动效，随当前天气与昼夜切换
├─ 头部       城市 · 当前温度 · 天气现象 ·（小字：数据源名，点击切换）
├─ 降雨带     未来 20 小时逐时降雨概率，横向时间带（主角）
├─ 7 天列表   星期 · 图标 · 温度区间 · 降雨概率
├─ 署名行     数据来源（两家条款都要求署名）
└─ 状态层     加载 / 权限拒绝 / 网络错误 / 离线兜底
```

- 打开即内容，扫完即关；唯一主动交互是下拉刷新，其余自动
- 权限被拒时：一屏说清为什么需要定位 + 重试按钮，不做城市搜索兜底（v1）
- 沉浸式实现约束：
  - 场景 = 渐变天空 + 一层 Canvas 粒子（雨/雪/云/星），**不引入游戏引擎**
  - 目标 60fps；低端机掉帧自动降级为静态渐变
  - 文字区压半透明暗纱（scrim）保证对比度
  - `WeatherCode → SceneSpec` 是纯函数，归 theme 层，可单测

## 4. 架构与数据流

```
打开 App
   │
   ▼
WeatherRepository ──读快照──► DataStore（JSON）
   │                            ▲
   │ 立即渲染旧数据               └─写回新快照
   ▼
HomeViewModel ── UiState ──► Compose 界面
   ▲
   │ 静默替换
WeatherRepository ──定位──► LocationSource（系统 LocationManager）
   └──────────经纬度──────────► WeatherProvider（缝）
                                   ├─ OpenMeteoProvider
                                   └─ QWeatherProvider
```

- **SWR（先旧后新）**：启动先渲染缓存，后台静默刷新，体感秒开
- 快照有效期 **60 分钟**：过期自动拉新；离线时显示最后一次结果 +「数据时间」角标
- 单向数据流：`UiState = Ready | Loading | Error | PermissionDenied`，界面只做纯渲染
- 不用 Hilt / Room / RxJava：手写 `AppContainer` 注入；快照是一块 JSON，不需要数据库

## 5. 模块与缝

三个深模块 + 一条缝，接口全部很小：

```kotlin
// 唯一的缝：天气数据源
interface WeatherProvider {
    val id: String                      // "open-meteo" / "qweather"
    val label: String                    // 界面署名与切换标签
    suspend fun forecast(lat: Double, lon: Double): Forecast
}

// 定位：调用方只拿到坐标
interface LocationSource {
    suspend fun location(): Location    // 失败时 UI 走权限态/错误态
}

// 缓存策略归 Repository，SnapshotStore 只管存取
interface SnapshotStore {
    suspend fun load(): Snapshot?       // Snapshot = Forecast + 时间戳 + providerId
    suspend fun save(s: Snapshot)
}
```

- HTTP、JSON 解析、各家天气码映射、重试，全部关进 Provider 实现内部
- 换源/加源 = 加一个实现类 + 注册一行，其余零改动
- `WeatherCode` 是统一枚举（晴/多云/阴/雨/雪/雷…），两家 API 的码各自映射到它，沉浸场景也吃它

领域模型（v1 草案）：

```kotlin
data class Forecast(
    val locationName: String,        // 城市反查失败则回退「我的位置」
    val current: Current,             // 温度 + WeatherCode
    val hourly: List<Hour>,           // 未来 24h：时间 · 降雨概率 · 温度 · WeatherCode
    val daily: List<Day>,             // 7 天：日期 · 温度区间 · 降雨概率 · WeatherCode
)
```

## 6. 数据源（2026-11 官方口径核实）

| | Open-Meteo | 和风天气 |
|---|---|---|
| 注册 | 不需要，无 Key | 需账号 + 项目 + KEY（或 JWT） |
| 免费额度 | 非商用每日 1 万次 | 官方：天气类每月 5 万次内 ¥0；第三方评测称免费档约 1000 次/日，以控制台为准 |
| 降雨概率 | 逐时 + 逐日 | 逐时 + 逐日；另有分钟级降水（仅中国） |
| 国内速度 | 服务器在欧洲/北美，冷请求略慢 | 国内节点，快 |
| 数据来源 | ECMWF 等全球模型，含中国气象局 CMA | 商业数据源，中国本地化好 |
| 署名 | 必须（CC BY 4.0） | 必须（注明来源） |

- 用量预估：单机自用每天几十次，两家免费额度都绰绰有余
- 两家都**要求署名** → 界面底部固定署名行，随当前数据源切换
- 对比方法：点头部数据源标签即切换，选择持久化；用两周，对着实况比谁在你所在城市更准
- 和风接入前置动作：注册开发者账号、建项目拿 KEY；KEY 放 `local.properties`，不进 git
- 和风免费档限非商业用途，本 App 自用无广告，符合条款

## 7. 技术栈与依赖

- 语言 Kotlin；UI 用 Jetpack Compose（Material 3；不用动态取色，主题跟天气场景走）
- 网络 OkHttp + kotlinx-serialization；缓存 DataStore
- 定位走系统 `LocationManager`，不依赖 GMS（国产 ROM 无谷歌服务也能用）
- `minSdk 26`，`targetSdk` 取当时最新；包名暂定 `com.vibe.weather`（可改）
- 全部依赖 4 项；无广告、无埋点、无账号 SDK

## 8. 里程碑

| 阶段 | 内容 | 完成判据 |
|---|---|---|
| M0 界面先行 | 假数据驱动完整 UI：四套沉浸场景（晴/雨/云/夜）、降雨带、7 天列表、署名行 | 不接网，真机上界面你看了说「喜欢」 |
| M1 真数据 | 固定坐标接 Open-Meteo，DTO→domain 映射 + 单测 | 真机显示真实预报 |
| M1.5 双源对比 | 和风天气 Adapter + 头部切换 + 署名联动 | 同机可切换两源对比 |
| M2 定位与缓存 | LocationSource、DataStore 快照、SWR、错误/权限态 | 定位即天气，秒开，离线可看 |
| M3 打磨 | 图标、动效细节、排版、低端机降级 | 日常自用无粗糙感 |
| M4（v1.1）带伞提醒 | WorkManager 早间本地通知：今日降雨概率 > 50% 提醒带伞 | 零服务器跑通 |

顺序刻意：你最在意界面，所以先做界面再接数据；和风放在 M1.5 而不是 M1，避免第一天就背两个 API 的复杂度。

## 9. 项目结构（M0 建立）

```
com/vibe/weather
├─ data/          # 与外部世界打交道的实现
│  ├─ location/   LocationSource 实现（LocationManager）
│  ├─ weather/    OpenMeteoProvider · QWeatherProvider · 各自 DTO 与码表映射
│  └─ cache/      SnapshotStore 实现（DataStore JSON）
├─ domain/        # 纯模型与接口：Forecast · WeatherCode · WeatherProvider · LocationSource
├─ ui/
│  ├─ home/       HomeScreen · RainStrip · DailyList · 状态层
│  └─ theme/      场景映射（WeatherCode→SceneSpec）· 配色 · 字体
└─ MainActivity + AppContainer   # 手写依赖注入
```

## 10. 测试策略

- 单测（纯函数）：两家 API 样例 JSON → domain 映射；天气码 → WeatherCode → SceneSpec；快照过期判定
- Repository 测试：FakeProvider + FakeSnapshotStore 验证 SWR 与 60 分钟过期
- UI 薄，手动过状态矩阵：加载 / 权限拒绝 / 网络错误 / 离线 / 四类天气场景

## 11. 明确不做（v1 减法清单）

多城市管理 · 城市搜索 · 天气新闻/资讯 · 穿衣运动指数 · 账号与云同步 · 推送服务（M4 只做本地通知） · 桌面小部件（v1.1 后再议） · 广告与一切埋点

## 12. 已知风险

1. **城市名反查**：系统 `Geocoder` 在无 GMS 的 ROM 上可能失败 → 回退显示「我的位置」，不引入第三方地理编码依赖
2. **Open-Meteo 国内延迟**：服务器在欧洲/北美，冷请求可能近秒级 → SWR 掩盖，体感无影响
3. **和风额度口径不一**：官方定价页与第三方评测说法有出入（5 万次/月 vs 约 1000 次/日）→ 接入时以控制台实际为准
4. **沉浸式动效性能**：低端机掉帧 → 自动降级静态渐变，功能不受损
5. **SDK 平台 37 装不了**（dl.google.com 被墙，镜像不可达）→ 最新 androidx（BOM 2026.11）与 Haze 2.x 全线要求 compileSdk 37；暂以 2025-era 依赖 + 同规格纱面卡替代（天空为柔和渐变，观感差异很小）。有 VPN 时 `sdkmanager "platforms;android-37"` 装上即可整体升级（2026-11-22 实测）
