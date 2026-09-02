package com.example.mcplusmod.item;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 手套的主动战斗效果：<b>主手持握</b>手套攻击生物时「击飞目标 + 造成额外冲击（摔落）伤害」。
 *
 * <p>这里注册 Fabric 的 {@link AttackEntityCallback}，在玩家攻击生物时检测其<b>主手</b>是否
 * 持握 {@link GloveItem}，若是则：
 * <ul>
 *   <li>把目标向远离玩家的方向<b>击飞</b>（含向上分量），强度取自手套的 {@code knockbackStrength}；</li>
 *   <li>对目标造成额外<b>冲击 / 摔落伤害</b>（{@code bonusFallDamage}）。</li>
 * </ul>
 * 仅在服务端执行，避免客户端重复施加。
 */
public final class GloveCombat {
	private GloveCombat() {
	}

	/** 在 mod 初始化时注册攻击事件。 */
	public static void register() {
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			// 仅服务端处理战斗逻辑。
			if (world.isClientSide()) {
				return InteractionResult.PASS;
			}
			if (!(entity instanceof LivingEntity target)) {
				return InteractionResult.PASS;
			}

			// 检测玩家主手是否持握本 mod 的手套。
			ItemStack mainHand = player.getMainHandItem();
			if (!(mainHand.getItem() instanceof GloveItem glove)) {
				return InteractionResult.PASS;
			}

			// —— 击飞：沿「玩家 → 目标」的水平方向推开目标 ——
			double dx = target.getX() - player.getX();
			double dz = target.getZ() - player.getZ();
			target.knockback(glove.knockbackStrength(), -dx, -dz);

			// —— 额外冲击 / 摔落伤害 ——（用玩家攻击伤害源，计入仇恨与统计）
			float bonus = glove.bonusFallDamage();
			if (bonus > 0.0F) {
				target.hurt(player.damageSources().playerAttack(player), bonus);
			}

			// 不吞掉原版近战攻击，让武器伤害照常结算。
			return InteractionResult.PASS;
		});
	}
}
