package random_toys.zz_404;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MinerOreMiningGoal extends Goal {
    public final boolean Xray = true;

    public final MinerEntity miner;
    private BlockPos orePos = null;

    public MinerOreMiningGoal(MinerEntity miner) {
        this.miner = miner;
    }

    private boolean findNearOre() {
        World world = miner.getWorld();
        if (!(world instanceof ServerWorld server)) return false;
        ArrayList<BlockPos> ores = miner.nearBlocks(5).stream()
                .filter(this::isFar)
                .filter(pos -> miner.canSeeOre(pos) || Xray)
                .filter(pos -> server.getBlockState(pos).isIn(ModTags.MINER_ORES))
                .collect(Collectors.toCollection(ArrayList::new));
        if (ores.isEmpty()) return false;
        int minX = ores.stream().map(BlockPos::getX).min(Integer::compareTo).orElseThrow();
        int minY = ores.stream().filter(pos -> pos.getX() == minX)
                .map(BlockPos::getY).min(Integer::compareTo).orElseThrow();
        int minZ = ores.stream().filter(pos -> pos.getX() == minX && pos.getY() == minY)
                .map(BlockPos::getZ).min(Integer::compareTo).orElseThrow();
        orePos = new BlockPos(minX, minY, minZ);
        return true;
    }

    @Override
    public boolean canStart() {
        return findNearOre();
    }

    @Override
    public boolean canStop() {
        return (orePos != null && isFar(orePos)) && !findNearOre();
    }

    public boolean isFar(BlockPos pos) {
        return pos == null || !pos.isWithinDistance(miner.getBlockPos(), 1.5);
    }

    @Override
    public boolean shouldContinue() {
        return orePos.isWithinDistance(miner.getBlockPos(), 20) && canStart();
    }

    @Override
    public void start() {
        EntityNavigation navigator = miner.getNavigation();
        if (navigator.isIdle() && orePos != null)
            navigator.startMovingTo(orePos.getX(), orePos.getY(), orePos.getZ(), 1, 1);
    }

    @Override
    public void stop() {
        miner.mineBlock(orePos);
        orePos = null;
    }

    @Override
    public void tick() {
        if (orePos == null || miner.canMine(orePos)) {
            miner.mineBlock(orePos);
            miner.getNavigation().stop();
        }
        else {
            miner.miningPos = miner.blockingBlock(orePos);
            miner.getLookControl().lookAt(Vec3d.of(orePos));
            miner.getNavigation()
                    .startMovingTo(orePos.getX(), orePos.getY(), orePos.getZ(), 1, 1);
            miner.getNavigation().tick();
        }
    }
}
