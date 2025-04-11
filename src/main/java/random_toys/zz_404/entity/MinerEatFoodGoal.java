package random_toys.zz_404.entity;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class MinerEatFoodGoal extends Goal {
    final MinerEntity miner;

    public MinerEatFoodGoal(MinerEntity miner) {
        this.miner = miner;
    }

    private boolean isHungry() {
        return miner.hungerManager.getFoodLevel() < 10;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (isHungry()) {
            Predicate<ItemStack> isFood = itemStack
                    -> itemStack.getComponents().contains(DataComponentTypes.FOOD);
            miner.neededItems = stack -> true;
//            miner.compressorHelper.neededItems = isFood;
            List<ItemStack> foods = miner.inventory.heldStacks.stream().filter(isFood).toList();
            if (!foods.isEmpty()) {
                ItemStack food = foods.stream().max(Comparator
                        .comparingInt(stack -> Objects.requireNonNull(stack.getComponents()
                                .get(DataComponentTypes.FOOD)).nutrition())).get();
                miner.eatFood(miner.getWorld(), food, food.getComponents().get(DataComponentTypes.FOOD));
            }
        }
        else miner.neededItems = stack -> false;
    }
}
