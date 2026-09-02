package com.example.mcplusmod.datagen;

import java.util.concurrent.CompletableFuture;

import com.example.mcplusmod.ModBlocks;
import com.example.mcplusmod.block.SimpleFamily;
import com.example.mcplusmod.block.StoneFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * 方块标签数据生成。
 *
 * <p>本 mod 所有石制方块都：
 * <ul>
 *   <li>加入 {@code minecraft:mineable/pickaxe}（用镐挖掘）；</li>
 *   <li>加入 {@code minecraft:needs_stone_tool}（至少需要石镐才掉落，与
 *       {@code requiresCorrectToolForDrops()} 配合）。</li>
 * </ul>
 */
public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
	public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		var pickaxe = valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE);
		var needsStone = valueLookupBuilder(BlockTags.NEEDS_STONE_TOOL);

		// 全套石头系列：需镐挖掘，且至少石镐才掉落（与 requiresCorrectToolForDrops 配合）。
		for (StoneFamily family : ModBlocks.FAMILIES) {
			for (Block block : family.all()) {
				addBlock(pickaxe, needsStone, block);
			}
		}

		// 简版系列（末地石 / 紫珀 / 下界红砖）：原版这些方块木镐即可挖掘掉落，
		// 因此只加入 mineable/pickaxe，不加 needs_stone_tool。
		for (SimpleFamily family : ModBlocks.SIMPLE_FAMILIES) {
			for (Block block : family.all()) {
				pickaxe.add(block);
			}
		}

		// 让原版青金石块（minecraft:lapis_block）可作为信标底座。
		// Fabric 标签会与原版数据合并：这里向 beacon_base_blocks 追加原版青金石块。
		valueLookupBuilder(BlockTags.BEACON_BASE_BLOCKS).add(Blocks.LAPIS_BLOCK);
	}

	private static void addBlock(
			TagAppender<Block, Block> pickaxe,
			TagAppender<Block, Block> needsStone,
			Block block) {
		pickaxe.add(block);
		needsStone.add(block);
	}
}
