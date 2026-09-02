package com.example.mcplusmod;

import com.example.mcplusmod.item.GloveCombat;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mod 主入口（main entrypoint）。
 *
 * <p>在 Minecraft 26.1.2 中，所有 API 使用 Mojang 官方名（Minecraft 已去混淆）。
 * 该类在游戏启动的公共（服务端 + 客户端）阶段被调用，用于注册物品、方块等内容。
 */
public class MCPlusMod implements ModInitializer {
	/** Mod 的唯一 ID，需与 fabric.mod.json 中的 "id" 一致。 */
	public static final String MOD_ID = "mcplusmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// 触发静态初始化并完成创造栏归类。
		// 先方块后物品：方块的 BlockItem 与物品注册到同一物品注册表，顺序不影响，但保持一致性。
		ModBlocks.initialize();
		ModItems.initialize();

		// 注册手套的主动战斗效果（击飞 + 额外冲击伤害）。
		GloveCombat.register();

		LOGGER.info("[MCPlusMod] Initialized: stone building variants, simple families, lapis/emerald/amethyst equipment and gloves registered.");
	}
}
