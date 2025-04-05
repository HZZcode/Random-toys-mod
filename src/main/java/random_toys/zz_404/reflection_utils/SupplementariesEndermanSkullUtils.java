package random_toys.zz_404.reflection_utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public class SupplementariesEndermanSkullUtils {
    @SuppressWarnings("unchecked")
    private static @Nullable Item getHeadItem() {
        try {
            Class<?> ModRegistryClass = Class.forName("net.mehvahdjukaar.supplementaries.reg.ModRegistry");
            Field EndermanHeadField = ModRegistryClass.getDeclaredField("ENDERMAN_SKULL_ITEM");
            EndermanHeadField.setAccessible(true);
            return ((Supplier<Item>) EndermanHeadField.get(null)).get();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            return null;
        }
    }

    public static ItemStack getHead() {
        Item item = getHeadItem();
        if (item == null) return ItemStack.EMPTY;
        return new ItemStack(item);
    }
}
