package org.wiese2;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class Lights implements ClientModInitializer {
	public static final String ModId = "lights";

	public static LightsConfig config = new LightsConfig();

	public static KeyMapping visibilityKey;

	public static boolean isVisible = false;
	public static boolean isToggled = false;

	public static final Set<BlockPos> dangerousBlocks = new HashSet<>();

	private static int tickCounter = 0;

	@Override
	public void onInitializeClient() {
		visibilityKey = KeyBindingHelper.registerKeyBinding(
				new KeyMapping("key.lights.visibility", GLFW.GLFW_KEY_F7, KeyMapping.Category.MISC));

		ClientTickEvents.END_CLIENT_TICK.register(client -> updateLightOverlay(client));
	}

	private static void updateLightOverlay(Minecraft client) {
		if (client.player == null || client.level == null) {
			return;
		}

		switch (config.visibilityMode) {
			case ALWAYS -> {
				isVisible = true;

				while (visibilityKey.consumeClick()) {
				}
			}
			case TOGGLE -> {
				while (visibilityKey.consumeClick()) {
					isToggled = !isToggled;
				}

				isVisible = isToggled;
			}
			case HOLD -> {
				isVisible = visibilityKey.isDown();

				while (visibilityKey.consumeClick()) {
				}
			}
		}

		if (!isVisible) {
			dangerousBlocks.clear();

			return;
		}

		tickCounter++;

		if (tickCounter % 4 == 0) {
			updateLightMap(client.level, client.player.blockPosition());
		}
	}

	private static void updateLightMap(Level level, BlockPos center) {
		dangerousBlocks.clear();

		int hRadius = config.horizontalRadius;
		int vRadius = config.verticalRadius;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos below = new BlockPos.MutableBlockPos();

		for (int x = -hRadius; x <= hRadius; x++) {
			for (int y = -vRadius; y <= vRadius; y++) {
				for (int z = -hRadius; z <= hRadius; z++) {
					pos.setWithOffset(center, x, y, z);

					if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
							&& level.getFluidState(pos).isEmpty()) {

						below.setWithOffset(pos, 0, -1, 0);

						if (level.getBlockState(below).isSolidRender()) {
							int blockLight = level.getBrightness(LightLayer.BLOCK, pos);

							if (blockLight == 0) {
								dangerousBlocks.add(pos.immutable());
							}
						}
					}
				}
			}
		}
	}
}