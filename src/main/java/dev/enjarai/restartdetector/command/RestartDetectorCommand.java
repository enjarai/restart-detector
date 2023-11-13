package dev.enjarai.restartdetector.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.enjarai.restartdetector.ModConfig;
import dev.enjarai.restartdetector.RestartDetector;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;


public class RestartDetectorCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("restartdetector")
                .requires(Permissions.require(RestartDetector.MOD_ID + ".command.restartdetector", 4))
                .then(CommandManager.literal("countdown")
                        .requires(Permissions.require(RestartDetector.MOD_ID + ".command.restartdetector.countdown", 4))
                        .then(CommandManager.literal("start")
                                .executes(RestartDetectorCommand::startCountdown)
                        )
                        .then(CommandManager.literal("cancel")
                                .executes(RestartDetectorCommand::cancelCountdown)
                        )
                )
        );
    }

    public static int startCountdown(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(/*?>=1.20 {?*/() -> /*?}?*/Text.translatable("commands.stop.stopping",
                ModConfig.INSTANCE.stopCountdownTicks / 20, ModConfig.INSTANCE.stopCountdownTicks), true);
        RestartDetector.startStopCountdown();

        return 1;
    }

    public static int cancelCountdown(CommandContext<ServerCommandSource> context) {
        if (RestartDetector.isServerStopping()) {
            context.getSource().sendFeedback(/*?>=1.20 {?*/() -> /*?}?*/Text.translatable("commands.stop.cancelled"), true);
            RestartDetector.cancelStopCountdown();

            return 1;
        } else {
            context.getSource().sendFeedback(/*?>=1.20 {?*/() -> /*?}?*/Text.translatable("commands.stop.not_stopping"), true);

            return 0;
        }
    }
}
