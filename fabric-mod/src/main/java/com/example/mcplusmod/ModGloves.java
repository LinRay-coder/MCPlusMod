package com.example.mcplusmod;

import java.util.ArrayList;
import java.util.List;

import com.example.mcplusmod.item.GloveItem;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 手套系统：一套<b>主手持握</b>的战斗手套（不再依赖 Trinkets 饰品槽）。
 *
 * <p>覆盖 7 种基础材质（皮革 / 锁链 / 铜 / 铁 / 金 / 钻石 / 下界合金）加 2 种宝石强化（青金石 / 绿宝石），共 9 件。
 * 手套作为普通物品拿在主手即可生效，无需任何前置 mod。
 *
 * <h2>平衡与作用</h2>
 * <ul>
 *   <li><b>护甲加成</b>：主手持握时通过 {@code ItemAttributeModifiers} 组件提供少量护甲值 / 护甲韧性
 *       （越高级越多），仅在 {@link EquipmentSlotGroup#MAINHAND} 生效。</li>
 *   <li><b>主动战斗效果</b>：主手持握攻击生物时会把目标<b>击飞</b>，并根据手套等级造成额外
 *       <b>摔落 / 冲击伤害</b>（越高级越强），见 {@link GloveItem} 与 {@code GloveCombat} 的实现。</li>
 *   <li><b>可附魔</b>：手套加入 {@code minecraft:enchantable/*} 相关标签，可附「击退 / 保护系列 /
 *       荆棘 / 经验修补 / 耐久」等（见 {@link com.example.mcplusmod.datagen.ModItemTagProvider}）。</li>
 * </ul>
 */
public final class ModGloves {
	private ModGloves() {
	}

	/** 所有手套（供 datagen 遍历：翻译 / 模型 / 标签）。 */
	public static final List<GloveEntry> ALL = new ArrayList<>();

	// —— 手套定义 ——
	// 参数：内部名，中/英文材料名，护甲加成，护甲韧性加成，攻击附加伤害（摔落/冲击），击飞强度，
	//       附魔性（enchantment value），耐久。整体略弱于同材质盔甲的单件，作为「附加」装备避免过强。
	public static final GloveEntry LEATHER = glove("leather_glove", "皮革", "Leather",
			0.0, 0.0, 1.0F, 0.35, 15, 96);
	public static final GloveEntry CHAINMAIL = glove("chainmail_glove", "锁链", "Chainmail",
			1.0, 0.0, 1.0F, 0.4, 12, 192);
	public static final GloveEntry COPPER = glove("copper_glove", "铜", "Copper",
			1.0, 0.0, 1.5F, 0.4, 10, 176);
	public static final GloveEntry IRON = glove("iron_glove", "铁", "Iron",
			1.0, 0.0, 2.0F, 0.45, 9, 220);
	public static final GloveEntry GOLDEN = glove("golden_glove", "金", "Golden",
			1.0, 0.0, 1.5F, 0.5, 22, 92);
	public static final GloveEntry DIAMOND = glove("diamond_glove", "钻石", "Diamond",
			2.0, 1.0, 3.0F, 0.5, 10, 528);
	public static final GloveEntry NETHERITE = glove("netherite_glove", "下界合金", "Netherite",
			2.0, 2.0, 4.0F, 0.6, 15, 592);
	// —— 宝石强化款 ——
	public static final GloveEntry LAPIS = glove("lapis_glove", "青金石", "Lapis",
			1.0, 0.0, 2.0F, 0.45, 25, 200);
	public static final GloveEntry EMERALD = glove("emerald_glove", "绿宝石", "Emerald",
			2.0, 1.0, 3.0F, 0.5, 14, 480);
	// 紫水晶款：用紫水晶「块」合成（更贵），因此数值略高于绿宝石。
	public static final GloveEntry AMETHYST = glove("amethyst_glove", "紫水晶", "Amethyst",
			2.0, 2.0, 3.5F, 0.55, 18, 560);

	/** 创建并注册一件手套。 */
	private static GloveEntry glove(String name, String zh, String en,
			double armor, double toughness, float bonusDamage, double knockback,
			int enchantValue, int durability) {
		Item item = ModItems.register(
				ModItemIds.create(name),
				properties -> new GloveItem(properties, armor, toughness, bonusDamage, knockback),
				new Item.Properties()
						.durability(durability)
						.enchantable(enchantValue)
						.attributes(mainHandArmor(name, armor, toughness))
		);
		ModItems.EQUIPMENT.add(item);
		GloveEntry entry = new GloveEntry(name, en + " Glove", zh + "手套", item);
		ALL.add(entry);
		return entry;
	}

	/**
	 * 构建「主手护甲」属性组件：把护甲 / 护甲韧性加成放到 {@link EquipmentSlotGroup#MAINHAND}，
	 * 使手套只有拿在主手时才提供防护。modifier id 用手套内部名保证唯一。
	 */
	private static ItemAttributeModifiers mainHandArmor(String name, double armor, double toughness) {
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		if (armor != 0.0) {
			builder.add(Attributes.ARMOR,
					modifier(name, "armor", armor), EquipmentSlotGroup.MAINHAND);
		}
		if (toughness != 0.0) {
			builder.add(Attributes.ARMOR_TOUGHNESS,
					modifier(name, "toughness", toughness), EquipmentSlotGroup.MAINHAND);
		}
		return builder.build();
	}

	/** 生成一个 ADD_VALUE 型属性修饰符，id 形如 {@code mcplusmod:glove/<name>/<attr>}。 */
	private static AttributeModifier modifier(String name, String attr, double value) {
		return new AttributeModifier(
				Identifier.fromNamespaceAndPath(MCPlusMod.MOD_ID, "glove/" + name + "/" + attr),
				value, AttributeModifier.Operation.ADD_VALUE);
	}

	/** 触发静态初始化，加载所有手套物品。 */
	public static void initialize() {
		// 所有 static 字段在类加载时已注册完毕。
	}

	/** 一件手套的元数据，供 datagen 使用。 */
	public record GloveEntry(String name, String englishName, String chineseName, Item item) {
	}
}
