package random_toys.zz_404.mixin_utils.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import random_toys.zz_404.item.BlackBedrockArmorItem;
import random_toys.zz_404.entity.ZZEntity;

@Mixin(PlayerEntity.class)
public class PlayerDamageMixin {
    @Inject(method = "damage", at = @At(value = "RETURN", ordinal = 4, shift = At.Shift.BEFORE), cancellable = true)
    private void beforeDamage(@NotNull DamageSource source, float amount, @NotNull CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        PlayerEntity player = (PlayerEntity) entity;
        Entity attacker = source.getSource();
        if (BlackBedrockArmorItem.isWearingAll(player)
                && (player.equals(attacker)
                || (attacker instanceof ProjectileEntity projectile
                && (player.equals(projectile.getOwner()))))) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (BlackBedrockArmorItem.isWearingAll(player)
                && (attacker instanceof ZZEntity
                || attacker instanceof WardenEntity
                || (attacker instanceof ProjectileEntity projectile
                && (projectile.getOwner() instanceof ZZEntity)))) {
            amount /= player.getWorld().random.nextBetween(4, 10);
            cir.setReturnValue(amount != 0.0F && entity.damage(source, amount));
            cir.cancel();
        }
    }
}
