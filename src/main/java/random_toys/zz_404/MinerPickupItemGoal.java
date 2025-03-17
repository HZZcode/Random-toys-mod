package random_toys.zz_404;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

class MinerPickupItemGoal extends Goal {
    private final MinerEntity miner;

    public MinerPickupItemGoal(MinerEntity miner) {
        this.miner = miner;
        setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        List<ItemEntity> list = miner.getWorld().getEntitiesByClass(ItemEntity.class,
                miner.getBoundingBox().expand(8.0, 8.0, 8.0), MinerEntity.PICKABLE_DROP_FILTER);
        return !list.isEmpty() && miner.inventory.isEmpty();
    }

    @Override
    public void tick() {
        List<ItemEntity> list = miner.getWorld().getEntitiesByClass(ItemEntity.class,
                miner.getBoundingBox().expand(8.0, 8.0, 8.0), MinerEntity.PICKABLE_DROP_FILTER);
        if (miner.inventory.isEmpty() && !list.isEmpty()) {
            miner.getNavigation().startMovingTo(list.getFirst(), 1.2F);
        }
    }

    @Override
    public void start() {
        List<ItemEntity> list = miner.getWorld()
                .getEntitiesByClass(ItemEntity.class, miner.getBoundingBox().expand(8.0, 8.0, 8.0), MinerEntity.PICKABLE_DROP_FILTER);
        if (!list.isEmpty()) {
            miner.getNavigation().startMovingTo(list.getFirst(), 1.2F);
        }
    }
}
