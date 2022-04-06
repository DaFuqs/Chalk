package de.dafuqs.chalk.chalk;

import com.mojang.logging.LogUtils;
import de.dafuqs.chalk.chalk.blocks.ChalkMarkBlock;
import de.dafuqs.chalk.chalk.blocks.GlowChalkMarkBlock;
import de.dafuqs.chalk.chalk.items.ChalkItem;
import de.dafuqs.chalk.chalk.items.GlowChalkItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.slf4j.Logger;

import java.util.HashMap;

public class Chalk implements ModInitializer {

    public static final String MOD_ID = "chalk";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static class ChalkVariant {
        String colorString;
        int color;
        
        public Item chalkItem;
        public Block chalkBlock;
        public Item glowChalkItem;
        public Block glowChalkBlock;
        
        public ChalkVariant(DyeColor dyeColor, int color, String colorString) {
            this.color = color;
            this.colorString = colorString;
            this.chalkItem = new ChalkItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1).maxDamage(64), dyeColor);
            this.chalkBlock = new ChalkMarkBlock(FabricBlockSettings.of(Material.REPLACEABLE_PLANT).breakInstantly().noCollision().nonOpaque().sounds(BlockSoundGroup.GRAVEL), dyeColor);
            this.glowChalkItem = new GlowChalkItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1).maxDamage(64), dyeColor);
            this.glowChalkBlock = new GlowChalkMarkBlock(FabricBlockSettings.of(Material.REPLACEABLE_PLANT).breakInstantly().noCollision().nonOpaque().sounds(BlockSoundGroup.GRAVEL).luminance(1).postProcess(Chalk::always).emissiveLighting(Chalk::always), dyeColor);
        }
        
        public void register() {
            registerBlock(colorString + "chalk_mark", chalkBlock);
            registerItem(colorString + "chalk", chalkItem);
            registerBlock(colorString + "glow_chalk_mark", glowChalkBlock);
            registerItem(colorString + "glow_chalk", glowChalkItem);
        }
        
        public void registerClient() {
            BlockRenderLayerMap.INSTANCE.putBlock(this.chalkBlock, RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(this.glowChalkBlock, RenderLayer.getCutout());
    
            ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> color, chalkBlock);
            ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> color, glowChalkBlock);
            ColorProviderRegistry.ITEM.register((stack, index) -> color, chalkItem);
            ColorProviderRegistry.ITEM.register((stack, index) -> color, glowChalkItem);
        }
    }
    
    public HashMap<DyeColor, Integer> dyeColors = new HashMap<>() {{
        put(DyeColor.BLACK, 0x1c1c1d);
        put(DyeColor.BLUE, 0x2c2e8e);
        put(DyeColor.BROWN, 0x613c20);
        put(DyeColor.CYAN, 0x157687);
        put(DyeColor.GRAY, 0x36393d);
        put(DyeColor.GREEN, 0x495b24);
        put(DyeColor.LIGHT_BLUE, 0x258ac8);
        put(DyeColor.LIGHT_GRAY, 0x7d7d73);
        put(DyeColor.LIME, 0x5faa19);
        put(DyeColor.MAGENTA, 0xaa32a0);
        put(DyeColor.ORANGE, 0xe16201);
        put(DyeColor.PINK, 0xd6658f);
        put(DyeColor.PURPLE, 0x641f9c);
        put(DyeColor.RED, 0x8f2121);
        put(DyeColor.WHITE, 0xFFFFFF);
        put(DyeColor.YELLOW, 0xf0ff15);
    }};
    
    public static HashMap<DyeColor, ChalkVariant> chalkVariants = new HashMap<>();
    
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
        log("Registering blocks and items...");
    
        ChalkVariant chalkVariant;
        for(DyeColor dyeColor : DyeColor.values()) {
            int color = dyeColors.get(dyeColor);
            if(dyeColor.equals(DyeColor.WHITE)) {
                // backwards compatibility
                chalkVariant = new ChalkVariant(dyeColor, color, "");
            } else {
                chalkVariant = new ChalkVariant(dyeColor, color, dyeColor + "_");
            }
            
            chalkVariant.register();
            chalkVariants.put(dyeColor, chalkVariant);
        }
        
        log("Startup finished!");
    }

    public static void log(String message) {
        LOGGER.info("[Chalk] " + message);
    }

}
