package fr.maxlego08.items.api.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.utils.Helper;
import org.bukkit.Axis;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Brushable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Hangable;
import org.bukkit.block.data.Hatchable;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.block.data.type.BrewingStand;
import org.bukkit.block.data.type.BubbleColumn;
import org.bukkit.block.data.type.Cake;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Candle;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.Crafter;
import org.bukkit.block.data.type.DaylightDetector;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.Jigsaw;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.PinkPetals;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.block.data.type.Scaffolding;
import org.bukkit.block.data.type.SculkCatalyst;
import org.bukkit.block.data.type.SculkSensor;
import org.bukkit.block.data.type.SculkShrieker;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Snow;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TNT;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.block.data.type.TurtleEgg;
import org.bukkit.block.data.type.Vault;
import org.bukkit.block.data.type.Wall;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record BlockDataMetaConfiguration(boolean enable, BlockData blockData) {

    public static BlockDataMetaConfiguration loadBlockDataMeta(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        boolean enableBlockDataMeta = configuration.getBoolean(path + "block-data-meta.enable", false);
        BlockData blockData = null;

        if (enableBlockDataMeta) {
            try {
                Material material = Material.valueOf(configuration.getString(path + "block-data-meta.material"));
                blockData = plugin.getServer().createBlockData(material);

                if (blockData instanceof Directional directional) {
                    String face = configuration.getString(path + "block-data-meta.block-face");
                    if (face != null) directional.setFacing(BlockFace.valueOf(face.toUpperCase()));
                }

                if (blockData instanceof Waterlogged waterlogged) {
                    waterlogged.setWaterlogged(configuration.getBoolean(path + "block-data-meta.waterlogged"));
                }

                if (blockData instanceof Attachable attachable) {
                    attachable.setAttached(configuration.getBoolean(path + "block-data-meta.attached"));
                }

                if (blockData instanceof Openable openable) {
                    openable.setOpen(configuration.getBoolean(path + "block-data-meta.open"));
                }

                if (blockData instanceof Ageable ageable) {
                    if (configuration.getString(path + "block-data-meta.age", "").equalsIgnoreCase("max")) {
                        ageable.setAge(ageable.getMaximumAge());
                    } else ageable.setAge(configuration.getInt(path + "block-data-meta.age", 0));
                }

                if (blockData instanceof Sapling sapling) {
                    if (configuration.getString(path + "block-data-meta.stage", "").equalsIgnoreCase("max")) {
                        sapling.setStage(sapling.getMaximumStage());
                    } else sapling.setStage(configuration.getInt(path + "block-data-meta.stage", 0));
                }

                if (blockData instanceof AnaloguePowerable analoguePowerable) {
                    if (configuration.getString(path + "block-data-meta.power", "").equalsIgnoreCase("max")) {
                        analoguePowerable.setPower(analoguePowerable.getMaximumPower());
                    } else analoguePowerable.setPower(configuration.getInt(path + "block-data-meta.power", 0));
                }

                if (blockData instanceof Bamboo bamboo) {
                    String leaves = configuration.getString(path + "block-data-meta.bamboo-leaves");
                    if (leaves != null) bamboo.setLeaves(Bamboo.Leaves.valueOf(leaves.toUpperCase()));
                }

                if (blockData instanceof Orientable orientable) {
                    String axisAsString = configuration.getString(path + "block-data-meta.axis");
                    if (axisAsString != null) {
                        Axis axis = Axis.valueOf(axisAsString.toUpperCase());
                        if (orientable.getAxes().contains(axis)) {
                            orientable.setAxis(axis);
                        }
                    }
                }

                if (blockData instanceof Beehive beehive) {
                    int honeyLevel = configuration.getInt(path + "block-data-meta.honey-level", 0);
                    if (honeyLevel < 0 || honeyLevel > beehive.getMaximumHoneyLevel()) {
                        throw new IllegalArgumentException("Honey level must be between 0 and " + beehive.getMaximumHoneyLevel());
                    }
                    beehive.setHoneyLevel(honeyLevel);
                }

                if (blockData instanceof Powerable powerable) {
                    powerable.setPowered(configuration.getBoolean(path + "block-data-meta.powered", false));
                }

                if (blockData instanceof Bisected bisected) {
                    String halfAsString = configuration.getString(path + "block-data-meta.half");
                    if (halfAsString != null) {
                        bisected.setHalf(Bisected.Half.valueOf(halfAsString.toUpperCase()));
                    }
                }

                if (blockData instanceof BrewingStand brewingStand) {
                    List<Map<?, ?>> bottles = configuration.getMapList(path + "block-data-meta.bottles");
                    for (int i = 0; i < bottles.size(); i++) {
                        Map<?, ?> bottle = bottles.get(i);
                        brewingStand.setBottle(i, (boolean) bottle.get(i));
                    }
                }

                if (blockData instanceof Brushable brushable) {
                    int dusted = configuration.getInt(path + "block-data-meta.dusted", 0);
                    if (dusted < 0 || dusted > brushable.getMaximumDusted()) {
                        throw new IllegalArgumentException("Dusted must be between 0 and " + brushable.getMaximumDusted());
                    }
                    brushable.setDusted(dusted);
                }

                if (blockData instanceof BubbleColumn bubbleColumn) {
                    bubbleColumn.setDrag(configuration.getBoolean(path + "block-data-meta.drag", false));
                }

                if (blockData instanceof Cake cake) {
                    int bites = configuration.getInt(path + "block-data-meta.bites", 0);
                    if (bites < 0 || bites > cake.getMaximumBites()) {
                        throw new IllegalArgumentException("Bites must be between 0 and " + cake.getMaximumBites());
                    }
                    cake.setBites(bites);
                }

                if (blockData instanceof Campfire campfire) {
                    campfire.setSignalFire(configuration.getBoolean(path + "block-data-meta.signal-fire", false));
                }

                if (blockData instanceof Candle candle) {
                    int candles = configuration.getInt(path + "block-data-meta.candles", 1);
                    if (candles < 1 || candles > candle.getMaximumCandles()) {
                        throw new IllegalArgumentException("Candles must be between 1 and " + candle.getMaximumCandles());
                    }
                    candle.setCandles(candles);
                }

                if (blockData instanceof CaveVinesPlant caveVinesPlant) {
                    caveVinesPlant.setBerries(configuration.getBoolean(path + "block-data-meta.berries", false));
                }

                if (blockData instanceof Chest chest) {
                    String typeAsString = configuration.getString(path + "block-data-meta.chest-type");
                    if (typeAsString != null) {
                        chest.setType(Chest.Type.valueOf(typeAsString.toUpperCase()));
                    }
                }

                if (blockData instanceof CommandBlock commandBlock) {
                    commandBlock.setConditional(configuration.getBoolean(path + "block-data-meta.conditional", false));
                }

                if (blockData instanceof Comparator comparator) {
                    String modeAsString = configuration.getString(path + "block-data-meta.comparator-mode");
                    if (modeAsString != null) {
                        comparator.setMode(Comparator.Mode.valueOf(modeAsString.toUpperCase()));
                    }
                }

                if (blockData instanceof Crafter crafter) {
                    boolean crafting = configuration.getBoolean(path + "block-data-meta.crafter-crafting", false);
                    boolean triggered = configuration.getBoolean(path + "block-data-meta.crafter-triggered", false);
                    String orientationAsString = configuration.getString(path + "block-data-meta.crafter-orientation");
                    if (orientationAsString != null) {
                        crafter.setOrientation(Crafter.Orientation.valueOf(orientationAsString.toUpperCase()));
                    }
                    crafter.setCrafting(crafting);
                    crafter.setTriggered(triggered);
                }

                if (blockData instanceof DaylightDetector daylightDetector) {
                    daylightDetector.setInverted(configuration.getBoolean(path + "block-data-meta.inverted", false));
                }

                if (blockData instanceof Door door) {
                    String hinge = configuration.getString(path + "block-data-meta.hinge");
                    if (hinge != null) {
                        door.setHinge(Door.Hinge.valueOf(hinge));
                    }
                }

                if (blockData instanceof EndPortalFrame endPortalFrame) {
                    endPortalFrame.setEye(configuration.getBoolean(path + "block-data-meta.eye", false));
                }

                if (blockData instanceof Farmland farmland) {
                    if (configuration.getString(path + "block-data-meta.moisture", "").equalsIgnoreCase("max")) {
                        farmland.setMoisture(farmland.getMaximumMoisture());
                    } else farmland.setMoisture(configuration.getInt(path + "block-data-meta.moisture", 0));
                }

                if (blockData instanceof Gate gate) {
                    gate.setInWall(configuration.getBoolean(path + "block-data-meta.in_wall", false));
                }

                if (blockData instanceof Hangable hangable) {
                    hangable.setHanging(configuration.getBoolean(path + "block-data-meta.hanging", false));
                }

                if (blockData instanceof Hatchable hatchable) {
                    if (configuration.getString(path + "block-data-meta.hatch", "").equalsIgnoreCase("max")) {
                        hatchable.setHatch(hatchable.getMaximumHatch());
                    } else hatchable.setHatch(configuration.getInt(path + "block-data-meta.hatch", 0));
                }

                if (blockData instanceof Jigsaw jigsaw) {
                    String orientationAsString = configuration.getString(path + "block-data-meta.jigsaw-orientation");
                    if (orientationAsString != null) {
                        jigsaw.setOrientation(Jigsaw.Orientation.valueOf(orientationAsString));
                    }
                }

                if (blockData instanceof Leaves leaves) {
                    leaves.setPersistent(configuration.getBoolean(path + "block-data-meta.persistent", false));
                    leaves.setDistance(configuration.getInt(path + "block-data-meta.distance", 1));
                }

                if (blockData instanceof Levelled levelled) {
                    if (configuration.getString(path + "block-data-meta.level", "").equalsIgnoreCase("max")) {
                        levelled.setLevel(levelled.getMaximumLevel());
                    } else levelled.setLevel(configuration.getInt(path + "block-data-meta.level", 0));
                }

                if (blockData instanceof Lightable lightable) {
                    lightable.setLit(configuration.getBoolean(path + "block-data-meta.lit", false));
                }

                if (blockData instanceof MultipleFacing multipleFacing) {
                    Set<BlockFace> faces = multipleFacing.getAllowedFaces();
                    for (BlockFace face : faces) {
                        multipleFacing.setFace(face, configuration.getBoolean(path + "block-data-meta.face." + face.name(), false));
                    }
                }

                if (blockData instanceof NoteBlock noteBlock) {
                    String instrument = configuration.getString(path + "block-data-meta.instrument");
                    if (instrument != null) {
                        noteBlock.setInstrument(Instrument.valueOf(instrument));
                    }
                    noteBlock.setNote(new Note(configuration.getInt(path + "block-data-meta.note", 0)));
                }

                if (blockData instanceof PinkPetals pinkPetals) {
                    pinkPetals.setFlowerAmount(configuration.getInt(path + "block-data-meta.flower-amount", 1));
                }

                if (blockData instanceof Piston piston) {
                    piston.setExtended(configuration.getBoolean(path + "block-data-meta.extended", false));
                }

                if (blockData instanceof PistonHead pistonHead) {
                    pistonHead.setShort(configuration.getBoolean(path + "block-data-meta.short", false));
                }

                if (blockData instanceof PointedDripstone pointedDripstone) {
                    String thickness = configuration.getString(path + "block-data-meta.thickness");
                    if (thickness != null) {
                        pointedDripstone.setThickness(PointedDripstone.Thickness.valueOf(thickness));
                    }
                    String verticalDirection = configuration.getString(path + "block-data-meta.vertical-direction");
                    if (verticalDirection != null) {
                        pointedDripstone.setVerticalDirection(BlockFace.valueOf(verticalDirection));
                    }
                }

                if (blockData instanceof Rail rail) {
                    String shape = configuration.getString(path + "block-data-meta.rail-shape");
                    if (shape != null) {
                        rail.setShape(Rail.Shape.valueOf(shape));
                    }
                }

                if (blockData instanceof Repeater repeater) {
                    repeater.setDelay(Helper.between(configuration.getInt(path + "block-data-meta.repeater-delay", 1), repeater.getMinimumDelay(), repeater.getMaximumDelay()));
                    repeater.setLocked(configuration.getBoolean(path + "block-data-meta.repeater-locked", false));
                }

                if (blockData instanceof RespawnAnchor respawnAnchor) {
                    respawnAnchor.setCharges(configuration.getInt(path + "block-data-meta.charges", 0));
                }

                if (blockData instanceof Rotatable rotatable) {
                    String rotation = configuration.getString(path + "block-data-meta.rotation");
                    if (rotation != null) {
                        rotatable.setRotation(BlockFace.valueOf(rotation));
                    }
                }

                if (blockData instanceof Scaffolding scaffolding) {
                    scaffolding.setBottom(configuration.getBoolean(path + "block-data-meta.scaffolding-bottom", false));
                    scaffolding.setDistance(configuration.getInt(path + "block-data-meta.scaffolding-distance", 0));
                }

                if (blockData instanceof SculkCatalyst sculkCatalyst) {
                    sculkCatalyst.setBloom(configuration.getBoolean(path + "block-data-meta.bloom", false));
                }

                if (blockData instanceof SculkSensor sculkSensor) {
                    String phase = configuration.getString(path + "block-data-meta.phase");
                    if (phase != null) {
                        sculkSensor.setPhase(SculkSensor.Phase.valueOf(phase));
                    }
                }

                if (blockData instanceof SculkShrieker sculkShrieker) {
                    sculkShrieker.setShrieking(configuration.getBoolean(path + "block-data-meta.shrieker-shrieking", false));
                    sculkShrieker.setCanSummon(configuration.getBoolean(path + "block-data-meta.shrieker-can-summon", false));
                }

                if (blockData instanceof SeaPickle seaPickle) {
                    seaPickle.setPickles(Helper.between(configuration.getInt(path + "block-data-meta.pickles", 1), seaPickle.getMinimumPickles(), seaPickle.getMaximumPickles()));
                }

                if (blockData instanceof Slab slab) {
                    String type = configuration.getString(path + "block-data-meta.slab-type");
                    if (type != null) {
                        slab.setType(Slab.Type.valueOf(type));
                    }
                }

                if (blockData instanceof Snow snow) {
                    snow.setLayers(Helper.between(configuration.getInt(path + "block-data-meta.layers", 1), snow.getMinimumLayers(), snow.getMaximumLayers()));
                }

                if (blockData instanceof Snowable snowable) {
                    snowable.setSnowy(configuration.getBoolean(path + "block-data-meta.snowy", false));
                }

                if (blockData instanceof Stairs stairs) {
                    String shape = configuration.getString(path + "block-data-meta.stairs-shape");
                    if (shape != null) {
                        stairs.setShape(Stairs.Shape.valueOf(shape));
                    }
                }

                if (blockData instanceof TechnicalPiston technicalPiston) {
                    String type = configuration.getString(path + "block-data-meta.technical-piston-type");
                    if (type != null) {
                        technicalPiston.setType(TechnicalPiston.Type.valueOf(type));
                    }
                }

                if (blockData instanceof TNT tnt) {
                    tnt.setUnstable(configuration.getBoolean(path + "block-data-meta.unstable", false));
                }

                if (blockData instanceof TrapDoor trapDoor) {
                    trapDoor.setOpen(configuration.getBoolean(path + "block-data-meta.open", false));
                }

                if (blockData instanceof Tripwire tripwire) {
                    tripwire.setDisarmed(configuration.getBoolean(path + "block-data-meta.disarmed", false));
                }

                if (blockData instanceof TripwireHook tripwireHook) {
                    tripwireHook.setAttached(configuration.getBoolean(path + "block-data-meta.attached", false));
                    tripwireHook.setPowered(configuration.getBoolean(path + "block-data-meta.powered", false));
                }

                if (blockData instanceof TrialSpawner trialSpawner) {
                    String state = configuration.getString(path + "block-data-meta.trial-spawner-state");
                    if (state != null) {
                        trialSpawner.setTrialSpawnerState(TrialSpawner.State.valueOf(state));
                    }
                    trialSpawner.setOminous(configuration.getBoolean(path + "block-data-meta.trial-spawner-ominous", false));
                }

                if (blockData instanceof TurtleEgg turtleEgg) {
                    turtleEgg.setEggs(Helper.between(configuration.getInt(path + "block-data-meta.eggs", 1), turtleEgg.getMinimumEggs(), turtleEgg.getMaximumEggs()));
                }

                if (blockData instanceof Vault vault) {
                    String state = configuration.getString(path + "block-data-meta.vault-state");
                    if (state != null) {
                        vault.setTrialSpawnerState(Vault.State.valueOf(state));
                    }
                    vault.setOminous(configuration.getBoolean(path + "block-data-meta.vault-ominous", false));
                }

                if (blockData instanceof Wall wall) {
                    wall.setUp(configuration.getBoolean(path + "block-data-meta.wall-up", false));
                    for (BlockFace face : BlockFace.values()) {
                        String height = configuration.getString(path + "block-data-meta.wall-height." + face.name());
                        if (height != null) {
                            wall.setHeight(face, Wall.Height.valueOf(height));
                        }
                    }
                }


            } catch (Exception exception) {
                plugin.getLogger().severe("Invalid block data or material configuration in " + fileName);
                exception.printStackTrace();
                enableBlockDataMeta = false;
            }
            return new BlockDataMetaConfiguration(enableBlockDataMeta, blockData);
        } else {
            return new BlockDataMetaConfiguration(false, null);
        }
    }

}