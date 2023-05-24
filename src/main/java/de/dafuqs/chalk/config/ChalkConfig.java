package de.dafuqs.chalk.config;

import com.mclegoman.simplefabric.fabric_simplelibs.simple_config.SimpleConfig;
import com.mojang.datafixers.util.Pair;

public class ChalkConfig {
    public static SimpleConfig CONFIG;
    private static ChalkConfigProvider CONFIG_PROVIDER;
    public static boolean EMIT_PARTICLES;
    public static void init() {
        CONFIG_PROVIDER = new ChalkConfigProvider();
        create();
        CONFIG = SimpleConfig.of("chalk").provider(CONFIG_PROVIDER).request();
        assign();
    }
    private static void create() {
        CONFIG_PROVIDER.add("chalk properties file");
        CONFIG_PROVIDER.add(new Pair<>("emit_particles", true));
    }
    private static void assign() {
        EMIT_PARTICLES = CONFIG.getOrDefault("emit_particles", true);
    }
}