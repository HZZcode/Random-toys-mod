package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.block.AbstractDestroyerBlock;
import random_toys.zz_404.block.ChunkDestroyerBlock;
import random_toys.zz_404.registry.ModBlockEntities;

import java.util.*;

public class ChunkDestroyerBlockEntity extends AbstractDestroyerBlockEntity {
    protected int speed = 1;

    public ChunkDestroyerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState, 54, 4);
    }

    public ChunkDestroyerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.CHUNK_DESTROYER, blockPos, blockState);
    }

    @Override
    public Text getContainerName() {
        return Text.translatable("container.random-toys.chunk_destroyer");
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
    }

    public void tick(@NotNull World world, BlockPos pos, BlockState state) {
        if (world.isClient || !state.get(ChunkDestroyerBlock.POWERED)) return;
        tickDestroy(world, state, () -> nears(pos).stream()
                .filter(blockPos -> !(blockPos.equals(pos)
                        || world.getBlockState(blockPos).getBlock() instanceof AbstractDestroyerBlock
                        || DestroyerHelper.isNotBreakable(world, blockPos)))
                .sorted(Comparator.comparingDouble(pos::getSquaredDistance)).limit(speed).toList());

        List<BlockPos> fluids = nears(pos).stream().filter(this::isFluid).toList();
        if (!fluids.isEmpty()) {
            BlockPos fluid = fluids.get(world.random.nextInt(fluids.size()));
            List<BlockPos> fluidPart = connectedFluids(fluid);
            fluidPart.forEach(blockPos -> world.setBlockState(blockPos, Blocks.AIR.getDefaultState()));
        }
    }

    protected @NotNull List<BlockPos> nears(@NotNull BlockPos pos) {
        int x = pos.getX() >> 4, z = pos.getZ() >> 4;
        List<BlockPos> nears = new ArrayList<>();
        for (int i = 16 * x; i < 16 * (x + 1); i++)
            for (int j = 16 * z; j < 16 * (z + 1); j++)
                for (int k = pos.getY() - 32; k <= pos.getY() + 32; k++)
                    nears.add(new BlockPos(i, k, j));
        return nears;
    }

    @SuppressWarnings("deprecation")
    private boolean isFluid(BlockPos blockPos) {
        return world != null && world.getBlockState(blockPos).isLiquid()
                && world.getFluidState(blockPos).isStill();
    }

    private @NotNull List<BlockPos> connectedFluids(@NotNull BlockPos pos) {
        List<BlockPos> fluids = new ArrayList<>();
        findConnectedFluids(pos, fluids, new HashSet<>());
        return fluids;
    }

    private void findConnectedFluids(@NotNull BlockPos pos, @NotNull List<BlockPos> fluids, @NotNull Set<BlockPos> visited) {
        if (visited.size() < 1000 && visited.add(pos) && isFluid(pos)) {
            fluids.add(pos);
            for (Direction direction : Direction.values())
                findConnectedFluids(pos.offset(direction), fluids, visited);
        }
    }
}