package de.dafuqs.chalk;

import de.dafuqs.chalk.config.ChalkConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChalkClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ChalkConfig.init();
        for (Chalk.ChalkVariant chalkVariant : Chalk.chalkVariants.values()) {
            chalkVariant.registerClient();
        }
    }

}
