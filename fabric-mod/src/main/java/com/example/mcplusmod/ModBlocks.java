package com.example.mcplusmod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.example.mcplusmod.block.SimpleFamily;
import com.example.mcplusmod.block.StoneFamily;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 方块注册：三个石头系列（红宝石砖、石砖、深板岩砖），每系全套建筑变种。
 *
 * <p>26.1 注册流程：{@code factory.apply(properties.setId(blockKey))} 创建方块后
 * 用 {@link Registry#register} 注册到 {@code BuiltInRegistries.BLOCK}；带物品的方块再注册一个
 * {@link BlockItem} 到 {@code BuiltInRegistries.ITEM}。
 */
public final class ModBlocks {
	private ModBlocks() {
	}

	/** 收集本 mod 注册的「全套变种」石头系列（含平滑/切制/雕纹），供 datagen 与创造栏遍历。 */
	public static final List<StoneFamily> FAMILIES = new ArrayList<>();

	/** 收集「简版」石头系列：仅基座 + 楼梯 + 台阶 + 墙。 */
	public static final List<SimpleFamily> SIMPLE_FAMILIES = new ArrayList<>();

	// —— 全套变种石头系列 ——
	// 每系用一个内部前缀 + 一个原版模板方块（复制硬度/音效/工具属性）。
	public static final StoneFamily STONE = createFamily("mcplusmod_stone", Blocks.STONE);
	public static final StoneFamily STONE_BRICKS = createFamily("mcplusmod_stone_bricks", Blocks.STONE_BRICKS);
	public static final StoneFamily DEEPSLATE = createFamily("mcplusmod_deepslate", Blocks.DEEPSLATE);
	public static final StoneFamily DEEPSLATE_BRICKS = createFamily("mcplusmod_deepslate_bricks", Blocks.DEEPSLATE_BRICKS);
	public static final StoneFamily ANDESITE = createFamily("mcplusmod_andesite", Blocks.ANDESITE);
	public static final StoneFamily SANDSTONE = createFamily("mcplusmod_sandstone", Blocks.SANDSTONE);
	public static final StoneFamily COPPER = createFamily("mcplusmod_copper", Blocks.COPPER_BLOCK);

	// —— 简版系列（仅 楼梯 / 台阶 / 墙）——
	public static final SimpleFamily END_STONE = createSimpleFamily("mcplusmod_end_stone", Blocks.END_STONE);
	public static final SimpleFamily PURPUR = createSimpleFamily("mcplusmod_purpur", Blocks.PURPUR_BLOCK);
	public static final SimpleFamily NETHER_BRICKS = createSimpleFamily("mcplusmod_nether_bricks", Blocks.NETHER_BRICKS);

	/**
	 * 创建并注册「一个全套变种石头系列」的全部方块。
	 *
	 * @param prefix   系列前缀（如 {@code mcplusmod_stone}）
	 * @param template 用于复制基础属性（硬度/音效/工具）的原版模板方块
	 */
	private static StoneFamily createFamily(String prefix, Block template) {
		// 注意：每个方块都需要独立的 Properties 实例，因为 setId 会写入该实例。
		// BlockBehaviour.Properties.ofFullCopy(template) 每次调用都会新建实例，故用它来复制模板属性。

		// —— 基座（砖）+ 其楼梯 / 台阶 / 墙 ——
		Block base = registerWithItem(prefix, Block::new, props(template));
		Block stairs = registerWithItem(prefix + "_stairs", stairsFactory(base), props(template));
		Block slab = registerWithItem(prefix + "_slab", SlabBlock::new, props(template));
		Block wall = registerWithItem(prefix + "_wall", WallBlock::new, props(template));

		// —— 雕纹整块 + 楼梯 / 台阶 / 墙 ——
		Block chiseled = registerWithItem("chiseled_" + prefix, Block::new, props(template));
		Block chiseledStairs = registerWithItem("chiseled_" + prefix + "_stairs", stairsFactory(chiseled), props(template));
		Block chiseledSlab = registerWithItem("chiseled_" + prefix + "_slab", SlabBlock::new, props(template));
		Block chiseledWall = registerWithItem("chiseled_" + prefix + "_wall", WallBlock::new, props(template));

		// —— 平滑整块 + 楼梯 / 台阶 / 墙 ——
		Block smooth = registerWithItem("smooth_" + prefix, Block::new, props(template));
		Block smoothStairs = registerWithItem("smooth_" + prefix + "_stairs", stairsFactory(smooth), props(template));
		Block smoothSlab = registerWithItem("smooth_" + prefix + "_slab", SlabBlock::new, props(template));
		Block smoothWall = registerWithItem("smooth_" + prefix + "_wall", WallBlock::new, props(template));

		// —— 切制整块 + 楼梯 / 台阶 / 墙 ——
		Block cut = registerWithItem("cut_" + prefix, Block::new, props(template));
		Block cutStairs = registerWithItem("cut_" + prefix + "_stairs", stairsFactory(cut), props(template));
		Block cutSlab = registerWithItem("cut_" + prefix + "_slab", SlabBlock::new, props(template));
		Block cutWall = registerWithItem("cut_" + prefix + "_wall", WallBlock::new, props(template));

		StoneFamily family = new StoneFamily(
				prefix, base, stairs, slab, wall,
				chiseled, chiseledStairs, chiseledSlab, chiseledWall,
				smooth, smoothStairs, smoothSlab, smoothWall,
				cut, cutStairs, cutSlab, cutWall
		);
		FAMILIES.add(family);
		return family;
	}

	/**
	 * 创建并注册「一个简版系列」：仅基座 + 楼梯 + 台阶 + 墙。
	 */
	private static SimpleFamily createSimpleFamily(String prefix, Block template) {
		Block base = registerWithItem(prefix, Block::new, props(template));
		Block stairs = registerWithItem(prefix + "_stairs", stairsFactory(base), props(template));
		Block slab = registerWithItem(prefix + "_slab", SlabBlock::new, props(template));
		Block wall = registerWithItem(prefix + "_wall", WallBlock::new, props(template));

		SimpleFamily family = new SimpleFamily(prefix, base, stairs, slab, wall);
		SIMPLE_FAMILIES.add(family);
		return family;
	}

	/** 楼梯工厂：楼梯需要一个「基础方块默认状态」作为外观来源。 */
	private static Function<BlockBehaviour.Properties, Block> stairsFactory(Block baseBlock) {
		return properties -> new StairBlock(baseBlock.defaultBlockState(), properties);
	}

	/** 从模板方块复制一份全新的 Properties（每次都是独立实例，可安全 setId）。 */
	private static BlockBehaviour.Properties props(Block template) {
		return BlockBehaviour.Properties.ofFullCopy(template).requiresCorrectToolForDrops();
	}

	/**
	 * 注册方块并附带 BlockItem。
	 *
	 * @param name    方块与物品的路径名（同名）
	 * @param factory 方块工厂
	 * @param props   方块属性（会被 setId）
	 */
	public static Block registerWithItem(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties props) {
		ResourceKey<Block> blockKey = ModBlockIds.create(name);
		Block block = factory.apply(props.setId(blockKey));
		Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

		ResourceKey<Item> itemKey = ModItemIds.create(name);
		BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey));
		Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

		return block;
	}

	/**
	 * 触发静态初始化，并将所有方块加入创造模式「建筑方块」标签页。
	 */
	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> {
			for (StoneFamily family : FAMILIES) {
				for (Block block : family.all()) {
					output.accept(block.asItem());
				}
			}
			for (SimpleFamily family : SIMPLE_FAMILIES) {
				for (Block block : family.all()) {
					output.accept(block.asItem());
				}
			}
		});
	}
}
