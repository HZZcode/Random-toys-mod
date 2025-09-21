package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.registry.ModBlocks;
import random_toys.zz_404.block.block_entity.ChunkDestroyerBlockEntity;

import java.util.Set;
import java.util.function.Supplier;

public class ChunkDestroyerBlock extends AbstractDestroyerBlock<ChunkDestroyerBlockEntity> {
    public static final MapCodec<ChunkDestroyerBlock> CODEC = createCodec(settings -> new ChunkDestroyerBlock(settings, () -> ModBlockEntities.CHUNK_DESTROYER));

    @Override
    protected MapCodec<? extends AbstractDestroyerBlock<ChunkDestroyerBlockEntity>> getCodec() {
        return CODEC;
    }

    public ChunkDestroyerBlock(Settings settings, Supplier<BlockEntityType<? extends ChunkDestroyerBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChunkDestroyerBlockEntity(pos, state);
    }

    @Override
    protected Set<Item> getActivateItem() {
        return Set.of(Blocks.REDSTONE_BLOCK.asItem(), ModBlocks.COPPERED_REDSTONE_BLOCK.asItem());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.CHUNK_DESTROYER,
                (world, pos, state, blockEntity) -> blockEntity.tick(world, pos, state));
    }
}
