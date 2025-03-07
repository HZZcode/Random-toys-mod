package random_toys.zz_404.mixin_utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class MixinSets {
    public static MixinSet<Block> EndCrystalPlacingBlocks = new MixinSet<>();
    public static MixinSet<Item> EndermanAvoidStarringItems = new MixinSet<>();
    public static MixinSet<Block> BeaconBlockSpecialCaseBlocks = new MixinSet<>();

    public static boolean isEndermanAvoidable(PlayerEntity player) {
        boolean inTrinkets = false;
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
                inTrinkets = slots.stream().anyMatch(slot
                        -> EndermanAvoidStarringItems.check(slot.getRight().getItem()));
			}
        }
		catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException
			   | IllegalAccessException ignored) {
			//Trinkets mod doesn't exist
        }
        return inTrinkets || EndermanAvoidStarringItems.check(player.getInventory().armor.get(3).getItem());
    }

    static {
        EndCrystalPlacingBlocks.add(Blocks.OBSIDIAN);
        EndCrystalPlacingBlocks.add(Blocks.BEDROCK);

        EndermanAvoidStarringItems.add(Items.CARVED_PUMPKIN);

        BeaconBlockSpecialCaseBlocks.add(Blocks.BEDROCK);
    }
}
