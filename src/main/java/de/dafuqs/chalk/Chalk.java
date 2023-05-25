package de.dafuqs.chalk;

import com.mojang.logging.*;
import de.dafuqs.chalk.chalk.blocks.*;
import de.dafuqs.chalk.chalk.items.*;
import de.dafuqs.chalk.util.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.fabricmc.fabric.api.object.builder.v1.block.*;
import net.minecraft.block.*;
import net.minecraft.client.render.*;
import net.minecraft.item.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import org.slf4j.*;

import java.util.*;

public class Chalk implements ModInitializer {
	
	public static final String MOD_ID = "chalk";
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public static boolean colorfulAddonPresent;
	public static boolean continuityLoaded;
	
	public static class ChalkVariant {
		public String colorString;
		public int color;
		
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
			this.glowChalkBlock = new GlowChalkMarkBlock(FabricBlockSettings.of(Material.REPLACEABLE_PLANT).breakInstantly().noCollision().nonOpaque().sounds(BlockSoundGroup.GRAVEL)
					.luminance((state) -> continuityLoaded ? 0 : 1)
					.postProcess(continuityLoaded ? Chalk::never : Chalk::always)
					.emissiveLighting(continuityLoaded ? Chalk::never : Chalk::always), dyeColor);
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
		}
	}
	
	public HashMap<DyeColor, Integer> dyeColors = new HashMap<>() {{
		put(DyeColor.BLACK, 0x171717);
		put(DyeColor.BLUE, 0x2c2e8e);
		put(DyeColor.BROWN, 0x613c20);
		put(DyeColor.CYAN, 0x157687);
		put(DyeColor.GRAY, 0x292929);
		put(DyeColor.GREEN, 0x495b24);
		put(DyeColor.LIGHT_BLUE, 0x258ac8);
		put(DyeColor.LIGHT_GRAY, 0x8b8b8b);
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
	
	private static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
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
		
		ChalkLoader.detectLoader();
		colorfulAddonPresent = ChalkLoader.isColorfulAddonLoaded();
		continuityLoaded = ChalkLoader.isContinuityLoaded();
		
		// colored chalk variants are only added if the colorful addon is installed
		// this allows chalk to use the "chalk" mod to use the chalk namespace for all functionality
		// while still having it configurable / backwards compatible
		ChalkVariant chalkVariant;
		for(DyeColor dyeColor : DyeColor.values()) {
			int color = dyeColors.get(dyeColor);
			if(dyeColor.equals(DyeColor.WHITE)) {
				// backwards compatibility
				chalkVariant = new ChalkVariant(dyeColor, color, "");
				chalkVariant.register();
				chalkVariants.put(dyeColor, chalkVariant);
			} else if(colorfulAddonPresent) {
				// if colorful addon present
				chalkVariant = new ChalkVariant(dyeColor, color, dyeColor + "_");
				chalkVariant.register();
				chalkVariants.put(dyeColor, chalkVariant);
			}
		}
		
		log("Startup finished!");
	}
	
	public static void log(String message) {
		LOGGER.info("[Chalk] " + message);
	}
	
}