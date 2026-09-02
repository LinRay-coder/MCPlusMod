package com.example.mcplusmod.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.mcplusmod.ModEquipment;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * 物品标签数据生成。
 *
 * <p>为青金石 / 绿宝石 / 紫水晶盔甲与工具打上：
 * <ul>
 *   <li><b>分类标签</b>（{@code minecraft:swords} / {@code axes} / ... / {@code head_armor} 等）——
 *       让原版逻辑（如附魔适用性、锄右键、斧去皮等）正确识别；</li>
 *   <li><b>附魔标签</b>（{@code minecraft:enchantable/*}）——让物品能在附魔台/铁砧上附魔。
 *       盔甲进 {@code enchantable/armor} + 对应部位 + {@code durability}；
 *       剑进 {@code enchantable/sword} + {@code weapon} + {@code fire_aspect} + {@code durability}；
 *       其余工具进 {@code enchantable/mining} + {@code mining_loot} + {@code durability}。
 *       其中 {@code durability} 是「耐久 / 经验修补」附魔的关键标签。</li>
 *   <li><b>修复材料标签</b>（本 mod 的 {@code repairs_*_equipment}）——铁砧用对应宝石修复装备。</li>
 * </ul>
 */
public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	// —— 附魔标签（原版没有公开常量的用 Identifier 构造）——
	private static final TagKey<Item> ENCHANTABLE_ARMOR = vanillaItemTag("enchantable/armor");
	private static final TagKey<Item> ENCHANTABLE_HEAD = vanillaItemTag("enchantable/head_armor");
	private static final TagKey<Item> ENCHANTABLE_CHEST = vanillaItemTag("enchantable/chest_armor");
	private static final TagKey<Item> ENCHANTABLE_LEG = vanillaItemTag("enchantable/leg_armor");
	private static final TagKey<Item> ENCHANTABLE_FOOT = vanillaItemTag("enchantable/foot_armor");
	private static final TagKey<Item> ENCHANTABLE_EQUIPPABLE = vanillaItemTag("enchantable/equippable");
	private static final TagKey<Item> ENCHANTABLE_DURABILITY = vanillaItemTag("enchantable/durability");
	private static final TagKey<Item> ENCHANTABLE_WEAPON = vanillaItemTag("enchantable/weapon");
	private static final TagKey<Item> ENCHANTABLE_SWORD = vanillaItemTag("enchantable/sword");
	private static final TagKey<Item> ENCHANTABLE_FIRE_ASPECT = vanillaItemTag("enchantable/fire_aspect");
	private static final TagKey<Item> ENCHANTABLE_SHARP_WEAPON = vanillaItemTag("enchantable/sharp_weapon");
	private static final TagKey<Item> ENCHANTABLE_MINING = vanillaItemTag("enchantable/mining");
	private static final TagKey<Item> ENCHANTABLE_MINING_LOOT = vanillaItemTag("enchantable/mining_loot");

	// —— 本 mod 手套集合标签：供覆盖版 knockback.json 的 supported_items 引用，使手套仅能附「击退」这一武器附魔。——
	private static final TagKey<Item> GLOVES = TagKey.create(Registries.ITEM,
			Identifier.fromNamespaceAndPath(com.example.mcplusmod.MCPlusMod.MOD_ID, "gloves"));

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		// —— 盔甲：分类 + 附魔（护甲通用 + 部位 + 可装备 + 耐久）——
		armor(ModEquipment.HELMETS, ItemTags.HEAD_ARMOR, ENCHANTABLE_HEAD);
		armor(ModEquipment.CHESTPLATES, ItemTags.CHEST_ARMOR, ENCHANTABLE_CHEST);
		armor(ModEquipment.LEGGINGS_LIST, ItemTags.LEG_ARMOR, ENCHANTABLE_LEG);
		armor(ModEquipment.BOOTS_LIST, ItemTags.FOOT_ARMOR, ENCHANTABLE_FOOT);

		// —— 剑：swords + weapon/sword/fire_aspect/sharp_weapon + 耐久 ——
		for (Item sword : ModEquipment.SWORDS) {
			add(ItemTags.SWORDS, sword);
			add(ENCHANTABLE_WEAPON, sword);
			add(ENCHANTABLE_SWORD, sword);
			add(ENCHANTABLE_FIRE_ASPECT, sword);
			add(ENCHANTABLE_SHARP_WEAPON, sword);
			add(ENCHANTABLE_DURABILITY, sword);
		}

		// —— 矛：仅加入原版 minecraft:spears 分类标签即可。
		// 原版附魔标签体系里 enchantable/melee_weapon = #swords + #spears，
		// 再逐级并入 sharp_weapon → weapon、fire_aspect、durability，
		// 因此矛加进 #spears 后会自动获得锋利/击退/火焰附加/经验修补等全部近战武器附魔，无需手动加 enchantable/*。
		for (Item spear : ModEquipment.SPEARS) {
			add(ItemTags.SPEARS, spear);
		}

		// —— 镐 / 斧 / 锹 / 锄：对应分类 + 挖掘附魔 + 耐久 ——
		mining(ModEquipment.PICKAXES, ItemTags.PICKAXES);
		mining(ModEquipment.AXES, ItemTags.AXES);
		mining(ModEquipment.SHOVELS, ItemTags.SHOVELS);
		mining(ModEquipment.HOES, ItemTags.HOES);
		// 斧同时算武器，可上锋利/火焰附加。
		for (Item axe : ModEquipment.AXES) {
			add(ENCHANTABLE_WEAPON, axe);
			add(ENCHANTABLE_SHARP_WEAPON, axe);
		}

		// —— 修复材料标签：铁砧用对应宝石修复 ——
		add(ModEquipment.REPAIRS_LAPIS, net.minecraft.world.item.Items.LAPIS_LAZULI);
		add(ModEquipment.REPAIRS_EMERALD, net.minecraft.world.item.Items.EMERALD);
		add(ModEquipment.REPAIRS_AMETHYST, net.minecraft.world.item.Items.AMETHYST_SHARD);

		// —— 手套（主手持握装备）：附魔只保留【击退】与【经验修补 / 耐久】——
		// 手套改为主手物品后，护甲【数值】加成由主手属性组件提供（见 ModGloves）。
		// 附魔方面：
		//   · 不加入 enchantable/weapon（会连带解锁锋利/火焰附加/杀手等全部近战附魔）；
		//   · 不加入 enchantable/armor 等护甲附魔（保护/荆棘生效槽是 armor，主手拿着不触发，属无效附魔）；
		//   · 击退由覆盖原版 minecraft:enchantment/knockback.json 的 supported_items 单独授予
		//     （指向下方 mcplusmod:gloves 标签，且原版击退 slots=mainhand，正好在主手生效）；
		//   · 经验修补 / 耐久 走 enchantable/durability（slots=any，主手同样生效）。
		for (com.example.mcplusmod.ModGloves.GloveEntry g : com.example.mcplusmod.ModGloves.ALL) {
			Item glove = g.item();
			add(GLOVES, glove);                   // 本 mod 手套集合标签（供 knockback.json 引用，授予击退）
			add(ENCHANTABLE_DURABILITY, glove);   // 经验修补 / 耐久
		}
	}

	/** 盔甲件：加入分类标签 + 护甲附魔组 + 部位附魔 + 可装备 + 耐久。 */
	private void armor(List<Item> items, TagKey<Item> categoryTag, TagKey<Item> partEnchantTag) {
		for (Item item : items) {
			add(categoryTag, item);
			add(ENCHANTABLE_ARMOR, item);
			add(partEnchantTag, item);
			add(ENCHANTABLE_EQUIPPABLE, item);
			add(ENCHANTABLE_DURABILITY, item);
		}
	}

	/** 挖掘工具（镐/斧/锹/锄）：加入分类标签 + 挖掘附魔 + 耐久。 */
	private void mining(List<Item> items, TagKey<Item> categoryTag) {
		for (Item item : items) {
			add(categoryTag, item);
			add(ENCHANTABLE_MINING, item);
			add(ENCHANTABLE_MINING_LOOT, item);
			add(ENCHANTABLE_DURABILITY, item);
		}
	}

	private void add(TagKey<Item> tag, Item item) {
		valueLookupBuilder(tag).add(item);
	}

	private static TagKey<Item> vanillaItemTag(String path) {
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", path));
	}
}
