package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.block.ChunkDestroyerBlock;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractDestroyerBlockEntity extends LootableContainerBlockEntity implements TransferableBlockEntity {
    public DefaultedList<ItemStack> inventory;
    protected int cooldown = 0;
    protected final int maxCooldown;

    protected AbstractDestroyerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState,
                                           int size, int maxCooldown) {
        super(blockEntityType, blockPos, blockState);
        this.inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
        this.maxCooldown = maxCooldown;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public void setInventory(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        if (!readLootTable(nbt)) Inventories.readNbt(nbt, inventory, registryLookup);
        if (nbt.contains("Cooldown")) cooldown = nbt.getInt("Cooldown");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!writeLootTable(nbt)) Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("Cooldown", cooldown);
    }

    public void tickDestroy(@NotNull World world, BlockState state, Supplier<List<@NotNull BlockPos>> destroyPosSupplier) {
        if (world instanceof ServerWorld server) {
            if (!state.get(ChunkDestroyerBlock.POWERED)) return;
            if (cooldown > 0) {
                cooldown--;
                return;
            }
            for (BlockPos pos : destroyPosSupplier.get())
                DestroyerHelper.destroy(server, pos, getStorage(world));
            mergeStacks();
            cooldown = maxCooldown;
        }
    }

    public void tickDestroy(@NotNull World world, BlockState state, @NotNull BlockPos destroyPos) {
        tickDestroy(world, state, () -> List.of(destroyPos));
    }

    protected DefaultedList<ItemStack> getStorage(@NotNull World world) {
        for (BlockPos near : new BlockPos[]{pos.up(), pos.down(), pos.north(), pos.south(), pos.west(), pos.east()})
            if (world.getBlockEntity(near) instanceof TransferableBlockEntity entity)
                if (entity.getSpace().isPresent()) return entity.getInventory();
        return inventory;
    }
}
