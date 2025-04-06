package random_toys.zz_404;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ThrownBlackBedrockEntity extends AbstractThrownBlackstoneEntity {
    public ThrownBlackBedrockEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModBlocks.BLACK_BEDROCK.asItem();
    }

    @Override
    protected void hitEntityUnchecked(@NotNull Entity entity, float distance) {
        Entity owner = getOwner();
        DamageSource damageSource = getDamageSources().indirectMagic(owner, owner);
        entity.damage(damageSource, 20.0f / (distance + 1));
        if (entity instanceof LivingEntity livingEntity
                && !(entity instanceof PlayerEntity player && BlackBedrockArmorItem.isWearingAll(player))) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,
                    (int) (200 / (distance + 1)), 2));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS,
                    (int) (200 / (distance + 1)), 0));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER,
                    (int) (200 / (distance + 1)), 0));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,
                    (int) (200 / (distance + 1)), 0));
        }
    }

    @Override
    protected void onBlockHit(@NotNull BlockHitResult blockHitResult) {
        World world = this.getWorld();
        BlockPos blockPos = blockHitResult.getBlockPos();
        for (BlockPos pos : transformPos(blockPos))
            if (world.getBlockState(pos).isIn(BlockTags.SCULK_REPLACEABLE))
                if (world.random.nextInt(3) == 0)
                    world.setBlockState(pos, ModBlocks.BLACK_BEDROCK.getDefaultState());
        super.onBlockHit(blockHitResult);
    }

    private @NotNull ArrayList<BlockPos> transformPos(@NotNull BlockPos pos) {
        ArrayList<BlockPos> ans = new ArrayList<>();
        for (int x = pos.getX() - 1; x <= pos.getX() + 1; x++)
            for (int y = pos.getY() - 1; y <= pos.getY() + 1; y++)
                for (int z = pos.getZ() - 1; z <= pos.getZ() + 1; z++)
                    ans.add(new BlockPos(x, y, z));
        return ans;
    }
}
