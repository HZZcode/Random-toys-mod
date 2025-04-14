package random_toys.zz_404.registry;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import random_toys.zz_404.RandomToys;

public class ModModelLayers {
    public static final EntityModelLayer ZZ =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "zz"), "main");
    public static final EntityModelLayer MINER =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "miner"), "main");
    public static final EntityModelLayer GRAPPLING_HOOK =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "grappling_hook"), "main");
    public static final EntityModelLayer BEDROCK_DEMON =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "bedrock_demon"), "main");

    public static void registerModelLayers() {
        RandomToys.log("Registering Model Layers");
    }
}