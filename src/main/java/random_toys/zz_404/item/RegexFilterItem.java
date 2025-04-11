package random_toys.zz_404.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexFilterItem extends Item {
    public RegexFilterItem(Settings settings) {
        super(settings);
    }

    private static String getRegex(@NotNull ItemStack stack) {
        return stack.getOrDefault(DataComponentTypes.CUSTOM_NAME,
                stack.getOrDefault(DataComponentTypes.ITEM_NAME, Text.of(".*"))).getString();
    }

    private static String getItemName(@NotNull ItemStack stack) {
        return stack.getOrDefault(DataComponentTypes.CUSTOM_NAME,
                stack.getOrDefault(DataComponentTypes.ITEM_NAME, Text.of(getItemOriginalName(stack))))
                .getString();
    }

    private static String getItemOriginalName(@NotNull ItemStack stack) {
        Optional<RegistryKey<Item>> key = stack.getRegistryEntry().getKey();
        if (key.isEmpty()) return "";
        return key.get().getValue().getPath();
    }

    public static boolean match(@NotNull ItemStack stack, @NotNull ItemStack match) {
        return match(stack, getItemName(match));
    }

    public static boolean match(@NotNull ItemStack stack, String name) {
        try {
            Pattern pattern = Pattern.compile(getRegex(stack));
            Matcher matcher = pattern.matcher(name);
            return matcher.matches();
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) return false;
        ItemStack itemStack = slot.getStack();
        RandomToys.msg(player, Text.translatable(match(stack, itemStack)
                ? "message.random-toys.regex_filter.true"
                : "message.random-toys.regex_filter.false",
                getRegex(stack), getItemName(itemStack)));
        return true;
    }
}