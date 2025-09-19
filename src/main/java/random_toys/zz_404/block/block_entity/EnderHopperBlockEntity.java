package random_toys.zz_404.block.block_entity;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.registry.ModCriteria;
import random_toys.zz_404.registry.ModGamerules;
import random_toys.zz_404.block.BeltBlock;
import random_toys.zz_404.block.EnderHopperBlock;
import random_toys.zz_404.reflection_utils.BlockMovingUtils;

public class EnderHopperBlockEntity extends BlockEntity implements EnderBlockEntity {
    public BlockPos linked;
    public RegistryKey<World> dimension;

    public BlockPos getLinked() {
        return linked;
    }

    public void setLinked(BlockPos linked) {
        this.linked = linked;
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public void setDimension(RegistryKey<World> dimension) {
        this.dimension = dimension;
    }

    public EnderHopperBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        linked = null;
        dimension = null;
    }

    public EnderHopperBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.ENDER_HOPPER, blockPos, blockState);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        readNbtFrom(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        writeNbtTo(nbt);
    }

    public void tick(@NotNull World world, BlockPos pos, @NotNull BlockState state) {
        if (nullCheck()) return;
        world.setBlockState(pos, state.with(EnderHopperBlock.POWERED,
                world.getReceivedRedstonePower(pos) != 0));
        if (world.getBlockEntity(linked) instanceof EnderHopperBlockEntity
                && state.get(EnderHopperBlock.POWERED) && world instanceof ServerWorld && world.getTime() % 4 == 0) {
            if (canMoveTo(pos.up(), linked.down())) {
                copyBlock(world, pos.up(), linked.down());
                moveBlock(world, pos.up(), linked.down());
            }
            for (Entity entity : world.getEntitiesByClass(Entity.class, Box.from(Vec3d.of(pos)).expand(3),
                    entity -> BeltBlock.isStepping(pos, entity) && !entity.isSpectator())) {
                Vec3d vec = linked.toBottomCenterPos().subtract(0, entity.getHeight(), 0);
                if (entity.getPos().subtract(vec).length() >= 500 && entity instanceof ServerPlayerEntity player)
                    ModCriteria.ENDER_HOPPER_TELEPORT.trigger(player);
                entity.requestTeleport(vec.x, vec.y, vec.z);
            }
        }
    }

    private boolean canMoveTo(@NotNull BlockPos from, @NotNull BlockPos to) {
        return world != null && (world.getBlockState(to).isAir()
                || world.getBlockState(to).isReplaceable()
                || BlockMovingUtils.canMerge(world, from, to));
    }

    private void copyBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (!world.getGameRules().getBoolean(ModGamerules.ENDER_HOPPER_MOVE_BLOCK_ENTITY)
                && world.getBlockEntity(from) != null) return;
        BlockMovingUtils.copyBlock(world, from, to);
    }

    private void moveBlock(@NotNull World world, @NotNull BlockPos from, @NotNull BlockPos to) {
        if (!world.getGameRules().getBoolean(ModGamerules.ENDER_HOPPER_MOVE_BLOCK_ENTITY)
                && world.getBlockEntity(from) != null) return;
        boolean destroy = world.getGameRules().getBoolean(ModGamerules.ENDER_HOPPER_DESTROY_BLOCK_ENTITY);
        BlockMovingUtils.moveBlock(world, from, to, pos, destroy, true);
    }
}
