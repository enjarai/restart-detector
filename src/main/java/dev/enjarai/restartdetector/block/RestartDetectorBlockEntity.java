package dev.enjarai.restartdetector.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class RestartDetectorBlockEntity extends BlockEntity {
    public RestartDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.RESTART_DETECTOR_ENTITY, pos, state);
    }
}
