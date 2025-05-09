package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChunkDestroyerBlockEntity extends LootableContainerBlockEntity implements TransferableBlockEntity {
    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
    private int cooldown = 0;
    private static final int MaxCooldown = 4;

    public ChunkDestroyerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public ChunkDestroyerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.CHUNK_DESTROYER, blockPos, blockState);
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public void setInventory(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    public Text getContainerName() {
        return Text.translatable("container.random-toys.chunk_destroyer");
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory, registryLookup);
        }
        if (nbt.contains("Cooldown")) cooldown = nbt.getInt("Cooldown");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }
        nbt.putInt("Cooldown", cooldown);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null) return;
        if (world instanceof ServerWorld server) {
            if (!state.get(ChunkDestroyerBlock.POWERED)) return;
            if (cooldown > 0) {
                cooldown--;
                return;
            }
            Optional<BlockPos> destroyPos = nears(pos).stream()
                    .filter(blockPos -> !blockPos.equals(pos))
                    .filter(blockPos -> !world.getBlockState(blockPos).isOf(ModBlocks.CHUNK_DESTROYER))
                    .filter(blockPos -> !world.getBlockState(blockPos).isOf(ModBlocks.DESTROYER))
                    .filter(blockPos -> !DestroyerHelper.isNotBreakable(world, blockPos))
                    .min(Comparator.comparingDouble(pos::getSquaredDistance));
            destroyPos.ifPresent(blockPos -> DestroyerHelper.destroy(server, blockPos, inventory));
            List<BlockPos> fluids = nears(pos).stream().filter(this::isFluid).toList();
            if (!fluids.isEmpty()) {
                BlockPos fluid = fluids.get(world.random.nextInt(fluids.size()));
                List<BlockPos> fluidPart = connectedFluids(fluid);
                fluidPart.forEach(blockPos -> world.setBlockState(blockPos, Blocks.AIR.getDefaultState()));
            }
            mergeStacks();
            cooldown = MaxCooldown;
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isFluid(BlockPos blockPos) {
        return world != null && world.getBlockState(blockPos).isLiquid()
                && world.getFluidState(blockPos).isStill();
    }

    private @NotNull List<BlockPos> nears(@NotNull BlockPos pos) {
        int x = pos.getX() >> 4, z = pos.getZ() >> 4;
        List<BlockPos> nears = new ArrayList<>();
        for (int i = 16 * x; i < 16 * (x + 1); i++)
            for (int j = 16 * z; j < 16 * (z + 1); j++)
                for (int k = pos.getY() - 32; k <= pos.getY() + 32; k++)
                    nears.add(new BlockPos(i, k, j));
        return nears;
    }

    private @NotNull List<BlockPos> connectedFluids(@NotNull BlockPos pos) {
        List<BlockPos> fluids = new ArrayList<>();
        findConnectedFluids(pos, fluids, new HashSet<>());
        return fluids;
    }

    private void findConnectedFluids(@NotNull BlockPos pos, @NotNull List<BlockPos> fluids, @NotNull Set<BlockPos> visited) {
        if (visited.contains(pos) || visited.size() >= 1000) return;
        visited.add(pos);
        if (isFluid(pos)) {
            fluids.add(pos);
            for (Direction direction : Direction.values())
                findConnectedFluids(pos.offset(direction), fluids, visited);
        }
    }
}