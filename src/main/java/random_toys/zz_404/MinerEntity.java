package random_toys.zz_404;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class MinerEntity extends AnimalEntity {
    public MinerStoneMiningGoal miningHelper;

    public MinerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        miningHelper = new MinerStoneMiningGoal(this);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new PowderSnowJumpGoal(this, this.getWorld()));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25F));
        this.goalSelector.add(2, new TemptGoal(this, 1.2,
                stack -> stack.isIn(ModTags.MINER_LOVED), false));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0F, false));
        this.goalSelector.add(3, new MinerOreMiningGoal(this));
        this.goalSelector.add(3, miningHelper = new MinerStoneMiningGoal(this));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0F));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, (new RevengeGoal(this)).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, AbstractSkeletonEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, CreeperEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SpiderEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, WitchEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SilverfishEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createMinerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0F)
                .add(EntityAttributes.GENERIC_SCALE, 1.5F)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 0.5F);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public @NotNull ArrayList<BlockPos> nearBlocks(int n) {
        ArrayList<BlockPos> list = new ArrayList<>();
        for (int x = getBlockX() - n; x <= getBlockX() + n; x++)
            for (int y = getBlockY() - n; y <= getBlockY() + n; y++)
                for (int z = getBlockZ() - n; z <= getBlockZ() + n; z++)
                    list.add(new BlockPos(x, y, z));
        return list;
    }

    public void mineBlock(BlockPos pos) {
        World world = getWorld();
        if (pos != null && world != null) {
            getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
            world.breakBlock(pos, true, this);
        }
    }

    @Nullable
    public BlockHitResult hitResult(BlockPos pos) {
        if (!(getWorld() instanceof ServerWorld server)) return null;
        Vec3d minerEyePos = getEyePos();
        Vec3d targetPos = Vec3d.ofCenter(pos);
        return server.raycast(new RaycastContext(minerEyePos, targetPos,
                RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, this));
    }

    @Nullable
    public BlockPos blockingBlock(BlockPos pos) {
        BlockHitResult hitResult = hitResult(pos);
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK
                && !hitResult.getBlockPos().equals(pos)) return hitResult.getBlockPos();
        return null;
    }

    public boolean canSeeOre(BlockPos pos) {
        return blockingBlock(pos) != null;
    }

    public boolean canMine(BlockPos pos) {
        return pos != null && canSeeOre(pos) && pos.isWithinDistance(getBlockPos(), 3.5)
                && getWorld().getBlockState(pos).getHardness(getWorld(), pos) >= 0;
    }
}
