package com.example.mcplusmod.block;

import net.minecraft.world.level.block.Block;

/**
 * 描述「一个简版石头系列」：仅基座 + 楼梯 + 台阶 + 墙，不含平滑 / 切制 / 雕纹变种。
 *
 * <p>用于末地石、紫珀块、下界红砖等原版本身不提供全套切制/雕纹变种、或用户只需要基础
 * 建筑件的系列。所有字段均为已注册的 {@link Block} 实例。
 *
 * @param name   系列的内部名（如 {@code mcplusmod_end_stone}），用于生成资源键与调试
 * @param base   基础方块（整块）
 * @param stairs 楼梯
 * @param slab   台阶
 * @param wall   墙
 */
public record SimpleFamily(
		String name,
		Block base,
		Block stairs,
		Block slab,
		Block wall
) {
	/** 返回该系列的所有方块，便于 datagen 与创造栏统一遍历。 */
	public Block[] all() {
		return new Block[] { base, stairs, slab, wall };
	}
}
