package random_toys.zz_404;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrinketUtils {
    public static boolean isInTrinkets(PlayerEntity player, Item item) {
        return !findInTrinkets(player, item).isEmpty();
    }

    public static ArrayList<ItemStack> findInTrinkets(PlayerEntity player, Item item) {
        try {
            Class<?> trinketsApiClass = Class.forName("dev.emi.trinkets.api.TrinketsApi");
            Method getTrinketComponent = trinketsApiClass.getMethod("getTrinketComponent", LivingEntity.class);
            Optional<?> optional = (Optional<?>) getTrinketComponent.invoke(trinketsApiClass, player);
            if (optional.isPresent()) {
                Class<?> trinketComponentClass = Class.forName("dev.emi.trinkets.api.TrinketComponent");
                var components = optional.get();
                Method getAllEquipped = trinketComponentClass.getMethod("getAllEquipped");
                @SuppressWarnings("unchecked") //Of course this is fine
                List<Pair<?, ItemStack>> slots = (List<Pair<?, ItemStack>>) getAllEquipped.invoke(components);
                return slots.stream().filter(slot -> slot.getRight().getItem() == item)
                        .map(Pair::getRight).collect(Collectors.toCollection(ArrayList::new));
            }
        }
        catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException
               | IllegalAccessException ignored) {
            //Trinkets mod doesn't exist
        }
        return new ArrayList<>();
    }
}
