package de.dafuqs.chalk.chalk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChalkClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (Chalk.ChalkVariant chalkVariant : Chalk.chalkVariants.values()) {
            chalkVariant.registerClient();
        }
    }

}
