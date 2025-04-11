package random_toys.zz_404.block.block_entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.block.TransferringBlock;

import java.util.Arrays;
import java.util.Optional;

public class OxidizerBlockEntity extends LootableContainerBlockEntity implements TransferableBlockEntity {
    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public OxidizerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public OxidizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.OXIDIZER, blockPos, blockState);
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
    public Text getContainerName() {
        return Text.translatable("container.random-toys.oxidizer",
                world == null ? 0 : (double) getOxidizeTime(world, pos) / 20);
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
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory, registryLookup);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        world.setBlockState(pos, state.with(TransferringBlock.POWERED,
                world.getReceivedRedstonePower(pos) != 0));
        if (world.getReceivedRedstonePower(pos) != 0) return;
        for (int i = 0; i < size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.getItem() instanceof BlockItem item) {
                Optional<Block> optional = Oxidizable.getIncreasedOxidationBlock(item.getBlock());
                if (optional.isPresent()) {
                    Block oxidized = optional.get();
                    int count = stack.getCount();
                    if (world.random.nextInt(count * getOxidizeTime(world, pos)) == 0)
                        inventory.set(i, stack.copyComponentsToNewStack(oxidized, count));
                }
            }
        }
    }

    private int getOxidizeTime(@NotNull World world, @NotNull BlockPos pos) {
        BlockState[] nears = {
                world.getBlockState(pos.up()),
                world.getBlockState(pos.down()),
                world.getBlockState(pos.north()),
                world.getBlockState(pos.south()),
                world.getBlockState(pos.west()),
                world.getBlockState(pos.east()),
        };
        int water = (int) Arrays.stream(nears).filter(this::isWater).count();
        int air = (int) Arrays.stream(nears).filter(this::isAir).count();
        return 320 / ((water + 1) * (air + 1));
    }

    private boolean isWater(@NotNull BlockState state) {
        return state.getFluidState().isOf(Fluids.WATER) || state.getFluidState().isOf(Fluids.FLOWING_WATER);
    }

    private boolean isAir(@NotNull BlockState state) {
        return state.isAir();
    }
}