package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashSet;

public class EndCrystalPlacingBlocks {
    private static final HashSet<Block> blocks;

    public static void addPlacingBlocks(Block block) {
        blocks.add(block);
    }

    public static boolean canPlaceOn(Block block) {
        return blocks.contains(block);
    }

    static {
        blocks = new HashSet<>();
        blocks.add(Blocks.OBSIDIAN);
        blocks.add(Blocks.BEDROCK);
    }
}
