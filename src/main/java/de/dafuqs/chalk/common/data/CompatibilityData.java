package de.dafuqs.chalk.common.data;

import net.fabricmc.loader.api.FabricLoader;

public class CompatibilityData {
	public static boolean COLORFUL_ADDON;
	public static boolean CONTINUITY;

	static {
		COLORFUL_ADDON = (Data.getLoaderType() == Data.LoaderType.FABRIC || Data.getLoaderType() == Data.LoaderType.QUILT) && FabricLoader.getInstance().isModLoaded("chalk-colorful-addon");
		CONTINUITY = (Data.getLoaderType() == Data.LoaderType.FABRIC || Data.getLoaderType() == Data.LoaderType.QUILT) && FabricLoader.getInstance().isModLoaded("continuity");
	}
}