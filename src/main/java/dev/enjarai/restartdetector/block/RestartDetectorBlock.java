package dev.enjarai.restartdetector.block;

import dev.enjarai.restartdetector.ModConfig;
import dev.enjarai.restartdetector.RestartDetector;
import dev.enjarai.restartdetector.display.SpinnyHolder;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.*;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class RestartDetectorBlock extends Block implements PolymerBlock, BlockWithElementHolder {
    public static final IntProperty POWER = Properties.POWER;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

    public RestartDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(stateManager.getDefaultState().with(POWER, 0));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    private static void updateState(BlockState state, World world, BlockPos pos) {
        if (RestartDetector.isServerStopping()) {
            int totalTicks = ModConfig.INSTANCE.stopCountdownTicks;
            int power = (totalTicks - RestartDetector.getTicksToStop()) * 14 / totalTicks + 1;
            world.setBlockState(pos, state.with(POWER, power));
        } else {
            world.setBlockState(pos, state.with(POWER, Math.max(0, state.get(POWER) - 1)));
        }
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.DAYLIGHT_DETECTOR;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.DAYLIGHT_DETECTOR.getDefaultState().with(POWER, state.get(POWER));
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.scheduleBlockTick(pos, this, 20);

        updateState(state, world, pos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 1);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return true;
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
