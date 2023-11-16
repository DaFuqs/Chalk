package de.dafuqs.chalk.common.data;

import com.mclegoman.releasetypeutils.common.version.Helper;
import net.fabricmc.loader.api.FabricLoader;

public class Data extends com.mclegoman.releasetypeutils.common.version.Version {
	public static Data CURRENT_VERSION = new Data("Chalk", "chalk", 2, 2, 1, Helper.ReleaseType.RELEASE, 1);

	public Data(String name, String id, int major, int minor, int patch, Helper.ReleaseType type, int build) {
		super(name, id, major, minor, patch, type, build);
	}

	public static LoaderType getLoaderType() {
		if (FabricLoader.getInstance().getModContainer("quilt_loader").isPresent()) return LoaderType.QUILT;
		else if (FabricLoader.getInstance().getModContainer("fabricloader").isPresent()) return LoaderType.FABRIC;
		else return LoaderType.OTHER;
	}

	public enum LoaderType {
		FABRIC,
		QUILT,
		OTHER
	}
}