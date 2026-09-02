package com.example.mcplusmod.datagen;

import java.util.concurrent.CompletableFuture;

import com.example.mcplusmod.ModBlocks;
import com.example.mcplusmod.block.SimpleFamily;
import com.example.mcplusmod.block.StoneFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

/**
 * 方块战利品表数据生成。
 *
 * <p>本 mod 全部方块采用「掉落自身」({@code dropSelf}) 规则；台阶方块由 vanilla 的
 * {@code dropSelf} 自动处理双台阶掉两个的情况。
 */
public class ModBlockLootTableProvider extends FabricBlockLootSubProvider {
	public ModBlockLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generate() {
		for (StoneFamily family : ModBlocks.FAMILIES) {
			for (Block block : family.all()) {
				dropSelf(block);
			}
		}
		for (SimpleFamily family : ModBlocks.SIMPLE_FAMILIES) {
			for (Block block : family.all()) {
				dropSelf(block);
			}
		}
	}
}
