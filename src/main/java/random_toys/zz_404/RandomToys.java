package random_toys.zz_404;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class RandomToys implements ModInitializer {
	public static final String MOD_ID = "random-toys";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModEntities.registerModEntities();
		ModItems.registerModItems();
		ModGamerules.registerModGamerules();
		ModBlockEntities.registerBlockEntities();
		ModParticles.registerModParticles();
		ModScreenHandlers.registerScreenHandlers();
		ModModelLayers.registerModelLayers();
		DispenserBlock.registerBehavior(Items.SHEARS, new DispenserShearsHarvestBehavior());

		LOGGER.info("Hello from RandomToys.ZZ_404!");

		Random random = new Random();
		if(random.nextInt(64 * 64) == 0)
			LOGGER.error("This is a random message from RandomToys! You're lucky today!");
	}
}