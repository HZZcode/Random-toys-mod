package random_toys.zz_404;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final SimpleParticleType GREEN_ZZ = register("green_zz", FabricParticleTypes.simple());
    public static final SimpleParticleType RED_ZZ = register("red_zz", FabricParticleTypes.simple());
    public static final SimpleParticleType YELLOW_ZZ = register("yellow_zz", FabricParticleTypes.simple());
    public static final SimpleParticleType WHITE_ZZ = register("white_zz", FabricParticleTypes.simple());

    private static SimpleParticleType register(String name, SimpleParticleType type) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(RandomToys.MOD_ID, name), type);
    }

    public static void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.GREEN_ZZ, EndRodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.RED_ZZ, EndRodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.YELLOW_ZZ, EndRodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.WHITE_ZZ, EndRodParticle.Factory::new);

        RandomToys.log("Registering Particles");
    }
}
