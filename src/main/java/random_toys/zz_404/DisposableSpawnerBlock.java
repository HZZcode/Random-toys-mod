package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DisposableSpawnerBlock extends BlockWithEntity {
    public static final MapCodec<DisposableSpawnerBlock> CODEC = createCodec(DisposableSpawnerBlock::new);

    public MapCodec<DisposableSpawnerBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public DisposableSpawnerBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DisposableSpawnerBlockEntity(pos, state);
    }

    public static void spawn(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof DisposableSpawnerBlockEntity)) return;
        EntityType<?> entityType = ((DisposableSpawnerBlockEntity) blockEntity).getEntityType();
        if (world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient && entityType != null) {
            spawnEntity(entityType, (ServerWorld) world, pos);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        spawn(world, pos);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        spawn(world, pos);
    }

    private static void spawnEntity(EntityType<?> entityType, ServerWorld world, BlockPos pos) {
        entityType.spawn(world, pos, SpawnReason.SPAWNER);
        world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.DISPOSABLE_SPAWNER,
                (world, pos, state, blockEntity) -> blockEntity.tick(world, pos, state));
    }
}
