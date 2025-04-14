package random_toys.zz_404.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.entity.goal.BedrockDemonFlyingGoal;
import random_toys.zz_404.item.ThrowableItem;
import random_toys.zz_404.registry.ModEntities;

import java.util.function.Predicate;

public class BedrockDemonEntity extends FlyingEntity implements Monster, RangedAttackMob {
    private final ServerBossBar bossBar = (ServerBossBar) new ServerBossBar(getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS).setDarkenSky(true);
    public static final Predicate<LivingEntity> ENEMY_PREDICATE = (entity) -> {
        EntityType<?> entityType = entity.getType();
        return  (entityType == EntityType.PLAYER && !(entity.isInCreativeMode() || entity.isSpectator()))
                || (entityType == EntityType.IRON_GOLEM && ((IronGolemEntity) entity).isPlayerCreated())
                || (entity instanceof TameableEntity && ((TameableEntity) entity).isTamed());
    };
    private static final Box OriginalBoundingBox = new Box(-0.5, 0, -0.5, 0.5, 1, 0.5);

    public BedrockDemonEntity(EntityType<? extends FlyingEntity> entityType, World world) {
        super(entityType, world);
        setHealth(this.getMaxHealth());
        lookControl = new BedrockDemonLookControl(this);
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.0, 20, 64.0F));
        this.goalSelector.add(2, new BedrockDemonFlyingGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, 0, false, false, ENEMY_PREDICATE));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0F)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.0F);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (hasCustomName()) {
            bossBar.setName(getDisplayName());
        }
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        bossBar.setName(getDisplayName());
    }

    public float getHealthPercentage() {
        return getHealth() / getMaxHealth();
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        float percent = getHealthPercentage();
        setBoundingBox(multiplyBox(2 * getHealthPercentage() + 1));
        //Seems that this isn't working. Why???
        bossBar.setPercent(percent);
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        bossBar.removePlayer(player);
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        for (int i = 0; i < random.nextInt(3); i++) {
            ThrowableItem.spawn(ModEntities.THROWN_BLACK_BEDROCK, getWorld(), this,
                    target.getEyePos().subtract(getEyePos()).normalize().multiply(4.0).add(getRandomVec()));
        }
    }

    public Vec3d getRandomVec() {
        return new Vec3d(random.nextFloat() * (2 * random.nextInt(2) - 1),
                random.nextFloat() * (2 * random.nextInt(2) - 1),
                random.nextFloat() * (2 * random.nextInt(2) - 1));
    }

    private boolean isSelfDamage(Entity sourceEntity) {
        return sourceEntity instanceof ProjectileEntity projectile && projectile.getOwner() == this;
    }

    private boolean damageCanBeImmunized(@NotNull DamageSource source) {
        return source.isIn(DamageTypeTags.IS_FALL)
                || source.isIn(DamageTypeTags.IS_DROWNING)
                || source.isIn(DamageTypeTags.IS_FIRE)
                || source.isIn(DamageTypeTags.IS_EXPLOSION);
    }

    @Override
    public boolean damage(@NotNull DamageSource source, float amount) {
        Entity sourceEntity = source.getSource();
        if (sourceEntity instanceof LivingEntity && !ENEMY_PREDICATE.test((LivingEntity) sourceEntity)
                && !(sourceEntity instanceof PlayerEntity && ((PlayerEntity) sourceEntity).isCreative()))
            return false;
        if (damageCanBeImmunized(source) || isSelfDamage(sourceEntity))
            return false;
        return super.damage(source, amount);
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull Box multiplyBox(double scale) {
        return new Box(BedrockDemonEntity.OriginalBoundingBox.minX * scale, BedrockDemonEntity.OriginalBoundingBox.minY * scale, BedrockDemonEntity.OriginalBoundingBox.minZ * scale,
                BedrockDemonEntity.OriginalBoundingBox.maxX * scale, BedrockDemonEntity.OriginalBoundingBox.maxY * scale, BedrockDemonEntity.OriginalBoundingBox.maxZ * scale);
    }

    public static class BedrockDemonLookControl extends LookControl {
        public BedrockDemonLookControl(BedrockDemonEntity entity) {
            super(entity);
        }

        @Override
        public void lookAt(double x, double y, double z, float maxYawChange, float maxPitchChange) {}
    }
}
//TODO: a special crystal entity for regeneration
//TODO: generating?
