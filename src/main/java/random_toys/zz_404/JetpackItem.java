package random_toys.zz_404;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (getRemainingGas(stack) == 0 || player.isOnGround()) {
                player.getAbilities().flying = false;
                return;
            }
            if (world.getTime() % 20 == 0)
                stack.set(ModDataComponents.GAS_REMAINING, Math.max(0, getRemainingGas(stack) - 1));
            player.getAbilities().setFlySpeed(player.getMovementSpeed());
            player.getAbilities().flying = true;
        }
        //TODO: how to make it work in trinkets?
    }
}