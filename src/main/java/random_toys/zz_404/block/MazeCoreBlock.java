package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.block.block_entity.MazeCoreBlockEntity;

public class MazeCoreBlock extends BlockWithEntity {
    public static final MapCodec<MazeCoreBlock> CODEC = createCodec(MazeCoreBlock::new);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public MazeCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MazeCoreBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.MAZE_CORE,
                (world, pos, state, mazeCoreBlockEntity)
                        -> mazeCoreBlockEntity.tick(world, pos, state));
    }
}
