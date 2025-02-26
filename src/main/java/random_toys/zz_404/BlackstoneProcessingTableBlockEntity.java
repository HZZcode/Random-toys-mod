package random_toys.zz_404;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlackstoneProcessingTableBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlackstoneProcessingTableData> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;

    public static final ArrayList<Recipe> recipes = getRecipes();

    private static ArrayList<Recipe> getRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe(Items.BLACKSTONE, ModItems.BLACKSTONE_CRYSTAL));
        for (int i = 0; i < 19; i++) recipes.add(new Recipe(Items.GILDED_BLACKSTONE, ModItems.GILDED_BLACKSTONE_CRYSTAL));
        recipes.add(new Recipe(Items.GILDED_BLACKSTONE, ModItems.ENCHANTED_GILDED_BLACKSTONE_CRYSTAL));
        return recipes;
    }

    public BlackstoneProcessingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLACKSTONE_PROCESSING_TABLE, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> BlackstoneProcessingTableBlockEntity.this.progress;
                    case 1 -> BlackstoneProcessingTableBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> BlackstoneProcessingTableBlockEntity.this.progress = value;
                    case 1 -> BlackstoneProcessingTableBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.random-toys.blackstone_processing_table");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BlackstoneProcessingTableScreenHandler(syncId, playerInventory, this.propertyDelegate, this);
    }

    @Override
    public BlackstoneProcessingTableData getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return new BlackstoneProcessingTableData(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, false, registryLookup);
        nbt.putInt("blackstone_processing_table", progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        progress = nbt.getInt("blackstone_processing_table");
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    public void tick(@NotNull World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }
        if (isOutputSlotAvailable()) {
            if (hasRecipe()) {
                increaseCraftProgress();
                markDirty(world, pos, state);

                if (hasCraftingFinished()) {
                    craftItem();
                    resetProgress();
                }
            } else {
                resetProgress();
            }
        } else {
            resetProgress();
            markDirty(world, pos, state);
        }
    }

    private boolean isOutputSlotAvailable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() ||
                this.getStack(OUTPUT_SLOT).getCount() <= this.getMaxCountPerStack();
    }

    private boolean hasRecipe() {
        for (Recipe recipe: recipes) {
            ItemStack result = new ItemStack(recipe.output);
            boolean hasInput = getStack(INPUT_SLOT).getItem() == recipe.input;
            if (hasInput && canInsertAmountIntoOutputSlot(result) && canInsertIntoOutputSlot(result.getItem())) return true;
        }
        return false;
    }

    private void craftItem() {
        ItemStack inputStack = getStack(INPUT_SLOT);
        if (inputStack.isEmpty()) return;
        Item inputItem = inputStack.getItem();
        Item outputItem = randomOutput(inputItem);
        if (outputItem == null) return;
        ItemStack result = new ItemStack(outputItem);
        int outputCount = getStack(OUTPUT_SLOT).getCount() + result.getCount();
        this.setStack(OUTPUT_SLOT, new ItemStack(result.getItem(), outputCount));
        this.removeStack(INPUT_SLOT, 1);
    }

    private @NotNull List<Item> possibleOutputs(Item input) {
        return recipes.stream().filter((recipe) -> recipe.input == input)
                .map((recipe) -> recipe.output).toList();
    }

    private <T> @Nullable T randomElement(@NotNull List<T> list) {
        if (list.isEmpty() || world == null) return null;
        if (list.size() == 1) return list.getFirst();
        return list.get(world.random.nextInt(list.size()));
    }

    private Item randomOutput(Item input) {
        return randomElement(possibleOutputs(input));
    }

    private boolean canInsertIntoOutputSlot(Item item) {
        return this.getStack(OUTPUT_SLOT).isEmpty() ||
                this.getStack(OUTPUT_SLOT).getItem() == item;
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= this.getMaxCountPerStack();
    }

    private void increaseCraftProgress() {
        this.progress++;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void resetProgress() {
        this.progress = 0;
    }

    public static boolean canInput(Item item) {
        for (Recipe recipe: recipes) if (recipe.input == item) return true;
        return false;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return slot == INPUT_SLOT && canInput(stack.getItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return slot == OUTPUT_SLOT;
    }

    public record Recipe(Item input, Item output) {
    }
}
