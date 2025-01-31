package random_toys.zz_404;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;

public class ZZKeepDistanceGoal extends Goal {
    public final ZZEntity zz;
    private static final double range = 32.0f;

    public ZZKeepDistanceGoal(ZZEntity zz) {
        this.zz = zz;
    }

    @Override
    public boolean canStart() {
        return this.zz.getTarget() != null;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    private void moveForward() {
        World world = this.zz.getWorld();
        if (world.random.nextInt(10) != 0)
            this.zz.getMoveControl().strafeTo(-0.5f, world.random.nextFloat() - 0.5f);
        else
            this.zz.teleportTo(this.zz.getPos().subtract(this.zz.getRotationVector().multiply(world.random.nextDouble() * 5)));
    }

    private void moveBackward() {
        World world = this.zz.getWorld();
        if (world.random.nextInt(10) != 0)
            this.zz.getMoveControl().strafeTo(1.0f, world.random.nextFloat() - 0.5f);
        else
            this.zz.teleportTo(this.zz.getPos().add(this.zz.getRotationVector().multiply(world.random.nextDouble() * 2)));
    }

    private void move() {
        World world = this.zz.getWorld();
        this.zz.getMoveControl().strafeTo(world.random.nextFloat() - 0.5f, world.random.nextFloat() - 0.5f);
    }

    @Override
    public void tick() {
        LivingEntity target = this.zz.getTarget();
        if (target != null && this.zz.canSee(target) && this.zz.dizzyCooldownTime <= 0) {
            double distance = this.zz.distanceTo(target);
            this.zz.getLookControl().lookAt(target, 30.0F, 30.0F);
            if (distance < 0.5 * range) moveBackward();
            else if (distance > 0.75 * range) moveForward();
            else move();
        }
    }
}
