package de.dafuqs.chalk.client.config;

import com.mclegoman.simplefabriclibs.simple_config.SimpleConfig;
import com.mojang.datafixers.util.Pair;
import de.dafuqs.chalk.common.data.Data;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Config {
	protected static final String ID = Data.CURRENT_VERSION.getID();
	protected static SimpleConfig CONFIG;
	protected static ConfigProvider CONFIG_PROVIDER;
	protected static boolean EMIT_PARTICLES;
	protected static int CONFIG_VERSION;

	protected static void init() {
		CONFIG_PROVIDER = new ConfigProvider();
		create();
		CONFIG = SimpleConfig.of(ID).provider(CONFIG_PROVIDER).request();
		assign();
	}

	protected static void create() {
		CONFIG_PROVIDER.add(ID, new Pair<>("emit_particles", ConfigDefaults.EMIT_PARTICLES));
		CONFIG_PROVIDER.add(ID, new Pair<>("config_version", ConfigDefaults.CONFIG_VERSION));
	}

	protected static void assign() {
		EMIT_PARTICLES = CONFIG.getOrDefault("emit_particles", ConfigDefaults.EMIT_PARTICLES);
		CONFIG_VERSION = CONFIG.getOrDefault("config_version", ConfigDefaults.CONFIG_VERSION);
	}

	protected static void save() {
		CONFIG_PROVIDER.setConfig("emit_particles", EMIT_PARTICLES);
		CONFIG_PROVIDER.setConfig("config_version", CONFIG_VERSION);
	}
}