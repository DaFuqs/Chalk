package de.dafuqs.chalk.common.items;

import de.dafuqs.chalk.client.config.ConfigHelper;
import de.dafuqs.chalk.common.ChalkRegistry;
import de.dafuqs.chalk.common.blocks.ChalkMarkBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChalkItem extends Item {
	protected DyeColor dyeColor;

	public ChalkItem(Settings settings, DyeColor dyeColor) {
		super(settings);
		this.dyeColor = dyeColor;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		final World world = context.getWorld();
		final BlockPos pos = context.getBlockPos();
		final BlockState clickedBlockState = world.getBlockState(pos);
		final PlayerEntity player = context.getPlayer();
		final ItemStack stack = context.getStack();
		Direction clickedFace = context.getSide();
		BlockPos markPosition = pos.offset(clickedFace);
		if (world.isAir(markPosition) || world.getBlockState(markPosition).getBlock() instanceof ChalkMarkBlock) {
			if (clickedBlockState.getBlock() instanceof ChalkMarkBlock) { // replace mark
				clickedFace = clickedBlockState.get(ChalkMarkBlock.FACING);
				markPosition = pos;
				world.removeBlock(pos, false);
			} else if (!Block.isFaceFullSquare(clickedBlockState.getCollisionShape(world, pos, ShapeContext.of(player)), clickedFace)) {
				return ActionResult.PASS;
			} else if ((!world.isAir(markPosition) && world.getBlockState(markPosition).getBlock() instanceof ChalkMarkBlock) || stack.getItem() != this) {
				return ActionResult.PASS;
			}

			if (world.isClient) {
				Random r = new Random();
				if ((boolean) ConfigHelper.getConfig("emit_particles"))
					world.addParticle(ParticleTypes.CLOUD, markPosition.getX() + (0.5 * (r.nextFloat() + 0.4)), markPosition.getY() + 0.65, markPosition.getZ() + (0.5 * (r.nextFloat() + 0.4)), 0.0D, 0.005D, 0.0D);
				return ActionResult.SUCCESS;
			}

			final int orientation = getClickedRegion(context.getHitPos(), clickedFace);

			BlockState blockState = getChalkMarkBlock().getDefaultState()
					.with(ChalkMarkBlock.FACING, clickedFace)
					.with(ChalkMarkBlock.ORIENTATION, orientation);

			if (world.setBlockState(markPosition, blockState, 1 | 2)) {
				if (!player.isCreative()) {
					if (stack.getDamage() >= stack.getMaxDamage()) {
						world.playSound(null, markPosition, SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.BLOCKS, 0.5f, 1f);
					}
					stack.damage(1, player, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
				}

				world.playSound(null, markPosition, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 0.6f, world.random.nextFloat() * 0.2f + 0.8f);
				return ActionResult.CONSUME;
			}
		}
		return ActionResult.FAIL;
	}

	public Block getChalkMarkBlock() {
		return ChalkRegistry.chalkVariants.get(this.dyeColor).chalkBlock;
	}

	private int getClickedRegion(@NotNull Vec3d clickLocation, Direction face) {
		// Calculates which region of the block was clicked:
		// Matrix represents the block regions:
		final int[][] blockRegions = new int[][]{
				new int[]{0, 1, 2},
				new int[]{3, 4, 5},
				new int[]{6, 7, 8}
		};

		final double x = clickLocation.x;
		final double y = clickLocation.y;
		final double z = clickLocation.z;

		// Remove whole number: 21.31 => 0.31
		final double fracX = x - (int) x;
		final double fracY = y - (int) y;
		final double fracZ = z - (int) z;

		// Normalize negative values
		final double dx = fracX > 0 ? fracX : fracX + 1;
		final double dy = fracY > 0 ? fracY : fracY + 1;
		final double dz = fracZ > 0 ? fracZ : fracZ + 1;

		if (face == Direction.UP || face == Direction.DOWN) {
			final int xpart = Math.min(2, (int) (dx / 0.333));
			final int zpart = Math.min(2, (int) (dz / 0.333));

			return blockRegions[zpart][xpart];
		} else if (face == Direction.NORTH || face == Direction.SOUTH) {
			final int xpart = Math.min(2, (int) (dx / 0.333));
			final int ypart = Math.min(2, (int) ((1 - dy) / 0.333));

			return blockRegions[ypart][xpart];
		} else if (face == Direction.WEST || face == Direction.EAST) {
			final int zpart = Math.min(2, (int) (dz / 0.333));
			final int ypart = Math.min(2, (int) ((1 - dy) / 0.333));

			return blockRegions[ypart][zpart];
		} else
			return 4; // center of the block by default
	}
}