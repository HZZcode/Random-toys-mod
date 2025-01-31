package random_toys.zz_404;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class ZZAttackGoal extends MeleeAttackGoal {
    private final ZZEntity zz;
    private final int attackDelay = 10;
    private int ticksUntilNextAttack = attackDelay;
    private boolean shouldCountTillNextAttack = false;
    private boolean isAttacking = false;

    public ZZAttackGoal(ZZEntity zz, double speed, boolean pauseWhenMobIdle) {
        super(zz, speed, pauseWhenMobIdle);
        this.zz = zz;
    }

    @Override
    public void start() {
        if (isAttacking) return;
        super.start();
        ticksUntilNextAttack = attackDelay;
        isAttacking = true;
    }

    @Override
    public void stop() {
        super.stop();
        zz.setAttacking(false);
        isAttacking = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }

    @Override
    protected void attack(LivingEntity target) {
        if (isEnemyWithinDistance(target) && zz.dizzyCooldownTime <= 0) {
            shouldCountTillNextAttack = true;
            if (isTimeToStartAttackAnimation()) {
                zz.setAttacking(true);
            }
            if (isTimeToAttack()) {
                this.mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());
                performAttack(target);
            }
        } else {
            resetAttackCooldown();
            this.shouldCountTillNextAttack = false;
            zz.setAttacking(false);
//            zz.attackAnimationTimeOut = 0;
        }
    }

    private boolean isEnemyWithinDistance(LivingEntity target) {
        return this.zz.distanceTo(target) < 5.0f;
    }

    private boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= this.attackDelay;
    }

    private boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    private float getDamage() {
        return 7.0f;
    }

    private void performAttack(LivingEntity target) {
        this.resetAttackCooldown();
//        this.mob.swingHand(Hand.MAIN_HAND);
        if (this.mob.tryAttack(target))
            target.damage(target.getDamageSources().mobAttack(this.zz), getDamage());
    }

    private void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getTickCount(attackDelay * 2);
    }
}
