package random_toys.zz_404.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import random_toys.zz_404.registry.ModBlockEntities;
import random_toys.zz_404.registry.ModBlocks;
import random_toys.zz_404.block.block_entity.BlackBedrockProcessingTableBlockEntity;

import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class BlackBedrockProcessingTableBlock extends BlockWithEntity {
    public static final MapCodec<BlackBedrockProcessingTableBlock> CODEC = createCodec(BlackBedrockProcessingTableBlock::new);
    private static final HashSet<ItemStack> materials = new HashSet<>();

    public BlackBedrockProcessingTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlackBedrockProcessingTableBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world_, BlockState state_, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.BLACK_BEDROCK_PROCESSING_TABLE,
                (world, pos, state, blackBedrockProcessingTableBlockEntity)
                        -> blackBedrockProcessingTableBlockEntity.tick(world, pos, state));
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack itemStack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (materials.isEmpty()) {
            materials.add(new ItemStack(ModBlocks.BLACK_BEDROCK, 8));
            materials.add(new ItemStack(ModBlocks.COMPRESSOR, 6));
            materials.add(new ItemStack(ModBlocks.TIMER, 2));
        }
        if (!world.isClient && itemStack.isIn(ItemTags.PICKAXES)) {
            if (player.isCreative() || materials.stream().allMatch(stack
                    -> getItemCount(player, stack.getItem()) >= stack.getCount())) {
                if (!player.isCreative()) materials.forEach(stack -> decreaseItem(player, stack));
                buildStructure(world, pos);
                world.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                world.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.up().getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                world.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.down().getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
                return ItemActionResult.CONSUME;
            }
        }
        return super.onUseWithItem(itemStack, state, world, pos, player, hand, hit);
    }

    private static int getItemCount(PlayerEntity player, Item item) {
        return IntStream.range(0, PlayerInventory.MAIN_SIZE)
                .filter(i -> player.getInventory().main.get(i).isOf(item))
                .map(i -> player.getInventory().main.get(i).getCount()).sum();
    }

    private static void decreaseItem(@NotNull PlayerEntity player, @NotNull ItemStack itemStack) {
        Item item = itemStack.getItem();
        int count = itemStack.getCount();
        var found = player.getInventory().main.stream().filter(stack -> stack.isOf(item)).toList();
        for (ItemStack stack : found) {
            if (stack.getCount() >= count) {
                stack.decrement(count);
                break;
            }
            count -= stack.getCount();
            stack.setCount(0);
        }
    }

    private void destroy(@NotNull ServerWorld world, @NotNull BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        ItemStack itemStack = new ItemStack(Items.DIAMOND_AXE);
        EnchantmentHelper.applyEnchantmentProvider(itemStack, world.getRegistryManager(),
                EnchantmentProviders.ENDERMAN_LOOT_DROP, world.getLocalDifficulty(pos), world.random);
        List<ItemStack> drops = blockState.getDroppedStacks(new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.ORIGIN, Vec3d.of(pos))
                .add(LootContextParameters.TOOL, itemStack));
        for (ItemStack drop : drops) {
            Vec3d up = pos.toCenterPos();
            world.spawnEntity(new ItemEntity(world, up.x, up.y, up.z, drop.copy()));
        }
    }
    
    private void setBlock(@NotNull ServerWorld world, @NotNull BlockPos pos, BlockState state) {
        destroy(world, pos);
        world.setBlockState(pos, state);
    }

    public void buildStructure(@NotNull World world, @NotNull BlockPos pos) {
        if (world instanceof ServerWorld server) {
            setBlock(server, pos.north().west().up(), ModBlocks.BLACK_BEDROCK.getDefaultState());
            setBlock(server, pos.north().east().up(), ModBlocks.BLACK_BEDROCK.getDefaultState());
            setBlock(server, pos.south().west().up(), ModBlocks.BLACK_BEDROCK.getDefaultState());
            setBlock(server, pos.south().east().up(), ModBlocks.BLACK_BEDROCK.getDefaultState());
            setBlock(server, pos.north().west().down(), ModBlocks.BLACK_BEDROCK.getDefaultState());
            setBlock(server, pos.north().east().down(), ModBlocks.BLACK_BEDROCK.getDefaultState());
            setBlock(server, pos.south().west().down(), ModBlocks.BLACK_BEDROCK.getDefaultState());
            setBlock(server, pos.south().east().down(), ModBlocks.BLACK_BEDROCK.getDefaultState());
            setBlock(server, pos.up(), ModBlocks.COMPRESSOR.getDefaultState());
            setBlock(server, pos.down(), ModBlocks.COMPRESSOR.getDefaultState());
            setBlock(server, pos.north(), ModBlocks.COMPRESSOR.getDefaultState());
            setBlock(server, pos.south(), ModBlocks.COMPRESSOR.getDefaultState());
            setBlock(server, pos.west(), ModBlocks.COMPRESSOR.getDefaultState());
            setBlock(server, pos.east(), ModBlocks.COMPRESSOR.getDefaultState());
            setBlock(server, pos.up().up(), ModBlocks.TIMER.getDefaultState());
            setBlock(server, pos.down().down(), ModBlocks.TIMER.getDefaultState());
        }
    }
}