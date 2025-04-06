package random_toys.zz_404;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractThrownBlackstoneEntity extends ThrownItemEntity implements FlyingItemEntity {
    public AbstractThrownBlackstoneEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    protected boolean canHit(Entity entity) {
        if (entity instanceof EndCrystalEntity) return true;
        if (!(entity instanceof LivingEntity livingEntity)) return false;
        if (getOwner() instanceof ZZEntity && !(ZZEntity.ENEMY_PREDICATE.test(livingEntity))) return false;
        if (livingEntity instanceof PlayerEntity &&
                (livingEntity.isInCreativeMode() || livingEntity.isSpectator())) return false;
        if (Objects.equals(getOwner(), entity)) return false;
        if (getOwner() instanceof PlayerEntity && entity instanceof TameableEntity tameableEntity
                && tameableEntity.getOwner() == getOwner()) return false;
        return true;
    }

    protected abstract void hitEntityUnchecked(Entity entity, float distance);

    private boolean hitEntity(Entity entity, float distance) {
        if (!canHit(entity)) return false;
        hitEntityUnchecked(entity, distance);
        return true;
    }

    private void destroyNearCrystals() {
        World world = getWorld();
        Box box = new Box(this.getBlockPos()).expand(5.0f);
        for (EndCrystalEntity crystal: world.getEntitiesByClass(EndCrystalEntity.class, box, crystalEntity -> true)) {
            crystal.kill();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        World world = getWorld();
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (!hitEntity(entity, world.random.nextFloat() / 2)) return;

        Box box = new Box(this.getBlockPos()).expand(5.0f);
        for (Entity entity1: world.getEntitiesByClass(Entity.class, box, entity0 -> true)) {
            hitEntity(entity1, distanceTo(entity));
        }

        destroyNearCrystals();

        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onBlockHit(@NotNull BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        World world = this.getWorld();
        BlockPos blockPos = blockHitResult.getBlockPos();

        Box box = new Box(blockPos).expand(5.0f);
        for (Entity entity: world.getEntitiesByClass(Entity.class, box, entity -> true)) {
            hitEntity(entity, distanceTo(entity));
        }

        this.remove(RemovalReason.DISCARDED);
    }
}
