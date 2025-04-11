package random_toys.zz_404.misc;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.block.AppleLeavesBlock;
import random_toys.zz_404.registry.ModCriteria;
import random_toys.zz_404.registry.ModGamerules;

import static net.minecraft.block.Block.dropStack;

public class DispenserShearsHarvestBehavior extends ShearsDispenserBehavior {
    private boolean useShears(@NotNull World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof AppleLeavesBlock appleLeavesBlock) {
            appleLeavesBlock.useByDispenser(world, pos);
            return true;
        }
        if (state.getBlock() instanceof SweetBerryBushBlock) {
            int i = state.get(SweetBerryBushBlock.AGE);
            boolean bl = i == 3;
            if (i > 1) {
                int j = 1 + world.random.nextInt(2);
                dropStack(world, pos, new ItemStack(Items.SWEET_BERRIES, j + (bl ? 1 : 0)));
                world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
                BlockState blockState = state.with(SweetBerryBushBlock.AGE, 1);
                world.setBlockState(pos, blockState, 2);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
            }
            //Copied from net.minecraft.block.SweetBerryBushBlock.onUse
            return true;
        }
        if (state.getBlock() instanceof CaveVines) {
            if (state.get(CaveVines.BERRIES)) {
                dropStack(world, pos, new ItemStack(Items.GLOW_BERRIES, 1));
                world.playSound(null, pos, SoundEvents.BLOCK_CAVE_VINES_PICK_BERRIES, SoundCategory.BLOCKS, 1.0f, 0.8F + world.random.nextFloat() * 0.4F);
                BlockState blockState = state.with(CaveVines.BERRIES, false);
                world.setBlockState(pos, blockState, 2);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
            }
            //Copied from net.minecraft.block.CaveVines.pickBerries
            return true;
        }
        if (state.getBlock() instanceof NetherWartBlock) {
            int i = state.get(NetherWartBlock.AGE);
            if (i == 3) {
                int j = 1 + world.random.nextInt(3);
                dropStack(world, pos, new ItemStack(Items.NETHER_WART, j));
                BlockState blockState = state.with(NetherWartBlock.AGE, 0);
                world.setBlockState(pos, blockState, 2);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, @NotNull ItemStack stack) {
        if (stack.isOf(Items.SHEARS)) {
            ServerWorld world = pointer.world();
            BlockPos pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
            if (world.getGameRules().getBoolean(ModGamerules.DISPENSER_HARVEST_CROPS)) {
                if (useShears(world, pos)) {
                    if (world.getGameRules().getBoolean(ModGamerules.DISPENSER_HARVEST_CROP_DAMAGE_TOOL))
                        stack.damage(1, world, null, item -> {});
                    ModCriteria.triggerPlayers(world, pos, 6, ModCriteria.SHEARS_HARVEST::trigger);
                    return stack;
                }
            }
        }
        return super.dispenseSilently(pointer, stack);
    }
}