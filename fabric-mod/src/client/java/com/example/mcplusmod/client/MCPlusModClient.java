package com.example.mcplusmod.client;

import net.fabricmc.api.ClientModInitializer;

/**
 * 客户端入口（client entrypoint）。
 *
 * <p>26.1 起方块的渲染层由游戏根据纹理自动推断，client item（物品模型定义）由 datagen 生成，
 * 因此本类目前无需额外逻辑。若将来需要注册方块颜色、特殊渲染或按键绑定，可在此添加。
 */
public class MCPlusModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 暂无客户端专属初始化逻辑。
	}
}
