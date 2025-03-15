package random_toys.zz_404.mixin_utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import random_toys.zz_404.reflection_utils.TrinketUtils;

public class MixinSets {
    public static MixinSet<Block> EndCrystalPlacingBlocks = new MixinSet<>();
    public static MixinSet<Item> EndermanAvoidStarringItems = new MixinSet<>();
    public static MixinSet<Block> BeaconBlockSpecialCaseBlocks = new MixinSet<>();
    public static MixinSet<FluidTransformationRule> FluidTransformationRules = new MixinSet<>();

    public static boolean isEndermanAvoidable(PlayerEntity player) {
        return EndermanAvoidStarringItems.anyMatch(item -> TrinketUtils.isInTrinkets(player, item))
                || EndermanAvoidStarringItems.check(player.getInventory().armor.get(3).getItem());
    }

    static {
        EndCrystalPlacingBlocks.add(Blocks.OBSIDIAN);
        EndCrystalPlacingBlocks.add(Blocks.BEDROCK);

        EndermanAvoidStarringItems.add(Items.CARVED_PUMPKIN);

        BeaconBlockSpecialCaseBlocks.add(Blocks.BEDROCK);

        FluidTransformationRules.add(FluidTransformationRule.create()
                .fromFluid(fluid -> fluid.getDefaultState().isIn(FluidTags.LAVA))
                .toFluid(FluidState::isStill)
                .nearFluid(fluidState -> fluidState.isIn(FluidTags.WATER))
                .transformTo(Blocks.OBSIDIAN));
        FluidTransformationRules.add(FluidTransformationRule.create()
                .fromFluid(fluid -> fluid.getDefaultState().isIn(FluidTags.LAVA))
                .toFluid(fluidState -> !fluidState.isStill())
                .nearFluid(fluidState -> fluidState.isIn(FluidTags.WATER))
                .transformTo(Blocks.COBBLESTONE));
        FluidTransformationRules.add(FluidTransformationRule.create()
                .fromFluid(fluid -> fluid.getDefaultState().isIn(FluidTags.LAVA))
                .nearBlock(state -> state.isOf(Blocks.BLUE_ICE))
                .onBlock(state -> state.isOf(Blocks.SOUL_SOIL))
                .transformTo(Blocks.BASALT));
    }
}
