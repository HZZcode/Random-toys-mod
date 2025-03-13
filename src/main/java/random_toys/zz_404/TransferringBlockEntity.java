package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransferringBlockEntity extends BlockEntity implements Clearable, SingleStackInventory.SingleStackBlockEntityInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public TransferringBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public TransferringBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.TRANSFER, pos, state);
    }

    public void tick(@NotNull World world, @NotNull BlockPos pos, BlockState state) {
        if (world.isClient) return;
        world.setBlockState(pos, state.with(TransferringBlock.POWERED,
                world.getReceivedRedstonePower(pos) != 0));
        if (world.getReceivedRedstonePower(pos) != 0) return;
        BlockEntity[] inputs = {
                world.getBlockEntity(pos.up()),
                world.getBlockEntity(pos.north()),
                world.getBlockEntity(pos.south()),
                world.getBlockEntity(pos.west()),
                world.getBlockEntity(pos.east()),
        };
        BlockEntity out = world.getBlockEntity(pos.down());
        if (out instanceof TransferableBlockEntity output) {
            var space = output.getSpace();
            if (space.isEmpty()) return;
            for (BlockEntity in : inputs) {
                if (in instanceof TransferableBlockEntity input) {
                    if (input == output) continue;
                    for (int i = 0; i < 27; i++) {
                        ItemStack stack = input.get(i);
                        if (stack == null) continue;
                        if (match(stack) && !stack.isEmpty()) {
                            RandomToys.log("filter={}, {}->{}, item={}",
                                    getItem().getName().getString(),
                                    i, space.orElseThrow(),
                                    stack.getItem().getName().getString());
                            output.set(space.orElseThrow(), stack.copy());
                            input.set(i, ItemStack.EMPTY);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public ItemStack getStack() {
        return inventory.getFirst();
    }

    @Override
    public void setStack(ItemStack stack) {
        inventory.set(0, stack);
        stack.capCount(getMaxCountPerStack());
        markDirty();
    }

    public boolean isEmpty() {
        return inventory.getFirst().isEmpty();
    }

    public Item getItem() {
        return inventory.getFirst().getItem();
    }

    public boolean match(ItemStack stack) {
        if (isEmpty()) return true;
        if (getItem() == ModItems.REGEX_FILTER)
            return RegexFilterItem.match(inventory.getFirst(), stack);
        return stack.isOf(getItem());
    }

    public void setItem(Item item) {
        inventory.set(0, new ItemStack(item));
    }

    public void setItem(@NotNull ItemStack stack) {
        inventory.set(0, stack.copy());
    }

    public void clearItem() {
        inventory.set(0, ItemStack.EMPTY);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return false;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("FilterItem", 10)) inventory.set(0, ItemStack.fromNbt(registryLookup,
                nbt.getCompound("FilterItem")).orElse(ItemStack.EMPTY));
        else inventory.set(0, ItemStack.EMPTY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!getStack().isEmpty()) nbt.put("FilterItem", getStack().encode(registryLookup));
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
