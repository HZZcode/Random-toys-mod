package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class RandomizerBlockItem extends BlockItem {
    public RandomizerBlockItem(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        if(!Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.random-toys.randomizer_block"));
        }
        else {
            tooltip.add(Text.translatable("tooltip_shift_1.random-toys.randomizer_block"));
            tooltip.add(Text.translatable("tooltip_shift_2.random-toys.randomizer_block"));
            tooltip.add(Text.translatable("tooltip_shift_3.random-toys.randomizer_block"));
            tooltip.add(Text.translatable("tooltip_shift_4.random-toys.randomizer_block"));
        }
    }
}
