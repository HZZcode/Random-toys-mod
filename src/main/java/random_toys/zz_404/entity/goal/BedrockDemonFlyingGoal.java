package random_toys.zz_404.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.entity.BedrockDemonEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BedrockDemonFlyingGoal extends Goal {
    public final BedrockDemonEntity demon;

    public BedrockDemonFlyingGoal(BedrockDemonEntity demon) {
        this.demon = demon;
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
        World world = demon.getWorld();
        LivingEntity target = demon.getTarget();
        if (target != null) demon.setYaw(demon.getYaw() + 18);
        if (target == null || !target.isAlive() || target.getPos().subtract(demon.getPos()).length() <= 15)
            if (demon.getRandom().nextInt(15) == 0)
                turnVelocity(demon.getRandomVec().normalize().multiply(0.5));
        else if (target != null)
            turnVelocity(target.getPos().subtract(demon.getPos()).add(demon.getRandomVec().multiply(10))
                    .normalize().multiply(0.3));

        List<ProjectileEntity> projectiles = world.getEntitiesByClass(ProjectileEntity.class,
                demon.getBoundingBox().expand(15),
                projectile -> projectile.getOwner() != demon);
        Optional<ProjectileEntity> danger = projectiles.stream().max(Comparator.comparingDouble(this::calcDanger));
        danger.ifPresent(projectile -> turnVelocity(projectile.getPos().subtract(demon.getPos())
                .add(demon.getRandomVec().multiply(10)).normalize().multiply(-1.2)));
    }

    private void turnVelocity(@NotNull Vec3d velocity) {
        demon.addVelocity(velocity.subtract(demon.getVelocity()).multiply(0.25));
    }

    private double calcDanger(@NotNull ProjectileEntity projectile) {
        Vec3d pos = projectile.getPos();
        Vec3d direction = demon.getPos().subtract(pos);
        Vec3d velocity = projectile.getVelocity();
        double theta = Math.abs(Math.acos(direction.dotProduct(velocity)
                / (direction.length() * velocity.length())));
        if (Double.isNaN(theta)) return 0;
        double distance = direction.length() * Math.sin(theta);
        return distance == 0 ? 1000 : 1 / distance;
    }
}
