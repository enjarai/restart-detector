package dev.enjarai.restartdetector.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.enjarai.restartdetector.ModConfig;
import dev.enjarai.restartdetector.RestartDetector;
import dev.enjarai.restartdetector.command.RestartDetectorCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.server.command.CommandManager.literal;

// High priority to put our inject-at-head-cancel later than other potential injectors, which might help compatibility somewhat
@Mixin(value = StopCommand.class, priority = 1500)
public class StopCommandMixin {
    @Inject(
            method = "method_13676",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void hijackStopCommand(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.INSTANCE.hijackStopCommand) {
            cir.setReturnValue(RestartDetectorCommand.startCountdown(context));
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
            return builder
                    .then(literal("cancel")
                        .executes(RestartDetectorCommand::cancelCountdown)
                    )
                    .then(literal("now")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(/*?>=1.20 {*/() -> /*?}*/Text.translatable("commands.stop.stopping"), true);
                            ctx.getSource().getServer().stop(false);
                            return 1;
                        })
                    );
        }

        return builder;
    }
}
