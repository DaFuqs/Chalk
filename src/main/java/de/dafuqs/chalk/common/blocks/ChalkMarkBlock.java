package de.dafuqs.chalk.common.blocks;

import de.dafuqs.chalk.client.config.ConfigHelper;
import de.dafuqs.chalk.common.ChalkRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ChalkMarkBlock extends Block {

    protected DyeColor dyeColor;

    public static final DirectionProperty FACING = Properties.FACING;
    public static final IntProperty ORIENTATION = IntProperty.of("orientation", 0, 8);

    private static final VoxelShape DOWN_AABB = Block.createCuboidShape(1.5D, 15.5D, 1.5D, 14.5D, 16D, 14.5D);
    private static final VoxelShape UP_AABB = Block.createCuboidShape(1.5D, 0D, 1.5D, 14.5D, 0.5D, 14.5D);
    private static final VoxelShape SOUTH_AABB = Block.createCuboidShape(1.5D, 1.5D, 0D, 14.5D, 14.5D, 0.5D);
    private static final VoxelShape EAST_AABB = Block.createCuboidShape(0D, 1.5D, 1.5D, 0.5D, 14.5D, 14.5D);
    private static final VoxelShape WEST_AABB = Block.createCuboidShape(15.5D, 1.5D, 1.5D, 16D, 14.5D, 14.5D);
    private static final VoxelShape NORTH_AABB = Block.createCuboidShape(1.5D, 1.5D, 15.5D, 14.5D, 14.5D, 16D);

    public ChalkMarkBlock(Settings settings, DyeColor dyeColor) {
        super(settings);
        this.dyeColor = dyeColor;
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(ORIENTATION, 0));
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
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
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return ChalkRegistry.chalkVariants.get(dyeColor).chalkItem.getDefaultStack();
    }

    @Override
    protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        if (!world.isClient)
            world.playSound(null, pos, SoundEvents.BLOCK_WART_BLOCK_HIT, SoundCategory.BLOCKS, 0.5f, new Random().nextFloat() * 0.2f + 0.8f);
        else{
            Random r = new Random();
            if ((boolean) ConfigHelper.getConfig("emit_particles"))
                world.addParticle(ParticleTypes.CLOUD, pos.getX() + (0.5 * (r.nextFloat() + 0.15)), pos.getY() + 0.3, pos.getZ() + (0.5 * (r.nextFloat() + 0.15)), 0.0D, 0.0D, 0.0D);
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

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction facing = state.get(FACING);
        return Block.isFaceFullSquare(world.getBlockState(pos.offset(facing.getOpposite())).getCollisionShape(world, pos.offset(facing)), facing);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        boolean support = neighborPos.equals(pos.offset(state.get(FACING).getOpposite()));
        if(support) {
            if(!this.canPlaceAt(state, world, pos)) {
                return Blocks.AIR.getDefaultState();
            }
        }
        return state;
    }
}