package random_toys.zz_404;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;

public class ModKeyBindings {
    public static final StickyKeyBinding JETPACK_ACTIVATE = registerSticky("key.jetpacks.activate", InputUtil.GLFW_KEY_G, KeyBinding.GAMEPLAY_CATEGORY);

    public static KeyBinding register(String translationKey, int code, String category) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(translationKey, code, category));
    }

    public static StickyKeyBinding registerSticky(String translationKey, int code, String category) {
        return (StickyKeyBinding) KeyBindingHelper.registerKeyBinding(new StickyKeyBinding(translationKey, code, category, () -> true));
    }

    public static void registerKeyBindings() {
        RandomToys.log("Registering Key Bindings");
    }
}
