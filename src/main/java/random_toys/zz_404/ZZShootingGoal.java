package random_toys.zz_404;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.reflection_utils.FireballEntityExplosionPowerUtils;

import java.util.function.Consumer;

public class ZZShootingGoal extends Goal {
    private final ZZEntity zz;

    public ZZShootingGoal(ZZEntity zz) {
        this.zz = zz;
    }

    @Override
    public boolean canStart() {
        return this.zz.getTarget() != null;
    }

    @Override
    public void start() {
        this.zz.shootingCooldownTime = 0;
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    private Vec3d getRandomVec(@NotNull Random random) {
        return new Vec3d(random.nextDouble(), random.nextDouble(), random.nextDouble())
                .normalize().multiply(0.7f);
    }

    private double getSpeed(@NotNull Random random) {
        return 5.0f + random.nextDouble() * 1.0f;
    }

    private @Nullable Entity shootEntity(@NotNull World world, Vec3d pos, Vec3d direction) {
        int chooser = world.random.nextInt(10);
        boolean isStage2 = zz.getHealthPercentage() < 0.5;
        boolean bl = isStage2 || world.random.nextInt(5) < 2;
        return bl ? switch (chooser) {
            case 0 -> getProjectile(EntityType.WIND_CHARGE, world, pos, direction);
            case 1, 2 -> getProjectile(EntityType.BREEZE_WIND_CHARGE, world, pos, direction);
            case 3, 4 -> getProjectile(EntityType.SMALL_FIREBALL, world, pos, direction);
            case 5, 6, 7 -> getProjectile(EntityType.FIREBALL, world, pos, direction,fireball
                    -> FireballEntityExplosionPowerUtils.trySetExplosionPower(fireball, 6));
            case 8 -> getProjectile(ModEntities.THROWN_GILDED_BLACKSTONE, world, pos, direction);
            default -> world.random.nextBoolean() || isStage2 ? null :
                getProjectile(ModEntities.THROWN_ENCHANTED_GILDED_BLACKSTONE, world, pos, direction);
        } : null;
    }

    private <T extends ProjectileEntity> ProjectileEntity getProjectile(@Nullable EntityType<T> type,
                                           @NotNull World world, Vec3d pos, Vec3d direction) {
        return getProjectile(type, world, pos, direction, t -> {});
    }

    private <T extends ProjectileEntity> ProjectileEntity getProjectile(@Nullable EntityType<T> type,
                                           @NotNull World world, Vec3d pos, Vec3d direction,
                                           Consumer<T> consumer) {
        if (type == null) return null;
        T entity = type.create(world);
        if (entity == null) return null;
        consumer.accept(entity);
        entity.setPosition(pos);
        entity.setVelocity(direction);
        entity.setOwner(zz);
        return entity;
    }

    private void shoot(@NotNull LivingEntity livingEntity, @NotNull World world) {
        Vec3d eyePos = this.zz.getEyePos();
        Vec3d targetPos = livingEntity.getEyePos().add(getRandomVec(world.random));
        double speed = getSpeed(world.random);
        Vec3d direction = targetPos.subtract(eyePos).normalize().multiply(speed);
        Entity shoot = shootEntity(world, eyePos, direction);
        if (shoot != null) world.spawnEntity(shoot);
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.zz.getTarget();
        if (livingEntity != null && zz.dizzyCooldownTime <= 0) {
            float d = this.zz.distanceTo(livingEntity);
            if (d < 32.0f || d < 128.0f &&
                    (livingEntity instanceof FlyingEntity || livingEntity instanceof EnderDragonEntity)) {
                World world = this.zz.getWorld();
                this.zz.shootingCooldownTime++;
                if (this.zz.shootingCooldownTime == 100) {
                    shoot(livingEntity, world);
                    this.zz.shootingCooldownTime = -70;
                }
                if (this.zz.shootingCooldownTime > 0 && this.zz.shootingCooldownTime % 6 == 0) {
                    shoot(livingEntity, world);
                }
            } else if (this.zz.shootingCooldownTime > 0) {
                --this.zz.shootingCooldownTime;
            }
        }
    }
}
