package random_toys.zz_404;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ThrownGildedBlackstoneEntity extends AbstractThrownBlackstoneEntity {
    public ThrownGildedBlackstoneEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.GILDED_BLACKSTONE_CRYSTAL;
    }

    @Override
    protected void hitEntityUnchecked(Entity entity, float distance) {
        Entity owner = getOwner();
        LivingEntity livingEntity = (LivingEntity) entity;
        DamageSource damageSource = getDamageSources().indirectMagic(owner, owner);
        entity.damage(damageSource, 10.0f / (distance + 1));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,
                (int) (100 / (distance + 1)), 2));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS,
                (int) (100 / (distance + 1)), 0));
    }
}
