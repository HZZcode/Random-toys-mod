package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.block.block_entity.PerimeterDestroyerBlockEntity;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.registry.ModBlocks;

import java.util.Set;
import java.util.function.Supplier;

public class PerimeterDestroyerBlock extends AbstractDestroyerBlock<PerimeterDestroyerBlockEntity> {
    public static final MapCodec<PerimeterDestroyerBlock> CODEC = createCodec(settings -> new PerimeterDestroyerBlock(settings, () -> ModBlockEntities.PERIMETER_DESTROYER));

    @Override
    protected MapCodec<? extends AbstractDestroyerBlock<PerimeterDestroyerBlockEntity>> getCodec() {
        return CODEC;
    }

    public PerimeterDestroyerBlock(Settings settings, Supplier<BlockEntityType<? extends PerimeterDestroyerBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PerimeterDestroyerBlockEntity(pos, state);
    }

    protected Set<Item> getActivateItem() {
        return Set.of(Blocks.BEDROCK.asItem(), ModBlocks.BLACK_BEDROCK.asItem());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.PERIMETER_DESTROYER,
                (world, pos, state, blockEntity) -> blockEntity.tick(world, pos, state));
    }
}
