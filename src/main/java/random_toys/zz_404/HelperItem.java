package random_toys.zz_404;

import net.minecraft.block.Block;
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
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class HelperItem extends Item {
    public HelperItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        Text name = block.getName();
        if (context.getWorld().isClient && name.getContent() instanceof TranslatableTextContent translatable) {
            String key = translatable.getKey();
            String[] parts = key.split("\\.");
            showTooltip(parts, context.getPlayer());
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) return false;
        ItemStack itemStack = slot.getStack();
        Text name = itemStack.getItem().getName();
        if (!(name.getContent() instanceof TranslatableTextContent translatable)) return false;
        String key = translatable.getKey();
        String[] parts = key.split("\\.");
        showTooltip(parts, player);
        return true;
    }

    private void showTooltip(String @NotNull [] parts, PlayerEntity player) {
        if (parts.length > 0 && (Objects.equals(parts[0], "item") || Objects.equals(parts[0], "block")))
            parts[0] = "tooltip";
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
}
