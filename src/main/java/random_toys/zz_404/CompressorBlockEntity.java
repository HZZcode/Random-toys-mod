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

import java.util.HashMap;
import java.util.stream.IntStream;

public class CompressorBlockEntity extends LootableContainerBlockEntity {
    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

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

    private final HashMap<Item, CompressingResult> cache = new HashMap<>();
    private @Nullable CompressingResult getCompressRecipe(Item item) {
        if (world == null || item == Items.AIR) return null;
        if (cache.containsKey(item)) return cache.get(item);
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
                    CompressingResult ans = new CompressingResult(input, output);
                    cache.put(item, ans);
                    return ans;
                }
            }
        }
        return null;
    }

    private @NotNull ItemStack @NotNull [] mergeStack(@NotNull ItemStack stack1, @NotNull ItemStack stack2) {
        ItemStack result1, result2;
        if (stack1.isEmpty() && !stack2.isEmpty()) {
            result1 = stack2.copy();
            result2 = ItemStack.EMPTY;
        }
        else if (stack1.getItem() == stack2.getItem() && stack1.getCount() < stack1.getItem().getMaxCount()) {
            Item item = stack1.getItem();
            int max = item.getMaxCount();
            int count = stack1.getCount() + stack2.getCount();
            if (count <= max){
                result1 = new ItemStack(item, count);
                result2 = ItemStack.EMPTY;
            }
            else {
                result1 = new ItemStack(item, max);
                result2 = new ItemStack(item, count - max);
            }
        }
        else {
            result1 = stack1.copy();
            result2 = stack2.copy();
        }
        ItemStack[] result = new ItemStack[2];
        result[0] = result1;
        result[1] = result2;
        return result;
    }

    private void swapStack(int i, int j) {
        var m = inventory.get(i).copy();
        inventory.set(i, inventory.get(j).copy());
        inventory.set(j, m);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        world.setBlockState(pos, state.with(TransferringBlock.POWERED,
                world.getReceivedRedstonePower(pos) != 0));
        if (world.getReceivedRedstonePower(pos) != 0) return;
        int count = IntStream.range(0, 27).filter(i -> !inventory.get(i).isEmpty()).max().orElse(0) + 1;
        for (int i = 0; i < count; i++) {
            for (int j = i + 1; j < count; j++) {
                ItemStack stack1 = inventory.get(i);
                ItemStack stack2 = inventory.get(j);
                var stacks = mergeStack(stack1, stack2);
                inventory.set(i, stacks[0]);
                inventory.set(j, stacks[1]);
            }
        }
        for (int i = 0; i < count; i++) {
            var recipe = getCompressRecipe(inventory.get(i).getItem());
            if (recipe != null){
                int[] spaces = IntStream.range(0, 27).filter(k -> inventory.get(k).isEmpty()).toArray();
                if (spaces.length != 0){
                    int j = spaces[0];
                    int mul = inventory.get(i).getCount() / recipe.in().getCount();
                    int remaining = inventory.get(i).getCount() % recipe.in().getCount();
                    inventory.set(i, inventory.get(i).copyWithCount(remaining));
                    inventory.set(j, new ItemStack(recipe.out().getItem(), mul * recipe.out().getCount()));
                }
            }
        }
        for (int i = 0; i < count - 1; i++) {
            for (int j = i + 2; j < count; j++) {
                if (inventory.get(i).getItem() == inventory.get(j).getItem()
                        && inventory.get(i).getItem() != inventory.get(i + 1).getItem())
                    swapStack(i + 1, j);
            }
        }
    }

    public boolean transformSingle(Item from, ItemStack to) {
        int[] indexes = IntStream.range(0, 27)
                .filter(k -> !inventory.get(k).isEmpty() && inventory.get(k).getItem() == from).toArray();
        int[] spaces = IntStream.range(0, 27).filter(k -> inventory.get(k).isEmpty()).toArray();
        if (indexes.length != 0) {
            int index = indexes[0];
            if (spaces.length != 0) {
                int space = spaces[0];
                inventory.set(index, inventory.get(index).copyWithCount(inventory.get(index).getCount() - 1));
                inventory.set(space, to);
                return true;
            }
            else if (inventory.get(index).getCount() == 1) {
                inventory.set(index, to);
                return true;
            }
        }
        return false;
    }

    public record CompressingResult(ItemStack in, ItemStack out) { }
}