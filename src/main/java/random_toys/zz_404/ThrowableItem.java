package random_toys.zz_404;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ThrowableItem<T extends ThrownItemEntity> extends Item {
    EntityType<T> entityType;

    public ThrowableItem(EntityType<T> entityType, Settings settings) {
        super(settings);
        this.entityType = entityType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        itemStack.decrementUnlessCreative(1, user);
        T entity = entityType.create(world);
        if (entity == null) return TypedActionResult.fail(itemStack);
        entity.setOwner(user);
        entity.setPosition(user.getEyePos());
        entity.setVelocity(user.getRotationVector().normalize().multiply(3.0f));
        entity.setPitch(user.getPitch());
        entity.setYaw(user.getYaw());
        world.spawnEntity(entity);
        return TypedActionResult.consume(itemStack);
    }
}
