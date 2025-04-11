package random_toys.zz_404.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;

import java.util.Random;

public class RandomizerItem extends Item {
    final int size;

    public RandomizerItem(Settings settings, int n) {
        super(settings);
        this.size = (int)Math.pow(6, n);
    }

    public RandomizerItem(Settings settings){
        this(settings, 1);
    }

    @Override
    public TypedActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 1;
    }

    public int getRandomNumber(){
        Random random = new Random();
        return random.nextInt(size) + 1;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, @NotNull World world, LivingEntity user) {
        if (world.isClient) return stack;
        RandomToys.msg(user, Text.translatable("message.random-toys.randomizer", getRandomNumber()));
        return super.finishUsing(stack, world, user);
    }
}
