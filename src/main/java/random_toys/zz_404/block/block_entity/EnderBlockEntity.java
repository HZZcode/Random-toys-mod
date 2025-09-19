package random_toys.zz_404.block.block_entity;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface EnderBlockEntity {
    World getWorld();

    void setWorld(World world);

    BlockPos getLinked();

    void setLinked(BlockPos pos);

    RegistryKey<World> getDimension();

    void setDimension(RegistryKey<World> dimension);


    default boolean nullCheck() {
        World world = getWorld();
        RegistryKey<World> dimension = getDimension();
        if (world instanceof ServerWorld server && dimension != null)
            setWorld(server.getServer().getWorld(dimension));
        if (world == null) return true;
        if (world instanceof ServerWorld server && dimension == null)
            setDimension(server.getRegistryKey());
        return getLinked() == null;
    }

    default void readNbtFrom(@NotNull NbtCompound nbt) {
        if (nbt.contains("Linked", NbtElement.INT_ARRAY_TYPE)) {
            int[] pos = nbt.getIntArray("Linked");
            if (pos.length == 3) setLinked(new BlockPos(pos[0], pos[1], pos[2]));
        }
        if (nbt.contains("Dim", NbtElement.STRING_TYPE)) {
            String dimName = nbt.getString("Dim");
            setDimension(RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimName)));
        }
    }

    default void writeNbtTo(NbtCompound nbt) {
        BlockPos linked = getLinked();
        RegistryKey<World> dimension = getDimension();
        if (linked != null) {
            int[] pos = {linked.getX(), linked.getY(), linked.getZ()};
            nbt.putIntArray("Linked", pos);
        }
        if (dimension != null) {
            nbt.putString("Dim", dimension.getValue().toString());
        }
    }
}
