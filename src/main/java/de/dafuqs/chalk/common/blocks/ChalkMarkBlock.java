package de.dafuqs.chalk.common.blocks;

import de.dafuqs.chalk.common.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.particle.*;
import net.minecraft.sound.*;
import net.minecraft.state.*;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import net.minecraft.world.tick.*;
import org.jetbrains.annotations.*;

public class ChalkMarkBlock extends Block {

	protected DyeColor dyeColor;

	public static final EnumProperty<Direction> FACING = Properties.FACING;
	public static final IntProperty ORIENTATION = IntProperty.of("orientation", 0, 8);

	// Hitbox margin: 0 … 2 (Use 1.5D to create a look identical to previous versions)
	private static final double margin = 0D;
	// Hitbox thickness: 0.001 … 2 (use 0.5D to create a look identical to previous versions)
	private static final double thick = 0.001D;
	private static final VoxelShape DOWN_AABB = Block.createCuboidShape(margin, 16D - thick, margin, 16D - margin, 16D, 16D - margin);
	private static final VoxelShape UP_AABB = Block.createCuboidShape(margin, 0D, margin, 16D - margin, thick, 16D - margin);
	private static final VoxelShape SOUTH_AABB = Block.createCuboidShape(margin, margin, 0D, 16D - margin, 16D - margin, thick);
	private static final VoxelShape EAST_AABB = Block.createCuboidShape(0D, margin, margin, thick, 16D - margin, 16D - margin);
	private static final VoxelShape WEST_AABB = Block.createCuboidShape(16D - thick, margin, margin, 16D, 16D - margin, 16D - margin);
	private static final VoxelShape NORTH_AABB = Block.createCuboidShape(margin, margin, 16D - thick, 16D - margin, 16D - margin, 16D);

	public ChalkMarkBlock(Settings settings, DyeColor dyeColor) {
		super(settings);
		this.dyeColor = dyeColor;
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(ORIENTATION, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, ORIENTATION);
		super.appendProperties(builder);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, state, blockEntity, stack);
	}
	
	@Override
	protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
		return ChalkRegistry.chalkVariants.get(dyeColor).chalkItem.getDefaultStack();
	}

	@Override
	protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state) {
		Random random = world.getRandom();
		if (!world.isClient())
			world.playSound(null, pos, SoundEvents.BLOCK_WART_BLOCK_HIT, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.2f + 0.8f);
		else {
			if (Chalk.CONFIG.EmitParticles) {
				world.addParticleClient(ParticleTypes.CLOUD, pos.getX() + (0.5 * (random.nextFloat() + 0.15)), pos.getY() + 0.3, pos.getZ() + (0.5 * (random.nextFloat() + 0.15)), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return switch (state.get(FACING)) {
			case UP -> UP_AABB;
			case NORTH -> NORTH_AABB;
			case WEST -> WEST_AABB;
			case EAST -> EAST_AABB;
			case SOUTH -> SOUTH_AABB;
			default -> DOWN_AABB;
		};
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return true;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction facing = state.get(FACING);
		return Block.isFaceFullSquare(world.getBlockState(pos.offset(facing.getOpposite())).getCollisionShape(world, pos.offset(facing)), facing);
	}
	
	@Override
	protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
		boolean support = neighborPos.equals(pos.offset(state.get(FACING).getOpposite()));
		if (support) {
			if (!this.canPlaceAt(state, world, pos)) {
				return Blocks.AIR.getDefaultState();
			}
		}
		return state;
	}
	
}