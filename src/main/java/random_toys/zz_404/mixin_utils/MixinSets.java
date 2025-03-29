package random_toys.zz_404.mixin_utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import random_toys.zz_404.reflection_utils.TrinketUtils;

import java.util.function.Consumer;

public class MixinSets {
    public static MixinSet<Block> EndCrystalPlacingBlocks = new MixinSet<>();
    public static MixinSet<Item> EndermanAvoidStarringItems = new MixinSet<>();
    public static MixinSet<Block> BeaconBlockSpecialCaseBlocks = new MixinSet<>();
    public static MixinSet<FluidTransformationRule> FluidTransformationRules = new MixinSet<>();
    public static MixinSet<Consumer<PlayerEntity>> PlayerTickBehaviours = new MixinSet<>();

    public static boolean isEndermanAvoidable(PlayerEntity player) {
        return EndermanAvoidStarringItems.anyMatch(item -> TrinketUtils.isInTrinkets(player, item))
                || EndermanAvoidStarringItems.check(player.getInventory().armor.get(3).getItem());
    }

    static {
        EndCrystalPlacingBlocks.add(Blocks.OBSIDIAN);
        EndCrystalPlacingBlocks.add(Blocks.BEDROCK);

        EndermanAvoidStarringItems.add(Items.CARVED_PUMPKIN);

        BeaconBlockSpecialCaseBlocks.add(Blocks.BEDROCK);
    }
}
