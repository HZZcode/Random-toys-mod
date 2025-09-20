package random_toys.zz_404.block;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.reflection_utils.LitematicaUtils;

import java.util.*;
import java.util.stream.IntStream;

public class LitematicaPrinterBlock extends Block {
    public LitematicaPrinterBlock(Settings settings) {
        super(settings);
    }

    public static void requestPrint(BlockPos pos, PlayerEntity player) {
        try {
            var need = LitematicaUtils.getMaterials();
            if (!need.isEmpty()) ClientPlayNetworking.send(new RequestPrintPayload(pos, need));
        } catch (Exception e) {
            error(player, e);
        }
    }

    public static void checkPrint(@NotNull World world, ServerPlayerEntity player, RequestPrintPayload payload) {
        try {
            var pos = payload.pos();
            var need = payload.need();
            var remaining = getRemainingMaterials(world, pos, need);
            if (remaining.isEmpty()) {
                consumeMaterials(world, pos, need);
                ServerPlayNetworking.send(player, new PerformPrintPayload());
            }
            else RandomToys.msg(player, Text.translatable("message.random-toys.litematica_printer.remain", remainMessage(remaining)));
        } catch (Exception e) {
            error(player, e);
        }
    }

    public static void print(PlayerEntity player) {
        try {
            LitematicaUtils.paste();
        } catch (Exception e) {
            error(player, e);
        }
    }

    private static void error(PlayerEntity player, Exception exception) {
        RandomToys.error("Cannot print litematica: {}", exception);
        RandomToys.msg(player, Text.translatable("message.random-toys.litematica_printer.fail"));
    }

    private static String remainMessage(@NotNull List<LitematicaUtils.Material> remainings) {
        int max = 3;
        int size = remainings.size();
        boolean bl = size > max;
        if (bl) remainings = remainings.subList(0, max);
        String msg = String.join(", ", remainings.stream().map(material ->
                String.format("%d*%s", material.count(), material.item().getName().getString())).toList());
        if (bl) msg += String.format(", ...(%d)", size - max);
        return msg;
    }

    @Override
    protected ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) requestPrint(pos, player);
        return super.onUse(state, world, pos, player, hit);
    }

    private static @NotNull @Unmodifiable List<ItemStack> getStacks(@NotNull Inventory inventory) {
        return IntStream.range(0, inventory.size()).mapToObj(inventory::getStack)
                .filter(stack -> !stack.isEmpty()).toList();
    }

    private static @NotNull List<Inventory> nearInventories(@NotNull World world, @NotNull BlockPos pos) {
        ArrayList<Inventory> inventories = new ArrayList<>();
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                for (int k = -1; k <= 1; k++)
                    if (world.getBlockEntity(pos.add(i, j, k)) instanceof Inventory inventory)
                        inventories.add(inventory);
        return inventories;
    }

    private static @NotNull @Unmodifiable List<LitematicaUtils.Material> getMaterials(@NotNull World world, @NotNull BlockPos pos) {
        Map<Item, Integer> counts = new HashMap<>();
        for (Inventory inventory : nearInventories(world, pos)) {
            for (ItemStack stack : getStacks(inventory)) {
                Item item = stack.getItem();
                int count = stack.getCount();
                if (counts.containsKey(item)) counts.put(item, counts.get(item) + count);
                else counts.put(item, count);
            }
        }
        return counts.entrySet().stream().map(entry ->
                new LitematicaUtils.Material(entry.getKey(), entry.getValue())).toList();
    }

    private static @NotNull @Unmodifiable List<LitematicaUtils.Material> getRemainingMaterials(@NotNull World world, @NotNull BlockPos pos, @NotNull List<LitematicaUtils.Material> need) {
        var got = getMaterials(world, pos);
        return need.stream().map(material -> {
            Item item = material.item();
            var founds = got.stream().filter(m -> m.item() == item).toList();
            if (founds.isEmpty()) return material;
            return new LitematicaUtils.Material(item, material.count() - founds.getFirst().count());
        }).filter(material -> material.count() > 0).toList();
    }

    private static void consumeMaterials(@NotNull World world, @NotNull BlockPos pos, @NotNull List<LitematicaUtils.Material> need) {
        need = new ArrayList<>(need);
        for (Inventory inventory : nearInventories(world, pos)) {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                for (int j = 0; j < need.size(); j++) {
                    var material = need.get(j);
                    if (material.item() == stack.getItem()) {
                        int count = Math.min(material.count(), stack.getCount());
                        need.set(j, new  LitematicaUtils.Material(material.item(), material.count() - count));
                        inventory.setStack(i, stack.copyWithCount(stack.getCount() - count));
                    }
                }
                need.removeIf(material -> material.count() == 0);
            }
        }
    }

    public record RequestPrintPayload(String data) implements CustomPayload {
        public static final CustomPayload.Id<RequestPrintPayload> ID
                = new CustomPayload.Id<>(Identifier.of(RandomToys.MOD_ID, "request_print_litematica"));
        public static final PacketCodec<RegistryByteBuf, RequestPrintPayload> CODEC
                = PacketCodec.tuple(PacketCodecs.STRING, RequestPrintPayload::data, RequestPrintPayload::new);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }

        public RequestPrintPayload(@NotNull BlockPos pos, @NotNull List<LitematicaUtils.Material> need) {
            this(pos.toShortString() + ";" + String.join(",",
                    need.stream().map(LitematicaUtils.Material::toString).toList()));
        }

        public @NotNull @Unmodifiable List<LitematicaUtils.Material> need() {
            return Arrays.stream(data.substring(data.indexOf(';') + 1).split(","))
                    .map(LitematicaUtils.Material::fromString).toList();
        }

        public @NotNull BlockPos pos() {
            int[] pos = Arrays.stream(data.substring(0, data.indexOf(';'))
                    .split(", ")).mapToInt(Integer::parseInt).toArray();
            return new BlockPos(pos[0], pos[1], pos[2]);
        }
    }

    public record PerformPrintPayload() implements CustomPayload {
        public static final CustomPayload.Id<PerformPrintPayload> ID
                = new CustomPayload.Id<>(Identifier.of(RandomToys.MOD_ID, "perform_print_litematica"));
        public static final PacketCodec<RegistryByteBuf, PerformPrintPayload> CODEC
                = PacketCodec.unit(new PerformPrintPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
} // This block is not ready, so we're not adding its recipe and texture yet
