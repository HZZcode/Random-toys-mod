package random_toys.zz_404;

import net.fabricmc.api.ModInitializer;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import random_toys.zz_404.misc.DispenserShearsHarvestBehavior;
import random_toys.zz_404.registry.*;

import java.util.Random;

public class RandomToys implements ModInitializer {
	public static final String MOD_ID = "random-toys";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void log(String format, Object... objects) {
		LOGGER.info(String.format("[RandomToys] %s", format), objects);
	}

	public static void error(String format, Object... objects) {
		LOGGER.error(String.format("[RandomToys] %s", format), objects);
	}

	public static void msg(LivingEntity entity, Text message) {
		if (entity != null) entity.sendMessage(message);
		//TODO: change it to showing message on screen (like /title command)
	}

	@Override
	public void onInitialize() {
		ModBlocks.registerBlocks();
		ModEntities.registerEntities();
		ModDataComponents.registerDataComponents();
		ModItems.registerItems();
		ModTags.registerItemTags();
		ModGamerules.registerGamerules();
		ModBlockEntities.registerBlockEntities();
		ModParticles.registerParticles();
		ModScreenHandlers.registerScreenHandlers();
		ModModelLayers.registerModelLayers();
		ModDimensions.registerDimensions();
		ModKeyBindings.registerKeyBindings();
		ModArmorMaterials.registerArmorMaterials();
		ModFeatures.registerFeatures();
		ModModelPredicates.registerModModelPredicates();
		ModCriteria.registerCriteria();
		DispenserBlock.registerBehavior(Items.SHEARS, new DispenserShearsHarvestBehavior());

		log("Hello from RandomToys.ZZ_404!");

		Random random = new Random();
		if (random.nextInt(64 * 64) == 0)
			error("This is a random message from RandomToys! You're lucky today!");
		//TODO: a block to place blocks
		//TODO: a block to show real-world time
		//TODO: block comparator (filter)
		//TODO: some block to print litematica?
		//TODO: a way to create item shadow?
		//TODO: item imitator?
	}
}