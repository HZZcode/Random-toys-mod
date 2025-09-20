package random_toys.zz_404.registry;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.block.LitematicaPrinterBlock;

public class ModPayloads {
    public static void registerPayloads() {
        RandomToys.log("Registering Payloads");
    }

    static {
        PayloadTypeRegistry.playC2S().register(LitematicaPrinterBlock.RequestPrintPayload.ID,
                LitematicaPrinterBlock.RequestPrintPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LitematicaPrinterBlock.PerformPrintPayload.ID,
                LitematicaPrinterBlock.PerformPrintPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(LitematicaPrinterBlock.RequestPrintPayload.ID,
                ((payload, context) -> LitematicaPrinterBlock
                        .checkPrint(context.player().getWorld(), context.player(), payload)));
        ClientPlayNetworking.registerGlobalReceiver(LitematicaPrinterBlock.PerformPrintPayload.ID,
                ((payload, context) -> LitematicaPrinterBlock.print(context.player())));
    }
}
