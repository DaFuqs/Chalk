package de.dafuqs.chalk.util;

import de.dafuqs.chalk.Chalk;
import net.fabricmc.loader.api.FabricLoader;

/* This class was added by MCLegoMan for the 1.19.3 port. */
public class ChalkLoader {
	
	public static boolean isFabric;
	public static boolean isQuilt;
	public static boolean isOther;
	
	public static void detectLoader() {
		Chalk.log("Detecting Loader...");
		if (FabricLoader.getInstance().getModContainer("quilt_loader").isPresent()) {
			isQuilt = true;
			Chalk.log("Detected Loader: Quilt");
		} else if (FabricLoader.getInstance().getModContainer("fabricloader").isPresent()) {
			isFabric = true;
			Chalk.log("Detected Loader: Fabric");
		} else {
			isOther = true;
			Chalk.log("Detected Loader: Other");
		}
	}
	
	/**
	 * Checks for the presense of the Addon mod.
	 * detectLoader() has had to be called some time before
	 */
	public static boolean isColorfulAddonLoaded() {
		if (isFabric || isQuilt) {
			return FabricLoaderAccess.isColorfulAddonLoaded();
		}
		return false;
	}
	
}