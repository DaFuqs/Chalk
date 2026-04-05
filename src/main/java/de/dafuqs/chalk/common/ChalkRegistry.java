package de.dafuqs.chalk.common;

import de.dafuqs.chalk.common.blocks.ChalkMarkBlock;
import de.dafuqs.chalk.common.blocks.GlowChalkMarkBlock;
import de.dafuqs.chalk.common.items.ChalkItem;
import de.dafuqs.chalk.common.items.GlowChalkItem;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

import java.util.*;

public class ChalkRegistry {
	
	// We use this map instead of the DyeColor enum
	// in case a mod extends the DyeColor enum and stuff inevitably breaks
	public static Map<DyeColor, Integer> dyeColors = new TreeMap<>() {{
		put(DyeColor.WHITE, 0xffffffff);
		put(DyeColor.ORANGE, 0xffe16201);
		put(DyeColor.MAGENTA, 0xffaa32a0);
		put(DyeColor.LIGHT_BLUE, 0xff258ac8);
		put(DyeColor.YELLOW, 0xfff0ff15);
		put(DyeColor.LIME, 0xff5faa19);
		put(DyeColor.PINK, 0xffd6658f);
		put(DyeColor.GRAY, 0xff292929);
		put(DyeColor.LIGHT_GRAY, 0xff8b8b8b);
		put(DyeColor.CYAN, 0xff157687);
		put(DyeColor.PURPLE, 0xff641f9c);
		put(DyeColor.BLUE, 0xff2c2e8e);
		put(DyeColor.BROWN, 0xff613c20);
		put(DyeColor.GREEN, 0xff495b24);
		put(DyeColor.RED, 0xff8f2121);
		put(DyeColor.BLACK, 0xff171717);
	}};
	
	public static Map<DyeColor, ChalkRegistry.ChalkVariant> chalkVariants = new HashMap<>();

	public static void init() {
		boolean addonLoaded = FabricLoader.getInstance().isModLoaded("chalk-colorful-addon");
		
		/*
		 * colored chalk variants are only added if the colorful addon is installed
		 * this allows chalk to use the "chalk" mod to use the chalk namespace for all functionality
		 * while still having it configurable / backwards compatible
		 */
		for (Map.Entry<DyeColor, Integer> entry : dyeColors.entrySet()) {
			DyeColor dyeColor = entry.getKey();
			int color = entry.getValue();
			
			if (dyeColor.equals(DyeColor.WHITE) || addonLoaded) {
				new ChalkRegistry.ChalkVariant(dyeColor, color);
			}
		}
		
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(fabricItemGroupEntries -> {
			for(ChalkVariant chalkVariant1 : chalkVariants.values()) {
				fabricItemGroupEntries.accept(chalkVariant1.chalkItem);
				fabricItemGroupEntries.accept(chalkVariant1.glowChalkItem);
			}
		});
		
	}
	
	public static class ChalkVariant {

		public Item chalkItem;
		public Block chalkBlock;
		public Item glowChalkItem;
		public Block glowChalkBlock;
		String colorString;
		int color;

		public ChalkVariant(DyeColor dyeColor, int color) {
			this.color = color;
			this.colorString = dyeColor.toString();
			
			Identifier itemId = Chalk.id(colorString + "_chalk");
			Identifier blockId = Chalk.id(colorString + "_chalk_mark");

			Identifier glowItemId = Chalk.id(colorString + "_glow_chalk");
			Identifier glowBlockId = Chalk.id(colorString + "_glow_chalk_mark");
			
			ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, itemId);
			ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, blockId);
			
			ResourceKey<Item> glowItemKey = ResourceKey.create(Registries.ITEM, glowItemId);
			ResourceKey<Block> glowBlockKey = ResourceKey.create(Registries.BLOCK, glowBlockId);
			
			this.chalkItem = new ChalkItem(new Item.Properties().setId(itemKey).stacksTo(1).durability(64), dyeColor);
			this.chalkBlock = new ChalkMarkBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().setId(blockKey).replaceable().noCollision().noOcclusion().sound(SoundType.GRAVEL).pushReaction(PushReaction.DESTROY), dyeColor);
			this.glowChalkItem = new GlowChalkItem(new Item.Properties().setId(glowItemKey).stacksTo(1).durability(64), dyeColor);
			this.glowChalkBlock = new GlowChalkMarkBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().setId(glowBlockKey).replaceable().noCollision().noOcclusion().sound(SoundType.GRAVEL).pushReaction(PushReaction.DESTROY), dyeColor);
			
			Registry.register(BuiltInRegistries.ITEM, itemId, chalkItem);
			Registry.register(BuiltInRegistries.BLOCK, blockId, chalkBlock);
			Registry.register(BuiltInRegistries.ITEM, glowItemId, glowChalkItem);
			Registry.register(BuiltInRegistries.BLOCK, glowBlockId, glowChalkBlock);
			
			chalkVariants.put(dyeColor, this);
		}

		public void registerClient() {
			BlockColorRegistry.register(List.of(_ -> color), chalkBlock);
			BlockColorRegistry.register(List.of(_ -> color), glowChalkBlock);
		}
	}
}