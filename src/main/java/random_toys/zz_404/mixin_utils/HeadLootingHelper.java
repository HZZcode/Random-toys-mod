package random_toys.zz_404.mixin_utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;

import java.util.List;
import java.util.function.Supplier;

public class HeadLootingHelper {
    public static void dropHead(ServerWorld world, @NotNull DamageSource source, Entity entity, Supplier<ItemStack> supplier) {
        ItemStack stack = source.getWeaponStack();
        if (stack == null) return;
        List<RegistryEntry<Enchantment>> headLooting = stack.getEnchantments().getEnchantments().stream()
                .filter(enchantment
                        -> enchantment.matchesId(Identifier.of(RandomToys.MOD_ID, "head_looting"))).toList();
        if (!headLooting.isEmpty()) {
            int level = stack.getEnchantments().getLevel(headLooting.getFirst());
            ItemStack head = supplier.get();
            if (world.random.nextInt(level) != 0 && !head.isEmpty()) entity.dropStack(head);
        }
    }
}
