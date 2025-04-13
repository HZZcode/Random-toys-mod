package random_toys.zz_404.entity.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.block.block_entity.CompressorBlockEntity;
import random_toys.zz_404.entity.MinerEntity;
import random_toys.zz_404.registry.ModBlocks;

import java.util.List;

public class MinerInteractWithCompressorGoal extends Goal {
    private final MinerEntity miner;

    public MinerInteractWithCompressorGoal(MinerEntity miner) {
        this.miner = miner;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        World world = miner.getWorld();
        List<BlockPos> compressors = miner.nearBlocks(6).stream()
                .filter(pos -> world.getBlockState(pos).isOf(ModBlocks.COMPRESSOR)).toList();
        for (BlockPos pos : compressors) {
            if (world.getBlockEntity(pos) instanceof CompressorBlockEntity compressor) {
                for (int i = 0; i < compressor.size(); i++) {
                    for (int j = 0; j < miner.inventory.size(); j++) {
                        if (!compressor.inventory.get(i).isEmpty()
                                && miner.inventory.getStack(j).isEmpty()
                                && miner.neededItems.test(compressor.inventory.get(i))) {
                            RandomToys.log("Pull: [{}] -> [{}]", i, j);
                            miner.inventory.setStack(j, compressor.inventory.get(i));
                            compressor.inventory.set(i, ItemStack.EMPTY);
                            return;
                        }
                        if (miner.isTamed() && compressor.inventory.get(i).isEmpty()
                                && !miner.inventory.getStack(j).isEmpty()
                                && !miner.neededItems.test(miner.inventory.getStack(j))) {
                            RandomToys.log("Push: [{}] <- [{}]", i, j);
                            compressor.inventory.set(i, miner.inventory.getStack(j));
                            miner.inventory.setStack(j, ItemStack.EMPTY);
                            return;
                        }
                    }
                }
            }
        }
    }
}
