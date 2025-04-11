package random_toys.zz_404.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModBlocks;
import random_toys.zz_404.registry.ModCriteria;

public class ZZCoreItem extends Item {
    public ZZCoreItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        if (player == null) return ActionResult.PASS;
        if (!player.isSneaking()) return ActionResult.PASS;
        if (world.getBlockState(pos).isOf(Blocks.CRAFTING_TABLE)) {
            if (player instanceof ServerPlayerEntity serverPlayer)
                ModCriteria.BLACKSTONE_PROCESSING.trigger(serverPlayer);
            stack.decrementUnlessCreative(1, player);
            world.setBlockState(pos, ModBlocks.BLACKSTONE_PROCESSING_TABLE.getDefaultState());
            world.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
            return ActionResult.CONSUME;
        }
        if (world.getBlockState(pos).isOf(ModBlocks.BLACKSTONE_PROCESSING_TABLE)
                && world.getBlockState(pos.down()).isOf(ModBlocks.BLACK_BEDROCK)) {
            stack.decrementUnlessCreative(1, player);
            world.setBlockState(pos, ModBlocks.BLACK_BEDROCK_PROCESSING_TABLE.getDefaultState());
            world.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
            world.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }
}
