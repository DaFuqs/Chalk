package de.dafuqs.chalk.chalk;

import de.dafuqs.chalk.chalk.blocks.ChalkMarkBlock;
import de.dafuqs.chalk.chalk.blocks.GlowChalkMarkBlock;
import de.dafuqs.chalk.chalk.items.ChalkItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chalk implements ModInitializer {

    public static final String MOD_ID = "chalk";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Block CHALK_MARK_BLOCK = new ChalkMarkBlock(FabricBlockSettings.of(Material.REPLACEABLE_UNDERWATER_PLANT).breakInstantly().noCollision().nonOpaque().sounds(BlockSoundGroup.GRAVEL));
    public static Item CHALK_ITEM = new ChalkItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1).maxDamage(64), CHALK_MARK_BLOCK);

    public static Block GLOW_CHALK_MARK_BLOCK = new GlowChalkMarkBlock(FabricBlockSettings.of(Material.REPLACEABLE_UNDERWATER_PLANT).breakInstantly().noCollision().nonOpaque().sounds(BlockSoundGroup.GRAVEL).luminance((state) -> 2).postProcess(Chalk::always).emissiveLighting(Chalk::always));
    public static Item GLOW_CHALK_ITEM = new ChalkItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1).maxDamage(64), GLOW_CHALK_MARK_BLOCK);

    private static boolean always(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
    }

    private static void registerBlock(String name, Block block) {
        Registry.register(Registry.BLOCK, new Identifier(MOD_ID, name), block);
    }

    private static void registerItem(String name, Item item) {
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
    }

    @Override
    public void onInitialize() {
        log(Level.INFO, "Registering blocks and items...");
        
        registerBlock("chalk_mark", CHALK_MARK_BLOCK);
        registerItem("chalk", CHALK_ITEM);

        registerBlock("glow_chalk_mark", GLOW_CHALK_MARK_BLOCK);
        registerItem("glow_chalk", GLOW_CHALK_ITEM);
    
        log(Level.INFO, "Startup finished!");
    }

    public static void log(Level logLevel, String message) {
        LOGGER.log(logLevel, "[Chalk] " + message);
    }

}
