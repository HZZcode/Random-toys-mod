package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DestroyerBlockEntity extends LootableContainerBlockEntity implements TransferableBlockEntity {
    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private int cooldown = 0;
    private static final int MaxCooldown = 20;

    public DestroyerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public DestroyerBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.DESTROYER, blockPos, blockState);
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
        return Text.translatable("container.random-toys.destroyer");
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
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
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
            world.setBlockState(pos, state.with(TransferringBlock.POWERED,
                    world.getReceivedRedstonePower(pos) != 0));
            if (world.getReceivedRedstonePower(pos) == 0) return;
            if (cooldown > 0){
                cooldown--;
                return;
            }
            BlockState blockState = world.getBlockState(pos.up());
            if (blockState == null || blockState.isAir() || blockState.getHardness(world, pos.up()) < 0) return;
            ItemStack itemStack = new ItemStack(Items.DIAMOND_AXE);
            EnchantmentHelper.applyEnchantmentProvider(itemStack, world.getRegistryManager(),
                    EnchantmentProviders.ENDERMAN_LOOT_DROP, world.getLocalDifficulty(pos), world.random);
            List<ItemStack> drops = blockState.getDroppedStacks(new LootContextParameterSet.Builder(server)
                    .add(LootContextParameters.ORIGIN, Vec3d.of(pos))
                    .add(LootContextParameters.TOOL, itemStack));
            world.breakBlock(pos.up(), false);
            cooldown = MaxCooldown;
            drop:
            for (ItemStack drop : drops) {
                for (int i = 0; i < size(); i++) {
                    if (inventory.get(i).isEmpty()) {
                        inventory.set(i, drop.copy());
                        continue drop;
                    }
                }
                Vec3d up = pos.up().toCenterPos();
                world.spawnEntity(new ItemEntity(world, up.x, up.y, up.z, drop.copy()));
            }
            mergeStacks();
        }
    }
}