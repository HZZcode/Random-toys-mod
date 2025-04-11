package random_toys.zz_404;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final ComponentType<Integer> GAS_REMAINING = register("gas_remaining",
            (builder) -> builder.codec(Codecs.rangedInt(0, JetpackItem.getMaxGas()))
                    .packetCodec(PacketCodecs.VAR_INT));
    public static final ComponentType<String> HOOK_UUID = register("hook_uuid",
            (builder) -> builder.codec(Codec.STRING).packetCodec(PacketCodecs.STRING));

    private static <T> ComponentType<T> register(String id, @NotNull UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(RandomToys.MOD_ID, id), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponents() {
        RandomToys.log("Registering Data Components");
    }
}
