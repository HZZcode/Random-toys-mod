package random_toys.zz_404;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ModModelPredicates {
    public static void registerModModelPredicates() {
        ModelPredicateProviderRegistry.register(ModItems.GRAPPLING_HOOK,
                Identifier.of(RandomToys.MOD_ID, "cast"),
                (stack, world, entity, seed) -> {
                    if (entity == null) {
                        return 0.0F;
                    }
                    else {
                        boolean bl = entity.getMainHandStack() == stack;
                        boolean bl2 = entity.getOffHandStack() == stack;
                        if (entity.getMainHandStack().getItem() instanceof GrapplingHookItem) {
                            bl2 = false;
                        }
                        return (bl || bl2) && entity instanceof PlayerEntity
                                && stack.get(ModDataComponents.HOOK_UUID) != null ? 1.0F : 0.0F;
                    }
        });

        RandomToys.log("Registering Model Predicates");
    }
}
