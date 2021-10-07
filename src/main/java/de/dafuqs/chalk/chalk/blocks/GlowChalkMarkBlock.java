package de.dafuqs.chalk.chalk.blocks;

import de.dafuqs.chalk.chalk.Chalk;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GlowChalkMarkBlock extends ChalkMarkBlock {

    public GlowChalkMarkBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Chalk.GLOW_CHALK_ITEM);
    }

}
