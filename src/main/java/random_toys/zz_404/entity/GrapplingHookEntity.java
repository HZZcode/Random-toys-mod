package random_toys.zz_404.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModDataComponents;
import random_toys.zz_404.registry.ModItems;

import java.util.Objects;
import java.util.UUID;

public class GrapplingHookEntity extends ThrownEntity {
    public boolean hasHit = false;
    public UUID hitEntity = null;

    public GrapplingHookEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
        noClip = false;
        ignoreCameraFrustum = true;
    }

    @Override
    protected void initDataTracker(DataTracker.@NotNull Builder builder) {}

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!hasHit) {
            hasHit = true;
            setNoGravity(true);
        }
        super.onBlockHit(blockHitResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!hasHit && !entityHitResult.getEntity().equals(getOwner())) {
            hasHit = true;
            setNoGravity(true);
            hitEntity = entityHitResult.getEntity().getUuid();
        }
        super.onEntityHit(entityHitResult);
    }

    @Override
    public void tick() {
        if (hasHit) setVelocity(Vec3d.ZERO);
        super.tick();
        if (hasHit) setVelocity(Vec3d.ZERO);
        if (getWorld() instanceof ServerWorld server) {
            if (hitEntity != null) {
                Entity entity = server.getEntity(hitEntity);
                if (entity == null || entity.getWorld().getRegistryKey() != getWorld().getRegistryKey())
                    hitEntity = null;
                else setPosition(entity.getX(), entity.getBodyY(0.8), entity.getZ());
            }
            if (!(getOwner() instanceof PlayerEntity player) || !isOf(player)) kill();
        }
    }

    private boolean isOf(@NotNull PlayerEntity player) {
        return isOf(player.getMainHandStack()) || isOf(player.getOffHandStack());
    }

    private boolean isOf(ItemStack stack) {
        if (!stack.isOf(ModItems.GRAPPLING_HOOK)) return false;
        return Objects.equals(stack.get(ModDataComponents.HOOK_UUID), uuidString);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("hasHit", hasHit);
        if (hitEntity != null) nbt.putUuid("hitEntity", hitEntity);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("hasHit")) hasHit = nbt.getBoolean("hasHit");
        if (nbt.containsUuid("hitEntity")) hitEntity = nbt.getUuid("hitEntity");
        else hitEntity = null;
    }
}
