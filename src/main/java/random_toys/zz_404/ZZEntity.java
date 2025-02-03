package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

public class ZZEntity extends HostileEntity implements Angerable {
    private int angerTime;
    public int dizzyCooldownTime;
    public int teleportCooldownTime;
    public int shootingCooldownTime;
    public int crystalCooldownTime;
    Vec3d previousPos;
    @Nullable private UUID angryAt;
    private static final UniformIntProvider ANGER_TIME_RANGE;
    public static final TrackedData<Boolean> IS_ATTACKING =
            DataTracker.registerData(ZZEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final Predicate<LivingEntity> ENEMY_PREDICATE = (entity) -> {
        EntityType<?> entityType = entity.getType();
        return  (entityType == EntityType.PLAYER && !(entity.isInCreativeMode() || entity.isSpectator()))
                || entityType == EntityType.WARDEN
                || entityType == EntityType.ENDER_DRAGON
                || entityType == EntityType.WITHER
                || (entityType == EntityType.IRON_GOLEM && ((IronGolemEntity) entity).isPlayerCreated())
                || (entityType == EntityType.SNOW_GOLEM)
                || (entity instanceof TameableEntity && ((TameableEntity) entity).isTamed());
    };
    Status previousStatus = Status.NO_GOAL;

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    enum Status {
        NO_GOAL("no_goal"),
        RESTING("resting"),
        SHOOTING("shooting"),
        DIZZY("dizzy");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public void setAttacking(boolean attacking) {
        this.dataTracker.set(IS_ATTACKING, attacking);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_ATTACKING, false);
    }

    public ZZEntity(EntityType<? extends ZZEntity> entityType, World world) {
        super(entityType, world);
        this.setHealth(this.getMaxHealth());
        this.experiencePoints = 500;
        this.dizzyCooldownTime = 0;
        this.teleportCooldownTime = 0;
        this.crystalCooldownTime = 20;
        previousPos = null;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new ZZEscapeFluidGoal(this));
        this.goalSelector.add(0, new ZZKeepDistanceGoal(this));
        this.goalSelector.add(1, new ZZAttackGoal(this, 1.0F, false));
        this.goalSelector.add(1, new ZZShootingGoal(this));
        this.goalSelector.add(1, new ZZDestroyCrystalGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false, ENEMY_PREDICATE));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, IronGolemEntity.class, false, ENEMY_PREDICATE));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, WardenEntity.class, false, ENEMY_PREDICATE));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, EnderDragonEntity.class, false, ENEMY_PREDICATE));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, TameableEntity.class, false, ENEMY_PREDICATE));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.goalSelector.add(3, new ZZRandomTeleportGoal(this));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0F, 0.0F));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(5, new UniversalAngerGoal<>(this, false));
    }

    public static DefaultAttributeContainer.Builder createZZAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0F)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.0F);
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public int getAngerTime() {
        return angerTime;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public @Nullable UUID getAngryAt() {
        return angryAt;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    //Copied from net.minecraft.entity.mob.EndermanEntity.teleportTo
    @SuppressWarnings("deprecation")
    public void teleportTo(@NotNull Vec3d pos) {
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
        while(mutable.getY() > this.getWorld().getBottomY() && !this.getWorld().getBlockState(mutable).blocksMovement()) {
            mutable.move(Direction.DOWN);
        }
        BlockState blockState = this.getWorld().getBlockState(mutable);
        if (blockState.blocksMovement()) {
            Vec3d vec3d = this.getPos();
            boolean bl3 = this.teleport(x, y, z, true);
            if (bl3) {
                this.getWorld().emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(this));
                if (!this.isSilent()) {
                    this.getWorld().playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }
        }
    }

    public void randomTeleport() {
        Random random = getWorld().random;
        Vec3d newPos = new Vec3d(random.nextDouble() - 0.5f, random.nextDouble() - 0.5f, random.nextDouble() - 0.5f)
                .normalize()
                .multiply(15 * random.nextDouble())
                .add(getPos());
        teleportTo(newPos);
    }

    @Override
    public void tick() {
        super.tick();
        dizzyCooldownTime = Math.max(dizzyCooldownTime - 1, 0);
        generateParticleEffect();
    }

    private boolean isSelfDamage(Entity sourceEntity) {
        return (sourceEntity instanceof BreezeWindChargeEntity
                && ((BreezeWindChargeEntity) sourceEntity).getOwner() == this)
        || (sourceEntity instanceof WindChargeEntity
                && ((WindChargeEntity) sourceEntity).getOwner() == this)
        || (sourceEntity instanceof ArrowEntity
                && ((ArrowEntity) sourceEntity).getOwner() == this)
        || (sourceEntity instanceof FireballEntity
                && ((FireballEntity) sourceEntity).getOwner() == this)
        || (sourceEntity instanceof SmallFireballEntity
                && ((SmallFireballEntity) sourceEntity).getOwner() == this);
    }

    private boolean damageCanBeImmunized(@NotNull DamageSource source) {
        return source.isIn(DamageTypeTags.IS_FALL)
                || source.isIn(DamageTypeTags.IS_DROWNING)
                || source.isIn(DamageTypeTags.IS_FIRE)
                || source.isIn(DamageTypeTags.IS_EXPLOSION);
    }

    @Override
    public boolean damage(@NotNull DamageSource source, float amount) {
        return damage(source, amount, true);
    }

    public boolean damage(@NotNull DamageSource source, float amount, boolean canAvoid) {
        Entity sourceEntity = source.getSource();
        if (canAvoid && sourceEntity instanceof LivingEntity && !ENEMY_PREDICATE.test((LivingEntity) sourceEntity)
                && !(sourceEntity instanceof PlayerEntity && ((PlayerEntity) sourceEntity).isCreative()))
            return false;
        if (source.isOf(DamageTypes.FIREWORKS)) {
            if (sourceEntity instanceof FireworkRocketEntity firework) {
                this.dizzyCooldownTime += 160;
                if (!(firework.getOwner() instanceof LivingEntity owner)) return false;
                DamageSource newSource = owner.getDamageSources().mobAttack(owner);
                if (newSource.isOf(DamageTypes.FIREWORKS)) return false;
                float newAmount = amount * (8 + getEntityWorld().random.nextInt(3));
                this.damage(newSource, newAmount, false);
            }
        }
        if (canAvoid && damageCanBeImmunized(source))
            return false;
        if (canAvoid && isSelfDamage(sourceEntity))
            return false;
        return super.damage(source, amount);
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        DamageSource source = getRecentDamageSource();
        if (source == null) return;
        Entity sourceEntity = source.getSource();
        if (isSelfDamage(sourceEntity)) return;
        super.takeKnockback(strength, x, z);
    }

    private void generateParticleEffect() {
        Vec3d position = getPos();
        World world = getWorld();
        ParticleEffect particleEffect = currentParticle();

        double radius = 1.0;
        for (int i = 0; i < 360; i += 30) {
            double angle = Math.toRadians(i);
            double x = position.x + radius * Math.cos(angle);
            double y = position.y + 2.5;
            double z = position.z + radius * Math.sin(angle);
            double velocityX = 0;
            double velocityY = 0;
            double velocityZ = 0;

            world.addParticle(particleEffect, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    //Has weird BUG here, but I don't want to waste time to fix it
    //Particles are not important, are they?
    private Status currentStatus() {
        return Status.SHOOTING;
//        Status current;
//        if (dizzyCooldownTime == 0 && shootingCooldownTime == 0) return previousStatus;
//        if (getTarget() == null && previousStatus == Status.NO_GOAL) current = Status.NO_GOAL;
//        else if (dizzyCooldownTime > 0) current = Status.DIZZY;
//        else if (shootingCooldownTime > 0) current = Status.SHOOTING;
//        else current = Status.RESTING;
//        previousStatus = current;
//        return current;
    }

    private @NotNull ParticleEffect currentParticle() {
        Status status = currentStatus();
        return switch (status) {
            case NO_GOAL -> ModParticles.WHITE_ZZ;
            case RESTING -> ModParticles.YELLOW_ZZ;
            case SHOOTING -> ModParticles.RED_ZZ;
            case DIZZY -> ModParticles.GREEN_ZZ;
        };
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("AngerTime", this.angerTime);
        nbt.putInt("DizzyCooldownTime", this.dizzyCooldownTime);
        nbt.putInt("TeleportCooldownTime", this.teleportCooldownTime);
        nbt.putInt("ShootingCooldownTime", this.shootingCooldownTime);
        nbt.putInt("CrystalCooldownTime", this.crystalCooldownTime);
        if (this.previousPos != null) {
            nbt.putDouble("PreviousPosX", this.previousPos.x);
            nbt.putDouble("PreviousPosY", this.previousPos.y);
            nbt.putDouble("PreviousPosZ", this.previousPos.z);
        }
        if (this.angryAt != null) {
            nbt.putUuid("AngryAt", this.angryAt);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.angerTime = nbt.getInt("AngerTime");
        this.dizzyCooldownTime = nbt.getInt("DizzyCooldownTime");
        this.teleportCooldownTime = nbt.getInt("TeleportCooldownTime");
        this.shootingCooldownTime = nbt.getInt("ShootingCooldownTime");
        this.crystalCooldownTime = nbt.getInt("CrystalCooldownTime");
        if (nbt.contains("PreviousPosX", NbtElement.DOUBLE_TYPE)) {
            this.previousPos = new Vec3d(
                    nbt.getDouble("PreviousPosX"),
                    nbt.getDouble("PreviousPosY"),
                    nbt.getDouble("PreviousPosZ")
            );
        }
        if (nbt.contains("AngryAt")) {
            this.angryAt = nbt.getUuid("AngryAt");
        }
    }

    static {
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
    }
}
