package de.dafuqs.chalk.common;

import de.dafuqs.chalk.config.ChalkConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Chalk implements ModInitializer {
	
	public static final String MOD_ID = "chalk";
	
	public static ChalkConfig CONFIG;
	
	@Override
	public void onInitialize() {
		ChalkRegistry.init();
		
		AutoConfig.register(ChalkConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ChalkConfig.class).getConfig();
	}
	
	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(MOD_ID, name);
	}
}