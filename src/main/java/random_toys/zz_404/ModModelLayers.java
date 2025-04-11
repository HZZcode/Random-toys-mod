package random_toys.zz_404;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer ZZ =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "zz"), "main");
    public static final EntityModelLayer MINER =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "miner"), "main");
    public static final EntityModelLayer GRAPPLING_HOOK =
            new EntityModelLayer(Identifier.of(RandomToys.MOD_ID, "grappling_hook"), "main");

    public static void registerModelLayers() {
        RandomToys.log("Registering Model Layers");
    }
}