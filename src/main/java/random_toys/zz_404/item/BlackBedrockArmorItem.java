package random_toys.zz_404.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModArmorMaterials;
import random_toys.zz_404.registry.ModItems;

import java.util.ArrayList;

public class BlackBedrockArmorItem extends ArmorItem {
    private static final EquipmentSlot[] EquipmentSlots = {
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD
    };

    public BlackBedrockArmorItem(Type type) {
        super(ModArmorMaterials.BLACK_BEDROCK, type, new Item.Settings().maxCount(1)
                .fireproof().maxDamage(type.getMaxDamage(96)));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            ArrayList<Type> slots = getWearings(player);
            if (slots.contains(Type.HELMET) && world.getTime() % 50 == 0)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION,
                        300, 0, true, true));
            if (slots.contains(Type.CHESTPLATE) && world.getTime() % 200 == 0)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,
                        300, 3, true, true));
            if (slots.contains(Type.LEGGINGS) && world.getTime() % 200 == 0)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST,
                        300, 3, true, true));
            if (slots.contains(Type.BOOTS) && world.getTime() % 200 == 0)
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED,
                        300, 3, true, true));
            if (slots.size() >= 4 && world.getTime() % 60 == 0)
                if (world.random.nextFloat() * player.getHealth() / player.getMaxHealth() <= 0.5) {
                    player.heal(1.5f + 2 * world.random.nextFloat());
                    int t = world.random.nextInt(4);
                    for (int i = 0; i <= t; i++) {
                        int j = world.random.nextInt(4);
                        player.getInventory().getArmorStack(j).damage(1, player, EquipmentSlots[j]);
                    }
                }
            player.removeStatusEffect(StatusEffects.DARKNESS);
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
}