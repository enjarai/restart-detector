package dev.enjarai.restartdetector;

import dev.enjarai.restartdetector.block.ModBlocks;
import dev.enjarai.restartdetector.command.RestartDetectorCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestartDetector implements ModInitializer {
	public static final String MOD_ID = "restart_detector";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static int ticksToStop = -1;

	@Override
	public void onInitialize() {
		ModBlocks.register();

		ServerTickEvents.END_SERVER_TICK.register(RestartDetector::tick);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			RestartDetectorCommand.register(dispatcher);
		});
	}

	public static void tick(MinecraftServer server) {
		if (ticksToStop >= 0) {
			if (ticksToStop == 0) {
				LOGGER.info("Shutdown countdown complete, stopping the server.");
				server.stop(false);
			}

			ticksToStop--;
		}
	}

	public static void startStopCountdown() {
		ticksToStop = ModConfig.INSTANCE.stopCountdownTicks;
	}

	public static void cancelStopCountdown() {
		ticksToStop = -1;
	}

	public static boolean isServerStopping() {
		return ticksToStop != -1;
	}

	public static int getTicksToStop() {
		return ticksToStop;
	}

	public static Identifier id(String path) {
		/*? >=1.21 {*/
		return Identifier.of(MOD_ID, path);
		/*?} else {*//*
		return new Identifier(MOD_ID, path);
		*//*?}*/
	}
}
