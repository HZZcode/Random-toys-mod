package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.block.PerimeterDestroyerBlock;
import random_toys.zz_404.registry.ModBlockEntities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerimeterDestroyerBlockEntity extends ChunkDestroyerBlockEntity {
    @Nullable
    private SubChunkPos subChunkPos = null;

    public PerimeterDestroyerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        speed = 16;
    }

    public PerimeterDestroyerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.PERIMETER_DESTROYER, blockPos, blockState);
    }

    @Override
    public Text getContainerName() {
        return Text.translatable("container.random-toys.perimeter_destroyer");
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("SubChunk"))
            subChunkPos = SubChunkPos.fromArray(nbt.getIntArray("SubChunk"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (subChunkPos != null)
            nbt.putIntArray("SubChunk", subChunkPos.toArray());
    }

    public void tick(@NotNull World world, BlockPos pos, BlockState state) {
        if (world.isClient || !state.get(PerimeterDestroyerBlock.POWERED)) return;
        if (nears().stream().allMatch(blockPos -> DestroyerHelper.isNotBreakable(world, blockPos))) {
            var nearSubChunks = nearSubChunks(world, pos);
            subChunkPos = nearSubChunks.get(world.random.nextInt(nearSubChunks.size()));
            return;
        }
        super.tick(world, pos, state);
    }

    private @NotNull List<SubChunkPos> nearSubChunks(@NotNull World world, @NotNull BlockPos pos) {
        List<SubChunkPos> list = new ArrayList<>();
        var dim = world.getDimension();
        int x0 = pos.getX() >> 4, z0 = pos.getZ() >> 4;
        for (int x = x0 - 8; x <= x0 + 8; x++)
            for (int y = dim.minY() >> 4; y < (dim.minY() + dim.height()) >> 4; y++)
                for (int z = z0 - 8; z <= z0 + 8; z++)
                    list.add(new SubChunkPos(x, y, z));
        return list;
    }

    @Override
    protected @NotNull List<BlockPos> nears(@NotNull BlockPos pos) {
        return nears();
    }

    protected @NotNull List<BlockPos> nears() {
        if (subChunkPos == null) return Collections.emptyList();
        List<BlockPos> nears = new ArrayList<>();
        for (int x = 16 * subChunkPos.x; x < 16 * (subChunkPos.x + 1); x++)
            for (int y = 16 * subChunkPos.y; y < 16 * (subChunkPos.y + 1); y++)
                for (int z = 16 * subChunkPos.z; z < 16 * (subChunkPos.z + 1); z++)
                    nears.add(new BlockPos(x, y, z));
        return nears;
    }

    private record SubChunkPos(int x, int y, int z) {
        @Contract(value = " -> new", pure = true)
        public int @NotNull [] toArray() {
            return new int[]{x, y, z};
        }

        @Contract("_ -> new")
        public static @NotNull SubChunkPos fromArray(int @NotNull [] array) {
            return new SubChunkPos(array[0], array[1], array[2]);
        }
    }
}