package random_toys.zz_404.mixin_utils.mixin;

import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import random_toys.zz_404.mixin_utils.MixinSets;

@Mixin(EndermanEntity.class)
public class EndermanStarringMixin {
	@Inject(at = @At("HEAD"), method = "isPlayerStaring", cancellable = true)
	private void starring(@NotNull PlayerEntity player, @NotNull CallbackInfoReturnable<Boolean> cir) {
		if (MixinSets.isEndermanAvoidable(player)) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}