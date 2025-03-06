package random_toys.zz_404;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EnderLinkerBlock extends AbstractChestBlock<EnderLinkerBlockEntity> {
    public static final MapCodec<EnderLinkerBlock> CODEC = createCodec(settings -> new EnderLinkerBlock(settings, () -> ModBlockEntities.ENDER_LINKER));

    @Override
    protected MapCodec<? extends AbstractChestBlock<EnderLinkerBlockEntity>> getCodec() {
        return CODEC;
    }

    public EnderLinkerBlock(Settings settings, Supplier<BlockEntityType<? extends EnderLinkerBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    @Override
    public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnderLinkerBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient() && !player.getStackInHand(player.getActiveHand())
                .isOf(ModItems.ENDER_LINKER_CONFIGURATOR)) {
            NamedScreenHandlerFactory factory = this.createScreenHandlerFactory(state, world, pos);
            if (factory != null) {
                player.openHandledScreen(factory);
                return ActionResult.SUCCESS;
            }
            RandomToys.msg(player, Text.translatable("message.random-toys.ender_linker"));
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.ENDER_LINKER,
                (world, pos, state, blockEntity) -> blockEntity.nullCheck());
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, @NotNull World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof EnderLinkerBlockEntity linker)
            return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(linker.linked));
        return 0;
    }
}
