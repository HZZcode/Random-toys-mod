package random_toys.zz_404.mixin_utils;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.reflection_utils.SpikySpikesEnchantmentsUtils;

import java.util.List;
import java.util.function.Supplier;

public class HeadLootingHelper {
    public static void dropHead(ServerWorld world, @NotNull DamageSource source, Entity entity, Supplier<ItemStack> supplier) {
        ItemEnchantmentsComponent enchantments = getEnchantments(source);
        if (enchantments == null) return;
        List<RegistryEntry<Enchantment>> headLooting = enchantments.getEnchantments().stream()
                .filter(enchantment
                        -> enchantment.matchesId(Identifier.of(RandomToys.MOD_ID, "head_looting"))).toList();
        if (!headLooting.isEmpty()) {
            int level = enchantments.getLevel(headLooting.getFirst());
            ItemStack head = supplier.get();
            if (world.random.nextInt(level) != 0 && !head.isEmpty()) entity.dropStack(head);
        }
    }

    private static @Nullable ItemEnchantmentsComponent getEnchantments(@NotNull DamageSource source) {
        ItemEnchantmentsComponent spikeEnchantments = SpikySpikesEnchantmentsUtils.getSpikeEnchantments(source);
        if (spikeEnchantments != null) return spikeEnchantments;
        ItemStack stack = source.getWeaponStack();
        if (stack == null) return null;
        return stack.getEnchantments();
    }
}
