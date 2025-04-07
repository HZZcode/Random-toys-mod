package random_toys.zz_404.reflection_utils;

import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ZombieGetSkullUtils {
    public static ItemStack getSkull(ZombieEntity zombie) {
        try {
            Class<?> ZombieEntityClass = ZombieEntity.class;
            Method getSkull = ZombieEntityClass.getDeclaredMethod("getSkull");
            getSkull.setAccessible(true);
            return (ItemStack) getSkull.invoke(zombie);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            return ItemStack.EMPTY;
        }
    }
}
