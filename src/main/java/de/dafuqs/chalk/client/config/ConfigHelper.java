package de.dafuqs.chalk.client.config;

import com.mclegoman.releasetypeutils.common.version.Helper;
import de.dafuqs.chalk.common.data.Data;
import net.minecraft.client.MinecraftClient;

public class ConfigHelper {
	protected static final int SAVE_VIA_TICK_SAVE_TICK = 20;
	protected static boolean SAVE_VIA_TICK;
	protected static int SAVE_VIA_TICK_TICKS;

	public static void init() {
		Config.init();
		updateConfig();
	}

	public static void tick(MinecraftClient client) {
		if (SAVE_VIA_TICK_TICKS < SAVE_VIA_TICK_SAVE_TICK) SAVE_VIA_TICK_TICKS += 1;
		else {
			if (SAVE_VIA_TICK) {
				saveConfig(false);
				SAVE_VIA_TICK = false;
			}
			SAVE_VIA_TICK_TICKS = 0;
		}
	}

	protected static void updateConfig() {
		if ((int) getConfig("config_version") != ConfigDefaults.CONFIG_VERSION) {
			Data.CURRENT_VERSION.sendToLog(Helper.LogType.INFO, "Updating config to the latest version.");

			/*
			 * If any config options values change how they are used, update them here.
			 * example: if ((int)getConfig("config_version") < 2) { setConfig("configOption", (int)getConfig("configOption") / 20) }
			 */

			setConfig("config_version", ConfigDefaults.CONFIG_VERSION);
			saveConfig(false);
		}
	}

	public static void saveConfig(boolean onTick) {
		if (onTick) {
			SAVE_VIA_TICK = true;
		} else {
			Data.CURRENT_VERSION.sendToLog(Helper.LogType.INFO, "Writing config to file.");
			Config.save();
			Config.CONFIG_PROVIDER.saveConfig(Config.ID);
		}
	}

	public static void resetConfig() {
		setConfig("emit_particles", ConfigDefaults.EMIT_PARTICLES);
		setConfig("config_version", ConfigDefaults.CONFIG_VERSION);
	}

	public static void setConfig(String ID, Object VALUE) {
		switch (ID) {
			case "emit_particles" -> Config.EMIT_PARTICLES = (boolean) VALUE;
			case "config_version" -> Config.CONFIG_VERSION = (int) VALUE;
		}
	}

	public static Object getConfig(String ID) {
		switch (ID) {
			case "emit_particles" -> {
				return Config.EMIT_PARTICLES;
			}
			case "config_version" -> {
				return Config.CONFIG_VERSION;
			}
		}
		return new Object();
	}
}