package random_toys.zz_404;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ModBlocks {
    public static final Block BUD = register("bud", new BUDBlock(AbstractBlock.Settings.copy(Blocks.OBSERVER)));
    public static final Block RANDOMIZER = register("randomizer_block", new RandomizerBlock(AbstractBlock.Settings.copy(Blocks.CHEST), () -> ModBlockEntities.RANDOMIZER), RandomizerBlockItem.class);
    public static final Block APPLE_LEAVES = register("apple_leaves", new AppleLeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block COPPERED_REDSTONE_WIRE = register("coppered_redstone_wire", "coppered_redstone_dust", new CopperedRedstoneWireBlock(AbstractBlock.Settings.copy(Blocks.REDSTONE_WIRE)), CopperedRedstoneItem.class);
    public static final Block COPPERED_REDSTONE_BLOCK = register("coppered_redstone_block", new CopperedRedstoneBlock(AbstractBlock.Settings.copy(Blocks.REDSTONE_BLOCK)), CopperedRedstoneItem.class);
    public static final Block DISPOSABLE_SPAWNER = register("disposable_spawner", new DisposableSpawnerBlock(AbstractBlock.Settings.copy(Blocks.SPAWNER).strength(-1.0F, 3600000.0F).dropsNothing()));
    public static final Block BLACKSTONE_PROCESSING_TABLE = register("blackstone_processing_table", new BlackstoneProcessingTableBlock(AbstractBlock.Settings.copy(Blocks.BLACKSTONE)));
    public static final Block BLACK_BEDROCK = register("black_bedrock", new Block(AbstractBlock.Settings.copy(Blocks.BEDROCK).mapColor(MapColor.BLACK)));
    public static final Block COMPRESSOR = register("compressor", new CompressorBlock(AbstractBlock.Settings.copy(Blocks.CHEST), () -> ModBlockEntities.COMPRESSOR));
    public static final Block EXPERIENCE_COLLECTOR = register("experience_collector", new ExperienceCollectorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block TRANSFER = register("transfer", new TransferringBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));

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

    public static Block register(String id, Block block, @NotNull Class<? extends BlockItem> itemClass) {
        return register(id, id, block, itemClass);
    }
    public static Block register(String id, String item_id, Block block, @NotNull Class<? extends BlockItem> itemClass) {
        try {
            Constructor<? extends BlockItem> constructor
                    = itemClass.getConstructor(Block.class, Item.Settings.class);
            BlockItem item = constructor.newInstance(block, new Item.Settings());
            return register(id, item_id, block, item);
        } catch (NoSuchMethodException | IllegalAccessException
                 | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerBlocks() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COPPERED_REDSTONE_WIRE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DISPOSABLE_SPAWNER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EXPERIENCE_COLLECTOR, RenderLayer.getCutout());

        RandomToys.log("Registering Blocks");
    }
}