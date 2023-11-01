package dev.enjarai.restartdetector.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.enjarai.restartdetector.ModConfig;
import dev.enjarai.restartdetector.RestartDetector;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.server.command.CommandManager.literal;

@Mixin(StopCommand.class)
public class StopCommandMixin {
    @Inject(
            method = "method_13676",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void hijackStopCommand(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.INSTANCE.hijackStopCommand) {
            context.getSource().sendFeedback(() -> Text.literal("Server stopping in %d seconds (%d ticks)"
                    .formatted(ModConfig.INSTANCE.stopCountdownTicks / 20, ModConfig.INSTANCE.stopCountdownTicks)), true);
            RestartDetector.startStopCountdown();

            cir.setReturnValue(1);
        }
    }

    @ModifyExpressionValue(
            method = "register",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;executes(Lcom/mojang/brigadier/Command;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"
            )
    )
    private static <T extends ArgumentBuilder<ServerCommandSource, T>> T addSubCommand(T builder) {
        if (ModConfig.INSTANCE.hijackStopCommand) {
            return builder.then(literal("cancel")
                    .executes(ctx -> {
                        if (RestartDetector.isServerStopping()) {
                            ctx.getSource().sendFeedback(() -> Text.literal("Cancelled server stopping"), true);
                            RestartDetector.cancelStopCountdown();

                            return 1;
                        } else {
                            ctx.getSource().sendFeedback(() -> Text.literal("Server is not stopping right now"), true);

                            return 0;
                        }
                    }));
        }

        return builder;
    }
}
