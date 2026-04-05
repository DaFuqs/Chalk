package de.dafuqs.chalk.common.items;

import de.dafuqs.chalk.common.Chalk;
import de.dafuqs.chalk.common.ChalkRegistry;
import de.dafuqs.chalk.common.blocks.ChalkMarkBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ChalkItem extends Item {

	protected DyeColor dyeColor;

	public ChalkItem(Properties settings, DyeColor dyeColor) {
		super(settings);
		this.dyeColor = dyeColor;
	}

	@Override @NotNull
	public InteractionResult useOn(UseOnContext context) {
		final Level world = context.getLevel();
		final BlockPos pos = context.getClickedPos();
		final BlockState clickedBlockState = world.getBlockState(pos);
		final Player player = context.getPlayer();
		final ItemStack stack = context.getItemInHand();
		Direction clickedFace = context.getClickedFace();
		BlockPos markPosition = pos.relative(clickedFace);
		if (world.isEmptyBlock(markPosition) || world.getBlockState(markPosition).getBlock() instanceof ChalkMarkBlock) {
			if (clickedBlockState.getBlock() instanceof ChalkMarkBlock) { // replace mark
				clickedFace = clickedBlockState.getValue(ChalkMarkBlock.FACING);
				markPosition = pos;
				world.removeBlock(pos, false);
			} else if (player != null &&
					!Block.isFaceFull(clickedBlockState.getCollisionShape(world, pos, CollisionContext.of(player)), clickedFace)) {
				return InteractionResult.PASS;
			} else if ((!world.isEmptyBlock(markPosition) && world.getBlockState(markPosition).getBlock() instanceof ChalkMarkBlock) || stack.getItem() != this) {
				return InteractionResult.PASS;
			}

			if (world.isClientSide()) {
				RandomSource random = world.getRandom();
				if (Chalk.CONFIG.EmitParticles) {
					world.addParticle(ParticleTypes.CLOUD, markPosition.getX() + (0.5 * (random.nextFloat() + 0.4)), markPosition.getY() + 0.65, markPosition.getZ() + (0.5 * (random.nextFloat() + 0.4)), 0.0D, 0.005D, 0.0D);
				}
				return InteractionResult.SUCCESS;
			}

			final int orientation = getClickedRegion(context.getClickLocation(), clickedFace);

			BlockState blockState = getChalkMarkBlock().defaultBlockState()
					.setValue(ChalkMarkBlock.FACING, clickedFace)
					.setValue(ChalkMarkBlock.ORIENTATION, orientation);

			if (world.setBlock(markPosition, blockState, 1 | 2)) {
				if (player != null &&
						!player.isCreative()) {
					if (stack.getDamageValue() >= stack.getMaxDamage()) {
						world.playSound(null, markPosition, SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 0.5f, 1f);
					}
					stack.hurtAndBreak(1, player, context.getHand());
				}
				world.playSound(null, markPosition, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 0.6f, world.getRandom().nextFloat() * 0.2f + 0.8f);
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.FAIL;
	}

	public Block getChalkMarkBlock() {
		return ChalkRegistry.chalkVariants.get(this.dyeColor).chalkBlock;
	}

	/**
	 * Calculate the fractional part of v
	 * @return Fractional part of v (always non-negative and less than 1)
	 */
	private static double frac(double v) {
		return v - Math.floor(v);
	}

	/**
	 * Calculates which region of the block was clicked
	 * @param rx [0, 1, 2] = [left, center, right]
	 * @param ry [0, 1, 2] = [top, center, bottom]
	 * @return region number (top-left = 0 … bottom-right = 8)
	 */
	private static int blockreg(int rx, int ry) {
		return 3 * rx + ry;
	}

	private int getClickedRegion(@NotNull Vec3 clickLocation, @NotNull Direction face) {
		final double dx = frac(clickLocation.x);
		final double dy = frac(clickLocation.y);
		final double dz = frac(clickLocation.z);

		return switch (face) {
			case NORTH, SOUTH -> blockreg(Math.min(2, (int) (3 * (1 - dy))), Math.min(2, (int) (3 * dx)));
			case WEST, EAST -> blockreg(Math.min(2, (int) (3 * (1 - dy))), Math.min(2, (int) (3 * dz)));
			default -> blockreg(Math.min(2, (int) (3 * dz)), Math.min(2, (int) (3 * dx)));
		};
	}
}