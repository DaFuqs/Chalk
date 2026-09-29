package chalk.qa;

import java.util.List;
import de.dafuqs.chalk.common.Chalk;
import de.dafuqs.chalk.common.ChalkRegistry;
import de.dafuqs.chalk.common.blocks.ChalkMarkBlock;
import de.dafuqs.chalk.common.items.ChalkItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Test-only entrypoint, deliberately excluded from the production jar. */
public final class ChalkChecks implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> check(server, "startup"));
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resources, success) -> {
            require(success, "datapack reload");
            check(server, "reload");
        });
    }

    private static void check(MinecraftServer server, String phase) {
        require(Chalk.CONFIG != null, "Cloth Config initialized on dedicated server");
        require(ChalkRegistry.chalkVariants.size() == 1, "white variant without colorful addon");
        var variant = ChalkRegistry.chalkVariants.get(DyeColor.WHITE);
        require(variant != null, "white variant registered");
        var level = server.overworld();
        var plainInput = CraftingInput.of(2, 1, List.of(new ItemStack(Items.CALCITE), new ItemStack(Items.CALCITE)));
        var glowInput = CraftingInput.of(3, 1, List.of(new ItemStack(Items.CALCITE), new ItemStack(Items.CALCITE), new ItemStack(Items.GLOW_INK_SAC)));
        var plainRecipe = server.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, plainInput, level).orElseThrow();
        var glowRecipe = server.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, glowInput, level).orElseThrow();
        require(plainRecipe.value().assemble(plainInput).is(variant.chalkItem), "plain chalk recipe result");
        require(glowRecipe.value().assemble(glowInput).is(variant.glowChalkItem), "glow chalk recipe result");
        checkMarks(level, (ChalkItem) variant.chalkItem, variant.chalkBlock);
        checkMarks(level, (ChalkItem) variant.glowChalkItem, variant.glowChalkBlock);
        System.out.println("CHALK_QA_PASS " + phase + " (108 placements, support removal, shapes, piston reaction, destruction hooks, recipes)");
    }

    private static void checkMarks(ServerLevel level, ChalkItem item, Block mark) {
        BlockPos support = new BlockPos(0, 250, 0);
        require(item.getDefaultInstance().getMaxDamage() == 64, "chalk durability");
        for (Direction face : Direction.values()) {
            BlockPos position = support.relative(face);
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    level.setBlock(support, Blocks.STONE.defaultBlockState(), 3);
                    level.removeBlock(position, false);
                    double horizontal = (col + 0.5) / 3;
                    double vertical = (row + 0.5) / 3;
                    Vec3 offset = switch (face) {
                        case NORTH, SOUTH -> new Vec3(horizontal, 1 - vertical, face == Direction.NORTH ? 0 : 1);
                        case EAST, WEST -> new Vec3(face == Direction.WEST ? 0 : 1, 1 - vertical, horizontal);
                        default -> new Vec3(horizontal, face == Direction.DOWN ? 0 : 1, vertical);
                    };
                    var hit = new BlockHitResult(Vec3.atLowerCornerOf(support).add(offset), face, support, false);
                    require(item.useOn(new Context(level, item.getDefaultInstance(), hit)).consumesAction(), "placement accepted " + face);
                    var state = level.getBlockState(position);
                    require(state.is(mark), "placed correct chalk variant");
                    require(state.getValue(ChalkMarkBlock.FACING) == face, "attached facing");
                    require(state.getValue(ChalkMarkBlock.ORIENTATION) == row * 3 + col, "clicked arrow region");
                    require(state.getCollisionShape(level, position).isEmpty(), "chalk remains noncolliding");
                    require(!state.getShape(level, position).isEmpty(), "chalk remains targetable");
                    require(state.getPistonPushReaction() == PushReaction.POPPED, "piston breaks chalk");
                    require(state.canSurvive(level, position), "supported mark survives");
                    mark.spawnDestroyByEntityParticles(level, null, position, state);
                    level.removeBlock(support, false);
                    require(level.isEmptyBlock(position), "mark removed when support disappears");
                }
            }
        }
    }

    private static final class Context extends UseOnContext {
        Context(ServerLevel level, ItemStack stack, BlockHitResult hit) {
            super(level, null, InteractionHand.MAIN_HAND, stack, hit);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError("CHALK_QA_FAIL: " + message);
    }
}
