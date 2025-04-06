package random_toys.zz_404.reflection_utils;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpikySpikesEnchantmentsUtils {
    public static @Nullable ItemEnchantmentsComponent getSpikeEnchantments(@NotNull DamageSource source) {
        try {
            Class<?> SourceClass = source.getClass();
            if (SourceClass.getName().equals("fuzs.spikyspikes.world.damagesource.SpikeDamageSource")) {
                Method method = SourceClass.getMethod("getItemEnchantments");
                return (ItemEnchantmentsComponent) method.invoke(source);
            }
            return null;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            return null;
        }
    }
}
