# Tiny Takeover Plus — Fabric Mod (Minecraft 26.1.2) 实施计划

## Context（背景）

用户要为 **Minecraft 26.1.2（Java 版，Fabric）** 开发一个内容型 Mod，添加新物品与大量石头建筑变种方块（楼梯、墙、台阶、雕纹、雕纹楼梯、平滑、切制等）。

关键约束（来自联网核对 Fabric 官方 26.1 资料，与旧版教程差异极大）：

- **Minecraft 26.1 是首个「去混淆」版本**，使用 **Mojang 官方名**（不再是 Yarn）。
- **JDK 要求 Java 25 起**；用户已安装 **Oracle JDK 26**（`C:\Program Files\Java\jdk-26`，已验证 `java version "26"`）。
- Loom 用不重映射的 **`net.fabricmc.fabric-loom`** 插件；依赖用普通 `implementation`（非 `modImplementation`）；打包用 `jar`（非 `remapJar`）。
- 版本：`minecraft=26.1`、`loader=0.19.3`、`loom=1.17-SNAPSHOT`、`fabric_api=0.145.1+26.1`、Gradle `9.4`。
- 创造栏事件用 **`CreativeModeTabEvents`**（旧名 `ItemGroupEvents` 已废弃）。
- 注册用 `ResourceKey<Item>` / `BlockItemId` + `Item.Properties.setId(...)` + `Registry.register(BuiltInRegistries.*, key, obj)`。
- 方块数量大 → 采用官方推荐的 **Data Generation** 自动生成 blockstate/模型/client item/战利品表/标签；自定义纹理用图片生成服务生成。

预期成果：`gradlew build` 通过并产出 mod jar；`gradlew runDatagen` 自动生成全部资源；游戏内可见并可用所有物品与方块。

## 环境与已完成项

- ✅ JDK 26 已装并验证（满足 Java 25 最低要求）。
- ✅ 已创建 Gradle 层：`fabric-mod/gradle.properties`、`settings.gradle`、`build.gradle`、`gradlew.bat`、`gradle/wrapper/gradle-wrapper.properties` 和 `gradle-wrapper.jar`（已从 Gitee 镜像下载，48966 字节）。
- ✅ 已创建 `src/main/resources/fabric.mod.json`（含 main + client 入口、依赖声明）。
- ✅ 已创建 `src/main/java/com/example/tinyplus/TinyPlus.java`（主入口骨架）。
- ⚠️ 待办：datagen 需要在 `build.gradle` 增加 `fabric-loom` 的 datagen 配置 + `fabric-api` 的 datagen source set。

## 方块 / 物品矩阵

**基础物品**
- `ruby`（红宝石）→ 创造栏 INGREDIENTS

**三个石头基底系列**，每系一个「基础方块」+ 全套变种：

| 系列 | 基础方块 | 纹理来源 |
|---|---|---|
| 红宝石系 | `ruby_block`、`ruby_bricks` | 生成新纹理 |
| 普通石头系 | 复用原版 `stone` / `stone_bricks` 作为基底纹理 | 原版纹理 |
| 深板岩系 | 复用原版 `deepslate` / `deepslate_bricks` | 原版纹理 |

**每系变种**（以红宝石砖 `ruby_bricks` 为完整样板，其余系列同构）：
- `*_stairs`（楼梯）
- `*_slab`（台阶）
- `*_wall`（墙）
- `chiseled_*`（雕纹方块）
- `chiseled_*_stairs`（雕纹楼梯）
- `smooth_*`（平滑变种）
- `cut_*`（切制变种）
- `cut_*_stairs` / `cut_*_slab`（切制的楼梯/台阶，配套）

> 数量较大（约 3 系 × 8~10 变种）。用数据生成后，手写代码只需声明方块实例 + datagen provider，JSON 全自动。

## 代码结构（`com.example.tinyplus`）

```
src/main/java/com/example/tinyplus/
  TinyPlus.java                 // ModInitializer 主入口（已存在，补充调用）
  ModItems.java                 // register(ResourceKey<Item>, factory, Properties) + ruby
  ModItemIds.java               // ResourceKey<Item> 工厂
  ModBlocks.java                // register 方块（含/不含 BlockItem 两个重载）+ 所有变种声明
  ModBlockIds.java              // ResourceKey<Block> 工厂
  ModBlockItemIds.java          // BlockItemId 工厂
  block/StoneFamily.java        // 描述「一个石头系列全套变种」的聚合类，减少重复
  registry/ModCreativeTabs.java // CreativeModeTabEvents 归类：建筑方块栏 + 原材料栏

src/client/java/com/example/tinyplus/client/
  TinyPlusClient.java           // ClientModInitializer（26.1 client item 由 datagen 生成，这里基本为空/渲染层设置）

src/main/java/com/example/tinyplus/datagen/
  TinyPlusDataGenerator.java    // DataGeneratorEntrypoint
  ModModelProvider.java         // 生成 blockstate + block/item 模型 + client item
  ModLootTableProvider.java     // 生成方块战利品表（掉落自身；slab 掉两个逻辑）
  ModBlockTagProvider.java      // mineable/pickaxe + needs_stone_tool 等标签
  ModRecipeProvider.java        // 合成 + 切石机配方（可选，先给基础）
  ModLanguageProvider.java      // en_us + zh_cn 名称
```

关键 26.1 API 约定：
- 物品：`Registry.register(BuiltInRegistries.ITEM, key, factory.apply(props.setId(key)))`
- 带 item 的方块：`BlockItemId.create(id, id)` → 注册 `Block` 后再注册 `new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()))`
- 楼梯/台阶/墙用原版类：`StairBlock`、`SlabBlock`、`WallBlock`（`BlockBehaviour.Properties.ofFullCopy(base)` 复制基底属性）
- 创造栏：`CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)`
- 静态初始化：各注册类提供 `initialize()`，由 `TinyPlus.onInitialize()` 调用

## build.gradle 需补充

- 在 `fabric-api` datagen 支持下增加：
  ```groovy
  fabricApi { configureDataGeneration { client = true } }
  ```
  （具体 API 名以 loom 1.17 为准，实施时联网核对当前 datagen 配置写法）
- 新增 `runDatagen` 运行配置由 loom 自动提供。

## 纹理方案

用图片生成服务为自定义方块生成 16×16 风格纹理（红宝石块、红宝石砖、雕纹红宝石砖、平滑/切制红宝石等）；普通石头系与深板岩系复用原版纹理路径。生成的 png 放入：
`src/main/resources/assets/tinyplus/textures/block/*.png`

## 实施步骤

1. 补全 `build.gradle` 的 datagen 配置；添加 `src/main/resources`/`src/client` datagen 关联。
2. 写 ID 工厂类（`ModItemIds`/`ModBlockIds`/`ModBlockItemIds`）。
3. 写 `ModItems`（ruby）。
4. 写 `StoneFamily` 聚合 + `ModBlocks`（三系全套变种声明 + 注册方法）。
5. 写 `ModCreativeTabs`（建筑方块 / 原材料归类）。
6. 写 datagen 各 Provider（模型、战利品表、标签、语言、配方）。
7. 生成并放置自定义纹理 png。
8. 编译验证（见下）。

## 验证（Verification）

在 `fabric-mod` 目录，使用已装的 JDK 26：

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

# 1) 生成资源（应在 src/main/generated 下产出大量 JSON）
.\gradlew.bat runDatagen --no-daemon

# 2) 编译打包（产物在 build/libs/*.jar）
.\gradlew.bat build --no-daemon
```

- 编译成功 = jar 出现在 `build/libs/`。
- 可选端到端：`.\gradlew.bat runClient` 启动游戏，创造栏「建筑方块」应能找到全部石头变种，能放置、能挖掘掉落、名称正确（中英）。

> 首次 `gradlew` 会联网下载 Gradle 9.4 发行版与依赖；GitHub 直连不稳，计划在实施时把 Gradle 发行版源切到腾讯云镜像（`mirrors.cloud.tencent.com/gradle`，已探测可用），Maven 依赖可配置阿里云/腾讯云镜像。

## 风险与备选

- **首次依赖下载慢/失败**（GitHub、maven.fabricmc.net）：备选切国内镜像；必要时分步重试。
- **loom 1.17-SNAPSHOT / datagen API 微调**：实施时以官方 26.1 example-mod 的 `build.gradle` datagen 段为准，联网核对。
- **纹理生成风格不统一**：先保证功能（占位/生成），风格可后续迭代。
- **范围大**：按「红宝石砖全套」先跑通一系，再复制模式扩展另外两系，降低一次性出错概率。
