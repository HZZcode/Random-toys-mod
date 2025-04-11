package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class CopperedRedstoneBlock extends Block {
    public static final MapCodec<CopperedRedstoneBlock> CODEC = createCodec(CopperedRedstoneBlock::new);

    public MapCodec<CopperedRedstoneBlock> getCodec() {
        return CODEC;
    }

    public CopperedRedstoneBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 8;
    }
}
