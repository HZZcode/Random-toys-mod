package random_toys.zz_404;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

public class ModBlockEntities {
    public static final BlockEntityType<RandomizerBlockEntity> RANDOMIZER = create("randomizer", BlockEntityType.Builder.create(RandomizerBlockEntity::new, ModBlocks.RANDOMIZER));
    public static final BlockEntityType<DisposableSpawnerBlockEntity> DISPOSABLE_SPAWNER = create("disposable_spawner", BlockEntityType.Builder.create(DisposableSpawnerBlockEntity::new, ModBlocks.DISPOSABLE_SPAWNER));
    public static final BlockEntityType<BlackstoneProcessingTableBlockEntity> BLACKSTONE_PROCESSING_TABLE = create("blackstone_processing_table", BlockEntityType.Builder.create(BlackstoneProcessingTableBlockEntity::new, ModBlocks.BLACKSTONE_PROCESSING_TABLE));
    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = create("compressor", BlockEntityType.Builder.create(CompressorBlockEntity::new, ModBlocks.COMPRESSOR));
    public static final BlockEntityType<ExperienceCollectorBlockEntity> EXPERIENCE_COLLECTOR = create("experience_collector", BlockEntityType.Builder.create(ExperienceCollectorBlockEntity::new, ModBlocks.EXPERIENCE_COLLECTOR));
    public static final BlockEntityType<TransferringBlockEntity> TRANSFER = create("transfer", BlockEntityType.Builder.create(TransferringBlockEntity::new, ModBlocks.TRANSFER));
    public static final BlockEntityType<DisenchantmentBlockEntity> DISENCHANTMENTOR = create("disenchantmentor", BlockEntityType.Builder.create(DisenchantmentBlockEntity::new, ModBlocks.DISENCHANTMENTOR));
    public static final BlockEntityType<EnderLinkerBlockEntity> ENDER_LINKER = create("ender_linker", BlockEntityType.Builder.create(EnderLinkerBlockEntity::new, ModBlocks.ENDER_LINKER));
    public static final BlockEntityType<OxidizerBlockEntity> OXIDIZER = create("oxidizer", BlockEntityType.Builder.create(OxidizerBlockEntity::new, ModBlocks.OXIDIZER));
    public static final BlockEntityType<VanishingDoorBlockEntity> VANISHING_DOOR = create("vanishing_door", BlockEntityType.Builder.create(VanishingDoorBlockEntity::new, ModBlocks.VANISHING_DOOR));
    public static final BlockEntityType<ImitatorBlockEntity> IMITATOR = create("imitator", BlockEntityType.Builder.create(ImitatorBlockEntity::new, ModBlocks.IMITATOR));
    public static final BlockEntityType<MazeCoreBlockEntity> MAZE_CORE = create("maze_core", BlockEntityType.Builder.create(MazeCoreBlockEntity::new, ModBlocks.MAZE_CORE));
    public static final BlockEntityType<BeltBlockEntity> BELT = create("belt", BlockEntityType.Builder.create(BeltBlockEntity::new, ModBlocks.BELT));
    public static final BlockEntityType<DestroyerBlockEntity> DESTROYER = create("destroyer", BlockEntityType.Builder.create(DestroyerBlockEntity::new, ModBlocks.DESTROYER));
    public static final BlockEntityType<BlackBedrockProcessingTableBlockEntity> BLACK_BEDROCK_PROCESSING_TABLE = create("black_bedrock_processing_table", BlockEntityType.Builder.create(BlackBedrockProcessingTableBlockEntity::new, ModBlocks.BLACK_BEDROCK_PROCESSING_TABLE));
    public static final BlockEntityType<TimerBlockEntity> TIMER = create("timer", BlockEntityType.Builder.create(TimerBlockEntity::new, ModBlocks.TIMER));
    public static final BlockEntityType<EnderHopperBlockEntity> ENDER_HOPPER = create("ender_hopper", BlockEntityType.Builder.create(EnderHopperBlockEntity::new, ModBlocks.ENDER_HOPPER));
    public static final BlockEntityType<ChunkDestroyerBlockEntity> CHUNK_DESTROYER = create("chunk_destroyer", BlockEntityType.Builder.create(ChunkDestroyerBlockEntity::new, ModBlocks.CHUNK_DESTROYER));

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.@NotNull Builder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(RandomToys.MOD_ID, id), builder.build(type));
    }

    public static void registerBlockEntities() {
        RandomToys.log("Registering Block Entities");

        BlockEntityRendererFactories.register(TRANSFER, TransferringBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(IMITATOR, ImitatorBlockEntityRenderer::new);
    }
}