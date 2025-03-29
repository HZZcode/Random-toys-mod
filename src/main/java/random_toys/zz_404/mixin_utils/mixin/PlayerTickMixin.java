package random_toys.zz_404.mixin_utils.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static random_toys.zz_404.mixin_utils.MixinSets.PlayerTickBehaviours;

@Mixin(PlayerEntity.class)
public class PlayerTickMixin {
    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        for (Consumer<PlayerEntity> consumer : PlayerTickBehaviours) consumer.accept(player);
    }
}
