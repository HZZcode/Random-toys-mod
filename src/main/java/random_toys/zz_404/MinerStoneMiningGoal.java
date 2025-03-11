package random_toys.zz_404;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class MinerStoneMiningGoal extends Goal {
    public final MinerEntity miner;
    public BlockPos blockPos = null;

    private static final int maxCooldown = 3;
    private static final int minCooldown = 2;
    private int cooldown = 0;

    public MinerStoneMiningGoal(MinerEntity miner) {
        this.miner = miner;
    }

    @Override
    public boolean canStart() {
        return blockPos == null;
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (cooldown > 0){
            cooldown--;
            return;
        }
        else cooldown = miner.getWorld().random.nextBetween(minCooldown, maxCooldown);

        ArrayList<BlockPos> nears = miner.nearBlocks(2);
        BlockPos near = nears.get(miner.getWorld().random.nextInt(nears.size()));
        if (miner.canMine(near) && miner.getWorld().getBlockState(near).isIn(ModTags.MINER_STONES))
            miner.mineBlock(near);

        if (blockPos == null) return;
        if (miner.getWorld().getBlockState(blockPos).isAir()) blockPos = null;
        if (miner.canMine(blockPos)) miner.mineBlock(blockPos);
    }
}
