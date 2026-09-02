package com.example.mcplusmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

/**
 * 方块 ID 工厂：为本 mod 的每个方块创建 {@link ResourceKey}。
 *
 * <p>26.1 起方块也通过 ResourceKey 注册（{@code Registries.BLOCK}）。
 * 方块对应的 BlockItem 单独使用 {@link ModItemIds} 生成物品键，二者路径保持一致。
 */
public final class ModBlockIds {
	private ModBlockIds() {
	}

	public static ResourceKey<Block> create(String name) {
		return ResourceKey.create(
				Registries.BLOCK,
				Identifier.fromNamespaceAndPath(MCPlusMod.MOD_ID, name)
		);
	}
}
