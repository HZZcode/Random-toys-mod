package random_toys.zz_404;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;
import java.util.stream.IntStream;

public interface TransferableBlockEntity {
    DefaultedList<ItemStack> getInventory();

    default OptionalInt getSpace() {
        return IntStream.range(0, getInventory().size())
                .filter(k -> getInventory().get(k).isEmpty()).findFirst();
    }

    default ItemStack get(int index) {
        return getInventory().get(index);
    }

    default ItemStack set(int index, ItemStack element) {
        return getInventory().set(index, element);
    }

    default int size() {
        return getInventory().size();
    }

    default boolean transformSingle(Item from, ItemStack to) {
        int[] indexes = IntStream.range(0, getInventory().size())
                .filter(k -> !get(k).isEmpty() && get(k).getItem() == from).toArray();
        var space = getSpace().orElse(-1);
        if (indexes.length != 0) {
            int index = indexes[0];
            if (space != -1) {
                set(index, get(index).copyWithCount(get(index).getCount() - 1));
                set(space, to);
                return true;
            }
            else if (get(index).getCount() == 1) {
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
                var stacks = mergeStack(stack1, stack2);
                set(i, stacks[0]);
                set(j, stacks[1]);
            }
        }
    }

    default void swapStack(int i, int j) {
        var m = get(i).copy();
        set(i, get(j).copy());
        set(j, m);
    }
}
