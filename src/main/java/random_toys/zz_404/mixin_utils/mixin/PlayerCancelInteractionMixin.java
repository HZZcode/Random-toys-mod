package random_toys.zz_404.mixin_utils.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static random_toys.zz_404.mixin_utils.MixinSets.AllowSneakBlockInteractionBlocks;

@Mixin(PlayerEntity.class)
public class PlayerCancelInteractionMixin {
    @Inject(at = @At("HEAD"), method = "shouldCancelInteraction", cancellable = true)
    private void shouldCancel(@NotNull CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        BlockPos pos = lookingPos(player, player.getWorld(), player.getBlockInteractionRange());
        Block block = player.getWorld().getBlockState(pos).getBlock();
        cir.setReturnValue(player.isSneaking() && !AllowSneakBlockInteractionBlocks.contains(block));
        cir.cancel();
    }

    @Unique
    private static BlockPos lookingPos(@NotNull PlayerEntity player, @NotNull World world, double reachDistance) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d end = start.add(player.getRotationVector().multiply(reachDistance));

        return world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE, player)).getBlockPos();
    }
}
