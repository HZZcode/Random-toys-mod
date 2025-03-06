package random_toys.zz_404.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static random_toys.zz_404.mixin_utils.MixinSets.BeaconBlockSpecialCaseBlocks;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockSpecialCaseMixin {
    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 0
            ),
            require = 1
    )
    private static boolean isNotBlocking(BlockState instance, Block block) {
        return BeaconBlockSpecialCaseBlocks.check(block);
    }
}
