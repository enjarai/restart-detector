package dev.enjarai.restartdetector.block;

import dev.enjarai.restartdetector.ModConfig;
import dev.enjarai.restartdetector.RestartDetector;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("deprecation")
public class RestartDetectorBlock extends BlockWithEntity implements PolymerBlock, BlockWithElementHolder {
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
            world.setBlockState(pos, state.with(POWER, 0));
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RestartDetectorBlockEntity(pos, state);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient()
                ? validateTicker(type, ModBlocks.RESTART_DETECTOR_ENTITY, (world1, pos, state1, be) -> tick(world1, pos, state1))
                : null;
    }

    public static void tick(World world, BlockPos pos, BlockState state) {
        if (world.getTime() % 20L == 0L) {
            updateState(state, world, pos);
        }
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
        var spinnyElement = new BlockDisplayElement();
        spinnyElement.setBlockState(Blocks.COMMAND_BLOCK.getDefaultState().with(CommandBlock.FACING, Direction.UP));

        var matrix = new Matrix4f();
        matrix.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() * 6f));
        matrix.scale(0.5f);
        matrix.translate(-0.5f, 0.0f, -0.5f);
        spinnyElement.setTransformation(matrix);

        spinnyElement.setInterpolationDuration(3);

        return new ElementHolder() {
            {
                addElement(spinnyElement);
            }

            @Override
            protected void onTick() {
                if (world.getTime() % 3 == 0) {
                    var matrix = new Matrix4f();
                    matrix.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() * 6.0f));
                    matrix.scale(0.5f);
                    matrix.translate(-0.5f, (float) (Math.sin(world.getTime() / 10.0) * 0.2), -0.5f);
                    spinnyElement.setTransformation(matrix);
                    spinnyElement.startInterpolation();
                }
            }
        };
    }
}
