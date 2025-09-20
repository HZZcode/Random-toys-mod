package random_toys.zz_404.reflection_utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class LitematicaUtils {
    public static void paste() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;
        boolean creative = player.getAbilities().creativeMode;
        player.getAbilities().creativeMode = true;
        try {
            Class<?> DataManagerClass = Class.forName("fi.dy.masa.litematica.data.DataManager");
            Object manager = DataManagerClass.getMethod("getSchematicPlacementManager").invoke(null);
            manager.getClass().getMethod("pasteCurrentPlacementToWorld",
                    MinecraftClient.class).invoke(manager, mc);
//            DataManager.getSchematicPlacementManager().pasteCurrentPlacementToWorld(mc);
        } finally {
            player.getAbilities().creativeMode = creative;
        }
    }

    public static @NotNull List<Material> getMaterials() throws NullPointerException,
            ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> DataManagerClass = Class.forName("fi.dy.masa.litematica.data.DataManager");
        Object manager = DataManagerClass.getMethod("getSchematicPlacementManager").invoke(null);
        Object placement = manager.getClass().getMethod("getSelectedSchematicPlacement").invoke(manager);
        Object materialList = placement.getClass().getMethod("getMaterialList").invoke(placement);
        List<?> materialsAll = (List<?>) materialList.getClass().getMethod("getMaterialsAll").invoke(materialList);
        List<Material> list = new ArrayList<>();
        for (Object entry : materialsAll)
            list.add(new Material(
                    ((ItemStack) entry.getClass().getMethod("getStack").invoke(entry)).getItem(),
                    (int) entry.getClass().getMethod("getCountTotal").invoke(entry)
            ));
        return list;

//        return Objects.requireNonNull(Objects.requireNonNull(DataManager.getSchematicPlacementManager()
//                        .getSelectedSchematicPlacement()).getMaterialList()).getMaterialsAll().stream()
//                .map(entry -> new Material(entry.getStack().getItem(), entry.getCountTotal())).toList();
    }

    public record Material(Item item, int count) {
        @Override
        public @NotNull String toString() {
            return String.format("%s*%d", item.toString(), count);
        }

        @Contract("_ -> new")
        public static @NotNull Material fromString(@NotNull String string) {
            int index = string.indexOf('*');
            return new Material(Registries.ITEM.get(Identifier.of(string.substring(0, index))),
                    Integer.parseInt(string.substring(index + 1)));
        }
    }
}
