package de.dafuqs.chalk.util;

import net.fabricmc.loader.api.*;

public class FabricLoaderAccess {
	
	public static boolean isColorfulAddonLoaded() {
		return FabricLoader.getInstance().isModLoaded("chalk-colorful-addon");
	}
	
	public static boolean isContinuityLoaded() {
		return FabricLoader.getInstance().isModLoaded("continuity");
	}
	
}
