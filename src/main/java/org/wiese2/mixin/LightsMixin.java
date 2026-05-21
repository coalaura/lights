package org.wiese2.mixin;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wiese2.Lights;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

@Mixin(LevelRenderer.class)
public class LightsMixin {

	private void drawCarpet(VertexConsumer buffer, Matrix4f matrix, BlockPos pos, float cx, float cy, float cz, int r,
			int g, int b, int a, float u0, float u1, float v0, float v1, int overlay, int light) {
		float x = pos.getX() - cx;
		float y = pos.getY() - cy;
		float z = pos.getZ() - cz;
		float h = 0.0625f;

		// Top
		buffer.addVertex(matrix, x, y + h, z).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light)
				.setNormal(0, 1, 0);
		buffer.addVertex(matrix, x, y + h, z + 1).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light)
				.setNormal(0, 1, 0);
		buffer.addVertex(matrix, x + 1, y + h, z + 1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, 1, 0);
		buffer.addVertex(matrix, x + 1, y + h, z).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light)
				.setNormal(0, 1, 0);

		// North (-Z)
		buffer.addVertex(matrix, x, y + h, z).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light)
				.setNormal(0, 0, -1);
		buffer.addVertex(matrix, x, y, z).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light)
				.setNormal(0, 0, -1);
		buffer.addVertex(matrix, x + 1, y, z).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light)
				.setNormal(0, 0, -1);
		buffer.addVertex(matrix, x + 1, y + h, z).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light)
				.setNormal(0, 0, -1);

		// South (+Z)
		buffer.addVertex(matrix, x + 1, y + h, z + 1).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, 1);
		buffer.addVertex(matrix, x + 1, y, z + 1).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light)
				.setNormal(0, 0, 1);
		buffer.addVertex(matrix, x, y, z + 1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light)
				.setNormal(0, 0, 1);
		buffer.addVertex(matrix, x, y + h, z + 1).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light)
				.setNormal(0, 0, 1);

		// West (-X)
		buffer.addVertex(matrix, x, y + h, z).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay).setLight(light)
				.setNormal(-1, 0, 0);
		buffer.addVertex(matrix, x, y, z).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light)
				.setNormal(-1, 0, 0);
		buffer.addVertex(matrix, x, y, z + 1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light)
				.setNormal(-1, 0, 0);
		buffer.addVertex(matrix, x, y + h, z + 1).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light)
				.setNormal(-1, 0, 0);

		// East (+X)
		buffer.addVertex(matrix, x + 1, y + h, z + 1).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay)
				.setLight(light).setNormal(1, 0, 0);
		buffer.addVertex(matrix, x + 1, y, z + 1).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay).setLight(light)
				.setNormal(1, 0, 0);
		buffer.addVertex(matrix, x + 1, y, z).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay).setLight(light)
				.setNormal(1, 0, 0);
		buffer.addVertex(matrix, x + 1, y + h, z).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay).setLight(light)
				.setNormal(1, 0, 0);
	}

	private void drawTorch(VertexConsumer buffer, Matrix4f matrix, BlockPos pos, float cx, float cy, float cz, int r,
			int g, int b, int a, float u0, float u1, float v0, float v1, int overlay, int light) {
		float x = pos.getX() - cx;
		float y = pos.getY() - cy;
		float z = pos.getZ() - cz;

		float minX = x + 0.4f, maxX = x + 0.6f;
		float minY = y + 0.0f, maxY = y + 0.6f;
		float minZ = z + 0.4f, maxZ = z + 0.6f;

		// Top
		buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, 1, 0);
		buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, 1, 0);
		buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, 1, 0);
		buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, 1, 0);

		// Bottom
		buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, -1, 0);
		buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, -1, 0);
		buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, -1, 0);
		buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, -1, 0);

		// North (-Z)
		buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, -1);
		buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, -1);
		buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, -1);
		buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, -1);

		// South (+Z)
		buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, 1);
		buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, 1);
		buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, 1);
		buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay)
				.setLight(light).setNormal(0, 0, 1);

		// West (-X)
		buffer.addVertex(matrix, minX, maxY, maxZ).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay)
				.setLight(light).setNormal(-1, 0, 0);
		buffer.addVertex(matrix, minX, minY, maxZ).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay)
				.setLight(light).setNormal(-1, 0, 0);
		buffer.addVertex(matrix, minX, minY, minZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay)
				.setLight(light).setNormal(-1, 0, 0);
		buffer.addVertex(matrix, minX, maxY, minZ).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay)
				.setLight(light).setNormal(-1, 0, 0);

		// East (+X)
		buffer.addVertex(matrix, maxX, maxY, minZ).setColor(r, g, b, a).setUv(u0, v0).setOverlay(overlay)
				.setLight(light).setNormal(1, 0, 0);
		buffer.addVertex(matrix, maxX, minY, minZ).setColor(r, g, b, a).setUv(u0, v1).setOverlay(overlay)
				.setLight(light).setNormal(1, 0, 0);
		buffer.addVertex(matrix, maxX, minY, maxZ).setColor(r, g, b, a).setUv(u1, v1).setOverlay(overlay)
				.setLight(light).setNormal(1, 0, 0);
		buffer.addVertex(matrix, maxX, maxY, maxZ).setColor(r, g, b, a).setUv(u1, v0).setOverlay(overlay)
				.setLight(light).setNormal(1, 0, 0);
	}

	@Inject(method = "renderLevel", at = @At("HEAD"))
	private void renderLightOverlays(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker,
			boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix,
			Matrix4f frustumMatrix, GpuBufferSlice slice, Vector4f vector, boolean bool, CallbackInfo ci) {
		if (!Lights.isVisible || (Lights.dangerousBlocks.isEmpty())) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();

		if (mc.player == null) {
			return;
		}

		PoseStack poseStack = new PoseStack();

		Matrix4f matrix = poseStack.last().pose();

		RenderType renderType = RenderType
				.entityTranslucent(ResourceLocation.parse("minecraft:textures/block/white_concrete.png"), true);

		BufferSource bufferSource = mc.renderBuffers().bufferSource();
		VertexConsumer buffer = bufferSource.getBuffer(renderType);

		float u0 = 0.0f;
		float u1 = 1.0f;
		float v0 = 0.0f;
		float v1 = 1.0f;

		int overlay = OverlayTexture.NO_OVERLAY;
		int light = 15728880;
		int alpha = 120;

		float cx = (float) camera.getPosition().x;
		float cy = (float) camera.getPosition().y;
		float cz = (float) camera.getPosition().z;

		for (BlockPos pos : Lights.dangerousBlocks) {
			drawCarpet(buffer, matrix, pos, cx, cy, cz, 255, 0, 0, alpha, u0, u1, v0, v1, overlay, light);
		}

		if (Lights.config.showOptimalTorches) {
			for (BlockPos pos : Lights.optimalTorches) {
				drawTorch(buffer, matrix, pos, cx, cy, cz, 255, 200, 0, 200, u0, u1, v0, v1, overlay, light);
			}
		}
	}
}