package random_toys.zz_404.entity.goal;

import net.minecraft.entity.ai.goal.Goal;
import random_toys.zz_404.entity.ZZEntity;

public class ZZEscapeFluidGoal extends Goal {
    public final ZZEntity zz;


    public ZZEscapeFluidGoal(ZZEntity zz) {
        this.zz = zz;
    }

    @Override
    public boolean canStart() {
        return this.zz.isInFluid();
    }

    @Override
    public boolean shouldContinue() {
        return this.zz.isInFluid();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (canStart()) this.zz.randomTeleport();
    }
}
