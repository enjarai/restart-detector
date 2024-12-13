package dev.enjarai.restartdetector.block;

import dev.enjarai.restartdetector.ModConfig;
import dev.enjarai.restartdetector.RestartDetector;
import dev.enjarai.restartdetector.display.SpinnyHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class RestartDetectorBlock extends SpinnyBlock {
    public RestartDetectorBlock(Settings settings) {
        /*? if >=1.21.2 {*/
        super(settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, RestartDetector.id("restart_detector"))));
        /*?} else {*//*
        super(settings);
        *//*?}*/
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (RestartDetector.isServerStopping()) {
            int totalTicks = ModConfig.INSTANCE.stopCountdownTicks;
            int power = (totalTicks - RestartDetector.getTicksToStop()) * 14 / totalTicks + 1;
            world.setBlockState(pos, state.with(POWER, power));
        } else {
            world.setBlockState(pos, state.with(POWER, Math.max(0, state.get(POWER) - 1)));
        }

        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new SpinnyHolder(world, Items.COMMAND_BLOCK.getDefaultStack()) {
            @Override
            public float getSpeed() {
                float speed = 6;
                if (RestartDetector.isServerStopping()) {
                    var shutdownProgress = this.world.getBlockState(pos).get(POWER) / 15f;
                    speed += 60 * shutdownProgress;
                }
                return speed;
            }
        };
    }
}
