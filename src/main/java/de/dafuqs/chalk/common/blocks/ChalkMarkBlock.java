package de.dafuqs.chalk.common.blocks;

import de.dafuqs.chalk.common.Chalk;
import de.dafuqs.chalk.common.ChalkRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChalkMarkBlock extends Block {

	protected DyeColor dyeColor;

	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final IntegerProperty ORIENTATION = IntegerProperty.create("orientation", 0, 8);

	// Hitbox margin: 0 … 2 (Use 1.5D to create a look identical to previous versions)
	private static final double margin = 0D;
	// Hitbox thickness: 0.001 … 2 (use 0.5D to create a look identical to previous versions)
	private static final double thick = 0.001D;
	private static final VoxelShape DOWN_AABB = Block.box(margin, 16D - thick, margin, 16D - margin, 16D, 16D - margin);
	private static final VoxelShape UP_AABB = Block.box(margin, 0D, margin, 16D - margin, thick, 16D - margin);
	private static final VoxelShape SOUTH_AABB = Block.box(margin, margin, 0D, 16D - margin, 16D - margin, thick);
	private static final VoxelShape EAST_AABB = Block.box(0D, margin, margin, thick, 16D - margin, 16D - margin);
	private static final VoxelShape WEST_AABB = Block.box(16D - thick, margin, margin, 16D, 16D - margin, 16D - margin);
	private static final VoxelShape NORTH_AABB = Block.box(margin, margin, 16D - thick, 16D - margin, 16D - margin, 16D);

	public ChalkMarkBlock(Properties settings, DyeColor dyeColor) {
		super(settings);
		this.dyeColor = dyeColor;
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(ORIENTATION, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ORIENTATION);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public void playerDestroy(@NotNull Level world, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack stack) {
		super.playerDestroy(world, player, pos, state, blockEntity, stack);
	}
	
	@Override @NotNull
	protected ItemStack getCloneItemStack(@NotNull LevelReader world, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData) {
		return ChalkRegistry.chalkVariants.get(dyeColor).chalkItem.getDefaultInstance();
	}

	@Override
	protected void spawnDestroyParticles(Level world, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state) {
		RandomSource random = world.getRandom();
		if (!world.isClientSide())
			world.playSound(null, pos, SoundEvents.WART_BLOCK_HIT, SoundSource.BLOCKS, 0.5f, random.nextFloat() * 0.2f + 0.8f);
		else {
			if (Chalk.CONFIG.EmitParticles) {
				world.addParticle(ParticleTypes.CLOUD, pos.getX() + (0.5 * (random.nextFloat() + 0.15)), pos.getY() + 0.3, pos.getZ() + (0.5 * (random.nextFloat() + 0.15)), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override @NotNull
	public VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case UP -> UP_AABB;
			case NORTH -> NORTH_AABB;
			case WEST -> WEST_AABB;
			case EAST -> EAST_AABB;
			case SOUTH -> SOUTH_AABB;
			default -> DOWN_AABB;
		};
	}

	@Override @NotNull
	public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		return Block.isFaceFull(world.getBlockState(pos.relative(facing.getOpposite())).getCollisionShape(world, pos.relative(facing)), facing);
	}
	
	@Override @NotNull
	protected BlockState updateShape(BlockState state, @NotNull LevelReader world, @NotNull ScheduledTickAccess tickView, BlockPos pos, @NotNull Direction direction, BlockPos neighborPos, @NotNull BlockState neighborState, @NotNull RandomSource random) {
		boolean support = neighborPos.equals(pos.relative(state.getValue(FACING).getOpposite()));
		if (support) {
			if (!this.canSurvive(state, world, pos)) {
				return Blocks.AIR.defaultBlockState();
			}
		}
		return state;
	}
	
}