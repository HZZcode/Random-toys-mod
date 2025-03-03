package random_toys.zz_404;

import net.minecraft.item.Item;

import java.util.HashSet;

public class EndermanAvoidStarringItems {
    private static final HashSet<Item> items;

    public static void addAvoidStarringItem(Item item) {
        items.add(item);
    }

    public static boolean canAvoidStarring(Item item) {
        return items.contains(item);
    }

    static {
        items = new HashSet<>();
    }
}
