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
        switch (chooser) {
            case 0: {
                WindChargeEntity entity = new WindChargeEntity(EntityType.WIND_CHARGE, world);
                entity.setPosition(pos);
                entity.setVelocity(direction);
                entity.setOwner(this.zz);
                return entity;
            }
            case 1: case 2: {
                BreezeWindChargeEntity entity = new BreezeWindChargeEntity(EntityType.BREEZE_WIND_CHARGE, world);
                entity.setPosition(pos);
                entity.setVelocity(direction);
                entity.setOwner(this.zz);
                return entity;
            }
            case 3: case 4: {
                SmallFireballEntity entity = new SmallFireballEntity(world, this.zz, direction);
                entity.setPosition(pos);
                return entity;
            }
            case 5: case 6: case 7: {
                FireballEntity entity = new FireballEntity(world, this.zz, direction, 6);
                entity.setPosition(pos);
                return entity;
            }
            case 8: {
                ThrownGildedBlackstoneEntity entity = new ThrownGildedBlackstoneEntity(ModEntities.THROWN_GILDED_BLACKSTONE, world);
                entity.setPosition(pos);
                entity.setVelocity(direction);
                entity.setOwner(this.zz);
                return entity;
            }
            default: {
                ThrownEnchantedGildedBlackstoneEntity entity = new ThrownEnchantedGildedBlackstoneEntity(ModEntities.THROWN_GILDED_BLACKSTONE, world);
                entity.setPosition(pos);
                entity.setVelocity(direction);
                entity.setOwner(this.zz);
                return world.random.nextBoolean() ? entity : null;
            }
        }
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
