package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;

import java.util.OptionalInt;

public class UnlimitedStorageBlockEntity extends BlockEntity implements TransferableBlockEntity {
    public final UnlimitedStacks stacks = new UnlimitedStacks();

    public UnlimitedStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public UnlimitedStorageBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.UNLIMITED_STORAGE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return stacks.getProxy();
    }

    @Override
    public void setInventory(DefaultedList<ItemStack> inventory) {
        stacks.setFromStacks(inventory);
    }

    @Override
    public OptionalInt getSpace() {
        var proxy = stacks.getProxy();
        return proxy.getFirst().isEmpty() ? OptionalInt.of(0) : OptionalInt.empty();
    }

    @Override
    public boolean transformSingle(Item from, ItemStack to) {
        // We're doing this because this method is used only in ExperienceCollectorBlockEntity.tick
        // to transform books into enchanted books, but unlimited storage doesn't store enchantments
        return false;
    }

    @Override
    public @NotNull ItemStack @NotNull [] mergeStack(@NotNull ItemStack stack1, @NotNull ItemStack stack2) {
        return new ItemStack[]{stack1, stack2};
    }

    @Override
    public void mergeStacks() {
    }

    @Override
    public void swapStack(int i, int j) {
    }

    @Override
    public @Nullable ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public @Nullable Text getContainerName() {
        return null;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        stacks.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        stacks.writeNbt(nbt);
    }

    public ItemStack takeRandomStack() {
        var view = stacks.stacksView();
        if (view.isEmpty() || world == null) return ItemStack.EMPTY;
        var stack = view.get(world.random.nextInt(view.size()));
        Item item = stack.item();
        int count = Math.min(item.getMaxCount(), stack.count());
        return stacks.removeStack(new ItemStack(item, count));
    }

    public ItemStack takeStack(Item item, int maxCount) {
        return stacks.removeStack(new ItemStack(item, Math.min(stacks.getCount(item), maxCount)));
    }

    public ItemStack takeStack(Item item) {
        return takeStack(item, item.getMaxCount());
    }

    public Text getInfo() {
        final int maxCount = 5;
        var view = stacks.stacksView();
        int count = view.size();
        if (count == 0) return Text.translatable("message.random-toys.unlimited_storage.empty");
        var part = count > maxCount ? view.subList(0, maxCount) : view;
        String itemsMsg = String.join("\n", part.stream().map(UnlimitedStacks.UnlimitedStack::toString).toList());
        int stacks = view.stream().mapToInt(stack -> (stack.count() + 1) / stack.item().getMaxCount()).sum();
        if (count <= maxCount)
            return Text.translatable("message.random-toys.unlimited_storage.few_items",
                    count, stacks, itemsMsg);
        return Text.translatable("message.random-toys.unlimited_storage.lot_items",
                count, stacks, itemsMsg, count - maxCount);
    }

    public Text getInfo(Item item) {
        return Text.translatable("message.random-toys.unlimited_storage.item", stacks.getStack(item).toString());
    }
}
