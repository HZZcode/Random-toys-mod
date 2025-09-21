package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.block.DestroyerBlock;

public class DestroyerBlockEntity extends AbstractDestroyerBlockEntity {
    public DestroyerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState, 27, 20);
    }

    public DestroyerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.DESTROYER, blockPos, blockState);
    }

    @Override
    public Text getContainerName() {
        return Text.translatable("container.random-toys.destroyer");
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    public void tick(@NotNull World world, BlockPos pos, BlockState state) {
        if (!world.isClient)
            world.setBlockState(pos, state.with(DestroyerBlock.POWERED,
                    world.getReceivedRedstonePower(pos) != 0));
        tickDestroy(world, state, pos.up());
    }
}