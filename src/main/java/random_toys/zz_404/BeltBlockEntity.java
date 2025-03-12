package random_toys.zz_404;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.reflection_utils.BlockEntityMovingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BeltBlockEntity extends BlockEntity {
    public BlockPos powerSource = null;
    private static long previousTick = 0;
    private static final ArrayList<MovedBlockPos> moved = new ArrayList<>();

    public BeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BeltBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.BELT, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("Source", NbtElement.INT_ARRAY_TYPE)) {
            int[] pos = nbt.getIntArray("Source");
            if (pos.length == 3) powerSource = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (powerSource != null) {
            int[] pos = {powerSource.getX(), powerSource.getY(), powerSource.getZ()};
            nbt.putIntArray("Source", pos);
        }
    }

    public synchronized void tick(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state) {
        for (Entity entity : world.getEntitiesByClass(Entity.class, Box.from(Vec3d.of(pos)).expand(3),
                entity -> BeltBlock.isStepping(pos, entity)))
            BeltBlock.moveEntity(state, world, pos, entity);

        world.setBlockState(pos, state.with(BeltBlock.POWERED, BeltBlock.isPowered(world, pos, state)));
        Direction direction = state.get(BeltBlock.DIRECTION);
        BlockPos up = pos.up();
        long time = world.getTime();
        if (!world.isClient && time % (20 / BeltBlock.speed) == 0 && state.get(BeltBlock.POWERED)) {
            if (time > previousTick) moved.clear();
            if (!world.getBlockState(up).isReplaceable()) {
                ArrayList<BlockPos> froms = getStickingBlocks(up);
                ArrayList<BlockPos> tos = froms.stream().map(from -> from.offset(direction))
                        .collect(Collectors.toCollection(ArrayList::new));
                if (tos.stream().allMatch(to -> canMoveTo(direction, to, froms))) {
                    froms.sort((pos1, pos2)
                            -> -Integer.compare(getDirectionComponent(pos1, direction),
                            getDirectionComponent(pos2, direction)));
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

    private boolean canMoveTo(@NotNull Direction direction, @NotNull BlockPos to, ArrayList<BlockPos> froms) {
        return world != null && (world.getBlockState(to).isAir() || froms.contains(to)
                || world.getBlockState(to).isReplaceable() || canMerge(direction, to));
    }

    private boolean canMerge(@NotNull Direction direction, @NotNull BlockPos to) {
        if (world == null) return false;
        BlockPos from = to.offset(direction.getOpposite());
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

    private synchronized boolean isMoved(BlockPos from) {
        return moved.stream().anyMatch(movedBlockPos
                -> movedBlockPos.to.equals(from) && !movedBlockPos.by.equals(pos));
    }

    private synchronized void copyBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (isMoved(from)) return;
        if (!world.getGameRules().getBoolean(ModGamerules.BELT_MOVE_BLOCK_ENTITY)
                && world.getBlockEntity(from) != null) return;
        BlockState state = world.getBlockState(from);
        moved.add(new MovedBlockPos(pos, from, to));
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

    private synchronized void moveBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
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
        updateAt(world, from, pos);
        updateAt(world, to, pos);
    }

    private void updateAt(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockPos by) {
        world.getBlockState(pos).neighborUpdate(world, pos, world.getBlockState(by).getBlock(), by, true);
        Block.postProcessState(world.getBlockState(pos), world, pos);
        world.updateListeners(pos, Blocks.AIR.getDefaultState(), world.getBlockState(pos), Block.NOTIFY_ALL_AND_REDRAW);
        world.setBlockState(pos, world.getBlockState(pos), Block.NOTIFY_ALL);
        world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock());
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
    }

    @Contract("_ -> new")
    private @NotNull ArrayList<BlockPos> getStickingBlocks(BlockPos pos) {
        try {
            return getStickingBlocks(pos, new ArrayList<>());
        } catch (OutOfMaxException ignored) {
            return new ArrayList<>();
        }
    }

    @Contract("_, _ -> new")
    private @NotNull ArrayList<BlockPos> getStickingBlocks(BlockPos pos, @NotNull ArrayList<BlockPos> found)
            throws OutOfMaxException {
        if (world == null) return new ArrayList<>();
        if (found.size() > world.getGameRules().getInt(ModGamerules.BELT_MAX_BLOCK_COUNT))
            throw new OutOfMaxException(found);
        if (found.contains(pos)) return found;
        found.add(pos);
        BlockPos[] nears = {pos.up(), pos.down(), pos.north(), pos.south(), pos.west(), pos.east()};
        ArrayList<BlockPos> remaining = Arrays.stream(nears)
                .filter(blockPos -> !found.contains(blockPos))
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<BlockPos> linking = getLinkingBlocks(pos).stream()
                .filter(blockPos -> !found.contains(blockPos))
                .collect(Collectors.toCollection(ArrayList::new));
        found.addAll(linking);
        remaining.removeAll(linking);
        for (BlockPos pos1 : remaining.stream()
                .filter(blockPos -> isSticking(pos, blockPos)).toList()) {
            ArrayList<BlockPos> sub = getStickingBlocks(pos1, found).stream()
                    .filter(blockPos -> !found.contains(blockPos))
                    .collect(Collectors.toCollection(ArrayList::new));
            found.addAll(sub);
        }
        return found;
    }

    private boolean isSticking(BlockPos pos1, BlockPos pos2) {
        if (world == null) return false;
        if (world.getBlockState(pos1).isReplaceable() || world.getBlockState(pos2).isReplaceable())
            return false;
        return isSticking(world.getBlockState(pos1).getBlock(), world.getBlockState(pos2).getBlock());
    }

    private boolean isSticking(Block block1, Block block2) {
        if (block1 == Blocks.SLIME_BLOCK && block2 == Blocks.HONEY_BLOCK) return false;
        if (block1 == Blocks.HONEY_BLOCK && block2 == Blocks.SLIME_BLOCK) return false;
        return (block1 == Blocks.SLIME_BLOCK || block1 == Blocks.HONEY_BLOCK)
                && block2.getDefaultState().getPistonBehavior() != PistonBehavior.PUSH_ONLY
                && block2 != ModBlocks.BELT;
    }

    @Contract("_ -> new")
    private @NotNull ArrayList<BlockPos> getLinkingBlocks(BlockPos pos) {
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
        if (state.getBlock() instanceof PistonBlock && state.get(PistonBlock.EXTENDED)) {
            if (world.getBlockState(pos.offset(state.get(PistonBlock.FACING)))
                    .getBlock() instanceof PistonHeadBlock)
                ans.add(pos.offset(state.get(PistonBlock.FACING)));
        }
        if (state.getBlock() instanceof PistonHeadBlock) {
            if (world.getBlockState(pos.offset(state.get(PistonHeadBlock.FACING).getOpposite()))
                    .getBlock() instanceof PistonBlock)
                ans.add(pos.offset(state.get(PistonHeadBlock.FACING).getOpposite()));
        }
        return ans;
    }

    record MovedBlockPos(BlockPos by, BlockPos from, BlockPos to) {}

    static class OutOfMaxException extends Exception {
        public ArrayList<BlockPos> current;

        OutOfMaxException(ArrayList<BlockPos> current) {
            this.current = current;
        }
    }
}
