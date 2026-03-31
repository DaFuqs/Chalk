package de.dafuqs.chalk.common.items;

import de.dafuqs.chalk.common.ChalkRegistry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

public class GlowChalkItem extends ChalkItem {

    public GlowChalkItem(Properties settings, DyeColor dyeColor) {
        super(settings, dyeColor);
    }
    
    public Block getChalkMarkBlock() {
        return ChalkRegistry.chalkVariants.get(this.dyeColor).glowChalkBlock;
    }
}