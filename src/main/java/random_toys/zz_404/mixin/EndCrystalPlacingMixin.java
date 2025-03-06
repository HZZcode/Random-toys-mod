package random_toys.zz_404.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.EndCrystalItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static random_toys.zz_404.mixin_utils.MixinSets.EndCrystalPlacingBlocks;

@Mixin(EndCrystalItem.class)
public class EndCrystalPlacingMixin {
    @Redirect(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 0
            ),
            require = 1
    )
    private boolean redirectFirstIsOf(BlockState instance, Block block) {
        return EndCrystalPlacingBlocks.check(block);
    }

    @Redirect(
            method = "useOnBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z",
                    ordinal = 1
            ),
            require = 1
    )
    private boolean redirectSecondIsOf(BlockState instance, Block block) {
        return true;
    }
}
