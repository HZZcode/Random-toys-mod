package random_toys.zz_404.mixin_utils.mixin.head_lootings;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import random_toys.zz_404.mixin_utils.HeadLootingHelper;

@Mixin(CreeperEntity.class)
public class CreeperHeadLootingMixin {
    @Inject(at = @At("RETURN"), method = "dropEquipment")
    protected void dropEquipment(ServerWorld world, @NotNull DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        CreeperEntity creeper = (CreeperEntity) (Object) this;
        HeadLootingHelper.dropHead(world, source, creeper, () -> new ItemStack(Items.CREEPER_HEAD));
    }
}
