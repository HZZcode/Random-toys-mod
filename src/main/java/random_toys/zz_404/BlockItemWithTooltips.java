package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BlockItemWithTooltips extends BlockItem {
    public BlockItemWithTooltips(Block block, Settings settings) {
        super(block, settings);
    }

    protected abstract HashMap<Text, TooltipShowType> getTooltips();

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        getTooltips().entrySet().stream().filter(BlockItemWithTooltips::canShow)
                .forEach(entry -> tooltip.add(entry.getKey()));
    }

    private static boolean canShow(Map.Entry<Text, TooltipShowType> entry) {
        return Screen.hasShiftDown() && entry.getValue() != TooltipShowType.NON_SHIFT
                || !Screen.hasShiftDown() && entry.getValue() != TooltipShowType.SHIFT;
    }

    public enum TooltipShowType {
        ALWAYS, NON_SHIFT, SHIFT
    }
}
