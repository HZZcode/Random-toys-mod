package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;

public class EnderSwitchBlockEntity extends BlockEntity implements EnderBlockEntity {
    public BlockPos linked;
    public RegistryKey<World> dimension;

    @Override
    public @Nullable World getWorld() {
        return super.getWorld();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
    }

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

    public EnderSwitchBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        linked = null;
        dimension = null;
    }

    public EnderSwitchBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.ENDER_SWITCH, blockPos, blockState);
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

    public void syncState(@NotNull World world, @NotNull BlockState state) {
        if (nullCheck()) return;
        if (world instanceof ServerWorld serverWorld) {
            ServerWorld leverWorld = serverWorld.getServer().getWorld(dimension);
            if (leverWorld == null) return;
            BlockState leverState = leverWorld.getBlockState(linked);
            if (!leverState.isOf(Blocks.LEVER)) return;
            boolean powered = state.get(Properties.POWERED);
            if (leverState.get(Properties.POWERED) != powered) {
                leverWorld.setBlockState(linked, leverState.with(Properties.POWERED, powered));
                leverWorld.updateNeighbors(linked, Blocks.LEVER);
            }
        }
    }
}
