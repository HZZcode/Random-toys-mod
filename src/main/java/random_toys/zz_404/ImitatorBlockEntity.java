package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ImitatorBlockEntity extends BlockEntity {
    public Block block;
    private final String noBlock = "none";

    public ImitatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ImitatorBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.IMITATOR, pos, state);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putString("block", block == null ? noBlock : Registries.BLOCK.getEntry(block).getIdAsString());
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (nbt.contains("block")) {
            String name = nbt.getString("block");
            if (Objects.equals(name, noBlock)) block = null;
            else block = Registries.BLOCK.get(Identifier.of(name));
        }
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = new NbtCompound();
        writeNbt(nbtCompound, registryLookup);
        return nbtCompound;
    }

    public void updateListeners() {
        markDirty();
        if (world != null) {
            if (world instanceof ServerWorld server)
                server.getChunkManager().markForUpdate(pos);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(getCachedState()));
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL_AND_REDRAW);
        }
    }

    public void tick(@NotNull World world, BlockPos pos, @NotNull BlockState state) {
        world.setBlockState(pos, state.with(ImitatorBlock.HAS_BLOCK, block != null));
    }
}
