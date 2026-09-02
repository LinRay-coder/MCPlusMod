package com.example.mcplusmod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

/**
 * 物品注册。
 *
 * <p>26.1 起注册流程：先用工厂根据 {@link Item.Properties#setId(ResourceKey)} 创建物品实例，
 * 再通过 {@link Registry#register} 注册到 {@code BuiltInRegistries.ITEM}。
 *
 * <p>本类目前仅保留通用注册工具与两个「收集列表」，供装备系统（青金石 / 绿宝石 / 紫水晶
 * 材料与手套等）后续填充。红宝石已按需求移除。
 */
public final class ModItems {
	private ModItems() {
	}

	/** 收集需要加入创造模式「原材料」标签页的物品（如宝石材料）。 */
	public static final List<Item> INGREDIENTS = new ArrayList<>();

	/** 收集需要加入创造模式「装备」标签页的物品（如工具、盔甲、手套）。 */
	public static final List<Item> EQUIPMENT = new ArrayList<>();

	/**
	 * 通用注册方法：接受物品键、工厂与属性，返回注册好的物品实例。
	 */
	public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties properties) {
		Item item = itemFactory.apply(properties.setId(itemKey));
		return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
	}

	/**
	 * 触发本类的静态初始化（加载所有 static 字段），并把物品加入创造模式标签页。
	 * 由 {@link MCPlusMod#onInitialize()} 调用。
	 */
	public static void initialize() {
		// 触发装备类静态初始化，填充 INGREDIENTS / EQUIPMENT 列表。
		ModEquipment.initialize();
		ModGloves.initialize();

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
			for (Item item : INGREDIENTS) {
				output.accept(item);
			}
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
			for (Item item : EQUIPMENT) {
				output.accept(item);
			}
		});
	}
}
