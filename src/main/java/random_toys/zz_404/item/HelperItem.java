package random_toys.zz_404.item;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;

import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class HelperItem extends Item {
    private static final HashSet<String> ReplaceableKeyParts = new HashSet<>();

    public HelperItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        if (context.getWorld().isClient) tooltip(block.getName(), context.getPlayer());
        return ActionResult.PASS;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) return false;
        ItemStack itemStack = slot.getStack();
        tooltip(itemStack.getItem().getName(), player);
        return true;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, @NotNull LivingEntity entity, Hand hand) {
        tooltip(entity.getType().getName(), user);
        return ActionResult.PASS;
    }

    private void tooltip(@NotNull Text name, PlayerEntity player) {
        if (name.getContent() instanceof TranslatableTextContent translatable) {
            String key = translatable.getKey();
            String[] parts = key.split("\\.");
            showTooltip(parts, player);
        }
    }

    private void showTooltip(String @NotNull [] parts, PlayerEntity player) {
        if (parts.length > 0 && ReplaceableKeyParts.contains(parts[0])) parts[0] = "tooltip";
        String key = String.join(".", parts);
        List<String> keys = IntStream.range(0, 10).mapToObj(i -> String.format("%d.%s", i, key))
                .filter(k -> Language.getInstance().hasTranslation(k)).toList();
        if (!keys.isEmpty()) {
            RandomToys.msg(player, Text.translatable("message.random-toys.helper"));
            for (String k : keys) RandomToys.msg(player, Text.translatable(k));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, @NotNull List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("0.tooltip.random-toys.helper"));
    }

    static {
        ReplaceableKeyParts.add("item");
        ReplaceableKeyParts.add("block");
        ReplaceableKeyParts.add("entity");
    }
}
