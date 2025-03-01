package random_toys.zz_404;

import net.minecraft.block.Block;
import net.minecraft.text.Text;

import java.util.HashMap;

public class CopperedRedstoneItem extends BlockItemWithTooltips {
    private static final HashMap<Text, TooltipShowType> tooltips;

    public CopperedRedstoneItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    protected HashMap<Text, TooltipShowType> getTooltips() {
        return tooltips;
    }

    static {
        tooltips = new HashMap<>();
        tooltips.put(Text.translatable("tooltip.random-toys.coppered_redstone"), TooltipShowType.ALWAYS);
    }
}
