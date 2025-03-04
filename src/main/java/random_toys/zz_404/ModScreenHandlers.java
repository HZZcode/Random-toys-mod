package random_toys.zz_404;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<BlackstoneProcessingTableScreenHandler> BLACKSTONE_PROCESSING_TABLE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(RandomToys.MOD_ID, "polishing_machine"),
                    new ExtendedScreenHandlerType<>(BlackstoneProcessingTableScreenHandler::new, BlackstoneProcessingTableData.CODEC));

    public static void registerScreenHandlers() {
        HandledScreens.register(ModScreenHandlers.BLACKSTONE_PROCESSING_TABLE_SCREEN_HANDLER, BlackstoneProcessingTableScreen::new);
    }
}