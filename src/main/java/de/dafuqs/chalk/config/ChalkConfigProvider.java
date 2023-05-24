package de.dafuqs.chalk.config;

import com.mclegoman.simplefabric.fabric_simplelibs.simple_config.SimpleConfig;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ChalkConfigProvider implements SimpleConfig.DefaultConfig {
    private String CONTENTS = "";
    public List<Pair> getConfigList() {
        return CONFIG_LIST;
    }
    private final List<Pair> CONFIG_LIST = new ArrayList<>();
    public void add(Pair<String, ?> keyValuePair) {
        CONFIG_LIST.add(keyValuePair);
        CONTENTS += keyValuePair.getFirst() + "=" + keyValuePair.getSecond() + "\n";
    }
    public void add(String string) {
        CONTENTS += "#" + string + "\n";
    }
    @Override
    public String get(String namespace) {
        return CONTENTS;
    }
}