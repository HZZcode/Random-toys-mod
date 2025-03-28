package random_toys.zz_404;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlackBedrockProcessingTableBlockEntity extends BlockEntity {
    public static final ArrayList<Recipe> recipes = new ArrayList<>();

    public BlackBedrockProcessingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlackBedrockProcessingTableBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.BLACK_BEDROCK_PROCESSING_TABLE, pos, state);
    }

    public void tick(@NotNull World world, BlockPos pos, BlockState state) {
        if (!(world instanceof ServerWorld server) || world.getTime() % 40 != 0) return;
        BlockEntity[] nears = {
                world.getBlockEntity(pos.north()),
                world.getBlockEntity(pos.south()),
                world.getBlockEntity(pos.west()),
                world.getBlockEntity(pos.east())
        };
        BlockState[] bottoms = {
                world.getBlockState(pos.north().west().down()),
                world.getBlockState(pos.north().east().down()),
                world.getBlockState(pos.south().west().down()),
                world.getBlockState(pos.south().east().down()),
        };
        BlockState[] tops = {
                world.getBlockState(pos.north().west().up()),
                world.getBlockState(pos.north().east().up()),
                world.getBlockState(pos.south().west().up()),
                world.getBlockState(pos.south().east().up()),
        };
        if (Arrays.stream(nears).allMatch(blockEntity -> blockEntity instanceof CompressorBlockEntity)
                && Arrays.stream(bottoms).allMatch(blockState -> blockState.isOf(ModBlocks.BLACK_BEDROCK))
                && Arrays.stream(tops).allMatch(blockState -> blockState.isOf(ModBlocks.BLACK_BEDROCK))
                && world.getBlockEntity(pos.up()) instanceof CompressorBlockEntity input
                && world.getBlockEntity(pos.down()) instanceof CompressorBlockEntity output
                && world.getBlockEntity(pos.up().up()) instanceof TimerBlockEntity timer
                && state.isOf(ModBlocks.BLACK_BEDROCK_PROCESSING_TABLE)) {
            CompressorBlockEntity[] compressors = Arrays.stream(nears)
                    .map(blockEntity -> (CompressorBlockEntity) blockEntity)
                    .toArray(CompressorBlockEntity[]::new);
            OptionalInt inputSlotOptional = findMinSlot(input, stack
                    -> recipes.stream().anyMatch(recipe -> stack.isOf(recipe.input)));
            OptionalInt outputSlotOptional = findMinSlot(output, ItemStack::isEmpty);
            ArrayList<OptionalInt> ingredientSlotsOptional = Arrays.stream(compressors)
                    .map(compressor -> findMinSlot(compressor, stack
                            -> stack.isOf(ModBlocks.BLACK_BEDROCK.asItem())))
                    .collect(Collectors.toCollection(ArrayList::new));
            if (inputSlotOptional.isEmpty() || outputSlotOptional.isEmpty()
                    || ingredientSlotsOptional.stream().anyMatch(OptionalInt::isEmpty)) return;
            int inputSlot = inputSlotOptional.getAsInt();
            int outputSlot = outputSlotOptional.getAsInt();
            int[] ingredientSlots = ingredientSlotsOptional.stream().mapToInt(OptionalInt::orElseThrow).toArray();
            ItemStack in = input.get(inputSlot);
            ArrayList<ItemStack> ingredients = IntStream.range(0, compressors.length)
                    .mapToObj(i -> compressors[i].get(ingredientSlots[i]))
                    .collect(Collectors.toCollection(ArrayList::new));
            if (in == null || ingredients.stream().anyMatch(Objects::isNull)) return;
            Item outputItem = recipes.stream().filter(recipe -> in.isOf(recipe.input))
                    .map(recipe -> recipe.output).findFirst().orElseThrow();
            ArrayList<Pattern> patterns = getPattern(server);
            if (IntStream.range(0, 5).allMatch(i -> patterns.get(i).size == TimerBlock.getPower(timer.get(i)))) {
                in.decrement(1);
                output.set(outputSlot, new ItemStack(outputItem));
                ingredients.forEach(stack -> stack.decrement(1));
            }
            else {
                if (!(world.getBlockEntity(pos.down().down()) instanceof TimerBlockEntity down)
                        || !down.inventory.stream().allMatch(ItemStack::isEmpty)
                        || timer.inventory.stream().allMatch(ItemStack::isEmpty)) return;
                ArrayList<Pattern> inputs = timer.inventory.stream().map(TimerBlock::getPower)
                        .map(Pattern::getPatternBySize).collect(Collectors.toCollection(ArrayList::new));
                ArrayList<Result> results = getResult(inputs, patterns);
                IntStream.range(0, 5).forEach(i -> down.set(i, results.get(i).item.getDefaultStack()));
            }
        }
    }

    private @NotNull ArrayList<Pattern> getPattern(@NotNull ServerWorld world) {
        ArrayList<Pattern> ans = new ArrayList<>();
        long seed = Math.abs(world.getSeed());
        double d = Math.log(seed);
        for (int i = 0; i < 5; i++) {
            int x = (int) (d * Math.pow(10, i + 1)) % 10;
            ans.add(switch (x) {
                case 0, 1, 2 -> Pattern.PATTERN0;
                case 3, 4, 5 -> Pattern.PATTERN1;
                case 6, 7 -> Pattern.PATTERN2;
                default -> Pattern.PATTERN3;
            });
        }
        return ans;
    }

    private @NotNull ArrayList<Result> getResult(@NotNull ArrayList<Pattern> inputs, ArrayList<Pattern> expected) {
        final int size = 5;
        assert inputs.size() == size && expected.size() == size;
        ArrayList<Result> results = new ArrayList<>(Collections.nCopies(size, null));
        IntStream.range(0, size).filter(i -> inputs.get(i) != null && expected.get(i) != null
                        && inputs.get(i) == expected.get(i))
                .forEach(i -> {
                    inputs.set(i, null);
                    expected.set(i, null);
                    results.set(i, Result.CORRECT);
                });
        for (int i = 0; i < size; i++) {
            Pattern input = inputs.get(i);
            if (input == null) continue;
            int[] sames = IntStream.range(0, size).filter(j -> expected.get(j) == input).toArray();
            if (sames.length == 0) results.set(i, Result.WRONG);
            else {
                results.set(i, Result.PARTIAL);
                inputs.set(i, null);
                results.set(sames[0], null);
            }
        }
        for (int i = 0; i < size; i++) if (results.get(i) == null) results.set(i, Result.WRONG);
        return results;
    }

    public static OptionalInt findMinSlot(@NotNull CompressorBlockEntity compressor, Predicate<ItemStack> predicate) {
        return IntStream.range(0, compressor.size()).filter(i -> predicate.test(compressor.get(i))).min();
    }

    public record Recipe(Item input, Item output) {}

    enum Pattern {
        PATTERN0(TimerBlock.getPower(0)),
        PATTERN1(TimerBlock.getPower(1)),
        PATTERN2(TimerBlock.getPower(16)),
        PATTERN3(TimerBlock.getPower(64));

        final int size;
        Pattern(int size) {
            this.size = size;
        }

        public static @NotNull Pattern getPatternBySize(int size) {
            for (Pattern pattern : Pattern.values()) {
                if (pattern.size == size) {
                    return pattern;
                }
            }
            throw new IllegalArgumentException("No pattern found with size: " + size);
        }
    }

    enum Result {
        CORRECT(Items.GREEN_DYE),
        PARTIAL(Items.YELLOW_DYE),
        WRONG(Items.GRAY_DYE);

        final Item item;
        Result(Item item) {
            this.item = item;
        }
    }

    static {
        recipes.add(new Recipe(Items.NETHERITE_HELMET, ModItems.BLACK_BEDROCK_HELMET));
        recipes.add(new Recipe(Items.NETHERITE_CHESTPLATE, ModItems.BLACK_BEDROCK_CHESTPLATE));
        recipes.add(new Recipe(Items.NETHERITE_LEGGINGS, ModItems.BLACK_BEDROCK_LEGGINGS));
        recipes.add(new Recipe(Items.NETHERITE_BOOTS, ModItems.BLACK_BEDROCK_BOOTS));
    }
}
