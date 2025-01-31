package random_toys.zz_404;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class ModGamerules {
    static CustomGameRuleCategory ModCategory = new CustomGameRuleCategory(Identifier.of(RandomToys.MOD_ID), Text.translatable("gamerule.category.random-toys"));

    public static final GameRules.Key<GameRules.BooleanRule> DISPENSER_HARVEST_CROPS =
            registerGamerules("dispenserHarvestCrops",
                    GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> DISPENSER_HARVEST_CROP_DAMAGE_TOOL =
            registerGamerules("dispenserHarvestCropDamageTool",
                    GameRuleFactory.createBooleanRule(true));

    public static <T extends GameRules.Rule<T>> GameRules.Key<T> registerGamerules(String name, GameRules.Type<T> gamerulesType) {
        return GameRuleRegistry.register(name, ModCategory, gamerulesType);
    }

    public static void registerModGamerules() {
        RandomToys.LOGGER.info("Registering Gamerules");
    }
}
