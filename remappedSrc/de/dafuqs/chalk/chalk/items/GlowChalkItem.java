package de.dafuqs.chalk.chalk.items;

import de.dafuqs.chalk.chalk.Chalk;
import de.dafuqs.chalk.chalk.blocks.ChalkMarkBlock;
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

public class GlowChalkItem extends ChalkItem {

    public GlowChalkItem(Settings settings, DyeColor dyeColor) {
        super(settings, dyeColor);
    }
    
    public Block getChalkMarkBlock() {
        return Chalk.chalkVariants.get(this.dyeColor).glowChalkBlock;
    }

}
