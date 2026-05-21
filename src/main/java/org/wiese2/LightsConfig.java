package org.wiese2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class LightsConfig {
	private static final Gson GsonBuilder = new GsonBuilder().setPrettyPrinting().create();
	private static final Path ConfigPath = FabricLoader.getInstance().getConfigDir().resolve("lights.json");

	public enum VisibilityMode {
		HOLD, TOGGLE, ALWAYS
	}

	public VisibilityMode visibilityMode = VisibilityMode.TOGGLE;

	public int horizontalRadius = 24;
	public int verticalRadius = 32;

	public boolean showOptimalTorches = false;

	public LightsConfig() {
		load();
	}

	public void load() {
		if (Files.exists(ConfigPath)) {
			try {
				LightsConfig loaded = GsonBuilder.fromJson(Files.readString(ConfigPath), LightsConfig.class);

				if (loaded.visibilityMode != null) {
					this.visibilityMode = loaded.visibilityMode;
				}

				if (loaded.horizontalRadius > 0) {
					this.horizontalRadius = loaded.horizontalRadius;
				}

				if (loaded.verticalRadius > 0) {
					this.verticalRadius = loaded.verticalRadius;
				}

				this.showOptimalTorches = loaded.showOptimalTorches;
			} catch (Exception e) {
				// ignored, keep defaults
			}
		}
	}

	public void save() {
		try {
			Files.writeString(ConfigPath, GsonBuilder.toJson(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}