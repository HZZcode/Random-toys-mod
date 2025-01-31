package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DisposableSpawnerBlockEntity extends BlockEntity implements Spawner {
    private EntityType<?> entityType;

    public DisposableSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISPOSABLE_SPAWNER, pos, state);
    }

    @Override
    public void setEntityType(EntityType<?> type, Random random) {
        this.entityType = type;
        this.markDirty();
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        DisposableSpawnerBlock.spawn(world, pos);
    }
}
