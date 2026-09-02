package com.example.mcplusmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Block;

/**
 * 装备系统：青金石 / 绿宝石 / 紫水晶三套盔甲与工具，以及青金石块（可激活信标）。
 *
 * <p>26.1 装备为「组件化」：{@link ArmorMaterial} / {@link ToolMaterial} 均为 record，无需注册；
 * 盔甲/工具通过 {@link Item.Properties} 的 builder 方法（{@code humanoidArmor} / {@code sword} 等）创建。
 *
 * <h2>平衡定位</h2>
 * <ul>
 *   <li><b>青金石</b>：≈铁略强。护甲值同铁，但附魔性极高（青金石本就是附魔材料），耐久适中；
 *       工具接近铁但更易附魔。定位「易附魔的中端装备」。</li>
 *   <li><b>绿宝石</b>：≈钻石级但弱于下界合金。护甲值同钻石、带韧性，耐久接近钻石；工具接近钻石。
 *       绿宝石在生存中较易通过村民交易获得，故不给击退抗性以免过强。</li>
 *   <li><b>紫水晶</b>：介于铁与钻石之间的过渡装备。护甲值/耐久/工具属性居中，附魔性中等。</li>
 * </ul>
 */
public final class ModEquipment {
	private ModEquipment() {
	}

	// —— datagen 用的收集列表 ——
	/** 需要生成「平面物品模型」(item/generated) 的物品：宝石材料 + 盔甲件。 */
	public static final List<Item> FLAT_ITEM_MODELS = new ArrayList<>();
	/** 需要生成「手持物品模型」(item/handheld) 的物品：工具。 */
	public static final List<Item> HANDHELD_ITEM_MODELS = new ArrayList<>();

	// —— 按用途分类的收集列表，供标签 / 配方 / 翻译 datagen 遍历 ——
	/** 头盔（head_armor）。 */
	public static final List<Item> HELMETS = new ArrayList<>();
	/** 胸甲（chest_armor）。 */
	public static final List<Item> CHESTPLATES = new ArrayList<>();
	/** 护腿（leg_armor）。 */
	public static final List<Item> LEGGINGS_LIST = new ArrayList<>();
	/** 靴子（foot_armor）。 */
	public static final List<Item> BOOTS_LIST = new ArrayList<>();
	/** 剑（swords）。 */
	public static final List<Item> SWORDS = new ArrayList<>();
	/** 矛（spears）。26.1 新增武器，可近战戳刺 + 右键蓄力投掷。 */
	public static final List<Item> SPEARS = new ArrayList<>();
	/** 镐（pickaxes）。 */
	public static final List<Item> PICKAXES = new ArrayList<>();
	/** 斧（axes）。 */
	public static final List<Item> AXES = new ArrayList<>();
	/** 锹（shovels）。 */
	public static final List<Item> SHOVELS = new ArrayList<>();
	/** 锄（hoes）。 */
	public static final List<Item> HOES = new ArrayList<>();

	// —— 修复材料物品标签（用铁砧修复对应装备）——
	public static final TagKey<Item> REPAIRS_LAPIS = itemTag("repairs_lapis_equipment");
	public static final TagKey<Item> REPAIRS_EMERALD = itemTag("repairs_emerald_equipment");
	public static final TagKey<Item> REPAIRS_AMETHYST = itemTag("repairs_amethyst_equipment");

	// —— 工具「挖不掉落」方块标签（沿用原版分级）——
	// 定位为铁/钻石级，直接复用原版标签：铁级用 INCORRECT_FOR_IRON_TOOL，钻石级用 INCORRECT_FOR_DIAMOND_TOOL。
	private static final TagKey<Block> INCORRECT_FOR_IRON = blockTag("minecraft", "incorrect_for_iron_tool");
	private static final TagKey<Block> INCORRECT_FOR_DIAMOND = blockTag("minecraft", "incorrect_for_diamond_tool");

	// —— 装备资产 key（定位穿戴纹理 / equipment JSON，不注册到注册表）——
	public static final ResourceKey<EquipmentAsset> LAPIS_ASSET = equipmentAsset("lapis");
	public static final ResourceKey<EquipmentAsset> EMERALD_ASSET = equipmentAsset("emerald");
	public static final ResourceKey<EquipmentAsset> AMETHYST_ASSET = equipmentAsset("amethyst");

	// ============================ 盔甲材料 ============================

	private static final int LAPIS_ARMOR_DURABILITY = 15;    // ≈铁
	private static final int EMERALD_ARMOR_DURABILITY = 33;  // ≈钻石
	private static final int AMETHYST_ARMOR_DURABILITY = 20; // 铁与钻石之间

	private static final ArmorMaterial LAPIS_ARMOR = new ArmorMaterial(
			LAPIS_ARMOR_DURABILITY,
			Map.of(
					ArmorType.HELMET, 3,
					ArmorType.CHESTPLATE, 8,
					ArmorType.LEGGINGS, 6,
					ArmorType.BOOTS, 3
			),
			20,                                  // 附魔性：高（青金石特色）
			SoundEvents.ARMOR_EQUIP_IRON,
			0.0F,                                // 韧性
			0.0F,                                // 击退抗性
			REPAIRS_LAPIS,
			LAPIS_ASSET
	);

	private static final ArmorMaterial EMERALD_ARMOR = new ArmorMaterial(
			EMERALD_ARMOR_DURABILITY,
			Map.of(
					ArmorType.HELMET, 3,
					ArmorType.CHESTPLATE, 8,
					ArmorType.LEGGINGS, 6,
					ArmorType.BOOTS, 3
			),
			12,                                  // 附魔性：钻石级偏低
			SoundEvents.ARMOR_EQUIP_DIAMOND,
			2.0F,                                // 韧性：同钻石
			0.0F,                                // 不给击退抗性（绿宝石易获取，避免过强）
			REPAIRS_EMERALD,
			EMERALD_ASSET
	);

	private static final ArmorMaterial AMETHYST_ARMOR = new ArmorMaterial(
			AMETHYST_ARMOR_DURABILITY,
			Map.of(
					ArmorType.HELMET, 2,
					ArmorType.CHESTPLATE, 6,
					ArmorType.LEGGINGS, 5,
					ArmorType.BOOTS, 2
			),
			15,                                  // 附魔性：中等
			SoundEvents.ARMOR_EQUIP_GENERIC,
			0.0F,
			0.0F,
			REPAIRS_AMETHYST,
			AMETHYST_ASSET
	);

	// ============================ 工具材料 ============================

	private static final ToolMaterial LAPIS_TOOL = new ToolMaterial(
			INCORRECT_FOR_IRON,   // 铁级：挖不动黑曜石等
			320,                  // 耐久（略高于铁 250）
			6.5F,                 // 挖掘速度（铁 6.0）
			2.0F,                 // 攻击加成（铁 2.0）
			20,                   // 附魔性：高
			REPAIRS_LAPIS
	);

	private static final ToolMaterial EMERALD_TOOL = new ToolMaterial(
			INCORRECT_FOR_DIAMOND, // 钻石级
			1400,                  // 耐久（钻石 1561，略低）
			8.0F,                  // 速度（钻石 8.0）
			3.0F,                  // 攻击加成（钻石 3.0）
			14,                    // 附魔性
			REPAIRS_EMERALD
	);

	private static final ToolMaterial AMETHYST_TOOL = new ToolMaterial(
			INCORRECT_FOR_IRON,    // 铁级
			600,                   // 耐久（铁与钻石之间）
			7.0F,                  // 速度
			2.5F,                  // 攻击加成
			16,                    // 附魔性
			REPAIRS_AMETHYST
	);

	// ============================ 材料物品 ============================
	// 青金石 / 绿宝石 / 紫水晶均沿用原版材料物品（minecraft:lapis_lazuli / emerald / amethyst_shard），
	// 无需本 mod 额外注册材料；装备通过配方与修复标签绑定到这些原版物品。
	// 「让青金石块激活信标」直接对原版 minecraft:lapis_block 追加 beacon_base_blocks 标签实现，
	// 见 ModBlockTagProvider（不再新建自定义青金石块）。

	// ============================ 盔甲物品 ============================

	public static final Item LAPIS_HELMET = armor("lapis_helmet", LAPIS_ARMOR, ArmorType.HELMET, LAPIS_ARMOR_DURABILITY);
	public static final Item LAPIS_CHESTPLATE = armor("lapis_chestplate", LAPIS_ARMOR, ArmorType.CHESTPLATE, LAPIS_ARMOR_DURABILITY);
	public static final Item LAPIS_LEGGINGS = armor("lapis_leggings", LAPIS_ARMOR, ArmorType.LEGGINGS, LAPIS_ARMOR_DURABILITY);
	public static final Item LAPIS_BOOTS = armor("lapis_boots", LAPIS_ARMOR, ArmorType.BOOTS, LAPIS_ARMOR_DURABILITY);

	public static final Item EMERALD_HELMET = armor("emerald_helmet", EMERALD_ARMOR, ArmorType.HELMET, EMERALD_ARMOR_DURABILITY);
	public static final Item EMERALD_CHESTPLATE = armor("emerald_chestplate", EMERALD_ARMOR, ArmorType.CHESTPLATE, EMERALD_ARMOR_DURABILITY);
	public static final Item EMERALD_LEGGINGS = armor("emerald_leggings", EMERALD_ARMOR, ArmorType.LEGGINGS, EMERALD_ARMOR_DURABILITY);
	public static final Item EMERALD_BOOTS = armor("emerald_boots", EMERALD_ARMOR, ArmorType.BOOTS, EMERALD_ARMOR_DURABILITY);

	public static final Item AMETHYST_HELMET = armor("amethyst_helmet", AMETHYST_ARMOR, ArmorType.HELMET, AMETHYST_ARMOR_DURABILITY);
	public static final Item AMETHYST_CHESTPLATE = armor("amethyst_chestplate", AMETHYST_ARMOR, ArmorType.CHESTPLATE, AMETHYST_ARMOR_DURABILITY);
	public static final Item AMETHYST_LEGGINGS = armor("amethyst_leggings", AMETHYST_ARMOR, ArmorType.LEGGINGS, AMETHYST_ARMOR_DURABILITY);
	public static final Item AMETHYST_BOOTS = armor("amethyst_boots", AMETHYST_ARMOR, ArmorType.BOOTS, AMETHYST_ARMOR_DURABILITY);

	// ============================ 工具物品 ============================
	// 剑/镐用普通 Item + Properties(sword/pickaxe)；斧/锹/锄用专用类以保留右键交互。
	// 26.1.2 起 PickaxeItem 已移除（镐无右键交互），改用 Item.Properties.pickaxe(material, dmg, speed)。
	// 攻速参考原版：剑 -2.4、镐 -2.8、斧 -3.0、锹 -3.0、锄按材料（此处统一 -3.0）。

	public static final Item LAPIS_SWORD = swordItem("lapis_sword", LAPIS_TOOL, 3, -2.4F);
	public static final Item LAPIS_SPEAR = spearItem("lapis_spear", LAPIS_TOOL, SpearTier.IRON);
	public static final Item LAPIS_PICKAXE = toolItem("lapis_pickaxe", ToolKind.PICKAXE, p -> new Item(p.pickaxe(LAPIS_TOOL, 1.0F, -2.8F)));
	public static final Item LAPIS_AXE = toolItem("lapis_axe", ToolKind.AXE, p -> new AxeItem(LAPIS_TOOL, 6.0F, -3.1F, p));
	public static final Item LAPIS_SHOVEL = toolItem("lapis_shovel", ToolKind.SHOVEL, p -> new ShovelItem(LAPIS_TOOL, 1.5F, -3.0F, p));
	public static final Item LAPIS_HOE = toolItem("lapis_hoe", ToolKind.HOE, p -> new HoeItem(LAPIS_TOOL, -2.0F, -1.0F, p));

	public static final Item EMERALD_SWORD = swordItem("emerald_sword", EMERALD_TOOL, 3, -2.4F);
	public static final Item EMERALD_SPEAR = spearItem("emerald_spear", EMERALD_TOOL, SpearTier.DIAMOND);
	public static final Item EMERALD_PICKAXE = toolItem("emerald_pickaxe", ToolKind.PICKAXE, p -> new Item(p.pickaxe(EMERALD_TOOL, 1.0F, -2.8F)));
	public static final Item EMERALD_AXE = toolItem("emerald_axe", ToolKind.AXE, p -> new AxeItem(EMERALD_TOOL, 5.0F, -3.0F, p));
	public static final Item EMERALD_SHOVEL = toolItem("emerald_shovel", ToolKind.SHOVEL, p -> new ShovelItem(EMERALD_TOOL, 1.5F, -3.0F, p));
	public static final Item EMERALD_HOE = toolItem("emerald_hoe", ToolKind.HOE, p -> new HoeItem(EMERALD_TOOL, -3.0F, 0.0F, p));

	public static final Item AMETHYST_SWORD = swordItem("amethyst_sword", AMETHYST_TOOL, 3, -2.4F);
	public static final Item AMETHYST_SPEAR = spearItem("amethyst_spear", AMETHYST_TOOL, SpearTier.IRON);
	public static final Item AMETHYST_PICKAXE = toolItem("amethyst_pickaxe", ToolKind.PICKAXE, p -> new Item(p.pickaxe(AMETHYST_TOOL, 1.0F, -2.8F)));
	public static final Item AMETHYST_AXE = toolItem("amethyst_axe", ToolKind.AXE, p -> new AxeItem(AMETHYST_TOOL, 5.5F, -3.0F, p));
	public static final Item AMETHYST_SHOVEL = toolItem("amethyst_shovel", ToolKind.SHOVEL, p -> new ShovelItem(AMETHYST_TOOL, 1.5F, -3.0F, p));
	public static final Item AMETHYST_HOE = toolItem("amethyst_hoe", ToolKind.HOE, p -> new HoeItem(AMETHYST_TOOL, -2.5F, -0.5F, p));

	// ============================ 辅助方法 ============================

	/** 创建并注册一件盔甲，收集到装备栏、分类列表与「平面模型」列表。 */
	private static Item armor(String name, ArmorMaterial material, ArmorType type, int baseDurability) {
		Item item = ModItems.register(
				ModItemIds.create(name),
				Item::new,
				new Item.Properties()
						.humanoidArmor(material, type)
						.durability(type.getDurability(baseDurability))
		);
		ModItems.EQUIPMENT.add(item);
		FLAT_ITEM_MODELS.add(item);
		switch (type) {
			case HELMET -> HELMETS.add(item);
			case CHESTPLATE -> CHESTPLATES.add(item);
			case LEGGINGS -> LEGGINGS_LIST.add(item);
			case BOOTS -> BOOTS_LIST.add(item);
			default -> { /* BODY 等不涉及 */ }
		}
		return item;
	}

	/** 创建并注册一把剑（普通 Item + sword 属性），收集到装备栏、剑列表与「手持模型」列表。 */
	private static Item swordItem(String name, ToolMaterial material, int attackDamage, float attackSpeed) {
		Item item = ModItems.register(
				ModItemIds.create(name),
				Item::new,
				new Item.Properties().sword(material, attackDamage, attackSpeed)
		);
		ModItems.EQUIPMENT.add(item);
		HANDHELD_ITEM_MODELS.add(item);
		SWORDS.add(item);
		return item;
	}

	/**
	 * 矛的等级档位：直接沿用原版对应材料矛的 9 个投掷/戳刺参数，保证行为与原版同级矛一致。
	 *
	 * <p>{@link Item.Properties#spear(ToolMaterial, float, float, float, float, float, float, float, float, float)}
	 * 的 9 个 float 依次为原版矛内部参数（攻击伤害 / 攻速 / 蓄力戳刺相关 / 投掷伤害与飞行等）。
	 * 数值取自反编译的原版 {@code Items} 类：铁矛档与钻石矛档。
	 */
	private enum SpearTier {
		/** 铁矛档（青金石 / 紫水晶用，均为铁级材料）。 */
		IRON(0.95F, 0.95F, 0.6F, 2.5F, 11.0F, 6.75F, 5.1F, 11.25F, 4.6F),
		/** 钻石矛档（绿宝石用，钻石级材料）。 */
		DIAMOND(1.05F, 1.075F, 0.5F, 3.0F, 10.0F, 6.5F, 5.1F, 10.0F, 4.6F);

		final float[] params;

		SpearTier(float... params) {
			this.params = params;
		}
	}

	/**
	 * 创建并注册一把矛（普通 Item + spear 属性）。
	 *
	 * <p>矛的物品模型<b>不走 datagen 的 handheld 模板</b>，而是手写资源
	 * {@code assets/mcplusmod/models/item/<name>.json}，以完全复刻原版矛的握持姿态
	 * （{@code item/generated} + 原版 {@code spear_in_hand} 的 display 变换）。因此这里
	 * 只收集到装备栏与矛列表，不加入 {@code HANDHELD_ITEM_MODELS}。
	 */
	private static Item spearItem(String name, ToolMaterial material, SpearTier tier) {
		float[] p = tier.params;
		Item item = ModItems.register(
				ModItemIds.create(name),
				Item::new,
				new Item.Properties().spear(material, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8])
		);
		ModItems.EQUIPMENT.add(item);
		SPEARS.add(item);
		return item;
	}

	/** 工具类别，用于把镐/斧/锹/锄归入相应标签与配方形状。 */
	private enum ToolKind { PICKAXE, AXE, SHOVEL, HOE }

	/** 创建并注册一件工具（镐/斧/锹/锄，专用类保留右键交互），收集到装备栏、分类列表与「手持模型」列表。 */
	private static Item toolItem(String name, ToolKind kind, Function<Item.Properties, Item> factory) {
		Item item = ModItems.register(ModItemIds.create(name), factory, new Item.Properties());
		ModItems.EQUIPMENT.add(item);
		HANDHELD_ITEM_MODELS.add(item);
		switch (kind) {
			case PICKAXE -> PICKAXES.add(item);
			case AXE -> AXES.add(item);
			case SHOVEL -> SHOVELS.add(item);
			case HOE -> HOES.add(item);
		}
		return item;
	}

	private static TagKey<Item> itemTag(String path) {
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MCPlusMod.MOD_ID, path));
	}

	private static TagKey<Block> blockTag(String namespace, String path) {
		return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(namespace, path));
	}

	private static ResourceKey<EquipmentAsset> equipmentAsset(String path) {
		return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(MCPlusMod.MOD_ID, path));
	}

	/** 触发静态初始化，加载所有装备物品并填充收集列表。 */
	public static void initialize() {
		// 所有 static 字段在类加载时已注册完毕，此方法仅用于确保类被加载。
	}
}
