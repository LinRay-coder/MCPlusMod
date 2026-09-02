package com.example.mcplusmod.datagen;

import java.util.concurrent.CompletableFuture;

import com.example.mcplusmod.ModBlocks;
import com.example.mcplusmod.ModEquipment;
import com.example.mcplusmod.ModGloves;
import com.example.mcplusmod.block.SimpleFamily;
import com.example.mcplusmod.block.StoneFamily;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * 合成与切石机配方数据生成。
 *
 * <p>为每个石头系列生成常见建筑配方：
 * <ul>
 *   <li>楼梯 / 台阶 / 墙：使用 vanilla 的形状合成辅助方法（{@code stairBuilder} 等）；</li>
 *   <li>切石机（stonecutter）：从基座一步切出楼梯/台阶/墙/雕纹/切制等所有变种。</li>
 * </ul>
 * 这样玩家既能用合成台，也能用切石机高效制作。
 */
public class ModRecipeProvider extends FabricRecipeProvider {
	public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
		return new RecipeProvider(registryLookup, exporter) {
			@Override
			public void buildRecipes() {
				for (StoneFamily family : ModBlocks.FAMILIES) {
					Block base = family.base();

					// —— 合成台配方（以基座为原料）——
					stairBuilder(family.stairs(), Ingredient.of(base))
							.unlockedBy(getHasName(base), has(base))
							.save(exporter);
					slabBuilder(RecipeCategory.BUILDING_BLOCKS, family.slab(), Ingredient.of(base))
							.unlockedBy(getHasName(base), has(base))
							.save(exporter);
					wallBuilder(RecipeCategory.BUILDING_BLOCKS, family.wall(), Ingredient.of(base))
							.unlockedBy(getHasName(base), has(base))
							.save(exporter);

					// 雕纹系：楼梯 / 台阶 / 墙 由雕纹整块合成。
					craftVariants(exporter, family.chiseled(),
							family.chiseledStairs(), family.chiseledSlab(), family.chiseledWall());
					// 平滑系：楼梯 / 台阶 / 墙 由平滑整块合成。
					craftVariants(exporter, family.smooth(),
							family.smoothStairs(), family.smoothSlab(), family.smoothWall());
					// 切制系：楼梯 / 台阶 / 墙 由切制整块合成。
					craftVariants(exporter, family.cut(),
							family.cutStairs(), family.cutSlab(), family.cutWall());

					// —— 切石机配方（从基座一步切出所有变种）——
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.stairs(), base);
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.slab(), base, 2);
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.wall(), base);

					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.chiseled(), base);
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.chiseledStairs(), base);
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.chiseledSlab(), base, 2);
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.chiseledWall(), base);

					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.smooth(), base);
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.smoothStairs(), base);
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.smoothSlab(), base, 2);
					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.smoothWall(), base);

					stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.cut(), base);
						stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.cutStairs(), base);
						stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.cutSlab(), base, 2);
						stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.cutWall(), base);
					}

					// —— 简版系列（末地石 / 紫珀 / 下界红砖）：仅楼梯 / 台阶 / 墙 ——
					for (SimpleFamily family : ModBlocks.SIMPLE_FAMILIES) {
						Block base = family.base();

						// 合成台配方（以基座为原料）。
						stairBuilder(family.stairs(), Ingredient.of(base))
								.unlockedBy(getHasName(base), has(base))
								.save(exporter);
						slabBuilder(RecipeCategory.BUILDING_BLOCKS, family.slab(), Ingredient.of(base))
								.unlockedBy(getHasName(base), has(base))
								.save(exporter);
						wallBuilder(RecipeCategory.BUILDING_BLOCKS, family.wall(), Ingredient.of(base))
								.unlockedBy(getHasName(base), has(base))
								.save(exporter);

						// 切石机配方（从基座一步切出楼梯 / 台阶 / 墙）。
						stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.stairs(), base);
						stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.slab(), base, 2);
						stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, family.wall(), base);
					}

					// —— 装备配方：三套盔甲 / 工具，用对应宝石按原版形状合成 ——
					// 青金石 / 绿宝石用「宝石本身」，紫水晶用「紫水晶块」（AMETHYST_BLOCK）。
					armorAndTools(exporter, Items.LAPIS_LAZULI,
							ModEquipment.LAPIS_HELMET, ModEquipment.LAPIS_CHESTPLATE,
							ModEquipment.LAPIS_LEGGINGS, ModEquipment.LAPIS_BOOTS,
							ModEquipment.LAPIS_SWORD, ModEquipment.LAPIS_SPEAR, ModEquipment.LAPIS_PICKAXE,
							ModEquipment.LAPIS_AXE, ModEquipment.LAPIS_SHOVEL, ModEquipment.LAPIS_HOE);
					armorAndTools(exporter, Items.EMERALD,
							ModEquipment.EMERALD_HELMET, ModEquipment.EMERALD_CHESTPLATE,
							ModEquipment.EMERALD_LEGGINGS, ModEquipment.EMERALD_BOOTS,
							ModEquipment.EMERALD_SWORD, ModEquipment.EMERALD_SPEAR, ModEquipment.EMERALD_PICKAXE,
							ModEquipment.EMERALD_AXE, ModEquipment.EMERALD_SHOVEL, ModEquipment.EMERALD_HOE);
					armorAndTools(exporter, Items.AMETHYST_BLOCK,
							ModEquipment.AMETHYST_HELMET, ModEquipment.AMETHYST_CHESTPLATE,
							ModEquipment.AMETHYST_LEGGINGS, ModEquipment.AMETHYST_BOOTS,
							ModEquipment.AMETHYST_SWORD, ModEquipment.AMETHYST_SPEAR, ModEquipment.AMETHYST_PICKAXE,
							ModEquipment.AMETHYST_AXE, ModEquipment.AMETHYST_SHOVEL, ModEquipment.AMETHYST_HOE);

					// —— 手套配方 ——
					// 工作台「手掌形」（# # / ### / ·##，共 7 个材料），每种手套用对应材质材料。
					// 下界合金手套不走工作台，改为「钻石手套 + 下界合金升级模板 + 下界合金锭」锻造升级（同原版下界合金逻辑）。
					gloveCraft(exporter, ModGloves.LEATHER.item(), Items.LEATHER);
					gloveCraft(exporter, ModGloves.CHAINMAIL.item(), Items.IRON_NUGGET);
					gloveCraft(exporter, ModGloves.COPPER.item(), Items.COPPER_INGOT);
					gloveCraft(exporter, ModGloves.IRON.item(), Items.IRON_INGOT);
					gloveCraft(exporter, ModGloves.GOLDEN.item(), Items.GOLD_INGOT);
					gloveCraft(exporter, ModGloves.DIAMOND.item(), Items.DIAMOND);
					gloveCraft(exporter, ModGloves.LAPIS.item(), Items.LAPIS_LAZULI);
					gloveCraft(exporter, ModGloves.EMERALD.item(), Items.EMERALD);
					gloveCraft(exporter, ModGloves.AMETHYST.item(), Items.AMETHYST_BLOCK);
					// 下界合金手套：钻石手套 + 下界合金升级模板 + 下界合金锭（锻造台）。
					netheriteSmithing(ModGloves.DIAMOND.item(), RecipeCategory.COMBAT, ModGloves.NETHERITE.item());
				}

				/**
				 * 手套工作台配方：「手掌形」布局，共 7 个同种材料。
				 * <pre>
				 *   # #     (食指 · 中指 · 无名指位置：左、右)
				 *   # # #   (掌背三格)
				 *   . # #   (手腕两格)
				 * </pre>
				 */
				private void gloveCraft(RecipeOutput out, Item glove, Item mat) {
					shaped(RecipeCategory.COMBAT, glove)
							.pattern("# #")
							.pattern("###")
							.pattern(" ##")
							.define('#', mat)
							.unlockedBy(getHasName(mat), has(mat))
							.save(out);
				}

				/**
				 * 用一种宝石材料合成整套盔甲与工具（标准原版合成形状）。
				 *
				 * @param mat        宝石材料（如青金石 / 绿宝石 / 紫水晶块）
				 * @param helmet     头盔 / 胸甲 / 护腿 / 靴子
				 * @param sword      剑 / 矛 / 镐 / 斧 / 锹 / 锄
				 */
				private void armorAndTools(RecipeOutput out, Item mat,
						Item helmet, Item chestplate, Item leggings, Item boots,
						Item sword, Item spear, Item pickaxe, Item axe, Item shovel, Item hoe) {
					// 盔甲（RecipeCategory.COMBAT）。
					shaped(RecipeCategory.COMBAT, helmet)
							.pattern("XXX").pattern("X X")
							.define('X', mat).unlockedBy(getHasName(mat), has(mat)).save(out);
					shaped(RecipeCategory.COMBAT, chestplate)
							.pattern("X X").pattern("XXX").pattern("XXX")
							.define('X', mat).unlockedBy(getHasName(mat), has(mat)).save(out);
					shaped(RecipeCategory.COMBAT, leggings)
							.pattern("XXX").pattern("X X").pattern("X X")
							.define('X', mat).unlockedBy(getHasName(mat), has(mat)).save(out);
					shaped(RecipeCategory.COMBAT, boots)
							.pattern("X X").pattern("X X")
							.define('X', mat).unlockedBy(getHasName(mat), has(mat)).save(out);

					// 工具（剑/锹 COMBAT/TOOLS，镐/斧/锄 TOOLS），棍用 minecraft:stick。
					shaped(RecipeCategory.COMBAT, sword)
							.pattern("X").pattern("X").pattern("S")
							.define('X', mat).define('S', Items.STICK)
							.unlockedBy(getHasName(mat), has(mat)).save(out);
					// 矛：原版矛形状——右上角放材料，中格与左下角放棍。
					//   " X"
					//   "S "   （原版为 3×3 的 "  X"/" S "/"S  "，此处用棍 S 表示 minecraft:stick）
					shaped(RecipeCategory.COMBAT, spear)
							.pattern("  X").pattern(" S ").pattern("S  ")
							.define('X', mat).define('S', Items.STICK)
							.unlockedBy(getHasName(mat), has(mat)).save(out);
					shaped(RecipeCategory.TOOLS, pickaxe)
							.pattern("XXX").pattern(" S ").pattern(" S ")
							.define('X', mat).define('S', Items.STICK)
							.unlockedBy(getHasName(mat), has(mat)).save(out);
					shaped(RecipeCategory.TOOLS, axe)
							.pattern("XX").pattern("XS").pattern(" S")
							.define('X', mat).define('S', Items.STICK)
							.unlockedBy(getHasName(mat), has(mat)).save(out);
					shaped(RecipeCategory.TOOLS, shovel)
							.pattern("X").pattern("S").pattern("S")
							.define('X', mat).define('S', Items.STICK)
							.unlockedBy(getHasName(mat), has(mat)).save(out);
					shaped(RecipeCategory.TOOLS, hoe)
							.pattern("XX").pattern(" S").pattern(" S")
							.define('X', mat).define('S', Items.STICK)
							.unlockedBy(getHasName(mat), has(mat)).save(out);
				}

			/** 为「一个整块」生成楼梯 / 台阶 / 墙 的合成台配方。 */
			private void craftVariants(RecipeOutput out, Block whole, Block stairs, Block slab, Block wall) {
				stairBuilder(stairs, Ingredient.of(whole))
						.unlockedBy(getHasName(whole), has(whole))
						.save(out);
				slabBuilder(RecipeCategory.BUILDING_BLOCKS, slab, Ingredient.of(whole))
						.unlockedBy(getHasName(whole), has(whole))
						.save(out);
				wallBuilder(RecipeCategory.BUILDING_BLOCKS, wall, Ingredient.of(whole))
						.unlockedBy(getHasName(whole), has(whole))
						.save(out);
			}
		};
	}

	@Override
	public String getName() {
		return "ModRecipeProvider";
	}
}
