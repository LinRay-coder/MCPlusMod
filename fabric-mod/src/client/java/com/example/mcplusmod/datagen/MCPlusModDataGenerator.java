package com.example.mcplusmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * 数据生成入口（fabric-datagen entrypoint）。
 *
 * <p>运行 {@code gradlew runDatagen} 时会调用本类，注册各 Provider，
 * 生成结果输出到 {@code src/main/generated}。
 */
public class MCPlusModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockLootTableProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModLanguageProvider.English::new);
		pack.addProvider(ModLanguageProvider.Chinese::new);
	}
}
