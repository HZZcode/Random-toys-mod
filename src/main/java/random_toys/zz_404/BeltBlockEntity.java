package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BeltBlockEntity extends BlockEntity {
    public BeltBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BeltBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BELT, pos, state);
    }

    public void tick(@NotNull World world, @NotNull BlockPos pos, BlockState state) {
    }
}
