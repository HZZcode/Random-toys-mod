package random_toys.zz_404.entity.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import random_toys.zz_404.entity.ZZEntity;

public class ZZRandomTeleportGoal extends Goal {
    public final ZZEntity zz;

    public ZZRandomTeleportGoal(ZZEntity zz) {
        this.zz = zz;
    }

    @Override
    public boolean canStart() {
        return this.zz.getTarget() == null;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.zz.teleportCooldownTime = Math.max(this.zz.teleportCooldownTime - 1, 0);
        if (this.zz.teleportCooldownTime == 0 && this.zz.dizzyCooldownTime == 0) {
            Vec3d pos = this.zz.getPos();
            if (this.zz.previousPos != null && pos.distanceTo(this.zz.previousPos) < 2.0f)
                this.zz.randomTeleport();
            this.zz.previousPos = pos;
            this.zz.teleportCooldownTime = 15 + this.zz.getWorld().random.nextInt(10);
        }
    }
}
