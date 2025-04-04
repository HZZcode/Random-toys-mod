package random_toys.zz_404;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class EnderLinkerConfiguratorItem extends Item {
    private BlockPos linked;
    private RegistryKey<World> dimension;

    public EnderLinkerConfiguratorItem(Settings settings) {
        super(settings);
        linked = null;
        dimension = null;
    }

    @Override
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.FAIL;
        World world = player.getWorld();
        if (!world.isClient) {
            if (player.isSneaking() && world.getBlockEntity(pos) instanceof EnderLinkerBlockEntity linker && linker.linked != null) {
                RandomToys.msg(player, Text.translatable("message.random-toys.ender_linker_configurator.check",
                        linker.dimension.getValue().toString(),
                        linker.linked.getX(), linker.linked.getY(), linker.linked.getZ()));
                return ActionResult.SUCCESS_NO_ITEM_USED;
            }
            if (player.isSneaking() && world.getBlockEntity(pos) instanceof EnderHopperBlockEntity hopper && hopper.linked != null) {
                RandomToys.msg(player, Text.translatable("message.random-toys.ender_linker_configurator.check",
                        hopper.dimension.getValue().toString(),
                        hopper.linked.getX(), hopper.linked.getY(), hopper.linked.getZ()));
                return ActionResult.SUCCESS_NO_ITEM_USED;
            }
            if (linked == null || dimension == null) {
                linked = pos;
                dimension = world.getRegistryKey();
                RandomToys.msg(player,
                        Text.translatable("message.random-toys.ender_linker_configurator.get",
                                dimension.getValue().toString(),
                                linked.getX(), linked.getY(), linked.getZ()));
                return ActionResult.SUCCESS_NO_ITEM_USED;
            }
            if (world.getBlockEntity(pos) instanceof EnderLinkerBlockEntity linker) {
                linker.linked = linked;
                linker.dimension = dimension;
                RandomToys.msg(player,
                        Text.translatable("message.random-toys.ender_linker_configurator.set",
                                dimension.getValue().toString(),
                                linked.getX(), linked.getY(), linked.getZ()));
                linked = null;
                dimension = null;
                return ActionResult.SUCCESS_NO_ITEM_USED;
            }
            if (world.getBlockEntity(pos) instanceof EnderHopperBlockEntity hopper) {
                if (!(world.getBlockEntity(linked) instanceof EnderHopperBlockEntity hopper0)
                        || hopper.getWorld() == null) return ActionResult.FAIL;
                hopper.linked = linked;
                hopper.dimension = dimension;
                hopper0.linked = hopper.getPos();
                hopper0.dimension = hopper.getWorld().getRegistryKey();
                RandomToys.msg(player,
                        Text.translatable("message.random-toys.ender_linker_configurator.set",
                                dimension.getValue().toString(),
                                linked.getX(), linked.getY(), linked.getZ()));
                linked = null;
                dimension = null;
                return ActionResult.SUCCESS_NO_ITEM_USED;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public void onItemEntityDestroyed(@NotNull ItemEntity entity) {
        if (entity.getOwner() instanceof LivingEntity owner) {
            owner.teleport(entity.getX(), entity.getY(), entity.getZ(), true);
            owner.getWorld().playSound(null, owner.getX(), owner.getY(), owner.getZ(),
                    SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL,
                    0.5F, 0.4F / (owner.getWorld().getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        return 4.04f;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker == null || target == null) return false;
        swapPos(attacker, target);
        return true;
    }

    private void swapPos(@NotNull LivingEntity entity1, @NotNull LivingEntity entity2) {
        Vec3d pos1 = entity1.getPos();
        Vec3d pos2 = entity2.getPos();
        entity1.teleport(pos2.x, pos2.y, pos2.z, true);
        entity2.teleport(pos1.x, pos1.y, pos1.z, true);
        float yaw1 = entity1.getYaw();
        float yaw2 = entity2.getYaw();
        entity1.setYaw(yaw2);
        entity2.setYaw(yaw1);
        float pitch1 = entity1.getPitch();
        float pitch2 = entity2.getPitch();
        entity1.setPitch(pitch2);
        entity2.setPitch(pitch1);
        entity1.getWorld().playSound(null, entity1.getX(), entity1.getY(), entity1.getZ(),
                SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL,
                0.5F, 0.4F / (entity1.getWorld().getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
