package random_toys.zz_404.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.entity.GrapplingHookEntity;
import random_toys.zz_404.registry.ModDataComponents;
import random_toys.zz_404.registry.ModEntities;

import java.util.UUID;

public class GrapplingHookItem extends Item {
    public GrapplingHookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld server) {
            ItemStack stack = user.getStackInHand(hand);
            stack.damage(1, user, hand == Hand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            String uuidString = stack.getComponents().get(ModDataComponents.HOOK_UUID);
            if (uuidString == null) {
                GrapplingHookEntity hook = new GrapplingHookEntity(ModEntities.GRAPPLING_HOOK, world);
                Vec3d rotation = user.getRotationVector().normalize();
                hook.setOwner(user);
                hook.setPosition(user.getEyePos().add(0, -0.4, 0).add(rotation.multiply(0.7)));
                hook.setVelocity(rotation.multiply(4));
                hook.setPitch(user.getPitch());
                hook.setYaw(user.getYaw());
                world.spawnEntity(hook);
                stack.set(ModDataComponents.HOOK_UUID, hook.getUuidAsString());
            }
            if (uuidString != null) {
                Entity entity = server.getEntity(UUID.fromString(uuidString));
                if (entity instanceof GrapplingHookEntity hook && hook.hasHit) {
                    hook.kill();
                    if (!user.isSneaking()) grapple(user, hook.getPos());
                    else if (hook.hitEntity != null) {
                        Entity hitEntity = server.getEntity(hook.hitEntity);
                        if (hitEntity != null) grapple(hitEntity, user.getPos());
                    }
                }
                stack.set(ModDataComponents.HOOK_UUID, null);
            }
        }
        return super.use(world, user, hand);
    }

    private void grapple(@NotNull Entity entity, @NotNull Vec3d pos) {
        final double MaxSpeed = 40;
        Vec3d direction = pos.subtract(entity.getPos()).multiply(0.3);
        if (direction.length() >= MaxSpeed) direction = direction.normalize().multiply(MaxSpeed);
        entity.addVelocity(direction);
        entity.velocityModified = true;
    }
}