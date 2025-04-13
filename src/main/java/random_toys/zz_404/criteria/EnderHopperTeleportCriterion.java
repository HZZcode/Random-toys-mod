package random_toys.zz_404.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class EnderHopperTeleportCriterion extends AbstractCriterion<EnderHopperTeleportCriterion.Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return EnderHopperTeleportCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<EnderHopperTeleportCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EnderHopperTeleportCriterion.Conditions::player)
                        )
                        .apply(instance, EnderHopperTeleportCriterion.Conditions::new)
        );
    }
}
