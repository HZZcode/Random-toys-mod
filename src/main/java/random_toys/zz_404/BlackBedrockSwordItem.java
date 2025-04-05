package random_toys.zz_404;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

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
        {
            ThrownBlackBedrockEntity entity = new ThrownBlackBedrockEntity(ModEntities.THROWN_BLACK_BEDROCK, world);
            entity.setPosition(target.getPos().add(0, 5, 0));
            entity.setVelocity(new Vec3d(0, 0, 0));
            entity.setOwner(attacker);
            entity.setTarget(target);
            world.spawnEntity(entity);
        }
        return true;
    }
}
