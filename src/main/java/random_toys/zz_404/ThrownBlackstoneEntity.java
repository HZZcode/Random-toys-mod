package random_toys.zz_404;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ThrownBlackstoneEntity extends AbstractThrownBlackstoneEntity {
    public ThrownBlackstoneEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BLACKSTONE_CRYSTAL;
    }

    @Override
    protected void hitEntityUnchecked(Entity entity, float distance) {
        Entity owner = getOwner();
        DamageSource damageSource = getDamageSources().indirectMagic(owner, owner);
        entity.damage(damageSource, 5.0f / (distance + 1));
    }
}