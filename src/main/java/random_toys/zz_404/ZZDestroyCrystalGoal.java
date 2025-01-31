package random_toys.zz_404;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ZZDestroyCrystalGoal extends Goal {
    public final ZZEntity zz;

    public ZZDestroyCrystalGoal(ZZEntity zz) {
        this.zz = zz;
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
        this.zz.crystalCooldownTime = Math.max(this.zz.crystalCooldownTime - 1, 0);
        if (this.zz.dizzyCooldownTime > 0 || this.zz.crystalCooldownTime > 0) return;
        World world = this.zz.getWorld();
        Box box = this.zz.getBoundingBox().expand(16.0f);
        List<EndCrystalEntity> crystals = world.getEntitiesByClass(EndCrystalEntity.class, box, (Entity) -> true);
        if (crystals.isEmpty()) return;
        EndCrystalEntity crystal = crystals.getFirst();
        Vec3d pos = this.zz.getEyePos();
        Vec3d direction = crystal.getPos().subtract(pos).normalize().multiply(2.0f);
        ThrownBlackstoneEntity entity = new ThrownBlackstoneEntity(ModEntities.THROWN_BLACKSTONE, world);
        entity.setPosition(pos);
        entity.setVelocity(direction);
        entity.setOwner(this.zz);
        world.spawnEntity(entity);
        this.zz.crystalCooldownTime = 15 + world.random.nextInt(10);
    }
}
