package random_toys.zz_404;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;

public record BlackstoneProcessingTableData(BlockPos pos) implements BlockPosPayload {
    public static final PacketCodec<RegistryByteBuf, BlackstoneProcessingTableData> CODEC=
            PacketCodec.tuple(BlockPos.PACKET_CODEC, BlackstoneProcessingTableData::pos, BlackstoneProcessingTableData::new);
}