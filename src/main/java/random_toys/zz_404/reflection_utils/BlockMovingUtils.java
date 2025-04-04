package random_toys.zz_404.reflection_utils;

import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;

public class BlockMovingUtils {
    public static boolean canMerge(World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (world == null) return false;
        BlockState fromState = world.getBlockState(from);
        BlockState toState = world.getBlockState(to);
        if (fromState.getBlock() instanceof Waterloggable && toState.isOf(Blocks.WATER))
            return true;
        if (toState.getBlock() instanceof Waterloggable && fromState.isOf(Blocks.WATER))
            return true;
        return fromState.getBlock() == toState.getBlock() && fromState.getBlock() instanceof SlabBlock
                && ((fromState.get(SlabBlock.TYPE) == SlabType.BOTTOM
                && toState.get(SlabBlock.TYPE) == SlabType.TOP)
                || (fromState.get(SlabBlock.TYPE) == SlabType.TOP
                && toState.get(SlabBlock.TYPE) == SlabType.BOTTOM));
    }

    public static void copyBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (world.getBlockEntity(from) != null) return;
        BlockState state = world.getBlockState(from);
        BlockState toState = world.getBlockState(to);
        boolean slab = state.getBlock() instanceof SlabBlock && toState.getBlock() instanceof SlabBlock;
        boolean fromWater = state.isOf(Blocks.WATER) && toState.getBlock() instanceof Waterloggable;
        boolean toWater = toState.isOf(Blocks.WATER) && state.getBlock() instanceof Waterloggable;
        boolean waterlogged = fromWater || toWater;
        if (!fromWater) world.setBlockState(to, state, Block.NOTIFY_ALL | Block.FORCE_STATE);
        try {
            if (waterlogged)
                world.setBlockState(to, world.getBlockState(to).with(Properties.WATERLOGGED, true),
                        Block.NOTIFY_ALL | Block.FORCE_STATE);
            if (slab) {
                world.setBlockState(to, world.getBlockState(to).with(SlabBlock.TYPE, SlabType.DOUBLE),
                        Block.NOTIFY_ALL | Block.FORCE_STATE);
                world.setBlockState(to, world.getBlockState(to).with(SlabBlock.WATERLOGGED, false),
                        Block.NOTIFY_ALL | Block.FORCE_STATE);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    public static void moveBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to, @NotNull BlockPos by, boolean destroy, boolean notMoved) {
        if (world.getBlockEntity(from) != null) return;
        if (!destroy) {
            boolean success = BlockEntityMovingUtils.tryMoveBlockEntity(world, from, to);
            if (!success) {
                RandomToys.error("Cannot move BlockEntity from {} to {}! Falling back to destroying",
                        from.toShortString(), to.toShortString());
                destroy = true;
            }
        }
        if (notMoved) {
            world.setBlockState(from, Blocks.AIR.getDefaultState(), Block.SKIP_DROPS | Block.FORCE_STATE | Block.MOVED);
            if (destroy) world.removeBlockEntity(from);
        }
        updateAt(world, from, by);
        updateAt(world, to, by);
    }

    public static void updateAt(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockPos by) {
        world.getBlockState(pos).neighborUpdate(world, pos, world.getBlockState(by).getBlock(), by, true);
        Block.postProcessState(world.getBlockState(pos), world, pos);
        world.updateListeners(pos, Blocks.AIR.getDefaultState(), world.getBlockState(pos), Block.NOTIFY_ALL_AND_REDRAW);
        world.setBlockState(pos, world.getBlockState(pos), Block.NOTIFY_ALL);
        world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock());
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
    }
}
