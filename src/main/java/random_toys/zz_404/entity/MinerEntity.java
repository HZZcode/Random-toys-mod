package random_toys.zz_404.entity;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.entity.goal.*;
import random_toys.zz_404.reflection_utils.HungerManagerUpdateUtils;
import random_toys.zz_404.registry.ModEntities;
import random_toys.zz_404.registry.ModTags;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

//TODO: Natural Spawning
public class MinerEntity extends TameableEntity implements InventoryOwner {
    public final SimpleInventory inventory = new SimpleInventory(27);
    public BlockPos miningPos = null;
    public final HungerManager hungerManager = new HungerManager();
    private int duplicateCooldown = 0;

    public Predicate<ItemStack> neededItems = stack -> false;

    static final Predicate<ItemEntity> PICKABLE_DROP_FILTER
            = item -> !item.cannotPickup() && item.isAlive();

    @Override
    public SimpleInventory getInventory() {
        return inventory;
    }

    public MinerEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        setCanPickUpLoot(true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new PowderSnowJumpGoal(this, this.getWorld()));
        this.goalSelector.add(1, new TameableEscapeDangerGoal(1.25F));
        this.goalSelector.add(2, new TemptGoal(this, 1.2,
                stack -> stack.isIn(ModTags.MINER_LOVED), false));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0F, false));
        this.goalSelector.add(2, new MinerEatFoodGoal(this));
        this.goalSelector.add(3, new MinerOreMiningGoal(this));
        this.goalSelector.add(3, new MinerStoneMiningGoal(this));
        this.goalSelector.add(4, new MinerPickupItemGoal(this));
        this.goalSelector.add(4, new MinerInteractWithCompressorGoal(this));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0F));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, AbstractSkeletonEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, CreeperEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SpiderEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, WitchEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SlimeEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SilverfishEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createMinerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0F)
                .add(EntityAttributes.GENERIC_SCALE, 1.2F)
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

    //There are some wierd problems with BlockPos::stream, so I wrote this
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
        addExhaustion(0.005F);
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

    @Override
    public boolean canGather(ItemStack stack) {
        return inventory.canInsert(stack) && getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    @Override
    protected void loot(ItemEntity item) {
        InventoryOwner.pickUpItem(this, this, item);
    }

    @Override
    public void tick() {
        super.tick();
        boolean success = HungerManagerUpdateUtils.tryUpdateHunger(this);
        if (!success) RandomToys.error("Failed to update miner hunger");
    }

    public boolean canFoodHeal() {
        return this.getHealth() > 0.0F && getHealth() < getMaxHealth();
    }

    @Override
    public ItemStack eatFood(@NotNull World world, ItemStack stack, FoodComponent foodComponent) {
        hungerManager.eat(foodComponent);
        world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_BURP,
                SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        ItemStack itemStack = super.eatFood(world, stack, foodComponent);
        Optional<ItemStack> optional = foodComponent.usingConvertsTo();
        if (optional.isPresent()) {
            if (itemStack.isEmpty())
                return optional.get().copy();
            if (!getWorld().isClient())
                getInventory().addStack(optional.get().copy());
        }
        return itemStack;
    }

    public void addExhaustion(float exhaustion) {
        if (!this.getWorld().isClient) {
            this.hungerManager.addExhaustion(exhaustion);
        }
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        super.applyDamage(source, amount);
        addExhaustion(source.getExhaustion());
    }

    @Override
    protected void attackLivingEntity(LivingEntity target) {
        super.attackLivingEntity(target);
        addExhaustion(0.1F);
    }

    @Override
    public void jump() {
        super.jump();
        addExhaustion(isSprinting()? 0.2F : 0.05F);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (duplicateCooldown > 0)
            duplicateCooldown--;
        if (getVelocity().length() > 0.2) addExhaustion(1e-4F);
        if (getWorld().getDifficulty() == Difficulty.PEACEFUL && getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (getHealth() < getMaxHealth() && age % 20 == 0) {
                heal(1.0F);
            }
            if (hungerManager.getSaturationLevel() < 20.0F && age % 20 == 0) {
                hungerManager.setSaturationLevel(hungerManager.getSaturationLevel() + 1.0F);
            }
            if (hungerManager.isNotFull() && age % 10 == 0) {
                hungerManager.setFoodLevel(hungerManager.getFoodLevel() + 1);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        writeInventory(nbt, getRegistryManager());
        nbt.putInt("DuplicateCooldown", duplicateCooldown);
        hungerManager.writeNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        readInventory(nbt, getRegistryManager());
        if (nbt.contains("DuplicateCooldown"))
            duplicateCooldown = nbt.getInt("DuplicateCooldown");
        hungerManager.readNbt(nbt);
    }

    private void tryTame(PlayerEntity player) {
        if (this.random.nextInt(3) == 0) {
            this.setOwner(player);
            this.setSitting(true);
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
        } else {
            this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
        }
    }

    private boolean isLovedItem(@NotNull ItemStack stack) {
        return stack.isIn(ModTags.MINER_LOVED);
    }

    private void duplicate() {
        MinerEntity miner = ModEntities.MINER.create(getWorld());
        if (miner != null) {
            miner.refreshPositionAfterTeleport(getPos());
            miner.setPersistent();
            duplicateCooldown = 400;
            miner.duplicateCooldown = 400;
            if (getOwner() instanceof PlayerEntity player)
                miner.setOwner(player);
            getWorld().spawnEntity(miner);
        }
    }

    @Override
    public ActionResult interactMob(@NotNull PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!getWorld().isClient()) {
            if (!isTamed()) {
                if (isLovedItem(itemStack)) {
                    eat(player, hand, itemStack);
                    tryTame(player);
                    setPersistent();
                }
            }
            else if (itemStack.isOf(Items.DIAMOND_BLOCK) && duplicateCooldown <= 0)
                duplicate();
            return ActionResult.success(getWorld().isClient());
        }
        return super.interactMob(player, hand);
    }
}
