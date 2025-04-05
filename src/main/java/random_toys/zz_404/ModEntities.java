package random_toys.zz_404;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ModEntities {
    public static EntityType<ZZEntity> ZZ;
    public static EntityType<ThrownBlackstoneEntity> THROWN_BLACKSTONE;
    public static EntityType<ThrownGildedBlackstoneEntity> THROWN_GILDED_BLACKSTONE;
    public static EntityType<ThrownEnchantedGildedBlackstoneEntity> THROWN_ENCHANTED_GILDED_BLACKSTONE;
    public static EntityType<MinerEntity> MINER;
    public static EntityType<ThrownBlackBedrockEntity> THROWN_BLACK_BEDROCK;

    public static <T extends Entity> EntityType<T> registerEntities(String id, EntityType.@NotNull Builder<T> entityTypeBuilder) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(RandomToys.MOD_ID, id), entityTypeBuilder.build(id));
    }

    public static void registerEntities() {
        ZZ = registerEntities("zz",
                EntityType.Builder.create(ZZEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 2.9f)
                    .maxTrackingRange(32));
        FabricDefaultAttributeRegistry.register(ZZ, ZZEntity.createZZAttributes());
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.ZZ, ZZModel::getTexturedModelData);
        EntityRendererRegistry.register(ZZ, ZZRenderer::new);

        THROWN_BLACKSTONE = registerEntities("thrown_blackstone",
                EntityType.Builder.create(ThrownBlackstoneEntity::new, SpawnGroup.MISC)
                        .dimensions(0.7f, 0.7f)
        );
        EntityRendererRegistry.register(THROWN_BLACKSTONE, ThrownBlackstonesRenderer::new);

        THROWN_GILDED_BLACKSTONE = registerEntities("thrown_gilded_blackstone",
                EntityType.Builder.create(ThrownGildedBlackstoneEntity::new, SpawnGroup.MISC)
                        .dimensions(0.7f, 0.7f)
                );
        EntityRendererRegistry.register(THROWN_GILDED_BLACKSTONE, ThrownBlackstonesRenderer::new);

        THROWN_ENCHANTED_GILDED_BLACKSTONE = registerEntities("thrown_enchanted_gilded_blackstone",
                EntityType.Builder.create(ThrownEnchantedGildedBlackstoneEntity::new, SpawnGroup.MISC)
                        .dimensions(0.7f, 0.7f)
                );
        EntityRendererRegistry.register(THROWN_ENCHANTED_GILDED_BLACKSTONE, ThrownBlackstonesRenderer::new);

        MINER = registerEntities("miner",
                EntityType.Builder.create(MinerEntity::new, SpawnGroup.AMBIENT)
                        .dimensions(0.6f, 0.8f));
        FabricDefaultAttributeRegistry.register(MINER, MinerEntity.createMinerAttributes());
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MINER, MinerModel::getTexturedModelData);
        EntityRendererRegistry.register(MINER, MinerRenderer::new);

        THROWN_BLACK_BEDROCK = registerEntities("thrown_black_bedrock",
                EntityType.Builder.create(ThrownBlackBedrockEntity::new, SpawnGroup.MISC)
                        .dimensions(0.7f, 0.7f)
        );
        EntityRendererRegistry.register(THROWN_BLACK_BEDROCK, ThrownBlackstonesRenderer::new);

        RandomToys.log("Registering Entities");
    }
}
