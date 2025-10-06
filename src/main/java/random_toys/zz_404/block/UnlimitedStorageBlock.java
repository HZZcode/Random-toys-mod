package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.block.block_entity.UnlimitedStorageBlockEntity;
import random_toys.zz_404.registry.ModBlockEntities;

import java.util.function.Supplier;

public class UnlimitedStorageBlock extends TransferableBlock<UnlimitedStorageBlockEntity> {
    public static final MapCodec<UnlimitedStorageBlock> CODEC = createCodec(settings -> new UnlimitedStorageBlock(settings, () -> ModBlockEntities.UNLIMITED_STORAGE));

    @Override
    protected MapCodec<? extends TransferableBlock<UnlimitedStorageBlockEntity>> getCodec() {
        return CODEC;
    }

    public UnlimitedStorageBlock(Settings settings, Supplier<BlockEntityType<? extends UnlimitedStorageBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new UnlimitedStorageBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, @NotNull PlayerEntity player, BlockHitResult hit) {
        if (super.onUse(state, world, pos, player, hit) == ActionResult.SUCCESS) return ActionResult.SUCCESS;
        ItemStack stack = player.getMainHandStack();
        if (!world.isClient && world.getBlockEntity(pos) instanceof UnlimitedStorageBlockEntity entity) {
            boolean sneaking = player.isSneaking();
            if (sneaking && stack.isOf(Items.NETHERITE_PICKAXE)
                    && player.getOffHandStack().isOf(Items.NETHERITE_PICKAXE)) {
                world.breakBlock(pos, true, player);
                return ActionResult.CONSUME;
            }
            if (stack.isEmpty()) {
                if (sneaking) {
                    ItemStack offHandStack = player.getOffHandStack();
                    if (offHandStack.isEmpty()) RandomToys.msg(player, entity.getInfo());
                    else RandomToys.msg(player, entity.getInfo(offHandStack.getItem()));
                } else player.giveItemStack(entity.takeRandomStack());
            } else {
                if (sneaking) {
                    player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    entity.stacks.addStack(stack);
                } else {
                    Item item = stack.getItem();
                    int count = stack.getCount();
                    int max = item.getMaxCount();
                    if (count == max) player.giveItemStack(entity.takeStack(item));
                    else player.giveItemStack(entity.takeStack(item, max - count));
                }
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    protected void onStateReplaced(@NotNull BlockState state, World world, BlockPos pos, @NotNull BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && !world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof UnlimitedStorageBlockEntity entity)
                for (ItemStack stack : entity.stacks.intoStacks())
                    Block.dropStack(world, pos, stack);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
