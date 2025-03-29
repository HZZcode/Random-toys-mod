package random_toys.zz_404;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DisenchantmentBlockEntity extends LootableContainerBlockEntity implements TransferableBlockEntity {
    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public DisenchantmentBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public DisenchantmentBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.DISENCHANTMENTOR, blockPos, blockState);
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
        return Text.translatable("container.random-toys.disenchantmentor");
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
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null) return;
        if (world instanceof ServerWorld server) {
            world.setBlockState(pos, state.with(TransferringBlock.POWERED,
                    world.getReceivedRedstonePower(pos) != 0));
            if (world.getReceivedRedstonePower(pos) != 0) return;
            for (int i = 0; i < size(); i++) {
                ItemStack item = inventory.get(i);
                ExperienceOrbEntity.spawn(server, Vec3d.ofCenter(pos), getExperience(item));
                var itemEnchantmentsComponent = EnchantmentHelper.apply(item,
                        components -> components.remove(enchantment
                                -> !enchantment.isIn(EnchantmentTags.CURSE)));
                if (item.isOf(Items.ENCHANTED_BOOK) && itemEnchantmentsComponent.isEmpty())
                    item = item.withItem(Items.BOOK);
                inventory.set(i, item);
            }
            mergeStacks();
        }
    }

    private int getExperience(ItemStack stack) {
        int i = 0;
        ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(stack);
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
            RegistryEntry<Enchantment> registryEntry = entry.getKey();
            int j = entry.getIntValue();
            if (!registryEntry.isIn(EnchantmentTags.CURSE)) {
                i += registryEntry.value().getMinPower(j);
            }
        }
        return i;
    }
}