package com.example.mcplusmod.item;

import net.minecraft.world.item.Item;

/**
 * 手套物品：一件<b>主手持握</b>的装备（不再依赖 Trinkets 饰品槽）。
 *
 * <h2>作用</h2>
 * <ul>
 *   <li><b>护甲加成</b>：护甲值（{@code minecraft:armor}）与护甲韧性（{@code minecraft:armor_toughness}）
 *       通过 {@link Item.Properties#attributes} 施加的 {@code ItemAttributeModifiers} 组件提供，
 *       仅在 <b>主手（MAINHAND）</b> 槽生效（见 {@link com.example.mcplusmod.ModGloves}）。</li>
 *   <li><b>击飞 + 摔落伤害</b>：攻击敌人的逻辑不在此类，而由 {@code GloveCombat}（注册
 *       {@code AttackEntityCallback}）统一处理——它检测玩家<b>主手</b>是否持握本类，若是则读取
 *       {@link #bonusFallDamage} 与 {@link #knockbackStrength} 对被击中目标施加击飞与冲击/摔落伤害。</li>
 * </ul>
 *
 * <p>护甲加成 / 韧性 / 击飞强度 / 附加伤害 均由构造参数决定，越高级的手套数值越高。
 */
public class GloveItem extends Item {
	private final double armorBonus;
	private final double toughnessBonus;
	private final float bonusFallDamage;
	private final double knockbackStrength;

	public GloveItem(Properties properties, double armorBonus, double toughnessBonus,
			float bonusFallDamage, double knockbackStrength) {
		super(properties);
		this.armorBonus = armorBonus;
		this.toughnessBonus = toughnessBonus;
		this.bonusFallDamage = bonusFallDamage;
		this.knockbackStrength = knockbackStrength;
	}

	/** 手套提供的额外护甲值（仅信息用途，实际由 attributes 组件施加）。 */
	public double armorBonus() {
		return armorBonus;
	}

	/** 手套提供的额外护甲韧性（仅信息用途，实际由 attributes 组件施加）。 */
	public double toughnessBonus() {
		return toughnessBonus;
	}

	/** 攻击敌人时的附加「摔落 / 冲击」伤害。 */
	public float bonusFallDamage() {
		return bonusFallDamage;
	}

	/** 攻击敌人时的击飞强度（向上 + 水平推力系数）。 */
	public double knockbackStrength() {
		return knockbackStrength;
	}
}
