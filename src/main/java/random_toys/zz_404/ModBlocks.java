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
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;
import random_toys.zz_404.mixin_utils.FluidTransformationRule;

import static random_toys.zz_404.mixin_utils.MixinSets.*;

public class ModBlocks {
    public static final Block BUD = register("bud", new BUDBlock(AbstractBlock.Settings.copy(Blocks.OBSERVER)));
    public static final Block RANDOMIZER = register("randomizer_block", new RandomizerBlock(AbstractBlock.Settings.copy(Blocks.CHEST), () -> ModBlockEntities.RANDOMIZER));
    public static final Block APPLE_LEAVES = register("apple_leaves", new AppleLeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block COPPERED_REDSTONE_WIRE = register("coppered_redstone_wire", "coppered_redstone_dust", new CopperedRedstoneWireBlock(AbstractBlock.Settings.copy(Blocks.REDSTONE_WIRE)));
    public static final Block COPPERED_REDSTONE_BLOCK = register("coppered_redstone_block", new CopperedRedstoneBlock(AbstractBlock.Settings.copy(Blocks.REDSTONE_BLOCK)));
    public static final Block DISPOSABLE_SPAWNER = register("disposable_spawner", new DisposableSpawnerBlock(AbstractBlock.Settings.copy(Blocks.SPAWNER).strength(-1.0F, 3600000.0F).dropsNothing()));
    public static final Block BLACKSTONE_PROCESSING_TABLE = register("blackstone_processing_table", new BlackstoneProcessingTableBlock(AbstractBlock.Settings.copy(Blocks.BLACKSTONE)));
    public static final Block BLACK_BEDROCK = register("black_bedrock", new Block(AbstractBlock.Settings.copy(Blocks.BEDROCK).mapColor(MapColor.BLACK)));
    public static final Block COMPRESSOR = register("compressor", new CompressorBlock(AbstractBlock.Settings.copy(Blocks.CHEST), () -> ModBlockEntities.COMPRESSOR));
    public static final Block EXPERIENCE_COLLECTOR = register("experience_collector", new ExperienceCollectorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block TRANSFER = register("transfer", new TransferringBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
    public static final Block DISENCHANTMENTOR = register("disenchantmentor", new DisenchantmentBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK), () -> ModBlockEntities.DISENCHANTMENTOR));
    public static final Block ENDER_LINKER = register("ender_linker", new EnderLinkerBlock(AbstractBlock.Settings.copy(Blocks.END_STONE), () -> ModBlockEntities.ENDER_LINKER));
    public static final Block OXIDIZER = register("oxidizer", new OxidizerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK), () -> ModBlockEntities.OXIDIZER));
    public static final Block VANISHING_DOOR = register("vanishing_door", new VanishingDoorBlock(AbstractBlock.Settings.copy(Blocks.GLASS).strength(50.0F, 1200.0F)));
    public static final Block IMITATOR = register("imitator", new ImitatorBlock(AbstractBlock.Settings.copy(Blocks.GLASS).strength(50.0F, 1200.0F)));
    public static final Block MAZE_CORE = register("maze_core", new MazeCoreBlock(AbstractBlock.Settings.copy(Blocks.GLASS).strength(50.0F, 1200.0F)));
    public static final Block BELT = register("belt", new BeltBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
    public static final Block SOLID_LAVA = register("solid_lava", new SolidLavaBlock(AbstractBlock.Settings.copy(Blocks.MAGMA_BLOCK).luminance(state -> 7).strength(0.5F).allowsSpawning((state, world, pos, type) -> type.isFireImmune())));
    public static final Block DESTROYER = register("destroyer", new DestroyerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK), () -> ModBlockEntities.DESTROYER));
    public static final Block BLACK_BEDROCK_PROCESSING_TABLE = register("black_bedrock_processing_table", new BlackBedrockProcessingTableBlock(AbstractBlock.Settings.copy(Blocks.OBSIDIAN)));
    public static final Block TIMER = register("timer", new TimerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK), () -> ModBlockEntities.TIMER));
    public static final Block ENDER_HOPPER = register("ender_hopper", new EnderHopperBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)));
    public static final Block CHUNK_DESTROYER = register("chunk_destroyer", new ChunkDestroyerBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK), () -> ModBlockEntities.CHUNK_DESTROYER));

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

    public static void registerBlocks() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COPPERED_REDSTONE_WIRE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DISPOSABLE_SPAWNER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EXPERIENCE_COLLECTOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.VANISHING_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IMITATOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BELT, RenderLayer.getCutout());

        EndCrystalPlacingBlocks.add(BLACK_BEDROCK);
        BeaconBlockSpecialCaseBlocks.add(BLACK_BEDROCK);
        FluidTransformationRules.add(FluidTransformationRule.create()
                .fromFluid(fluid -> fluid.getDefaultState().isIn(FluidTags.LAVA))
                .nearBlock(state -> state.isOf(Blocks.POWDER_SNOW))
                .underBlock(state -> state.isOf(Blocks.BLUE_ICE))
                .transformTo(SOLID_LAVA));

        RandomToys.log("Registering Blocks");
    }
}