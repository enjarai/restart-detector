package dev.enjarai.restartdetector.block;

import dev.enjarai.restartdetector.RestartDetector;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {
    public static final RestartDetectorBlock RESTART_DETECTOR =
            Registry.register(Registries.BLOCK, RestartDetector.id("restart_detector"),
                    new RestartDetectorBlock(FabricBlockSettings.create()));
    public static final BlockItem RESTART_DETECTOR_ITEM =
            Registry.register(Registries.ITEM, RestartDetector.id("restart_detector"),
                    new PolymerBlockItem(RESTART_DETECTOR, new FabricItemSettings(), Items.DAYLIGHT_DETECTOR));
    public static final BlockEntityType<RestartDetectorBlockEntity> RESTART_DETECTOR_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, RestartDetector.id("restart_detector"),
                    FabricBlockEntityTypeBuilder.create(RestartDetectorBlockEntity::new, RESTART_DETECTOR).build());

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(group -> group.add(RESTART_DETECTOR_ITEM));
    }
}
