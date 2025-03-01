package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class RandomizerBlockEntity extends LootableContainerBlockEntity implements Clearable, SingleStackInventory.SingleStackBlockEntityInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public RandomizerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public RandomizerBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.RANDOMIZER, pos, state);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.random-toys.randomizer");
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
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public int size() {
        return 1;
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

    public boolean addItem(ItemStack itemStack) {
        if (inventory.getFirst().isEmpty()) {
            inventory.set(0, itemStack.copy());
            markDirty();
            return true;
        }
        return false;
    }

    public ItemStack removeItem(){
        if(!inventory.getFirst().isEmpty()){
            ItemStack itemStack = inventory.getFirst().copy();
            inventory.set(0, ItemStack.EMPTY);
            markDirty();
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    public RandomizerItemType getItemType(){
        return RandomizerItemType.getTypeFromItem(inventory.getFirst());
    }

    public int getRandomNumber() {
        if (inventory.getFirst().isEmpty()) return 0;
        Item item = inventory.getFirst().getItem();
        if (item instanceof RandomizerItem randomizer) return randomizer.getRandomNumber();
        return 0;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }

    @Override
    public ItemStack getStack() {
        return this.inventory.getFirst();
    }

    @Override
    public void setStack(ItemStack stack) {
        this.inventory.set(0, stack);
        stack.capCount(getMaxCountPerStack());
        this.markDirty();
    }

    public void updateBlockState(@NotNull World world, BlockPos pos, @NotNull BlockState state) {
        if (!world.isClient)
            world.setBlockState(pos, state.with(RandomizerBlock.ITEM_TYPE, getItemType()));
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return inventory.getFirst().isEmpty() && stack.getItem() instanceof RandomizerItem;
    }

    public void dropItem() {
        if (world != null && !world.isClient) {
            BlockPos blockPos = getPos();
            ItemStack itemStack = new ItemStack(inventory.getFirst().getItem(), 1);
            Block.dropStack(world, blockPos, itemStack);
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        updateBlockState(world, pos, state);
    }
}