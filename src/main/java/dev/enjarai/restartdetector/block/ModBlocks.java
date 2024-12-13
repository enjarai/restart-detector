package dev.enjarai.restartdetector.block;

import dev.enjarai.restartdetector.RestartDetector;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
/*? <1.21.2 {*//*import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;*//*?}*/
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
/*? <1.20 {*//*import net.minecraft.block.Material;*//*?}*/
import net.minecraft.block.entity.BlockEntityType;
/*? <1.21 {*//*import net.minecraft.block.enums.Instrument;*//*?}*/
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {
    public static final AbstractBlock.Settings DETECTOR_SETTINGS = /*? >=1.21.2 {*/
            AbstractBlock.Settings.create()
            /*?} else if >=1.20 {*/
            /*FabricBlockSettings.create()*/
            /*?} else {*/
            /*FabricBlockSettings.of(Material.WOOD)*/
            /*?}*/
            .mapColor(MapColor.OAK_TAN)
            /*? >=1.20 <1.21 {*//*.instrument(Instrument.BASS)*//*?}*/
            .strength(0.2F)
            .sounds(BlockSoundGroup.WOOD)
            /*? >=1.20 {*/.burnable()/*?}*/;

    public static final RestartDetectorBlock RESTART_DETECTOR =
            Registry.register(Registries.BLOCK, RestartDetector.id("restart_detector"),
                    new RestartDetectorBlock(DETECTOR_SETTINGS));
    public static final TpsDetectorBlock TPS_DETECTOR =
            Registry.register(Registries.BLOCK, RestartDetector.id("tps_detector"),
                    new TpsDetectorBlock(AbstractBlock.Settings.copy(RESTART_DETECTOR)));

    public static final BlockItem RESTART_DETECTOR_ITEM =
            Registry.register(Registries.ITEM, RestartDetector.id("restart_detector"),
                    new PolymerBlockItem(RESTART_DETECTOR, new Item.Settings()
                            /*? if >=1.21.2 {*/.registryKey(RegistryKey.of(RegistryKeys.ITEM, RestartDetector.id("restart_detector")))/*?}*/,
                            Items.DAYLIGHT_DETECTOR));
    public static final BlockItem TPS_DETECTOR_ITEM =
            Registry.register(Registries.ITEM, RestartDetector.id("tps_detector"),
                    new PolymerBlockItem(TPS_DETECTOR, new Item.Settings()
                            /*? if >=1.21.2 {*/.registryKey(RegistryKey.of(RegistryKeys.ITEM, RestartDetector.id("tps_detector")))/*?}*/,
                            Items.DAYLIGHT_DETECTOR));

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(group -> {
            group.add(RESTART_DETECTOR_ITEM);
            group.add(TPS_DETECTOR_ITEM);
        });
    }
}
