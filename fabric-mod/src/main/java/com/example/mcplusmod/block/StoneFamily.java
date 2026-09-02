package com.example.mcplusmod.block;

import net.minecraft.world.level.block.Block;

/**
 * 描述「一个石头系列」的全套建筑变种方块，用于减少重复并方便 datagen 遍历。
 *
 * <p>每个系列提供 (平滑 / 切制 / 雕纹) × (整块 / 楼梯 / 台阶 / 墙) 的完整组合，
 * 外加基座（base，通常是砖）本身的楼梯 / 台阶 / 墙。所有字段均为已注册的 {@link Block} 实例。
 *
 * @param name             系列的内部名（如 {@code ruby_bricks}），用于生成资源键与调试
 * @param base             基础方块（整块，通常是砖）
 * @param stairs           基座楼梯
 * @param slab             基座台阶
 * @param wall             基座墙
 * @param chiseled         雕纹整块
 * @param chiseledStairs   雕纹楼梯
 * @param chiseledSlab     雕纹台阶
 * @param chiseledWall     雕纹墙
 * @param smooth           平滑整块
 * @param smoothStairs     平滑楼梯
 * @param smoothSlab       平滑台阶
 * @param smoothWall       平滑墙
 * @param cut              切制整块
 * @param cutStairs        切制楼梯
 * @param cutSlab          切制台阶
 * @param cutWall          切制墙
 */
public record StoneFamily(
		String name,
		Block base,
		Block stairs,
		Block slab,
		Block wall,
		Block chiseled,
		Block chiseledStairs,
		Block chiseledSlab,
		Block chiseledWall,
		Block smooth,
		Block smoothStairs,
		Block smoothSlab,
		Block smoothWall,
		Block cut,
		Block cutStairs,
		Block cutSlab,
		Block cutWall
) {
	/** 返回该系列的所有方块，便于 datagen 与创造栏统一遍历。 */
	public Block[] all() {
		return new Block[] {
				base, stairs, slab, wall,
				chiseled, chiseledStairs, chiseledSlab, chiseledWall,
				smooth, smoothStairs, smoothSlab, smoothWall,
				cut, cutStairs, cutSlab, cutWall
		};
	}
}
