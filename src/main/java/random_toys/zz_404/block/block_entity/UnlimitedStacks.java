package random_toys.zz_404.block.block_entity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import random_toys.zz_404.RandomToys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UnlimitedStacks {
    private ArrayList<UnlimitedStack> stacks = new ArrayList<>();

    public UnlimitedStacks() {
    }

    public @Unmodifiable List<UnlimitedStack> stacksView() {
        return Collections.unmodifiableList(stacks);
    }

    public UnlimitedStack getStack(Item item) {
        return stacks.stream().filter(stack -> stack.item == item)
                .findFirst().orElse(new UnlimitedStack(item, 0));
    }

    public int getCount(Item item) {
        return getStack(item).count;
    }

    public void setCount(Item item, int count) {
        if (count <= 0) {
            stacks.removeIf(stack -> stack.item == item);
            return;
        }
        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i).item == item) {
                stacks.set(i, new UnlimitedStack(item, count));
                return;
            }
        }
        stacks.add(new UnlimitedStack(item, count));
    }

    public void addCount(Item item, int count) {
        setCount(item, count + getCount(item));
    }

    public ItemStack addStack(@NotNull ItemStack stack) {
        if (stack.getItem() != null)
            addCount(stack.getItem(), stack.getCount());
        return stack;
    }

    public ItemStack removeStack(@NotNull ItemStack stack) {
        if (stack.getItem() != null)
            addCount(stack.getItem(), -stack.getCount());
        return stack;
    }

    public void clear() {
        stacks.clear();
    }

    public @NotNull ArrayList<ItemStack> intoStacks() {
        ArrayList<ItemStack> result = new ArrayList<>();
        result.add(ItemStack.EMPTY);
        for (UnlimitedStack stack : stacks) result.addAll(stack.splitStacks());
        return result;
    }

    public void setFromStacks(@NotNull DefaultedList<ItemStack> stacks) {
        clear();
        stacks.forEach(this::addStack);
    }

    public void readNbt(@NotNull NbtCompound nbt) {
        if (nbt.contains("UnlimitedStacks"))
            stacks = nbt.getList("UnlimitedStacks", NbtCompound.COMPOUND_TYPE).stream()
                    .map(UnlimitedStack::fromNbt).collect(Collectors.toCollection(ArrayList::new));
    }

    public void writeNbt(@NotNull NbtCompound nbt) {
        NbtList list = new NbtList();
        list.addAll(stacks.stream().map(UnlimitedStack::toNbt).toList());
        nbt.put("UnlimitedStacks", list);
    }

    public static class UnlimitedStacksProxy extends DefaultedList<ItemStack> {
        UnlimitedStacks stacks;

        private UnlimitedStacksProxy(@NotNull UnlimitedStacks stacks) {
            super(stacks.intoStacks(), ItemStack.EMPTY);
            this.stacks = stacks;
        }

        @Override
        public @NotNull ItemStack get(int index) {
            return super.get(index);
        }

        @Override
        public ItemStack set(int index, @NotNull ItemStack stack) {
            return stacks.removeStack(super.set(index, stacks.addStack(stack)));
        }

        @Override
        public void add(int index, ItemStack stack) {
            super.add(index, stacks.addStack(stack));
        }

        @Override
        public ItemStack remove(int index) {
            return stacks.removeStack(super.remove(index));
        }

        @Override
        public void clear() {
            super.clear();
            stacks.clear();
        }
    }

    public UnlimitedStacksProxy getProxy() {
        return new UnlimitedStacksProxy(this);
    }

    public record UnlimitedStack(Item item, int count) {
        private @NotNull ArrayList<ItemStack> splitStacks() {
            var result = new ArrayList<ItemStack>();
            int max = item.getMaxCount();
            int q = count / max, r = count % max;
            for (int i = 0; i < q; i++) result.add(new ItemStack(item, max));
            if (r != 0) result.add(new ItemStack(item, r));
            return result;
        }

        private @NotNull NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("Item", item.toString());
            nbt.putInt("Count", count);
            return nbt;
        }

        private static @Nullable UnlimitedStack fromNbt(NbtElement element) {
            try {
                NbtCompound nbt = (NbtCompound) element;
                Item item = Registries.ITEM.get(Identifier.of(nbt.getString("Item")));
                int count = nbt.getInt("Count");
                return new UnlimitedStack(item, count);
            } catch (Exception e) {
                RandomToys.error("Cannot read Unlimited Stacks from NBT Element: {}", element);
                RandomToys.error("", e);
                return null;
            }
        }

        @Override
        public @NotNull String toString() {
            return String.format("%s * %d", item.getName().getString(), count);
        }
    }
}
