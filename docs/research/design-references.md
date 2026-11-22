# Vibe Weather · 设计参考调研

> 调研日期：2026-11-22（后台调研代理产出）。
> 目的：为「沉浸式天气背景 + 白字 + 降雨概率带」的单屏界面寻找可借鉴的设计系统、开源标杆与具体规格。
> 本文只引用模式（颜色、间距、布局思想、参数区间），不复制任何受版权保护的截图/资产；引用处均附一手来源 URL。

## 0. 项目约束回顾（评估可行性的前提）

- Kotlin + Jetpack Compose，Material 3，Compose BOM 2025.09.00（≈ material3 1.3.x），AGP 9.5.2
- minSdk 26 / targetSdk 36 / compileSdk 36
- 单屏：沉浸背景（渐变天空 + Canvas 粒子）→ 头部（城市/76sp 大温度/现象）→ 24h 逐时降雨概率带（主角）→ 7 天列表 → 署名行
- 深色系基调、不用动态取色、无导航无设置页；用户在中国大陆（考虑无 GMS 设备）
- 现有痛点：白字直接压背景无层次、无卡片容器、概率条朴素、系统默认字体、视觉密度无节奏

---

## 1. 开源天气 App 里的设计标杆

### 1.1 Breezy Weather（首选标杆）

**仓库现状**：`breezy-weather/breezy-weather`，12.1k stars、443 forks，活跃维护（6300+ commits），自称「feature-rich free and open source **Material 3 Expressive** weather app with well-thought-out visualizations」，支持 50+ 天气源。README 与 F-Droid 描述均把「Design」列为第一卖点。
来源：<https://github.com/breezy-weather/breezy-weather> · <https://f-droid.org/en/packages/org.breezyweather/>

**设计特点**（官方文档 `docs/HOMEPAGE.md` 逐条写明）：

- **位置驱动昼夜主题**：主屏深浅色不是跟系统，而是跟「当前查看地点的昼夜」；白天亮色、夜晚暗色，且背景颜色随天气状况变化。这正是我们「背景随天气变」决策的成熟先例。
- **背景动画分层**：太阳/云等元素可作为独立动画层（可关、可用重力传感器随设备倾斜视差移动）。对应我们的 Canvas 粒子层。
- **逐时/逐日采用「trends」列式图表**：每列 = 图标 + 折线图（温度）+ 文本（降雨概率），日级为「白天图标 + 最高温折线 + 最低温折线 + 夜间图标 + 降雨概率条+文本」。降雨概率在我们的场景是主角，它的做法是「条 + 文本」并行。
- **数值→颜色的区间映射**贯穿全 App：AQI（绿/黄/橙/红/紫/棕六档）、UV（绿 0-2 / 黄 3-5 / 橙 6-7 / 红 8-10 / 紫 11+）、风力按蒲福风级六档变色。这套「数值分档→固定色板」的模式可以直接迁移到降雨概率条。
- **图标集**：支持可换图标包（延续 Geometric Weather 的 Material 图标传统，兼容 Chronus 图标包）。
- 来源：<https://github.com/breezy-weather/breezy-weather/blob/main/docs/HOMEPAGE.md>

**为什么被公认为好看**：在 Geometric Weather 停维护后，它是 FOSS 天气里唯一「设计驱动」的头部项目——把 Material 3 体系完整落到天气数据可视化（趋势图、色档映射、位置驱动主题），而不是简单堆卡片。第三方评测（How-To Geek）称其为 Google Weather 的开源平替且体验更好。
来源：<https://howtogeek.com/i-replaced-google-weather-for-an-open-source-alternative-and-im-never-going-back>

**许可证**：**LGPL-3.0**（README 明示，且注明「不授予商标/Logo 权利；禁止伪装来源」）。
来源：<https://github.com/breezy-weather/breezy-weather/blob/main/LICENSE>

**借鉴安全性结论**：

- 借鉴视觉模式（trends 列式布局、色档映射、昼夜驱动主题）：**安全**，模式不受版权保护。
- 复制其代码：**可行但需遵守 LGPL-3.0**（衍生作品须开源同等许可证）；本项目自用不发布，风险极低，但建议只学模式、不搬码。
- 一个重要反面信息：Breezy **明确拒绝实现 Geometric Weather 的「Circular sky」沉浸界面**（列在「不会实现」清单），理由是维护成本——这印证我们选「Canvas 粒子 + 渐变」轻量路线而非系统级天空动效是对的。
  来源：<https://github.com/breezy-weather/breezy-weather#features-that-will-not-be-implemented>

### 1.2 Geometric Weather（历史标杆，我们的直接血统）

- 仓库 `WangDaYeeeeee/GeometricWeather`：2.5k stars，LGPL-3.0，**已停止维护**；2023-12 从 Google Play 下架；Breezy Weather 即其 fork（原作者在 issue #492 中确认「I forked the project as Breezy Weather」）。
- 历史地位：2021 年前后公认的「最好看的开源 Material 天气」。它的两个遗产我们正在继承：① **天气驱动的沉浸背景 + 粒子动画**（其 `ui-weather-view` 模块做雨/雪等背景动画，该模块被 Breezy 整体保留为 `ui-weather-view`）；② 「Circular sky」单屏界面（后被 Breezy 放弃）。
- 来源：<https://github.com/WangDaYeeeeee/GeometricWeather> · <https://github.com/WangDaYeeeeee/GeometricWeather/issues/492> · <https://github.com/WangDaYeeeeee/GeometricWeather/issues/503>

### 1.3 其他 UI 好看的开源天气 App

| 项目 | 技术栈 | Stars | 许可证 | 参考价值 |
|---|---|---|---|---|
| **Overmorrow**（bmaroti9/Overmorrow） | Flutter + Material You | 822 | GPL-3.0 | 视觉口碑极佳的 Material You 天气：全屏「天气照片背景 + 前景玻璃感卡片」、Material You 动态色、雨量 15 分钟条、逐时列。它的「图片背景 + 半透明卡片」层叠是除 Apple 外的最佳沉浸式参照（非 Compose，只学布局）。来源：<https://github.com/bmaroti9/Overmorrow> |
| **WeatherYou**（rodrigmatrix/weather-you） | **Compose** + Material You | — | GPL-3.0 | Compose 侧的 Material You 参考（手机/平板/手表/TV），动态取色接壁纸色。我们不用动态取色，但其 Compose 版天气卡片实现可直接读码。来源：<https://github.com/rodrigmatrix/weather-you> |
| **Mist**（Llucs/Mist） | **Compose** + **M3 Expressive** | 1（WIP，仅 4 commits） | MIT | 规模太小不算标杆，但它是「Compose + Material 3 Expressive + 极简天气」的活样本，可看 Expressive 主题在天气上的实际气质。来源：<https://github.com/Llucs/Mist> |

未找到质量过硬的「iOS 天气 Compose 复刻」项目（搜到的多为 React Native/SwiftUI 复刻，如 <https://github.com/iamvinny/weather-app>，质量参差）。**iOS 视觉建议按第 3 节的规格自行实现，不依赖复刻仓库。**

---

## 2. Compose 生态：Material 3 Expressive 与毛玻璃

### 2.1 Material 3 Expressive：带来了什么

2025-05-13 发布，是 Material 3 的「情感化」扩展，核心三件事：**形状**（大圆角/多边形 `MaterialShapes` 35 种、形状 morph 动效）、**动效**（`MotionScheme` 弹簧物理体系，standard/expressive 两套 token，随主题下发）、**字体排印**（emphasized 字重、组件默认更圆润）。新组件含 `LoadingIndicator`（多边形 morph 加载器）、Wavy 进度条、`FloatingToolbar`、`ButtonGroup` 等。
来源：<https://blog.google/products-and-platforms/platforms/android/material-3-expressive-android-wearos-launch> · <https://m3.material.io/blog> · 代码示例：<https://android-developers.googleblog.com/2025/05/androidify-building-delightful-ui-with-compose.html>（官方 Androidify 样例：`MaterialExpressiveTheme + MotionScheme.expressive()`）

### 2.2 关键问题：我们的 BOM 版本能不能用？（一手版本时间线，来自官方 release notes）

| 版本 | 日期 | 与 Expressive 的关系 |
|---|---|---|
| material3 **1.3.0 stable** | 2024-09-04 | **无任何 Expressive API**（内容是 Carousel、Predictive Back、PullToRefresh 等） |
| 1.3.1 / 1.3.2 | 2024-2025 | 仅 bug fix → **BOM 2025.09.00 对应的 1.3.x 一样没有 Expressive API** |
| 1.4.0-alpha01…alpha10 | 2025 上半年 | Expressive API 的实验期（Androidify 用的是 1.4.0-alpha10） |
| **1.4.0-beta01** | 2025-07-30 | **把所有 `ExperimentalMaterial3ExpressiveApi` API 全部移除**，推迟到 1.5 线 |
| **1.4.0 stable** | 2025-09-24 | 只留下已稳定部分（`MotionScheme` 成为组件默认动效、icons 库退役推荐 Material Symbols） |
| **1.5.0 stable** | **2026-11-18** | Expressive 主题（`expressiveLightColorScheme`、`MaterialExpressiveTheme`）、`MaterialShapes`、`LoadingIndicator`、Wavy 指示器、`FloatingToolbar`、`ToggleButton/ButtonGroup` 等全部转正 |

来源：<https://developer.android.com/jetpack/androidx/releases/compose-material3>（1.3.0/1.4.0-beta01/1.4.0/1.5.0 各节）· <https://m3.material.io/blog/compose-material-3-is-stable>

**结论**：

1. **当前 BOM 2025.09.00（material3 1.3.x）用不了 Expressive API**——一个都没有，连实验的都没有。手动改字号圆角做「类 Expressive」当然可以，但那不是 API 层面的支持。
2. **要用就一步跳到 1.5.0**（BOM 2026.11 系列，2026-11-18 刚 stable，多数 Expressive API 已非实验）。**不要停在 1.4.x**，那是 Expressive API 的「空洞期」（beta 起被整体移除）。
3. 兼容性（2026-11-22 经本仓库构建日志订正）：material3 **1.5.0 stable 的 AAR 元数据要求 compileSdk ≥ 37**——BOM 2026.11.01 在本仓库 compileSdk 36 上的 `checkDebugAarMetadata` 直接失败（13 项，含 material3-android:1.5.0「requires version 37 or later」）。AAR 元数据实测优先于上表的版本叙事，因此 material3 1.5 线在本机当前 SDK（无平台 37）下不可用。
   来源：本仓库构建日志（2026-11-22，`checkDebugAarMetadata` 失败输出）· <https://developer.android.com/jetpack/androidx/releases/compose-material3>

### 2.3 毛玻璃（frosted glass）方案：Haze

**现状**：`chrisbanes/haze`，作者 Chris Banes（Google/Compose 团队出身），Compose Multiplatform 背景模糊与玻璃效果的事实标准库；2.x 架构分模块（`haze` 核心 + `haze-blur` / `haze-blur-materials` / **`haze-glass`** + `haze-glass-material3`），Glass 效果自 2.1 起 stable。**许可证 Apache-2.0**（可闭源商用，无忧）。
来源：<https://chrisbanes.github.io/haze/latest/> · <https://chrisbanes.me/posts/haze-2.1.0/>

**API 形态**（2.x）：

```kotlin
val hazeState = rememberHazeState()
// 背景层（我们的渐变天空+粒子）
Box(Modifier.hazeSource(hazeState)) { /* 天空场景 */ }
// 前景卡片
Card(Modifier.hazeGlass(
    input = HazeInput.Sources(hazeState),      // 或 HazeInput.Backdrop(hazeState)
    style = GlassStyle.regular,                // 官方推荐起点
)) { /* 白字内容 */ }
```

- 内置两风格：`GlassStyle.regular`（默认，模糊深度随尺寸增大、深色模式自动用更深色调）与 `GlassStyle.clear`（高透、折射集中在边缘，适合媒体背景上的控件）。文档明确参照 Apple Materials 指南设计，且强调「clear 置于亮内容上时需自加暗纱，Haze 不会自动加」。
- 可调参数：tint（示例常用白 16%）、shape（默认 16dp 圆角）、specular 高光（0.48）、edgeShadow（黑 14% 边缘暗边）、色散、折射强度等；Material 3 集成让卡片底色自动取 `MaterialTheme.colorScheme.surface`。
- 来源：<https://chrisbanes.github.io/haze/latest/effects/glass/>

**minSdk 26 下的降级行为**（一手，平台文档）：

- **Android 13+（API 33+）**：全功能。
- **Android 12/12L（API 31/32）**：可用，但 progressive 类效果多次绘制更贵；且「快照状态一变就重绘」——对持续动画的背景（正是我们的粒子天空）要测帧预算。
- **Android 11 及以下（API ≤30，即我们 minSdk 26-30 这段）**：**Blur 默认禁用，自动降级为半透明 scrim（白色纱+色滤）**；可实验性开启 RenderScript 模糊（后台线程、跳帧、明显更慢，官方标注 experimental）。
- **设计含义**：Haze 的降级产物恰好就是「半透明纱卡片」——一条代码路径在旧机上自动变成 scrim 卡片、新机上是玻璃卡片，视觉系统不破。这与我们「低端机掉帧自动降级静态渐变」的既定策略同构。
- 来源：<https://chrisbanes.github.io/haze/latest/blur/platforms/>

**性能注意点**（官方性能指南）：

- 先用默认（`HazePerformanceMode.Default` = Balanced）再按测量调优；Balanced 在 Pixel 8a 变化背景下仍留 ~7.9ms 帧余量（Quality 6.7ms、Performance 少约 9% 峰值 GPU 内存）。
- 三条最相关的规则：**缩小受影响面积**（卡片越少越小越好）、**减少数量**（官方明确警告「同时最多 2-3 个大面积模糊面」）、**把无关动画移出捕获源**——我们的粒子动画必须留在源内（它就是要被模糊的内容），所以更要控制卡片数量与面积。
- 帧慢时的官方次序：缩面积 → 减数量 → 降 Performance 模式 → 简化样式（uniform blur 替代 progressive）。
- 来源：<https://chrisbanes.github.io/haze/latest/performance/>

**备选方案对比**：

| 方案 | API 下限 | 说明 |
|---|---|---|
| 原生 `Modifier.blur` | **仅 API 31+ 有效**（映射到 Android 12 的 RenderEffect；≤30 静默无效），且只能模糊自身内容、无法做「背景透过」的 backdrop 模糊 | 参考：<https://developer.android.com/guide/topics/renderscript/migrate>（RenderScript 自 12 起废弃）· <https://github.com/skydoves/Cloudy>（README 明言 blur 路径只支持 Android 12+） |
| skydoves/**Cloudy** | minSdk 24（≤32 走 CPU 回退） | KMP 毛玻璃库，AGSL 渐进模糊（Android 33+）；可作为 Haze 的替代品对比评估。来源：<https://github.com/skydoves/Cloudy> |
| 纯 scrim（无库） | 全版本 | 半透明黑/白纱 + 圆角 + 描边，零依赖零开销；在深色系天气背景上与真玻璃的观感差距比想象小 |

---

## 3. Apple Weather：沉浸式视觉标杆的可实现规格

Apple Weather 是「背景即天气、前景即数据」流派的定义者。以下拆自 Apple 官方 HIG 与高质量拆解文档（数值为拆解者测量，标注※者为二手）。

### 3.1 毛玻璃卡片规格

- **材质分四档：ultra-thin / thin / regular（默认）/ thick**。官方描述 regular 的行为是「**模糊背景并调整其亮度**，以保证文字可读性」；越厚越不透明、文字对比越好，越薄越保留背景上下文。天气卡内文字多，官方默认 regular 起步。
  来源（一手）：<https://developer.apple.com/design/human-interface-guidelines/materials.md>
- ※拆解数值表（供 Compose 复刻参考）：thin ≈ 30-40px 模糊 + 黑/白 40-50% 填充 + 0.5pt 白描边 @60%；regular ≈ 40-60px 模糊 + 黑/白 65-75% 填充。深色天气背景上取「**黑 40-50% + 白描边 8-10% + 60px 级模糊**」即可复刻其观感。
  来源（二手，拆解文档）：<https://github.com/im-oree/Apple-Visual-Design-Documentation/blob/main/docs/06-materials-liquid-glass.md>
- **降级与无障碍规则**（官方）：bright 内容上的高透玻璃要加 **35% 暗纱**；`reduceTransparency` 时退回近实心表面。对应我们：深色系天气卡用 40-50% 黑纱打底，低端机/API≤30 直接不模糊也成立。
  来源：<https://developer.apple.com/design/human-interface-guidelines/materials.md>

### 3.2 前景对比处理（vibrancy 思想）

- HIG：玻璃上的文字要用**全不透明的系统 vibrant 色**，不靠降透明度做层次；层级由 vibrancy 档位（primary/secondary/tertiary label）表达。
  来源：<https://developer.apple.com/design/human-interface-guidelines/materials.md>
- ※拆解版可执行规则：白字全不透明；小字在玻璃上**升一档字重**（Regular→Medium）并加约 +0.005em 字距；实在不够对比就换更厚的材质，别去调文字颜色。
  来源：<https://github.com/im-oree/Apple-Visual-Design-Documentation/blob/main/docs/06-materials-liquid-glass.md>

### 3.3 降雨/温度条的颜色映射（可直接抄的数值）

- iPhone 天气 7 天列表的彩色温度条官方语义为六档：
  **深蓝 <0°C · 浅蓝 0-15°C · 绿 15-20°C · 黄 20-25°C · 橙 25-30°C · 红 >30°C**。条的位置 = 该日最低/最高温在全周范围内的相对区间（起点终点相对整周 min-max 归一化）。
  来源：<https://www.macrumors.com/how-to/iphone-weather-app-bar/>
- 逐时区带的形式：温度折线 + 条件图标 + 降雨概率数字（按需显示）。逐日为「星期 · 图标 · 低温/高温数字 + 彩色范围条」。
  来源（对官方 App 功能的第三方描述）：<https://macmost.com/getting-the-most-from-the-iphone-weather-app.html>

### 3.4 字体排印层级（SF Pro → 我们的映射）

- HIG 正文默认 17pt Regular；层级靠**字重 + 尺寸 + 颜色**共同表达；SF Pro 为可变字体，支持动态 optical size 与 Ultralight→Black 全字重。
- 官方原则「**一般避免细字重**」——但注明是针对小字可读性；天气类大温度数字正是行业惯例的反例（超大字号下细字重安全且有「空气感」）。落到我们：**76sp 大温度用细字重（Light 及以下），≤16sp 的正文标签用 Regular/Medium**，小字一律不碰细字重。
- 落到 Android 的字体替代：细字重几何无衬线即可复刻 SF Pro Thin 的气质（候选见 §7 落地建议）。
- 来源：<https://developer.apple.com/design/human-interface-guidelines/typography.md>

### 3.5 卡片间距节奏（布局思想）

从公开界面可归纳的模式（非官方数值）：头部大数字**无卡片**直接压在天空背景上（靠背景上部更干净的渐变保对比）→ 之下是**全宽等宽卡片列**，统一圆角、统一水平边距、恒定垂直间隔，卡片内 4-8pt 级小间隔分节。「一张卡一个主题（逐时一张、7 天一张）」，密度节奏来自卡片间的暗背景间隙而非卡内留白。

---

## 4. 极简/单色风格参考：Nothing OS 与点阵字体

### 4.1 Nothing OS 设计语言简述

- 核心是「**单色克制 + 点阵字体 + 硬件感的 UI**」：纯黑画布、灰阶文字层级、点阵（dot-matrix）字体作为身份符号，被评价为「monochrome themes aren't boring——点阵字体与现代图标学的混搭意外成立」。
  来源：<https://androidauthority.com/nothing-phone-2-monochrome-theme-3382235>
- 演进：OS 3.0 起 Nothing 自己也承认点阵不能当正文用——官方社区帖写明「点阵语言保留为身份点缀（define character），**不牺牲可读性**」，正文字体改走衬线/无衬线混合。
  来源：<https://nothing.community/d/61774-nothing-os-evolution> · <https://9to5google.com/2024/10/09/nothing-os-3-0-android-15-beta-hands-on> · <https://androidauthority.com/nothing-os-3-hands-on-3488739>
- 对我们的可借鉴点：**灰阶做层级**（白 100% / 白 70% / 白 45% 三档就够一屏）与**字体当点缀不当正文**。

### 4.2 Compose/Android 可用的点阵/等宽显示字体

| 字体 | 生态 | 说明 |
|---|---|---|
| **缝合像素字体 Fusion Pixel Font**（TakWolf） | 泛 CJK，8/10/12px，等宽+比例两版，**OFL** | **唯一实用级中文像素字体**：黑体风格、含简中常用字，TTF 直接可放 Compose `Font()`。另有算法加粗版与上游「方舟像素字体」。
来源：<https://github.com/TakWolf/fusion-pixel-font> · <https://github.com/TakWolf/ark-pixel-font> |
| **DotGothic16** | Google Fonts，日文点阵，OFL | 用户提到的 dotgothic 即此；16px 复古风，**含汉字但简体覆盖不全**，做中文正文不可靠，做数字点缀可。
来源：<https://fonts.google.com/specimen/DotGothic16> |
| Silkscreen / Press Start 2P / Pixelify Sans | Google Fonts，仅拉丁，OFL | 纯拉丁点阵/像素风，可用于时间、百分比数字。
来源：<https://fonts.google.com/specimen/Silkscreen> |
| 「PDMono」 | — | **未在 Google Fonts 检索到该名称**，可能是记忆偏差（最接近的是 Pixelify Sans / Press Start 2P 一类）。 |

> 结论：想要 Nothing 风的数字点缀，中文之外的选择很多；要中文像素字只有 Fusion Pixel 一条路（OFL、可子集化、体积小）。

---

## 5. 天气数据可视化：降雨概率与 7 天温度区间

### 5.1 降雨概率的公开模式

1. **Apple 式「按需标注」**：逐时区带里只在概率有意义时（如 ≥10% 或仅雨/雪时段）显示百分比数字 + 小水滴图标，其余留白——概率带的视觉噪音大幅下降。来源：<https://macmost.com/getting-the-most-from-the-iphone-weather-app.html>
2. **Breezy 式「条+数字」**：逐时 trends 每列固定含「降雨概率文本」，逐日为「白天+夜间最大概率 bar+text」。来源：<https://github.com/breezy-weather/breezy-weather/blob/main/docs/HOMEPAGE.md>
3. **数值→色档映射**（Breezy 全 App 通用模式，最值得抄）：把 0-100% 分 3-4 档映射到固定色板（例如 0-9% 隐去、10-39% 低饱和蓝、40-69% 中蓝、70%+ 亮蓝/青），条高=概率、条色=档位。这是 AQI/UV 那套色档思想的直接迁移，让概率带不用逐条标数字也能读出「哪段在下雨」。

### 5.2 7 天列表的温度区间怎么更精致

- **Apple 模式（首选）**：每天一行「星期 · 图标 · 低温数字 ──彩色范围条── 高温数字」；范围条两端点对全周 min/max 归一化（比单纯的低温点+高温点更直观地展示「周内冷暖走势」），颜色按 §3.3 的六档温度色。来源：<https://www.macrumors.com/how-to/iphone-weather-app-bar/>
- **Breezy 模式**：折线图趋势（max/min 双线）适合多数据场景，对 7 行的列表偏重。来源：<https://github.com/breezy-weather/breezy-weather/blob/main/docs/HOMEPAGE.md>
- 概率在 7 天行内用色档小条（不标数字），与逐时带形成同一套视觉语法。

---

## 6. 中文字体：系统默认够不够

**先修正一个前提**：「Android 系统默认中文字体 = Noto Sans SC」在国产设备上**不成立**。实际是：小米 HyperOS → **MiSans**，华为 → **HarmonyOS Sans**，OPPO/一加 → **OPPO Sans**，vivo → **vivo Sans**；原生系 Android 才是 Noto Sans CJK（思源黑体，Adobe/Google 出品，OFL）。它们全部是各厂商重点优化过的现代黑体。
来源：<https://zh.wikipedia.org/zh-hans/File:A_comparative_display_of_default_system_fonts_from_five_major_Chinese_smartphone_brands.png> · <https://en.wikipedia.org/wiki/CJK_font> · 厂商字体盘点（HarmonyOS Sans/OPPO Sans/MiSans 均免费商用）：<https://www.veleap.com/page/article_detail?istype=2&id=391>

**引入特定中文字体的成本**：

- Noto Sans SC 单字重 OTF ≈ **8.9MB**，可变字体（wght 100-900）≈ **17.7MB**——Google Fonts 官方仓库 issue 自己都标注它超出 9MB 的提交指南。
  来源：<https://github.com/google/fonts/issues/11169> · <https://huggingface.co/wq2012/mlfont-notosanssc>（8.89MB 实测数据）
- 子集化是必经之路：天气 App 的中文字符集极小（城市名 + 天气现象词 + 星期 + 设置词 ≈ 数百字），`fonttools pyftsubset` 裁剪后通常可压到几百 KB；但城市名是开放集合，子集表需保留裕量或做回退，工程成本真实存在。
- **Downloadable Fonts（Google Fonts 在线字体）不可用**：官方文档写明「设备须有 Google Play services v11+」才能用 Google Fonts Provider——我们用户群的无 GMS 国产 ROM 上直接失效，只能走 `res/font` 打包。
  来源：<https://developer.android.com/develop/ui/views/text-and-emoji/downloadable-fonts>

**结论**：**中文正文用系统默认**（MiSans/HarmonyOS Sans/思源黑体都是合格黑体，零 APK 体积、无子集化工程、随 ROM 渲染优化）；**温度数字/百分比单独引入一款拉丁显示字体**（仅数字+拉丁的子集 <100KB，`fontFamily` 只挂到大温度上），中文自动回退系统黑体——层级来自「同一屏两种气质」而非整体换字。

---

## 7. 落地建议：三套可选视觉方向

> 以下均满足硬约束：minSdk 26、无动态取色、深色基调、单屏、中国大陆可用。

### 方向一 · 「夜窗」——Apple Weather 式玻璃卡（推荐）

- **卡片**：2-3 张全宽卡（逐时降雨带一张、7 天一张），API 33+ 上用 **Haze Glass**（`GlassStyle.regular`，黑色 40-50% tint + 0.5dp 白描边 @60% + ≥16dp 圆角，复刻 §3.1 规格）；**API ≤30 自动降级为同参数的纯 scrim 卡**（Haze 内建降级，一条代码路径覆盖全机型）。头部大温度**不进卡片**，直接压渐变天空最干净的上部。
- **字体**：76sp 大温度用细字重几何 display 字体（Google Fonts OFL 之列，如 Outfit Light / Manrope ExtraLight，只打包数字子集 <100KB）；中文正文系统默认（§6 结论）；玻璃上的小字升一档字重（§3.2）。
- **概率条**：竖条高=概率 + **色档映射**（§5.1 模式 3），≥40% 才标数字（Apple 按需标注思想）；7 天行加**六档温度色范围条**（§3.3 官方数值）。
- **动效密度**：保留背景粒子动画（卡外），卡片内零动效，数据刷新时整卡淡入。卡片数量≤3 保证 Haze 帧预算（§2.3 官方「2-3 个大面积模糊面」上限）。
- **实现成本**：中。+1 依赖（Haze，Apache-2.0）；预估 2-3 天（含低端机真机帧率验证与 Performance 模式降档）。
- **风险**：粒子动画在 hazeSource 内持续变化会推高重绘成本——按官方指南先 Balanced 后测，必要时逐时卡退回 scrim。

### 方向二 · 「无卡·单色」——Nothing OS 式极简

- **卡片**：**无卡片**。全屏暗纱渐变（上浅下深的黑纱 scrim）直接压在天气背景上，层次全靠**灰阶三档**（白 100%/70%/45%）+ 分区间距节奏（区块间 24-32dp 大呼吸）。
- **字体**：大温度细字重 + 标签用**点阵字体点缀**（Fusion Pixel 的数字/拉丁子集做「%」「h」和署名行，OFL、子集 <50KB），正文中文系统默认——Nothing 的「字体当身份不当正文」原则（§4.1）。
- **概率条**：单色白系竖条（不透明度=概率或高度=概率），≥50% 单独亮一档并标数字；7 天行无温度条，只留低温/高温数字靠字重分档。
- **动效密度**：最低。粒子保留但降速降密度；刷新时整屏轻微呼吸。
- **实现成本**：最低。零新依赖，1-2 天纯 Compose。
- **风险**：上限低——「克制」能到达的精致度依赖排版功力，概率条作为主角的**信息表达力弱于方向一**（无色档就没有「扫一眼知道哪段下雨」的效果）。

### 方向三 · 「Expressive 拥抱」——Google 官方新语言

- **卡片**：不升 BOM 的前提下不可行——需先把 BOM 升到 **2026.11（material3 1.5.0 stable）**，然后用 `MaterialExpressiveTheme + MotionScheme.expressive()`（§2.1 官方样例代码），卡片走 M3 大圆角 `surfaceContainer` 半透明容器。
- **字体**：Expressive 排印体系（emphasized 字重）+ 系统中文；可选 Wavy 进度条语义化「降雨量」。
- **概率条**：M3 色系条 + `MaterialShapes` morph 做当前小时高亮；7 天行保持 M3 Expressive ListItem。
- **动效密度**：高（Expressive 的本意就是弹簧动效/形状 morph），可刻意收敛只保留入场 morph。
- **实现成本**：中。BOM 升级（2026.11 系列）+ 回归；但经 §2.2 订正，material3 1.5.0 要求 compileSdk ≥37，本机装不了平台 37，**方向三当前被版本硬阻塞**（需先解决平台 37 安装）。前提成立时 1-2 天。
- **风险**：**气质冲突**——Expressive 的「活泼、圆润、爱动」与本项目「沉浸式、克制、深色」的方向相反（Breezy 是功能型大 App 用它合适，我们是单屏氛围型 App）；且它把视觉主权交给组件默认值，天气场景色难穿透。

### 推荐结论

**推荐方向一「夜窗」**，并吸收方向二的一条：概率条不达标时**不引入额外色**、灰阶过渡。理由：

1. 它正面解决最痛的「白字压背景无层次」——一张 40-50% 黑纱圆角卡就把前景从背景里「抬」出来，而方向二靠灰阶排版，容错低；
2. **降级路径天然对齐**：Haze 在 API≤30 自动就是 scrim 卡片，与既定的「低端机降级静态渐变」策略同构，不需要两套设计；
3. 视觉语言的**信息表达力最强**：概率色档 + 温度范围条都是 Apple 已验证十年的模式（§3.3/§5），逐时带作为界面主角能真正「扫一眼即答案」；
4. 与「沉浸 + 极简 + 克制」兼容：只有 2-3 张卡，背景仍是主角，玻璃是「窗口」不是「贴纸」；
5. 成本可控且可逆：唯一新依赖是 Apache-2.0 的 Haze，若低端机表现不达标，逐时卡退回纯 scrim 即方向二形态，沉没成本≈0。

方向三不建议在 v1 采用；若未来 BOM 因其他原因必须升级，可顺手取其 `MotionScheme` 弹簧 token 用在刷新动效上（动效 token 与视觉风格解耦）。

---

## 附：本调研引用的许可证一览

| 引用对象 | 许可证 | 我们能否直接复制资产/代码 |
|---|---|---|
| Breezy Weather | LGPL-3.0 | 模式可学；复制代码需遵守 LGPL（自用无碍，发布需开源衍生） |
| Geometric Weather | LGPL-3.0 | 同上 |
| Haze | Apache-2.0 | 代码可自由使用/修改/商用 |
| Overmorrow | GPL-3.0 | 仅借鉴模式，勿复制代码（会传染开源义务） |
| Mist / weather-you | MIT / GPL-3.0 | Mist 可读码；weather-you 同 Overmorrow |
| Fusion Pixel Font / 方舟像素 / DotGothic16 / Google Fonts | OFL 1.1 | 字体文件可打包进 APK（OFL 要求随附许可文本） |
| Noto Sans SC（思源黑体） | OFL 1.1 | 可子集化打包 |
