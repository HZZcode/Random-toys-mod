package random_toys.zz_404.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModArmorMaterials;
import random_toys.zz_404.registry.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlackBedrockArmorItem extends ArmorItem {
    private static final EquipmentSlot[] EquipmentSlots = {
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
    };

    private static final ArmorEffect[] effects = {
            new ArmorEffect(Type.HELMET, 50, StatusEffects.NIGHT_VISION, 0),
            new ArmorEffect(Type.CHESTPLATE, 200, StatusEffects.FIRE_RESISTANCE, 3),
            new ArmorEffect(Type.LEGGINGS, 200, StatusEffects.JUMP_BOOST, 3),
            new ArmorEffect(Type.BOOTS, 200, StatusEffects.SPEED, 3)
    };

    public BlackBedrockArmorItem(Type type) {
        super(ModArmorMaterials.BLACK_BEDROCK, type, new Item.Settings().maxCount(1)
                .fireproof().maxDamage(type.getMaxDamage(96)));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof ServerPlayerEntity player)) return;
        ArrayList<Type> slots = getWearings(player);
        for (ArmorEffect effect : effects) effect.apply(world, player, slots);
        if (slots.size() >= 4 && world.getTime() % 60 == 0
                && player.getHealth() / player.getMaxHealth() < world.random.nextFloat()) {
            player.heal(1.5f + 2 * world.random.nextFloat());
            int t = world.random.nextInt(4);
            for (int i = 0; i <= t; i++) {
                int j = world.random.nextInt(4);
                player.getInventory().getArmorStack(j).damage(1, player, EquipmentSlots[j]);
            }
        }
        player.removeStatusEffect(StatusEffects.DARKNESS);

        if (world.getTime() % 40 == 0) {
            Optional<EnchantmentEffectContext> optional = EnchantmentHelper
                    .chooseEquipmentWith(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, player,
                            itemStack -> itemStack.getItem() instanceof BlackBedrockArmorItem
                                    && (double) itemStack.getDamage() / itemStack.getMaxDamage() > 0.7);
            if (optional.isPresent()) {
                ItemStack itemStack = optional.get().stack();
                int i = EnchantmentHelper.getRepairWithXp(player.getServerWorld(),
                        itemStack, player.totalExperience);
                int j = Math.min(i, itemStack.getDamage());
                itemStack.setDamage(itemStack.getDamage() - j);
                player.addExperience(-j);
            }
        }
    }

    private static @NotNull ArrayList<Type> getWearings(@NotNull PlayerEntity player) {
        ArrayList<Type> ans = new ArrayList<>();
        if (player.getInventory().getArmorStack(3)
                .isOf(ModItems.BLACK_BEDROCK_HELMET)) ans.add(Type.HELMET);
        if (player.getInventory().getArmorStack(2)
                .isOf(ModItems.BLACK_BEDROCK_CHESTPLATE)) ans.add(Type.CHESTPLATE);
        if (player.getInventory().getArmorStack(1)
                .isOf(ModItems.BLACK_BEDROCK_LEGGINGS)) ans.add(Type.LEGGINGS);
        if (player.getInventory().getArmorStack(0)
                .isOf(ModItems.BLACK_BEDROCK_BOOTS)) ans.add(Type.BOOTS);
        return ans;
    }

    public static boolean isWearingAll(@NotNull PlayerEntity player) {
        return getWearings(player).size() >= 4;
    }

    private record ArmorEffect(Type type, int time, RegistryEntry<StatusEffect> effect, int amplifier) {
        public void apply(@NotNull World world, PlayerEntity player, @NotNull List<Type> slots) {
            if (slots.contains(type) && world.getTime() % time == 0)
                player.addStatusEffect(new StatusEffectInstance(effect,
                        300, amplifier, true, true));
        }
    }
}