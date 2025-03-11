package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static final TagKey<Item> MINER_LOVED = registerItemTag("miner_loved");
    public static final TagKey<Block> MINER_ORES = registerBlockTag("miner_ores");
    public static final TagKey<Block> MINER_STONES = registerBlockTag("miner_stones");

    public static void registerItemTags() {
        RandomToys.log("Registering Tags");
    }

    private static TagKey<Item> registerItemTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(RandomToys.MOD_ID, name));
    }

    private static TagKey<Block> registerBlockTag(String name) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of(RandomToys.MOD_ID, name));
    }
}
