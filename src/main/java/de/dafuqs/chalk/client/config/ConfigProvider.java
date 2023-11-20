package de.dafuqs.chalk.client.config;

import com.mclegoman.releasetypeutils.common.version.Helper;
import com.mclegoman.simplefabriclibs.simple_config.SimpleConfig;
import com.mojang.datafixers.util.Pair;
import de.dafuqs.chalk.common.data.Data;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigProvider implements SimpleConfig.DefaultConfig {
	private String CONTENTS = "";
	private List<Pair<String, ?>> CONFIG_LIST = new ArrayList<>();

	public List<Pair<String, ?>> getConfigList() {
		return CONFIG_LIST;
	}

	public void add(String ID, Pair<String, ?> keyValuePair) {
		CONFIG_LIST.add(keyValuePair);
		getContents(ID);
	}

	@Override
	public String get(String namespace) {
		return CONTENTS;
	}

	public void getContents(String ID) {
		StringBuilder contents = new StringBuilder(("#" + ID + " properties file\n"));
		for (Pair<String, ?> option : CONFIG_LIST) {
			contents.append(option.getFirst()).append("=").append(option.getSecond()).append("\n");
		}
		CONTENTS = contents.toString();
	}

	public void setConfig(String KEY_NAME, Object KEY_VALUE) {
		try {
			List<Pair<String, ?>> NEW_CONFIG_LIST = this.CONFIG_LIST;
			for (Pair<String, ?> key : NEW_CONFIG_LIST) {
				String KEY_FIRST = key.getFirst();
				int KEY_INDEX = NEW_CONFIG_LIST.indexOf(key);
				if (KEY_FIRST.equals(KEY_NAME)) {
					NEW_CONFIG_LIST.set(KEY_INDEX, new Pair<>(KEY_NAME, KEY_VALUE));
				}
			}
			CONFIG_LIST = NEW_CONFIG_LIST;
		} catch (Exception error) {
			Data.CURRENT_VERSION.sendToLog(Helper.LogType.ERROR, String.valueOf(error));
		}
	}

	public void saveConfig(String ID) {
		try {
			getContents(ID);
			PrintWriter writer = new PrintWriter(FabricLoader.getInstance().getConfigDir().resolve(ID + ".properties").toFile(), StandardCharsets.UTF_8);
			writer.write(CONTENTS);
			writer.close();
		} catch (Exception error) {
			Data.CURRENT_VERSION.sendToLog(Helper.LogType.ERROR, String.valueOf(error));
		}
	}
}