package random_toys.zz_404.mixin_utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class MixinSets {
    public static MixinSet<Block> EndCrystalPlacingBlocks = new MixinSet<>();
    public static MixinSet<Item> EndermanAvoidStarringItems = new MixinSet<>();
    public static MixinSet<Block> BeaconBlockSpecialCaseBlocks = new MixinSet<>();

    static {
        EndCrystalPlacingBlocks.add(Blocks.OBSIDIAN);
        EndCrystalPlacingBlocks.add(Blocks.BEDROCK);

        EndermanAvoidStarringItems.add(Items.CARVED_PUMPKIN);

        BeaconBlockSpecialCaseBlocks.add(Blocks.BEDROCK);
    }
}
