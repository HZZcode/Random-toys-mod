package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class SolidLavaBlock extends Block {
    public SolidLavaBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, @NotNull Entity entity) {
        if (!entity.bypassesSteppingEffects() && entity instanceof LivingEntity) {
            entity.damage(world.getDamageSources().hotFloor(), 3.0F);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    public static BlockState getMeltedState() {
        return Blocks.LAVA.getDefaultState();
    }

    @Override
    public BlockState onBreak(@NotNull World world, BlockPos pos, BlockState state, @NotNull PlayerEntity player) {
        if (!world.isClient) {
            ItemStack tool = player.getMainHandStack().isEmpty()
                    ? player.getOffHandStack() : player.getMainHandStack();
            if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_ICE_MELTING)
                    && !player.isCreative()) {
                BlockState blockState = world.getBlockState(pos.down());
                if (blockState.blocksMovement() || blockState.isLiquid())
                    world.setBlockState(pos, getMeltedState());
            }
        }
        return super.onBreak(world, pos, state, player);
    }
}
