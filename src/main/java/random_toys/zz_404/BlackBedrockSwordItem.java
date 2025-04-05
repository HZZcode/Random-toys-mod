package random_toys.zz_404;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackBedrockSwordItem extends SwordItem {
    public BlackBedrockSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, @NotNull LivingEntity target, LivingEntity attacker) {
        StatusEffectInstance[] effects = {
                new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2),
                new StatusEffectInstance(StatusEffects.DARKNESS, 100, 2),
                new StatusEffectInstance(StatusEffects.WITHER, 100, 2),
                new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 2),
        };
        World world = target.getWorld();
        Random random = world.random;
        target.addStatusEffect(effects[random.nextInt(effects.length)]);
        if (attacker instanceof PlayerEntity user) {
            List<ItemStack> stacks = user.getInventory().main.stream()
                    .filter(itemStack -> itemStack.isOf(ModBlocks.BLACK_BEDROCK.asItem())).toList();
            if ((!stacks.isEmpty() && !stacks.getFirst().isEmpty()) || user.isCreative()) {
                if (!user.isCreative()) stacks.getFirst().decrement(1);
                ThrownBlackBedrockEntity entity = new ThrownBlackBedrockEntity(ModEntities.THROWN_BLACK_BEDROCK, world);
                entity.setPosition(target.getPos().add(0, 5, 0));
                entity.setVelocity(new Vec3d(0, 0, 0));
                entity.setOwner(attacker);
                world.spawnEntity(entity);
            }
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {
        List<ItemStack> stacks = user.getInventory().main.stream()
                .filter(itemStack -> itemStack.isOf(ModBlocks.BLACK_BEDROCK.asItem())).toList();
        if ((!stacks.isEmpty() && !stacks.getFirst().isEmpty()) || user.isCreative()) {
            if (!user.isCreative()) stacks.getFirst().decrement(1);
            return ThrowableItem.spawn(ModEntities.THROWN_BLACK_BEDROCK, world, user)
                    ? TypedActionResult.consume(user.getStackInHand(hand))
                    : TypedActionResult.fail(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }
}
