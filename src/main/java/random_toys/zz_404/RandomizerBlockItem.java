package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.HashMap;

public class RandomizerBlockItem extends BlockItemWithTooltips {
    private static final HashMap<Text, TooltipShowType> tooltips;

    public RandomizerBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    protected HashMap<Text, TooltipShowType> getTooltips() {
        return tooltips;
    }

    static {
        tooltips = new HashMap<>();
        tooltips.put(Text.translatable("tooltip.random-toys.randomizer_block"), TooltipShowType.NON_SHIFT);
        tooltips.put(Text.translatable("tooltip_shift_1.random-toys.randomizer_block"), TooltipShowType.SHIFT);
        tooltips.put(Text.translatable("tooltip_shift_2.random-toys.randomizer_block"), TooltipShowType.SHIFT);
        tooltips.put(Text.translatable("tooltip_shift_3.random-toys.randomizer_block"), TooltipShowType.SHIFT);
        tooltips.put(Text.translatable("tooltip_shift_4.random-toys.randomizer_block"), TooltipShowType.SHIFT);
    }
}
