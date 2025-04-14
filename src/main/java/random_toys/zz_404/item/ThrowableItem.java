package random_toys.zz_404.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ThrowableItem<T extends ThrownItemEntity> extends Item {
    final EntityType<T> entityType;

    public ThrowableItem(EntityType<T> entityType, Settings settings) {
        super(settings);
        this.entityType = entityType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        itemStack.decrementUnlessCreative(1, user);
        return spawn(entityType, world, user)
                ? TypedActionResult.consume(itemStack)
                : TypedActionResult.fail(itemStack);
    }

    public static <T extends ThrownItemEntity> boolean spawn(@NotNull EntityType<T> entityType, World world, @NotNull Entity user) {
        return spawn(entityType, world, user, user.getRotationVector().normalize().multiply(3.0f));
    }

    public static <T extends ThrownItemEntity> boolean spawn(@NotNull EntityType<T> entityType, World world, @NotNull Entity user, Vec3d velocity) {
        T entity = entityType.create(world);
        if (entity == null) return false;
        entity.setOwner(user);
        entity.setPosition(user.getEyePos());
        entity.setVelocity(velocity);
        entity.setPitch(user.getPitch());
        entity.setYaw(user.getYaw());
        world.spawnEntity(entity);
        return true;
    }
}
