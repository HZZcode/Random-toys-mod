package random_toys.zz_404.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random_toys.zz_404.JetpackItem;
import random_toys.zz_404.ModDataComponents;
import random_toys.zz_404.ModKeyBindings;

import java.util.ArrayList;

@Mixin(PlayerEntity.class)
public class PlayerTickMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!player.isSpectator() && !player.isCreative()) {
            ArrayList<ItemStack> stacks = JetpackItem.getRemainingWearingStacks(player);
            if (!stacks.isEmpty()) {
                ItemStack stack = stacks.getFirst();
                if (JetpackItem.getRemainingGas(stack) == 0 || player.isOnGround()
                        || !ModKeyBindings.JETPACK_ACTIVATE.isPressed()) {
                    player.getAbilities().flying = false;
                    return;
                }
                if (player.getWorld().getTime() % 20 == 0)
                    stack.set(ModDataComponents.GAS_REMAINING, Math.max(0, JetpackItem.getRemainingGas(stack) - 1));
                if (player.getAbilities().flying) return;
                player.getAbilities().setFlySpeed(player.getMovementSpeed());
                player.getAbilities().flying = true;
            } else player.getAbilities().flying = false;
        }
    }
}
