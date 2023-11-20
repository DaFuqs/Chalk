package de.dafuqs.chalk.client;

import de.dafuqs.chalk.client.config.ConfigHelper;
import de.dafuqs.chalk.common.ChalkRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ChalkClient implements ClientModInitializer {
    private static void tick(MinecraftClient client) {
        ConfigHelper.tick(client);
    }

    @Override
    public void onInitializeClient() {
        ConfigHelper.init();
        for (ChalkRegistry.ChalkVariant chalkVariant : ChalkRegistry.chalkVariants.values())
            chalkVariant.registerClient();
        ClientTickEvents.END_CLIENT_TICK.register(ChalkClient::tick);
    }
}
