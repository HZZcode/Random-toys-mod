package random_toys.zz_404.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import random_toys.zz_404.misc.BlackEndIslandFeature;
import random_toys.zz_404.RandomToys;

public class ModFeatures {
    public static final Feature<DefaultFeatureConfig> END_ISLAND = register("black_end_island", new BlackEndIslandFeature(DefaultFeatureConfig.CODEC));

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(Registries.FEATURE, Identifier.of(RandomToys.MOD_ID, name), feature);
    }

    public static void registerFeatures() {
        RandomToys.log("Registering Features");
    }
}
