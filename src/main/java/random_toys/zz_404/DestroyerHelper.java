package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DestroyerHelper {
    public static boolean isNotBreakable(World world, BlockPos pos) {
        return isNotBreakable(world, pos, world.getBlockState(pos));
    }

    @SuppressWarnings("deprecation")
    public static boolean isNotBreakable(World world, BlockPos pos, BlockState blockState) {
        return blockState == null || blockState.isAir() || blockState.isLiquid()
                || (!blockState.isOf(ModBlocks.BLACK_BEDROCK) && blockState.getHardness(world, pos) < 0);
    }

    public static List<ItemStack> breakAndDrop(@NotNull ServerWorld world, BlockPos pos, @NotNull BlockState blockState) {
        ItemStack itemStack = new ItemStack(Items.DIAMOND_AXE);
        EnchantmentHelper.applyEnchantmentProvider(itemStack, world.getRegistryManager(),
                EnchantmentProviders.ENDERMAN_LOOT_DROP, world.getLocalDifficulty(pos), world.random);
        List<ItemStack> drops = blockState.getDroppedStacks(new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.ORIGIN, Vec3d.of(pos))
                .add(LootContextParameters.TOOL, itemStack));
        world.breakBlock(pos, false);
        return drops;
    }

    public static void insertDrops(World world, List<ItemStack> drops, BlockPos pos, DefaultedList<ItemStack> inventory) {
        drop:
        for (ItemStack drop : drops) {
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).isEmpty()) {
                    inventory.set(i, drop.copy());
                    continue drop;
                }
            }
            Vec3d up = pos.toCenterPos();
            world.spawnEntity(new ItemEntity(world, up.x, up.y, up.z, drop.copy()));
        }
    }

    public static void destroy(@NotNull ServerWorld world, BlockPos pos, DefaultedList<ItemStack> inventory) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isOf(ModBlocks.BLACK_BEDROCK) && world.random.nextBoolean()) {
            world.breakBlock(pos, false);
            List<ItemStack> drops = new ArrayList<>();
            drops.add(new ItemStack(ModBlocks.BLACK_BEDROCK));
            insertDrops(world, drops, pos, inventory);
            return;
        }
        if (isNotBreakable(world, pos, blockState)) return;
        List<ItemStack> drops = breakAndDrop(world, pos, blockState);
        insertDrops(world, drops, pos, inventory);
    }
}
