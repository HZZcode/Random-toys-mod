package random_toys.zz_404.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import random_toys.zz_404.registry.ModItems;

public enum RandomizerItemType implements StringIdentifiable {
    EMPTY("empty"),
    RANDOMIZER1("randomizer1"),
    RANDOMIZER2("randomizer2"),
    RANDOMIZER3("randomizer3");

    private final String name;

    RandomizerItemType(final String name){
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }

    public static RandomizerItemType getTypeFromItem(ItemStack itemStack) {
        if (itemStack.isOf(ModItems.RANDOMIZER1)) {
            return RANDOMIZER1;
        }
        else if (itemStack.isOf(ModItems.RANDOMIZER2)) {
            return RANDOMIZER2;
        }
        else if (itemStack.isOf(ModItems.RANDOMIZER3)) {
            return RANDOMIZER3;
        }
        else {
            return EMPTY;
        }
    }
}

