package random_toys.zz_404;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.reflection_utils.BlockEntityMovingUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BeltBlockEntity extends BlockEntity {
//    public BlockPos powerSource = null;
    private static long previousTick = 0;
    private static final ArrayList<MovedBlockPos> moved = new ArrayList<>();

    public BeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BeltBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.BELT, pos, state);
    }

    public void tick(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state) {
        Direction direction = state.get(BeltBlock.DIRECTION);
        BlockPos up = pos.up();
        long time = world.getTime();
        if (!world.isClient && time % (20 / BeltBlock.speed) == 0 && state.get(BeltBlock.POWERED)) {
            if (time > previousTick) moved.clear();
            if (!world.getBlockState(up).isAir()) {
                ArrayList<BlockPos> froms = getStickingBlocks(up);
                ArrayList<BlockPos> tos = froms.stream().map(from -> from.offset(direction))
                        .collect(Collectors.toCollection(ArrayList::new));
                if (tos.stream().allMatch(to -> canMoveTo(to, froms))) {
                    froms.sort((pos1, pos2)
                            -> -Integer.compare(getDirectionComponent(pos1, direction),
                            getDirectionComponent(pos2, direction)));
                    RandomToys.log("Froms: {}", froms.toString());
                    for (BlockPos from : froms) copyBlock(world, from, from.offset(direction));
                    for (BlockPos from : froms) moveBlock(world, from, from.offset(direction));
                }
            }
            previousTick = time;
        }
    }

    private int getDirectionComponent(@NotNull BlockPos pos, @NotNull Direction direction) {
        return pos.getX() * direction.getOffsetX()
                + pos.getY() * direction.getOffsetY()
                + pos.getZ() * direction.getOffsetZ();
    }

    private boolean canMoveTo(@NotNull BlockPos to, ArrayList<BlockPos> froms) {
        return world != null && (world.getBlockState(to).isAir() || froms.contains(to));
    }

    private boolean isMoved(BlockPos from) {
        return moved.stream().anyMatch(movedBlockPos
                -> movedBlockPos.to.equals(from) && !movedBlockPos.by.equals(pos));
    }

    private void copyBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (isMoved(from)) return;
        if (!world.getGameRules().getBoolean(ModGamerules.BELT_MOVE_BLOCK_ENTITY)
                && world.getBlockEntity(from) != null) return;
        BlockState state = world.getBlockState(from);
        moved.add(new MovedBlockPos(pos, from, to));
        world.setBlockState(to, state, Block.SKIP_DROPS | Block.FORCE_STATE | Block.MOVED);
    }

    private void moveBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (isMoved(from)) return;
        if (!world.getGameRules().getBoolean(ModGamerules.BELT_MOVE_BLOCK_ENTITY)
                && world.getBlockEntity(from) != null) return;
        boolean destroy = world.getGameRules().getBoolean(ModGamerules.BELT_DESTROY_BLOCK_ENTITY);
        if (!destroy) {
            boolean success = BlockEntityMovingUtils.tryMoveBlockEntity(world, from, to);
            if (!success) {
                RandomToys.error("Cannot move BlockEntity from {} to {}! Falling back to destroying",
                        from.toShortString(), to.toShortString());
                destroy = true;
            }
        }
        if (moved.stream().noneMatch(movedBlockPos -> movedBlockPos.to.equals(from))) {
            world.setBlockState(from, Blocks.AIR.getDefaultState(), Block.SKIP_DROPS | Block.FORCE_STATE| Block.MOVED);
            if (destroy) world.removeBlockEntity(from);
        }
        updateAt(world, from);
        updateAt(world, to);
    }

    private void updateAt(@NotNull World world, @NotNull BlockPos pos) {
        world.updateListeners(pos, Blocks.AIR.getDefaultState(), world.getBlockState(pos), Block.NOTIFY_ALL_AND_REDRAW);
        world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock());
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
    }

    @Contract("_ -> new")
    private @NotNull ArrayList<BlockPos> getStickingBlocks(BlockPos pos) {
        ArrayList<BlockPos> ans = new ArrayList<>();
        ans.add(pos);
        if (world == null) return ans;
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof DoorBlock) {
            if (state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER
                    && world.getBlockState(pos.down()).getBlock() instanceof DoorBlock)
                ans.add(pos.down());
            if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                    && world.getBlockState(pos.up()).getBlock() instanceof DoorBlock)
                ans.add(pos.up());
        }
        if (state.getBlock() instanceof BedBlock) {
            if (state.get(BedBlock.PART) == BedPart.FOOT)
                ans.add(pos.offset(state.get(BedBlock.FACING)));
            if (state.get(BedBlock.PART) == BedPart.HEAD)
                ans.add(pos.offset(state.get(BedBlock.FACING).getOpposite()));
        }
        if (state.getBlock() instanceof ChestBlock) {
            if (state.get(ChestBlock.CHEST_TYPE) == ChestType.LEFT)
                ans.add(pos.offset(state.get(ChestBlock.FACING).rotateYClockwise()));
            if (state.get(ChestBlock.CHEST_TYPE) == ChestType.RIGHT)
                ans.add(pos.offset(state.get(ChestBlock.FACING).rotateYCounterclockwise()));
        }
        return ans;
    }

    record MovedBlockPos(BlockPos by, BlockPos from, BlockPos to) {}
}
