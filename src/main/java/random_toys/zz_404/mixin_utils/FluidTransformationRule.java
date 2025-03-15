package random_toys.zz_404.mixin_utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class FluidTransformationRule {
    private Predicate<FlowableFluid> fluidPredicate = falsePredicate();
    private Predicate<FluidState> originalFluidPredicate = truePredicate();
    private Predicate<BlockState> belowBlockPredicate = truePredicate();
    private Predicate<BlockState> aboveBlockPredicate = truePredicate();
    private Predicate<BlockState> nearBlockPredicate = falsePredicate();
    private Predicate<FluidState> nearFluidPredicate = falsePredicate();
    public Block transformBlock = Blocks.AIR;

    @Contract(pure = true)
    private static <T> @NotNull Predicate<T> truePredicate() {
        return t -> true;
    }

    @Contract(pure = true)
    private static <T> @NotNull Predicate<T> falsePredicate() {
        return t -> false;
    }

    @Contract(" -> new")
    public static @NotNull FluidTransformationRule create() {
        return new FluidTransformationRule();
    }

    public FluidTransformationRule fromFluid(Predicate<FlowableFluid> predicate) {
        fluidPredicate = predicate;
        return this;
    }

    public FluidTransformationRule toFluid(Predicate<FluidState> predicate) {
        originalFluidPredicate = predicate;
        return this;
    }

    public FluidTransformationRule onBlock(Predicate<BlockState> predicate) {
        belowBlockPredicate = predicate;
        return this;
    }

    public FluidTransformationRule underBlock(Predicate<BlockState> predicate) {
        aboveBlockPredicate = predicate;
        return this;
    }

    public FluidTransformationRule nearBlock(Predicate<BlockState> predicate) {
        nearBlockPredicate = predicate;
        return this;
    }

    public FluidTransformationRule nearFluid(Predicate<FluidState> predicate) {
        nearFluidPredicate = predicate;
        return this;
    }

    public FluidTransformationRule transformTo(Block block) {
        transformBlock = block;
        return this;
    }

    public boolean checkFluid(FlowableFluid fluid) {
        return fluidPredicate.test(fluid);
    }

    public boolean checkOriginalFluid(FluidState state) {
        return originalFluidPredicate.test(state);
    }

    public boolean checkBelowBlock(BlockState state) {
        return belowBlockPredicate.test(state);
    }

    public boolean checkAboveBlock(BlockState state) {
        return aboveBlockPredicate.test(state);
    }

    public boolean checkNearBlock(BlockState state) {
        return nearBlockPredicate.test(state);
    }

    public boolean checkNearFluid(FluidState state) {
        return nearFluidPredicate.test(state);
    }
}
