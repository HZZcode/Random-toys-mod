package random_toys.zz_404.reflection_utils;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.MinerEntity;

import java.lang.reflect.Field;

public class HungerManagerUpdateUtils {
    public static void updateHunger(@NotNull MinerEntity miner) throws NoSuchFieldException, IllegalAccessException {
        Class<?> HungerManagerClass = HungerManager.class;
        Field prevFoodLevel = HungerManagerClass.getDeclaredField("prevFoodLevel");
        prevFoodLevel.setAccessible(true);
        Field foodLevel = HungerManagerClass.getDeclaredField("foodLevel");
        foodLevel.setAccessible(true);
        Field exhaustion = HungerManagerClass.getDeclaredField("exhaustion");
        exhaustion.setAccessible(true);
        Field saturationLevel = HungerManagerClass.getDeclaredField("saturationLevel");
        saturationLevel.setAccessible(true);
        Field foodTickTimer = HungerManagerClass.getDeclaredField("foodTickTimer");
        foodTickTimer.setAccessible(true);

        Difficulty difficulty = miner.getWorld().getDifficulty();
        prevFoodLevel.set(miner.hungerManager, foodLevel.get(miner.hungerManager));
        if ((float) exhaustion.get(miner.hungerManager) > 4.0F) {
            exhaustion.set(miner.hungerManager, exhaustion.getFloat(miner.hungerManager) - 4.0F);
            if (saturationLevel.getFloat(miner.hungerManager) > 0.0F) {
                saturationLevel.set(miner.hungerManager, Math.max(saturationLevel.getFloat(miner.hungerManager) - 1.0F, 0.0F));
            } else if (difficulty != Difficulty.PEACEFUL) {
                foodLevel.set(miner.hungerManager, Math.max(foodLevel.getInt(miner.hungerManager) - 1, 0));
            }
        }
        boolean bl = miner.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
        if (bl && saturationLevel.getFloat(miner.hungerManager) > 0.0F && miner.canFoodHeal() && foodLevel.getInt(miner.hungerManager) >= 20) {
            foodTickTimer.set(miner.hungerManager, foodTickTimer.getInt(miner.hungerManager) + 1);
            if (foodTickTimer.getInt(miner.hungerManager) >= 10) {
                float f = Math.min(saturationLevel.getFloat(miner.hungerManager), 6.0F);
                miner.heal(f / 6.0F);
                miner.hungerManager.addExhaustion(f);
                foodTickTimer.set(miner.hungerManager, 0);
            }
        } else if (bl && foodLevel.getInt(miner.hungerManager) >= 18 && miner.canFoodHeal()) {
            foodTickTimer.set(miner.hungerManager, foodTickTimer.getInt(miner.hungerManager) + 1);
            if (foodTickTimer.getInt(miner.hungerManager) >= 80) {
                miner.heal(1.0F);
                miner.hungerManager.addExhaustion(6.0F);
                foodTickTimer.set(miner.hungerManager, 0);
            }
        } else if (foodLevel.getInt(miner.hungerManager) <= 0) {
            foodTickTimer.set(miner.hungerManager, foodTickTimer.getInt(miner.hungerManager) + 1);
            if (foodTickTimer.getInt(miner.hungerManager) >= 80) {
                if (miner.getHealth() > 10.0F || difficulty == Difficulty.HARD || miner.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    miner.damage(miner.getDamageSources().starve(), 1.0F);
                }
                foodTickTimer.set(miner.hungerManager, 0);
            }
        } else {
            foodTickTimer.set(miner.hungerManager, 0);
        }
    }

    public static boolean tryUpdateHunger(MinerEntity miner) {
        try {
            updateHunger(miner);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException ignored) {
            return false;
        }
    }
}
