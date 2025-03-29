package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class EnderLinkerBlockEntity extends LootableContainerBlockEntity implements TransferableBlockEntity {
    public BlockPos linked;
    public RegistryKey<World> dimension;

    public EnderLinkerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        linked = null;
        dimension = null;
    }

    public EnderLinkerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.ENDER_LINKER, blockPos, blockState);
    }

    @Override
    public @Nullable DefaultedList<ItemStack> getInventory() {
        TransferableBlockEntity entity = getLinked();
        return entity == null ? DefaultedList.ofSize(0) : entity.getInventory();
    }

    @Override
    public void setInventory(DefaultedList<ItemStack> inventory) {
        TransferableBlockEntity entity = getLinked();
        if (entity != null) entity.setInventory(inventory);
    }

    public @Nullable TransferableBlockEntity getLinked() {
        ArrayList<BlockPos> self = new ArrayList<>();
        self.add(pos);
        return getLinked(self);
    }

    private @Nullable TransferableBlockEntity getLinked(ArrayList<BlockPos> found) {
        if (nullCheck()) return null;
        BlockEntity link = Objects.requireNonNull(world).getBlockEntity(linked);
        if (link instanceof EnderLinkerBlockEntity entity) {
            if (found.stream().anyMatch(pos -> pos.equals(linked))) return null;
            found.add(entity.pos);
            return entity.getLinked(found);
        }
        if (link instanceof TransferableBlockEntity entity) return entity;
        return null;
    }

    @Override
    public Text getContainerName() {
        return Text.translatable("container.random-toys.ender_linker",
                getLinked() == null ? "" : getLinked().getContainerName());
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return getInventory();
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        setInventory(inventory);
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        TransferableBlockEntity entity = getLinked();
        return entity == null ? null : entity.createScreenHandler(syncId, playerInventory);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("Linked", NbtElement.INT_ARRAY_TYPE)) {
            int[] pos = nbt.getIntArray("Linked");
            if (pos.length == 3) linked = new BlockPos(pos[0], pos[1], pos[2]);
        }
        if (nbt.contains("Dim", NbtElement.STRING_TYPE)) {
            String dimName = nbt.getString("Dim");
            dimension = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimName));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (linked != null) {
            int[] pos = {linked.getX(), linked.getY(), linked.getZ()};
            nbt.putIntArray("Linked", pos);
        }
        if (dimension != null) {
            nbt.putString("Dim", dimension.getValue().toString());
        }
    }

    public boolean nullCheck() {
        if (world instanceof ServerWorld server && dimension != null)
            world = server.getServer().getWorld(dimension);
        if (world == null) return true;
        if (world instanceof ServerWorld server && dimension == null)
            dimension = server.getRegistryKey();
        return linked == null;
    }

    @Override
    public int size() {
        return getLinked() == null ? 0 : getLinked().size();
    }
}