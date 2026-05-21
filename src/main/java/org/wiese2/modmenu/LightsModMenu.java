package org.wiese2.modmenu;

import org.wiese2.Lights;
import org.wiese2.LightsConfig;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class LightsModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return LightsConfigScreen::new;
	}

	public static class LightsConfigScreen extends Screen {
		private final Screen parent;

		private final LightsConfig.VisibilityMode originalVisibilityMode;
		private LightsConfig.VisibilityMode visibilityMode;

		private final int originalHorizontalRadius;
		private int horizontalRadius;

		private final int originalVerticalRadius;
		private int verticalRadius;

		private static final int[] H_OPTIONS = { 8, 16, 24, 32, 48, 64 };
		private static final int[] V_OPTIONS = { 8, 16, 24, 32, 48, 64, 96, 128 };

		public LightsConfigScreen(Screen parent) {
			super(Component.literal("Lights Config"));

			this.parent = parent;

			this.visibilityMode = Lights.config.visibilityMode;
			this.originalVisibilityMode = this.visibilityMode;
			this.horizontalRadius = Lights.config.horizontalRadius;
			this.originalHorizontalRadius = this.horizontalRadius;
			this.verticalRadius = Lights.config.verticalRadius;
			this.originalVerticalRadius = this.verticalRadius;
		}

		private int nextOption(int current, int[] options) {
			for (int i = 0; i < options.length; i++) {
				if (options[i] > current) {
					return options[i];
				}
			}

			return options[0];
		}

		@Override
		protected void init() {
			int buttonW = 150;
			int buttonH = 20;
			int rowY = 60;

			int contentW = buttonW * 2 + 10; // 310
			int startX = (this.width - contentW) / 2;
			int buttonX = startX + contentW - buttonW;

			addRenderableWidget(Button.builder(getModeText(visibilityMode), btn -> {
				visibilityMode = LightsConfig.VisibilityMode.values()[(visibilityMode.ordinal() + 1)
						% LightsConfig.VisibilityMode.values().length];

				Lights.config.visibilityMode = visibilityMode;

				btn.setMessage(getModeText(visibilityMode));
			}).bounds(buttonX, rowY, buttonW, buttonH).build());

			addRenderableWidget(Button.builder(Component.literal(String.valueOf(horizontalRadius)), btn -> {
				horizontalRadius = nextOption(horizontalRadius, H_OPTIONS);

				Lights.config.horizontalRadius = horizontalRadius;

				btn.setMessage(Component.literal(String.valueOf(horizontalRadius)));
			}).bounds(buttonX, rowY + 30, buttonW, buttonH).build());

			addRenderableWidget(Button.builder(Component.literal(String.valueOf(verticalRadius)), btn -> {
				verticalRadius = nextOption(verticalRadius, V_OPTIONS);

				Lights.config.verticalRadius = verticalRadius;

				btn.setMessage(Component.literal(String.valueOf(verticalRadius)));
			}).bounds(buttonX, rowY + 60, buttonW, buttonH).build());

			int bottomY = this.height - 30;

			addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> {
				Lights.config.visibilityMode = originalVisibilityMode;
				Lights.config.horizontalRadius = originalHorizontalRadius;
				Lights.config.verticalRadius = originalVerticalRadius;

				this.minecraft.setScreen(parent);
			}).bounds(startX, bottomY, buttonW, buttonH).build());

			addRenderableWidget(Button.builder(Component.literal("Save & Quit"), btn -> {
				Lights.config.visibilityMode = visibilityMode;
				Lights.config.horizontalRadius = horizontalRadius;
				Lights.config.verticalRadius = verticalRadius;

				Lights.config.save();

				this.minecraft.setScreen(parent);
			}).bounds(startX + buttonW + 10, bottomY, buttonW, buttonH).build());
		}

		private Component getModeText(LightsConfig.VisibilityMode mode) {
			String text = mode.name().charAt(0) + mode.name().substring(1).toLowerCase();

			return Component.literal(text);
		}

		@Override
		public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
			super.render(graphics, mouseX, mouseY, delta);

			graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);

			int contentW = 150 * 2 + 10; // 310
			int startX = (this.width - contentW) / 2;
			int rowY = 60;

			graphics.drawString(this.font, Component.literal("Visibility Mode"), startX, rowY + 6, 0xFFFFFFFF);
			graphics.drawString(this.font, Component.literal("Horizontal Radius"), startX, rowY + 36, 0xFFFFFFFF);
			graphics.drawString(this.font, Component.literal("Vertical Radius"), startX, rowY + 66, 0xFFFFFFFF);
		}

		@Override
		public void onClose() {
			Lights.config.visibilityMode = originalVisibilityMode;
			Lights.config.horizontalRadius = originalHorizontalRadius;
			Lights.config.verticalRadius = originalVerticalRadius;

			this.minecraft.setScreen(parent);
		}
	}
}