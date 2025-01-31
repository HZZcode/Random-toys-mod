package random_toys.zz_404;

import net.fabricmc.api.ModInitializer;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class RandomToys implements ModInitializer {
	public static final String MOD_ID = "random-toys";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void log(String format, Object... objects) {
		LOGGER.info(String.format("[RandomToys] %s", format), objects);
	}

	public static void error(String format, Object... objects) {
		LOGGER.error(String.format("[RandomToys] %s", format), objects);
	}

	@Override
	public void onInitialize() {
		ModBlocks.registerBlocks();
		ModEntities.registerEntities();
		ModItems.registerItems();
		ModGamerules.registerGamerules();
		ModBlockEntities.registerBlockEntities();
		ModParticles.registerParticles();
		ModScreenHandlers.registerScreenHandlers();
		ModModelLayers.registerModelLayers();
		ModDimensions.registerDimensions();
		DispenserBlock.registerBehavior(Items.SHEARS, new DispenserShearsHarvestBehavior());

		log("Hello from RandomToys.ZZ_404!");

		Random random = new Random();
		if(random.nextInt(64 * 64) == 0)
			error("This is a random message from RandomToys! You're lucky today!");
	}
}