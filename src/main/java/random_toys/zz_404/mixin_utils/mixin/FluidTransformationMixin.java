package random_toys.zz_404.mixin_utils.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.mixin_utils.FluidTransformationRule;
import random_toys.zz_404.mixin_utils.MixinSets;

@Mixin(FluidBlock.class)
public abstract class FluidTransformationMixin {
    @Shadow @Final protected FlowableFluid fluid;

    @Shadow @Final public static ImmutableList<Direction> FLOW_DIRECTIONS;

    @Shadow protected abstract void playExtinguishSound(WorldAccess world, BlockPos pos);

    /**
     * @author ZZ_404
     * @reason Provide a public API for fluid transformation
     */
    @Overwrite
    private boolean receiveNeighborFluids(World world, BlockPos pos, BlockState state) {
        for (FluidTransformationRule rule : MixinSets.FluidTransformationRules) {
            if (rule.checkOriginalFluid(world.getFluidState(pos)) && rule.checkFluid(fluid)
                    && rule.checkBelowBlock(world.getBlockState(pos.down()))
                    && rule.checkAboveBlock(world.getBlockState(pos.up()))) {
                for (Direction direction : FLOW_DIRECTIONS) {
                    BlockPos blockPos = pos.offset(direction.getOpposite());
                    if (rule.checkNearFluid(world.getFluidState(blockPos))
                            || rule.checkNearBlock(world.getBlockState(blockPos))) {
                        world.setBlockState(pos, rule.transformBlock.getDefaultState());
                        playExtinguishSound(world, pos);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
