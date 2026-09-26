# MCPlusMod

![Banner](fabric-mod/promo/banner.png)

一个适用于 **Minecraft 26.1（Fabric）** 的扩展模组：新增青金石 / 绿宝石 / 紫水晶三套装备与工具、10 种战斗手套、3 种矛，以及大量石材建筑变种（楼梯 / 台阶 / 墙，含平滑 / 切制 / 雕纹）。

- **零 Mixin、零第三方硬依赖**：仅需 Fabric Loader + Fabric API，天然低冲突
- **中英双语**：完整的 `zh_cn` / `en_us` 本地化
- **数据生成驱动**：全部模型、配方、战利品表、标签由 datagen 生成

## 环境要求

| 组件 | 版本 |
|---|---|
| Minecraft | 26.1.x |
| Fabric Loader | ≥ 0.19.3 |
| Fabric API | 0.155.2+26.1.2 |
| Java | ≥ 25 |
| 内存 | **建议 ≥ 8GB**（分配不足会 OOM） |

## 安装

1. 安装 [Fabric Loader](https://fabricmc.net/) 与对应版本的 [Fabric API](https://modrinth.com/mod/fabric-api)
2. 从 [Releases](../../releases) 下载 `MCPlusMod-26.1.2-Fabric-x.x.x.jar`
3. 将 jar 放入 `.minecraft/mods/` 目录
4. 启动游戏即可，无需任何前置 mod

## 内容概览

### 装备与工具（3 套 × 10 件 = 30 件）
- **材质**：青金石（偏耐久）/ 绿宝石（≈钻石偏强）/ 紫水晶（居中）
- 每套含：头盔、胸甲、护腿、靴子、剑、**矛**、镐、斧、锹、锄
- 矛支持投掷与近战；装备可用对应材料在铁砧修复

### 战斗手套（10 种）
- 皮革 / 锁链 / 铜 / 铁 / 金 / 钻石 / 下界合金 / 青金石 / 绿宝石 / 紫水晶
- **主手持握生效**：提供护甲与韧性加成，攻击时将目标击飞
- 附魔限制：仅可附「击退」「经验修补」「耐久」（刻意设计）
- 下界合金手套经锻造台升级获得

### 建筑方块
- 31 种自定义石材方块：base / 楼梯 / 台阶 / 墙 + 平滑 / 切制 / 雕纹变体
- 3 套简版系列：末地石 / 紫珀 / 下界砖
- 青金石块可作为信标底座

## 从源码构建

```bash
cd fabric-mod
./gradlew runDatagen   # 先生成数据
./gradlew build        # 再构建（请勿与 runDatagen 串联为一条命令）
```

产物位于 `fabric-mod/build/libs/`。

## 测试

游戏内测试清单见 [TEST_REPORT.md](fabric-mod/TEST_REPORT.md)（自动化验证已全部通过，人工游戏内测试待执行）。

## 许可证

MIT，详见 [LICENSE](LICENSE)。
