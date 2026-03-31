package de.dafuqs.chalk.common.blocks;

import de.dafuqs.chalk.common.ChalkRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class GlowChalkMarkBlock extends ChalkMarkBlock {

	public GlowChalkMarkBlock(Properties settings, DyeColor dyeColor) {
		super(settings, dyeColor);
	}
	
	@Override @NotNull
	protected ItemStack getCloneItemStack(@NonNull LevelReader world, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData) {
		return ChalkRegistry.chalkVariants.get(dyeColor).glowChalkItem.getDefaultInstance();
	}
}
