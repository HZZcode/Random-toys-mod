package random_toys.zz_404.reflection_utils;

import net.minecraft.entity.projectile.FireballEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class FireballEntityExplosionPowerUtils {
    public static void trySetExplosionPower(FireballEntity fireball, int explosionPower) {
        try {
            setExplosionPower(fireball, explosionPower);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
    }

    public static void setExplosionPower(FireballEntity fireball, int explosionPower)
            throws NoSuchFieldException, IllegalAccessException {
        Class<?> FireballEntityClass = FireballEntity.class;
        Field explosionPowerField = FireballEntityClass.getDeclaredField("explosionPower");
        explosionPowerField.setAccessible(true);
        explosionPowerField.set(fireball, explosionPower);
    }
}
