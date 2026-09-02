package com.example.mcplusmod.datagen;

import java.util.Optional;

import com.example.mcplusmod.ModBlocks;
import com.example.mcplusmod.ModEquipment;
import com.example.mcplusmod.block.SimpleFamily;
import com.example.mcplusmod.block.StoneFamily;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * 模型与方块状态数据生成。
 *
 * <p>每个方块都使用「自己命名的纹理」（{@code mcplusmod:block/<name>}），因此对每个整块变种
 * 调用 {@link BlockModelGenerators#createTrivialCube(Block)} 生成 {@code cube_all} 模型；
 * 楼梯/台阶/墙则用 {@code family(base)} 家族方法，让它们复用基座纹理。
 *
 * <p>说明：{@code family(base)} 会顺带为基座生成一份 cube_all 模型，因此基座本身不再单独
 * 调用 createTrivialCube，以免重复生成同名模型报错。
 */
public class ModModelProvider extends FabricModelProvider {
	public ModModelProvider(FabricPackOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
		for (StoneFamily family : ModBlocks.FAMILIES) {
			// 基座 + 楼梯 + 台阶 + 墙 复用同一套「基座纹理」。
			// family(base) 会为基座生成 cube_all，并为子项生成对应模型/方块状态。
			generators.family(family.base())
					.stairs(family.stairs())
					.slab(family.slab())
					.wall(family.wall());

			// 雕纹整块（独立纹理）+ 楼梯 / 台阶 / 墙 复用雕纹纹理。
			generators.family(family.chiseled())
					.stairs(family.chiseledStairs())
					.slab(family.chiseledSlab())
					.wall(family.chiseledWall());

			// 平滑整块（独立纹理）+ 楼梯 / 台阶 / 墙 复用平滑纹理。
			generators.family(family.smooth())
					.stairs(family.smoothStairs())
					.slab(family.smoothSlab())
					.wall(family.smoothWall());

			// 切制整块（独立纹理）+ 楼梯 / 台阶 / 墙 复用切制纹理。
			generators.family(family.cut())
					.stairs(family.cutStairs())
					.slab(family.cutSlab())
					.wall(family.cutWall());
		}

		// 简版系列：仅基座 + 楼梯 + 台阶 + 墙。
		for (SimpleFamily family : ModBlocks.SIMPLE_FAMILIES) {
			generators.family(family.base())
					.stairs(family.stairs())
					.slab(family.slab())
					.wall(family.wall());
		}
	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {
		// 盔甲与材料物品：普通「平面」物品模型（item/generated），纹理位于 textures/item/<name>.png。
		for (Item item : ModEquipment.FLAT_ITEM_MODELS) {
			generators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
		}
		// 工具物品：手持模型（item/handheld），使玩家手中呈斜握姿态。
		for (Item item : ModEquipment.HANDHELD_ITEM_MODELS) {
			generators.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
		}
		// 手套：使用自定义 parent 模型 mcplusmod:item/glove_handheld —— 物品栏图标不变（gui/fixed 保持原样），
		// 但第一 / 第三人称「手持」时应用挥手姿态的 display 变换。
		ModelTemplate gloveTemplate = new ModelTemplate(
				Optional.of(Identifier.fromNamespaceAndPath("mcplusmod", "item/glove_handheld")),
				Optional.empty(),
				TextureSlot.LAYER0);
		for (com.example.mcplusmod.ModGloves.GloveEntry g : com.example.mcplusmod.ModGloves.ALL) {
			generators.generateFlatItem(g.item(), gloveTemplate);
		}
	}
}
