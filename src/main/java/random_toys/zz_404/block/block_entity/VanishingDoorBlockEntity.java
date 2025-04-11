package random_toys.zz_404.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.block.VanishingDoorBlock;

public class VanishingDoorBlockEntity extends BlockEntity {
    public static final int max = 150;
    private static final int spread = 145;
    public int time = 0; //time before appearing
    private boolean hasSpread = false;

    public VanishingDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public VanishingDoorBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.VANISHING_DOOR, pos, state);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("time", time);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (nbt.contains("time")) time = nbt.getInt("time");
    }

    public void tick(@NotNull World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        world.setBlockState(pos, state.with(VanishingDoorBlock.POWERED, isPowered()));
        world.setBlockState(pos, state.with(VanishingDoorBlock.APPEAR, isClosed()));
        if (isPowered()) openPowered();
        if (!isClosed()) time--;
        if (time < 0) time = 0;
        if (!hasSpread && time == spread) spread();
    }

    public boolean isPowered() {
        return world != null && world.getReceivedRedstonePower(pos) != 0;
    }

    public void open() {
        if (isClosed()){
            openUnchecked();
        }
    }

    public void openPowered() {
        if (time < spread){
            openUnchecked();
        }
    }

    public void openUnchecked() {
        time = max;
        hasSpread = false;
    }

    public boolean isClosed() {
        return time <= 0;
    }

    public void spread() {
        if (world == null) return;
        BlockEntity[] nears = {
                world.getBlockEntity(pos.up()),
                world.getBlockEntity(pos.down()),
                world.getBlockEntity(pos.north()),
                world.getBlockEntity(pos.south()),
                world.getBlockEntity(pos.west()),
                world.getBlockEntity(pos.east()),
        };
        for (BlockEntity entity : nears)
            if (entity instanceof VanishingDoorBlockEntity door)
                door.open();
        hasSpread = true;
    }
}
