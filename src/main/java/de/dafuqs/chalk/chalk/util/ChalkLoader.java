package de.dafuqs.chalk.chalk.util;

import de.dafuqs.chalk.chalk.Chalk;
import net.fabricmc.loader.api.FabricLoader;

public class ChalkLoader {
    public static Boolean isFabric;
    public static Boolean isQuilt;
    public static Boolean isOther;

    public static void detectLoader() {
        Chalk.log("Detecting Loader...");
        if (FabricLoader.getInstance().getModContainer("quilt_loader").isPresent()){
            isQuilt = true;
            Chalk.log("Detected Loader: Quilt");
        } else if (FabricLoader.getInstance().getModContainer("fabricloader").isPresent()){
            isFabric = true;
            Chalk.log("Detected Loader: Fabric");
        } else {
            isOther = true;
            Chalk.log("Detected Loader: Other");
        }
    }
}