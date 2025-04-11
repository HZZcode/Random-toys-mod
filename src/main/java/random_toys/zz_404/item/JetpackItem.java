package random_toys.zz_404.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.reflection_utils.TrinketUtils;
import random_toys.zz_404.registry.ModArmorMaterials;
import random_toys.zz_404.registry.ModDataComponents;
import random_toys.zz_404.registry.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JetpackItem extends ArmorItem {
    public JetpackItem(Settings settings) {
        super(ModArmorMaterials.JETPACK, Type.CHESTPLATE, settings);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(@NotNull ItemStack stack) {
        return MathHelper.clamp(Math.round((float)getRemainingGas(stack)
                * 13.0F / (float)getMaxGas()), 0, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        final Vec3d min = new Vec3d(93, 143, 194);
        final Vec3d max = new Vec3d(212, 229, 247);
        double rate = (float)getRemainingGas(stack) / (float)getMaxGas();
        Vec3d result = min.multiply(1 - rate).add(max.multiply(rate));
        return ColorHelper.Argb.getArgb((int) result.x, (int) result.y, (int) result.z);
    }

    public static int getRemainingGas(@NotNull ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.GAS_REMAINING, 0);
    }

    public static int getMaxGas() {
        return 600;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, @NotNull List<Text> tooltip, TooltipType type) {
        int time = getRemainingGas(stack);
        int minutes = time / 60, seconds = time % 60;
        tooltip.add(Text.translatable("tooltip.random-toys.jetpacks",
                String.format("%02d", minutes), String.format("%02d", seconds)));
    }

    public static ArrayList<ItemStack> getRemainingWearingStacks(PlayerEntity player) {
        ArrayList<ItemStack> jetpacks = TrinketUtils.findInTrinkets(player, ModItems.JETPACKS);
        if (player.getInventory().armor.get(2).isOf(ModItems.JETPACKS))
            jetpacks.add(player.getInventory().armor.get(2));
        return jetpacks.stream().filter(stack -> getRemainingGas(stack) != 0)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}