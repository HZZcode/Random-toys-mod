package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.stream.IntStream;

public class CompressorBlockEntity extends LootableContainerBlockEntity {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public CompressorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(ModBlockEntities.COMPRESSOR, blockPos, blockState);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.random-toys.compressor");
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
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return 27;
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

    private @Nullable CompressingResult getCompressRecipe(Item item) {
        if (world == null || item == Items.AIR) return null;
        var recipes = world.getRecipeManager().listAllOfType(RecipeType.CRAFTING);
        for (RecipeEntry<CraftingRecipe> recipe: recipes) {
            var ingredients = recipe.value().getIngredients();
            int inputCount = (int) ingredients.stream()
                    .filter(ingredient -> !ingredient.isEmpty()).count();
            boolean isCompressing = ingredients.stream()
                    .filter(ingredient -> !ingredient.isEmpty())
                    .allMatch(ingredient -> ingredient.test(new ItemStack(item, 1)))
                    && inputCount >= 4;
            if (isCompressing) {
                ItemStack output = recipe.value().getResult(world.getRegistryManager());
                Item result = output.getItem();
                boolean reversible = recipes.stream()
                        .anyMatch(r ->
                                r.value().getResult(world.getRegistryManager()).getItem() == item
                                        && r.value().getIngredients().stream()
                                        .filter(ingredient -> !ingredient.isEmpty()).count() == 1
                                        && r.value().getIngredients().stream()
                                        .filter(ingredient -> !ingredient.isEmpty()).toList()
                                        .getFirst().test(new ItemStack(result)));
                if (reversible) {
                    ItemStack input = new ItemStack(item, inputCount);
                    return new CompressingResult(input, output);
                }
            }
        }
        return null;
    }

    private @NotNull ItemStack @NotNull [] mergeStack(@NotNull ItemStack stack1, @NotNull ItemStack stack2) {
        ItemStack result1 = stack1.copy(), result2 = stack2.copy();
        if (result1.isEmpty() && !result2.isEmpty()) {
            result1 = result2.copy();
            result2.setCount(0);
        }
        if (result1.getItem() == result2.getItem()) {
            Item item = result1.getItem();
            int max = item.getMaxCount();
            int count = result1.getCount() + result2.getCount();
            if (count <= max){
                result1.setCount(count);
                result2.setCount(0);
            }
            else {
                result1.setCount(max);
                result2.setCount(count - max);
            }
        }
        ItemStack[] result = new ItemStack[2];
        result[0] = result1;
        result[1] = result2;
        return result;
    }

    private void swapStack(int i, int j) {
        var m = inventory.get(i);
        inventory.set(i, inventory.get(j));
        inventory.set(j, m);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null) return;
        if (world.getReceivedRedstonePower(pos) != 0) return;
        for (int i = 0; i < 27; i++) {
            for (int j = i + 1; j < 27; j++) {
                ItemStack stack1 = inventory.get(i);
                ItemStack stack2 = inventory.get(j);
                var stacks = mergeStack(stack1, stack2);
                inventory.set(i, stacks[0]);
                inventory.set(j, stacks[1]);
            }
        }
        for (int i = 0; i < 27; i++) {
            var recipe = getCompressRecipe(inventory.get(i).getItem());
            if (recipe != null){
                int[] empties = IntStream.range(0, 27).filter(k -> inventory.get(k).isEmpty()).toArray();
                if (empties.length != 0){
                    int j = empties[0];
                    int mul = inventory.get(i).getCount() / recipe.in().getCount();
                    int remaining = inventory.get(i).getCount() % recipe.in().getCount();
                    inventory.set(i, inventory.get(i).copyWithCount(remaining));
                    inventory.set(j, new ItemStack(recipe.out().getItem(), mul * recipe.out().getCount()));
                }
            }
        }
        for (int i = 0; i < 26; i++) {
            for (int j = i + 2; j < 27; j++) {
                if (inventory.get(i).getItem() == inventory.get(j).getItem()
                        && inventory.get(i).getItem() != inventory.get(i + 1).getItem())
                    swapStack(i + 1, j);
            }
        }
    }
}

record CompressingResult(ItemStack in, ItemStack out) { }