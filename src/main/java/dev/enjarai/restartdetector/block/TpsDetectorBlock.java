package dev.enjarai.restartdetector.block;

import dev.enjarai.restartdetector.ModConfig;
import dev.enjarai.restartdetector.RestartDetector;
import dev.enjarai.restartdetector.display.SpinnyHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class TpsDetectorBlock extends SpinnyBlock {
    private static final int maxMspt = 50;
    private static final int targetTps = 20;

    public static final IntProperty COMPARATOR_POWER = IntProperty.of("comparator_power", 0, 15);

    public TpsDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(stateManager.getDefaultState()
                .with(POWER, 0)
                .with(COMPARATOR_POWER, 0));
    }

    /*?<1.20.5 {*//*
    @Override
    public Block getPolymerBlock(BlockState state) {
        return null;
    }
    *//*?}*/

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        /*?>=1.20.4 {*/
        float mspt = world.getServer().getAverageTickTime();
        /*?} else {*//*
        float mspt = world.getServer().getTickTime();
        *//*?}*/
        float tps = 1000 / Math.max(mspt, maxMspt);

        int comparatorPower = Math.min((int) (mspt / maxMspt * 15), 15);
        int regularPower = MathHelper.clamp((int) (tps / targetTps * 15), 0, 15);

        world.setBlockState(pos, state.with(COMPARATOR_POWER, comparatorPower).with(POWER, regularPower));

        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(COMPARATOR_POWER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(COMPARATOR_POWER);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new SpinnyHolder(world, Items.REPEATING_COMMAND_BLOCK.getDefaultStack()) {
            @Override
            public float getSpeed() {
                var msptFraction = this.world.getBlockState(pos).get(COMPARATOR_POWER) / 15f;
                return 60 * (1 - msptFraction);
            }
        };
    }
}
