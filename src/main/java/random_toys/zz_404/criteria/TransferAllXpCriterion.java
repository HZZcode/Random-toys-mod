package random_toys.zz_404.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class TransferAllXpCriterion extends AbstractCriterion<TransferAllXpCriterion.Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return TransferAllXpCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<TransferAllXpCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(TransferAllXpCriterion.Conditions::player)
                        )
                        .apply(instance, TransferAllXpCriterion.Conditions::new)
        );
    }
}
