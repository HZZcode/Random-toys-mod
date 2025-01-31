package random_toys.zz_404;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;

public class ModBlocks {
    public static final Block BUD = register("bud", new BUDBlock(AbstractBlock.Settings.copy(Blocks.OBSERVER)));
    public static final Block RANDOMIZER = registerRandomizer("randomizer_block", new RandomizerBlock(AbstractBlock.Settings.copy(Blocks.CHEST), () -> ModBlockEntities.RANDOMIZER));
    public static final Block APPLE_LEAVES = register("apple_leaves", new AppleLeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block COPPERED_REDSTONE_WIRE = registerCopperedRedstone("coppered_redstone_wire", "coppered_redstone_dust", new CopperedRedstoneWireBlock(AbstractBlock.Settings.copy(Blocks.REDSTONE_WIRE)));
    public static final Block COPPERED_REDSTONE_BLOCK = registerCopperedRedstone("coppered_redstone_block", new CopperedRedstoneBlock(AbstractBlock.Settings.copy(Blocks.REDSTONE_BLOCK)));
    public static final Block DISPOSABLE_SPAWNER = register("disposable_spawner", new DisposableSpawnerBlock(AbstractBlock.Settings.copy(Blocks.SPAWNER).strength(-1.0F, 3600000.0F).dropsNothing()));
    public static final Block BLACKSTONE_PROCESSING_TABLE = register("blackstone_processing_table", new BlackstoneProcessingTableBlock(AbstractBlock.Settings.copy(Blocks.BLACKSTONE)));

    public static Block register(String id, Block block) {
        return register(id, id, block);
    }
    public static Block register(String id, String item_id, Block block) {
        registerBlockItems(item_id, block);
        return Registry.register(Registries.BLOCK, Identifier.of(RandomToys.MOD_ID, id), block);
    }
    public static Block register(String id, Block block, BlockItem blockItem) {
        return register(id, id, block, blockItem);
    }
    public static Block register(String id, String item_id, Block block, BlockItem blockItem) {
        registerBlockItems(item_id, blockItem);
        return Registry.register(Registries.BLOCK, Identifier.of(RandomToys.MOD_ID, id), block);
    }
    public static void registerBlockItems(String id, Block block) {
       registerBlockItems(id, new BlockItem(block, new Item.Settings()));
    }
    public static void registerBlockItems(String id, BlockItem blockItem) {
        BlockItem item = Registry.register(Registries.ITEM, Identifier.of(RandomToys.MOD_ID, id), blockItem);
        item.appendBlocks(Item.BLOCK_ITEMS, item);
    }

    public static Block registerRandomizer(String id, Block block) {
        return register(id, block, new RandomizerBlockItem(block, new Item.Settings()));
    }
    public static Block registerCopperedRedstone(String id, Block block) {
        return register(id, block, new CopperedRedstoneItem(block, new Item.Settings()));
    }
    public static Block registerCopperedRedstone(String id, String item_id, Block block) {
        return register(id, item_id, block, new CopperedRedstoneItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COPPERED_REDSTONE_WIRE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DISPOSABLE_SPAWNER, RenderLayer.getCutout());

        RandomToys.LOGGER.info("Registering Blocks");
    }
}