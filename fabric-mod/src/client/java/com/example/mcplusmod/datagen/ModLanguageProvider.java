package com.example.mcplusmod.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.example.mcplusmod.ModBlocks;
import com.example.mcplusmod.ModEquipment;
import com.example.mcplusmod.ModGloves;
import com.example.mcplusmod.block.SimpleFamily;
import com.example.mcplusmod.block.StoneFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 语言（翻译）数据生成。
 *
 * <p>提供英文（en_us）与简体中文（zh_cn）两套翻译。每种语言需要独立的 Provider 实例，
 * 因此本类以静态内部类形式提供 {@link English} 与 {@link Chinese}，二者共用
 * {@link #buildTranslations} 的遍历逻辑，只是名称映射不同。
 */
public final class ModLanguageProvider {
	private ModLanguageProvider() {
	}

	/** 英文翻译。 */
	public static class English extends FabricLanguageProvider {
		public English(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(output, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider lookup, TranslationBuilder builder) {
			buildTranslations(builder::add, ModLanguageProvider::englishName);
			buildEquipmentTranslations(builder::add, false);
		}
	}

	/** 简体中文翻译。 */
	public static class Chinese extends FabricLanguageProvider {
		public Chinese(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
			super(output, "zh_cn", registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider lookup, TranslationBuilder builder) {
			buildTranslations(builder::add, ModLanguageProvider::chineseName);
			buildEquipmentTranslations(builder::add, true);
		}
	}

	/**
	 * 遍历所有石头系列，为每个方块生成翻译。
	 *
	 * @param sink   接收 (方块, 显示名) 的消费者（即 TranslationBuilder::add）
	 * @param namer  根据「系列基名 + 变种类型」产出显示名的函数
	 */
	private static void buildTranslations(BiConsumer<Block, String> sink, Namer namer) {
		for (StoneFamily family : ModBlocks.FAMILIES) {
			String baseName = family.name();
			sink.accept(family.base(), namer.name(baseName, Variant.BASE));
			sink.accept(family.stairs(), namer.name(baseName, Variant.STAIRS));
			sink.accept(family.slab(), namer.name(baseName, Variant.SLAB));
			sink.accept(family.wall(), namer.name(baseName, Variant.WALL));

			sink.accept(family.chiseled(), namer.name(baseName, Variant.CHISELED));
			sink.accept(family.chiseledStairs(), namer.name(baseName, Variant.CHISELED_STAIRS));
			sink.accept(family.chiseledSlab(), namer.name(baseName, Variant.CHISELED_SLAB));
			sink.accept(family.chiseledWall(), namer.name(baseName, Variant.CHISELED_WALL));

			sink.accept(family.smooth(), namer.name(baseName, Variant.SMOOTH));
			sink.accept(family.smoothStairs(), namer.name(baseName, Variant.SMOOTH_STAIRS));
			sink.accept(family.smoothSlab(), namer.name(baseName, Variant.SMOOTH_SLAB));
			sink.accept(family.smoothWall(), namer.name(baseName, Variant.SMOOTH_WALL));

			sink.accept(family.cut(), namer.name(baseName, Variant.CUT));
			sink.accept(family.cutStairs(), namer.name(baseName, Variant.CUT_STAIRS));
			sink.accept(family.cutSlab(), namer.name(baseName, Variant.CUT_SLAB));
			sink.accept(family.cutWall(), namer.name(baseName, Variant.CUT_WALL));
		}

		// 简版系列（末地石 / 紫珀 / 下界红砖）：仅基座 + 楼梯 + 台阶 + 墙。
		for (SimpleFamily family : ModBlocks.SIMPLE_FAMILIES) {
			String baseName = family.name();
			sink.accept(family.base(), namer.name(baseName, Variant.BASE));
			sink.accept(family.stairs(), namer.name(baseName, Variant.STAIRS));
			sink.accept(family.slab(), namer.name(baseName, Variant.SLAB));
			sink.accept(family.wall(), namer.name(baseName, Variant.WALL));
		}
	}

	/**
	 * 为三套装备（青金石 / 绿宝石 / 紫水晶）与手套生成翻译。
	 *
	 * @param sink    接收 (物品, 显示名) 的消费者（即 TranslationBuilder::add）
	 * @param chinese true 生成中文，false 生成英文
	 */
	private static void buildEquipmentTranslations(BiConsumer<Item, String> sink, boolean chinese) {
		equipmentSet(sink, chinese, "lapis", "Lapis", "青金石",
				ModEquipment.LAPIS_HELMET, ModEquipment.LAPIS_CHESTPLATE,
				ModEquipment.LAPIS_LEGGINGS, ModEquipment.LAPIS_BOOTS,
				ModEquipment.LAPIS_SWORD, ModEquipment.LAPIS_SPEAR, ModEquipment.LAPIS_PICKAXE,
				ModEquipment.LAPIS_AXE, ModEquipment.LAPIS_SHOVEL, ModEquipment.LAPIS_HOE);
		equipmentSet(sink, chinese, "emerald", "Emerald", "绿宝石",
				ModEquipment.EMERALD_HELMET, ModEquipment.EMERALD_CHESTPLATE,
				ModEquipment.EMERALD_LEGGINGS, ModEquipment.EMERALD_BOOTS,
				ModEquipment.EMERALD_SWORD, ModEquipment.EMERALD_SPEAR, ModEquipment.EMERALD_PICKAXE,
				ModEquipment.EMERALD_AXE, ModEquipment.EMERALD_SHOVEL, ModEquipment.EMERALD_HOE);
		equipmentSet(sink, chinese, "amethyst", "Amethyst", "紫水晶",
				ModEquipment.AMETHYST_HELMET, ModEquipment.AMETHYST_CHESTPLATE,
				ModEquipment.AMETHYST_LEGGINGS, ModEquipment.AMETHYST_BOOTS,
				ModEquipment.AMETHYST_SWORD, ModEquipment.AMETHYST_SPEAR, ModEquipment.AMETHYST_PICKAXE,
				ModEquipment.AMETHYST_AXE, ModEquipment.AMETHYST_SHOVEL, ModEquipment.AMETHYST_HOE);

		// —— 手套（8 种材质 + 2 种宝石强化）——
		for (ModGloves.GloveEntry g : ModGloves.ALL) {
			sink.accept(g.item(), chinese ? g.chineseName() : g.englishName());
		}
	}

	/** 生成一套「材料前缀」的盔甲 + 工具翻译。 */
	private static void equipmentSet(BiConsumer<Item, String> sink, boolean chinese,
			String id, String en, String zh,
			Item helmet, Item chestplate, Item leggings, Item boots,
			Item sword, Item spear, Item pickaxe, Item axe, Item shovel, Item hoe) {
		if (chinese) {
			sink.accept(helmet, zh + "头盔");
			sink.accept(chestplate, zh + "胸甲");
			sink.accept(leggings, zh + "护腿");
			sink.accept(boots, zh + "靴子");
			sink.accept(sword, zh + "剑");
			sink.accept(spear, zh + "矛");
			sink.accept(pickaxe, zh + "镐");
			sink.accept(axe, zh + "斧");
			sink.accept(shovel, zh + "锹");
			sink.accept(hoe, zh + "锄");
		} else {
			sink.accept(helmet, en + " Helmet");
			sink.accept(chestplate, en + " Chestplate");
			sink.accept(leggings, en + " Leggings");
			sink.accept(boots, en + " Boots");
			sink.accept(sword, en + " Sword");
			sink.accept(spear, en + " Spear");
			sink.accept(pickaxe, en + " Pickaxe");
			sink.accept(axe, en + " Axe");
			sink.accept(shovel, en + " Shovel");
			sink.accept(hoe, en + " Hoe");
		}
	}

	/** 系列基名（内部 id）→ 人类可读的基础名。 */
	private static String englishBase(String internalName) {
		return switch (internalName) {
			case "mcplusmod_stone" -> "Stone";
			case "mcplusmod_stone_bricks" -> "Stone Bricks";
			case "mcplusmod_deepslate" -> "Deepslate";
			case "mcplusmod_deepslate_bricks" -> "Deepslate Bricks";
			case "mcplusmod_andesite" -> "Andesite";
			case "mcplusmod_sandstone" -> "Sandstone";
			case "mcplusmod_copper" -> "Copper";
			case "mcplusmod_end_stone" -> "End Stone";
			case "mcplusmod_purpur" -> "Purpur";
			case "mcplusmod_nether_bricks" -> "Nether Bricks";
			default -> internalName;
		};
	}

	private static String chineseBase(String internalName) {
		return switch (internalName) {
			case "mcplusmod_stone" -> "石头";
			case "mcplusmod_stone_bricks" -> "石砖";
			case "mcplusmod_deepslate" -> "深板岩";
			case "mcplusmod_deepslate_bricks" -> "深板岩砖";
			case "mcplusmod_andesite" -> "安山岩";
			case "mcplusmod_sandstone" -> "砂岩";
			case "mcplusmod_copper" -> "铜";
			case "mcplusmod_end_stone" -> "末地石";
			case "mcplusmod_purpur" -> "紫珀";
			case "mcplusmod_nether_bricks" -> "下界砖";
			default -> internalName;
		};
	}

	private static String englishName(String internalName, Variant variant) {
		String base = englishBase(internalName);
		return switch (variant) {
			case BASE -> base;
			case STAIRS -> base + " Stairs";
			case SLAB -> base + " Slab";
			case WALL -> base + " Wall";
			case CHISELED -> "Chiseled " + base;
			case CHISELED_STAIRS -> "Chiseled " + base + " Stairs";
			case CHISELED_SLAB -> "Chiseled " + base + " Slab";
			case CHISELED_WALL -> "Chiseled " + base + " Wall";
			case SMOOTH -> "Smooth " + base;
			case SMOOTH_STAIRS -> "Smooth " + base + " Stairs";
			case SMOOTH_SLAB -> "Smooth " + base + " Slab";
			case SMOOTH_WALL -> "Smooth " + base + " Wall";
			case CUT -> "Cut " + base;
			case CUT_STAIRS -> "Cut " + base + " Stairs";
			case CUT_SLAB -> "Cut " + base + " Slab";
			case CUT_WALL -> "Cut " + base + " Wall";
		};
	}

	private static String chineseName(String internalName, Variant variant) {
		String base = chineseBase(internalName);
		return switch (variant) {
			case BASE -> base;
			case STAIRS -> base + "楼梯";
			case SLAB -> base + "台阶";
			case WALL -> base + "墙";
			case CHISELED -> "雕纹" + base;
			case CHISELED_STAIRS -> "雕纹" + base + "楼梯";
			case CHISELED_SLAB -> "雕纹" + base + "台阶";
			case CHISELED_WALL -> "雕纹" + base + "墙";
			case SMOOTH -> "平滑" + base;
			case SMOOTH_STAIRS -> "平滑" + base + "楼梯";
			case SMOOTH_SLAB -> "平滑" + base + "台阶";
			case SMOOTH_WALL -> "平滑" + base + "墙";
			case CUT -> "切制" + base;
			case CUT_STAIRS -> "切制" + base + "楼梯";
			case CUT_SLAB -> "切制" + base + "台阶";
			case CUT_WALL -> "切制" + base + "墙";
		};
	}

	/** 一个系列内的各变种类型。 */
	private enum Variant {
		BASE, STAIRS, SLAB, WALL,
		CHISELED, CHISELED_STAIRS, CHISELED_SLAB, CHISELED_WALL,
		SMOOTH, SMOOTH_STAIRS, SMOOTH_SLAB, SMOOTH_WALL,
		CUT, CUT_STAIRS, CUT_SLAB, CUT_WALL
	}

	/** 命名函数：给定系列基名与变种，返回显示名。 */
	@FunctionalInterface
	private interface Namer {
		String name(String internalName, Variant variant);
	}
}
