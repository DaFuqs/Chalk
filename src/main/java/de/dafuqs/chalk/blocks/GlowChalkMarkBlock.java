package de.dafuqs.chalk.blocks;

import de.dafuqs.chalk.Chalk;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GlowChalkMarkBlock extends ChalkMarkBlock {

    public GlowChalkMarkBlock(Settings settings, DyeColor dyeColor) {
        super(settings, dyeColor);
    }
    
    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return Chalk.chalkVariants.get(dyeColor).glowChalkItem.getDefaultStack();
    }

}
