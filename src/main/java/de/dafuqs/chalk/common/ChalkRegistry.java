package de.dafuqs.chalk.common;

import com.mclegoman.releasetypeutils.common.version.Helper;
import de.dafuqs.chalk.common.blocks.ChalkMarkBlock;
import de.dafuqs.chalk.common.blocks.GlowChalkMarkBlock;
import de.dafuqs.chalk.common.data.CompatibilityData;
import de.dafuqs.chalk.common.data.Data;
import de.dafuqs.chalk.common.items.ChalkItem;
import de.dafuqs.chalk.common.items.GlowChalkItem;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.HashMap;

public class ChalkRegistry {
	private static final HashMap<DyeColor, Integer> dyeColors = new HashMap<>() {{
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
	public static HashMap<DyeColor, ChalkRegistry.ChalkVariant> chalkVariants = new HashMap<>();

	public static void init() {
		Data.CURRENT_VERSION.sendToLog(Helper.LogType.INFO, "Registering blocks and items...");
		/*
		 * colored chalk variants are only added if the colorful addon is installed
		 * this allows chalk to use the "chalk" mod to use the chalk namespace for all functionality
		 * while still having it configurable / backwards compatible
		 */
		ChalkRegistry.ChalkVariant chalkVariant;
		for (DyeColor dyeColor : DyeColor.values()) {
			int color = dyeColors.get(dyeColor);
			if (dyeColor.equals(DyeColor.WHITE)) {
				/* backwards compatibility */
				chalkVariant = new ChalkRegistry.ChalkVariant(dyeColor, color, "");
				chalkVariant.register();
				chalkVariants.put(dyeColor, chalkVariant);
			} else if (CompatibilityData.COLORFUL_ADDON) {
				/* if colourful addon present */
				chalkVariant = new ChalkRegistry.ChalkVariant(dyeColor, color, dyeColor + "_");
				chalkVariant.register();
				chalkVariants.put(dyeColor, chalkVariant);
			}
		}
	}

	private static void registerBlock(String name, Block block) {
		Registry.register(Registries.BLOCK, new Identifier(Data.CURRENT_VERSION.getID(), name), block);
	}

	private static void registerItem(String name, Item item) {
		Registry.register(Registries.ITEM, new Identifier(Data.CURRENT_VERSION.getID(), name), item);
	}

	private static boolean always(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	private static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	public static class ChalkVariant {
		public Item chalkItem;
		public Block chalkBlock;
		public Item glowChalkItem;
		public Block glowChalkBlock;
		String colorString;
		int color;

		public ChalkVariant(DyeColor dyeColor, int color, String colorString) {
			this.color = color;
			this.colorString = colorString;
			this.chalkItem = new ChalkItem(new Item.Settings().maxCount(1).maxDamage(64), dyeColor);
			this.chalkBlock = new ChalkMarkBlock(AbstractBlock.Settings.create().replaceable().noCollision().nonOpaque().sounds(BlockSoundGroup.GRAVEL).pistonBehavior(PistonBehavior.DESTROY), dyeColor);
			this.glowChalkItem = new GlowChalkItem(new Item.Settings().maxCount(1).maxDamage(64), dyeColor);
			this.glowChalkBlock = new GlowChalkMarkBlock(AbstractBlock.Settings.create().replaceable().noCollision().nonOpaque().sounds(BlockSoundGroup.GRAVEL)
					.luminance((state) -> CompatibilityData.CONTINUITY ? 0 : 1)
					.postProcess(CompatibilityData.CONTINUITY ? ChalkRegistry::never : ChalkRegistry::always)
					.emissiveLighting(CompatibilityData.CONTINUITY ? ChalkRegistry::never : ChalkRegistry::always)
					.pistonBehavior(PistonBehavior.DESTROY), dyeColor);
			this.ItemGroups();
		}

		public void ItemGroups() {
			/* Chalk ItemGroups: Functional Blocks, Tools and Utilities */
			ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(this.chalkItem));
			ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(this.chalkItem));
			/* Glow Chalk ItemGroups: Functional Blocks, Tools and Utilities */
			ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(this.glowChalkItem));
			ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(this.glowChalkItem));
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
}