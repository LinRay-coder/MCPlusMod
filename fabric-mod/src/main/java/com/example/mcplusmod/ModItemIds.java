package com.example.mcplusmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * 物品 ID 工厂：为本 mod 的每个物品创建 {@link ResourceKey}。
 *
 * <p>ResourceKey 同时携带命名空间（mod id）、路径（物品名）以及所属注册表（物品注册表），
 * 是 26.1 起注册物品所需的键。Mojang 的 {@code ItemIds} 类做法与此一致。
 */
public final class ModItemIds {
	private ModItemIds() {
	}

	public static ResourceKey<Item> create(String name) {
		return ResourceKey.create(
				Registries.ITEM,
				Identifier.fromNamespaceAndPath(MCPlusMod.MOD_ID, name)
		);
	}
}
