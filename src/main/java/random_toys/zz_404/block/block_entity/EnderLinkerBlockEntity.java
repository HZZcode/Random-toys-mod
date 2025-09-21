package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;

import java.util.ArrayList;
import java.util.Objects;

public class EnderLinkerBlockEntity extends LootableContainerBlockEntity implements TransferableBlockEntity, EnderBlockEntity {
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

    public EnderLinkerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        linked = null;
        dimension = null;
    }

    public EnderLinkerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.ENDER_LINKER, blockPos, blockState);
    }

    @Override
    public @Nullable DefaultedList<ItemStack> getInventory() {
        TransferableBlockEntity entity = getLinkedEntity();
        return entity == null ? DefaultedList.of() : entity.getInventory();
    }

    @Override
    public void setInventory(DefaultedList<ItemStack> inventory) {
        TransferableBlockEntity entity = getLinkedEntity();
        if (entity != null) entity.setInventory(inventory);
    }

    public @Nullable TransferableBlockEntity getLinkedEntity() {
        ArrayList<BlockPos> self = new ArrayList<>();
        self.add(pos);
        return getLinkedEntity(self);
    }

    private @Nullable TransferableBlockEntity getLinkedEntity(ArrayList<BlockPos> found) {
        if (nullCheck()) return null;
        BlockEntity link = Objects.requireNonNull(world).getBlockEntity(linked);
        if (link instanceof EnderLinkerBlockEntity entity) {
            if (found.stream().anyMatch(pos -> pos.equals(linked))) return null;
            found.add(entity.pos);
            return entity.getLinkedEntity(found);
        }
        if (link instanceof TransferableBlockEntity entity) return entity;
        return null;
    }

    @Override
    public Text getContainerName() {
        return Text.translatable("container.random-toys.ender_linker",
                getLinkedEntity() == null ? "" : getLinkedEntity().getContainerName());
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return getInventory();
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        setInventory(inventory);
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        TransferableBlockEntity entity = getLinkedEntity();
        return entity == null ? null : entity.createScreenHandler(syncId, playerInventory);
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

    @Override
    public int size() {
        return TransferableBlockEntity.super.size();
    }
}