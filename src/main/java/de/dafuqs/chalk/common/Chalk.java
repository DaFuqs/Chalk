package de.dafuqs.chalk.common;

import com.mclegoman.releasetypeutils.common.version.Helper;
import de.dafuqs.chalk.common.data.Data;
import net.fabricmc.api.ModInitializer;

public class Chalk implements ModInitializer {
	@Override
	public void onInitialize() {
		Data.CURRENT_VERSION.sendToLog(Helper.LogType.INFO, "Initializing Chalk...");
		ChalkRegistry.init();
		Data.CURRENT_VERSION.sendToLog(Helper.LogType.INFO, "Finished initializing Chalk!");
	}
}