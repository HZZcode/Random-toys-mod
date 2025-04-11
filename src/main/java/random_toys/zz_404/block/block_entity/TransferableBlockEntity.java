package random_toys.zz_404.block.block_entity;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public interface TransferableBlockEntity {
    @Nullable DefaultedList<ItemStack> getInventory();
    void setInventory(DefaultedList<ItemStack> inventory);
    ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory);
    Text getContainerName();

    default OptionalInt getSpace() {
        DefaultedList<ItemStack> inventory = getInventory();
        if (inventory == null || inventory.isEmpty()) return OptionalInt.empty();
        return IntStream.range(0, inventory.size())
                .filter(k -> inventory.get(k).isEmpty()).findFirst();
    }

    @Nullable default ItemStack get(int index) {
        DefaultedList<ItemStack> inventory = getInventory();
        if (inventory == null || inventory.isEmpty()) return null;
        return inventory.get(index);
    }

    @Nullable default ItemStack set(int index, ItemStack element) {
        DefaultedList<ItemStack> inventory = getInventory();
        if (inventory == null || inventory.isEmpty()) return null;
        return inventory.set(index, element);
    }

    default int size() {
        if (getInventory() == null) return 0;
        return getInventory().size();
    }

    default boolean transformSingle(Item from, ItemStack to) {
        if (getInventory() == null) return false;
        int[] indexes = IntStream.range(0, getInventory().size())
                .filter(k -> get(k) != null && !Objects.requireNonNull(get(k)).isEmpty()
                        && Objects.requireNonNull(get(k)).getItem() == from).toArray();
        var space = getSpace().orElse(-1);
        if (indexes.length != 0) {
            int index = indexes[0];
            ItemStack stack = Objects.requireNonNull(get(index));
            if (space != -1) {
                set(index, stack.copyWithCount(stack.getCount() - 1));
                set(space, to);
                return true;
            }
            else if (stack.getCount() == 1) {
                set(index, to);
                return true;
            }
        }
        return false;
    }

    default @NotNull ItemStack @NotNull [] mergeStack(@NotNull ItemStack stack1, @NotNull ItemStack stack2) {
        ItemStack result1, result2;
        if (stack1.isEmpty() && !stack2.isEmpty()) {
            result1 = stack2.copy();
            result2 = ItemStack.EMPTY;
        }
        else if (stack1.getItem() == stack2.getItem() && stack1.getCount() < stack1.getItem().getMaxCount()) {
            Item item = stack1.getItem();
            int max = item.getMaxCount();
            int count = stack1.getCount() + stack2.getCount();
            if (count <= max){
                result1 = new ItemStack(item, count);
                result2 = ItemStack.EMPTY;
            }
            else {
                result1 = new ItemStack(item, max);
                result2 = new ItemStack(item, count - max);
            }
        }
        else {
            result1 = stack1.copy();
            result2 = stack2.copy();
        }
        ItemStack[] result = new ItemStack[2];
        result[0] = result1;
        result[1] = result2;
        return result;
    }

    default void mergeStacks() {
        for (int i = 0; i < size(); i++) {
            for (int j = i + 1; j < size(); j++) {
                ItemStack stack1 = get(i);
                ItemStack stack2 = get(j);
                if (stack1 == null || stack2 == null) continue;
                var stacks = mergeStack(stack1, stack2);
                set(i, stacks[0]);
                set(j, stacks[1]);
            }
        }
    }

    default void swapStack(int i, int j) {
        ItemStack stack1 = get(i);
        ItemStack stack2 = get(j);
        if (stack1 == null || stack2 == null) return;
        var m = stack1.copy();
        set(i, stack2.copy());
        set(j, m);
    }
}
