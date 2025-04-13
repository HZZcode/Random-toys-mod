package random_toys.zz_404.criteria;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class BlockTransferringCriterion extends AbstractCriterion<BlockTransferringCriterion.Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return BlockTransferringCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<BlockTransferringCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(BlockTransferringCriterion.Conditions::player)
                        )
                        .apply(instance, BlockTransferringCriterion.Conditions::new)
        );
    }
}
