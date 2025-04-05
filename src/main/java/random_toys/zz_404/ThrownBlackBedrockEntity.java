package random_toys.zz_404;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ThrownBlackBedrockEntity extends AbstractThrownBlackstoneEntity {
    public ThrownBlackBedrockEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModBlocks.BLACK_BEDROCK.asItem();
    }

    @Override
    protected void hitEntityUnchecked(@NotNull Entity entity, float distance) {
        Entity owner = getOwner();
        DamageSource damageSource = getDamageSources().indirectMagic(owner, owner);
        entity.damage(damageSource, 20.0f / (distance + 1));
        if (entity instanceof LivingEntity livingEntity
                && !(entity instanceof PlayerEntity player && BlackBedrockArmorItem.isWearingAll(player))) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,
                    (int) (200 / (distance + 1)), 2));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS,
                    (int) (200 / (distance + 1)), 0));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER,
                    (int) (200 / (distance + 1)), 0));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,
                    (int) (200 / (distance + 1)), 0));
        }
    }
}
