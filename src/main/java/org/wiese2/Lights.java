package org.wiese2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	public static final Set<BlockPos> optimalTorches = new HashSet<>();

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
			optimalTorches.clear();

			return;
		}

		tickCounter++;

		if (tickCounter % 4 == 0) {
			updateLightMap(client.level, client.player.blockPosition());
		}
	}

	private static void updateLightMap(Level level, BlockPos center) {
		dangerousBlocks.clear();
		optimalTorches.clear();

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

		if (config.showOptimalTorches && !dangerousBlocks.isEmpty()) {
			int cellSizeH = 13;
			int cellSizeV = 7;

			Map<BlockPos, List<BlockPos>> cells = new HashMap<>();

			for (BlockPos dangerPos : dangerousBlocks) {
				int cx = Math.floorDiv(dangerPos.getX(), cellSizeH);
				int cy = Math.floorDiv(dangerPos.getY(), cellSizeV);
				int cz = Math.floorDiv(dangerPos.getZ(), cellSizeH);

				BlockPos cellPos = new BlockPos(cx, cy, cz);

				cells.computeIfAbsent(cellPos, k -> new ArrayList<>()).add(dangerPos);
			}

			List<BlockPos> candidates = new ArrayList<>();

			for (List<BlockPos> blocks : cells.values()) {
				long sumX = 0, sumY = 0, sumZ = 0;

				for (BlockPos p : blocks) {
					sumX += p.getX();
					sumY += p.getY();
					sumZ += p.getZ();
				}

				int avgX = (int) (sumX / blocks.size());
				int avgY = (int) (sumY / blocks.size());
				int avgZ = (int) (sumZ / blocks.size());

				BlockPos best = null;

				int bestDist = Integer.MAX_VALUE;

				for (BlockPos p : blocks) {
					int dist = Math.abs(p.getX() - avgX) + Math.abs(p.getY() - avgY) + Math.abs(p.getZ() - avgZ);

					if (dist < bestDist) {
						bestDist = dist;
						best = p;
					} else if (dist == bestDist && best != null) {
						if (p.getY() > best.getY() || (p.getY() == best.getY() && p.getX() > best.getX())
								|| (p.getY() == best.getY() && p.getX() == best.getX() && p.getZ() > best.getZ())) {
							best = p;
						}
					}
				}

				if (best != null) {
					candidates.add(best);
				}
			}

			candidates.sort(Comparator.comparingInt((BlockPos p) -> p.getY()).thenComparingInt(p -> p.getX())
					.thenComparingInt(p -> p.getZ()));

			for (BlockPos candidate : candidates) {
				boolean tooClose = false;

				for (BlockPos kept : optimalTorches) {
					int dist = Math.abs(candidate.getX() - kept.getX()) + Math.abs(candidate.getY() - kept.getY())
							+ Math.abs(candidate.getZ() - kept.getZ());

					if (dist <= 9) {
						tooClose = true;

						break;
					}
				}

				if (!tooClose) {
					optimalTorches.add(candidate);
				}
			}
		}
	}
}