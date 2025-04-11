package random_toys.zz_404.block.block_entity;

import net.minecraft.block.*;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import random_toys.zz_404.registry.ModBlocks;
import random_toys.zz_404.registry.ModItems;
import random_toys.zz_404.RandomToys;
import random_toys.zz_404.block.CompressorBlock;
import random_toys.zz_404.block.DisenchantmentBlock;
import random_toys.zz_404.block.ExperienceCollectorBlock;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MazeGenerator {
    private final World world;
    private final BlockPos pos;

    private static final int unit = 6;
    private static final int length = 6;
    private static final int height = 5;
    private static final int[] range = IntStream.rangeClosed(-length, length).toArray();
    private static final double[] halfRange = IntStream.range(-length, length)
            .mapToDouble(k -> k + 0.5).toArray();

    public MazeGenerator(@NotNull World world, @NotNull BlockPos pos) {
        this.world = world;
        this.pos = pos;
    }

    private void setBlock(BlockPos pos, BlockState state) {
        world.setBlockState(pos, state);
    }

    public void generate() {
        StopWatch watch = new StopWatch();
        watch.start();
        placeFloorAndCeiling();
        placePillars();
        placeSubPillars();
        placeRoomFeatures();
        placeCenterFeatures();
        placeCornerFeatures();
        placeDoors(new Structure().generate());
        watch.stop();
        RandomToys.log("Maze Generation: {}ms", watch.getTime());
    }

    private class RelativePos {
        double x, z;
        private BlockPos toBlockPos() {
            return pos.add((int)Math.round(unit * x), 0, (int)Math.round(unit * z));
        }
        @Contract("_, _ -> new")
        public @NotNull RelativePos add(double dx, double dz) {
            return relative(x + dx, z + dz);
        }
        @Override
        public boolean equals(Object object) {
            if (!(object instanceof RelativePos that)) return false;
            return Math.round(x * 2) == Math.round(that.x * 2)
                    && Math.round(z * 2) == Math.round(that.z * 2);
        }
        @Override
        public int hashCode() {
            return Objects.hash(Math.round(x * 2), Math.round(z * 2));
        }
    }
    @Contract("_, _ -> new")
    private @NotNull RelativePos relative(double x, double z) {
        var pos = new RelativePos();
        pos.x = x;
        pos.z = z;
        return pos;
    }

    private @NotNull ArrayList<BlockPos> getPillarBlocks() {
        ArrayList<BlockPos> pillars = new ArrayList<>();
        for (int i : range)
            for (int j : range)
                for (int h = 0; h < height; h++)
                    if (i != 0 || j != 0)
                        pillars.add(relative(i, j).toBlockPos().up(h));
        return pillars;
    }

    private void placePillars() {
        for (BlockPos pos : getPillarBlocks())
            setBlock(pos, Blocks.ANCIENT_DEBRIS.getDefaultState());
    }

    private void placeSubPillars() {
        for (BlockPos pos : getSubPillarBlocks())
            setBlock(pos, Blocks.OBSIDIAN.getDefaultState());
    }

    private @NotNull ArrayList<BlockPos> getSubPillarBlocks() {
        ArrayList<BlockPos> pillars = new ArrayList<>();
        for (int i : range)
            for (int j : range)
                for (int h = 0; h < height; h++)
                    if (i != 0 || j != 0){
                        if ((i != -1 || j != 0) && i != length)
                            pillars.add(relative(i, j).toBlockPos().up(h).add(1, 0, 0));
                        if ((i != 1 || j != 0) && i != -length)
                            pillars.add(relative(i, j).toBlockPos().up(h).add(-1, 0, 0));
                        if ((i != 0 || j != -1) && j != length)
                            pillars.add(relative(i, j).toBlockPos().up(h).add(0, 0, 1));
                        if ((i != 0 || j != 1) && j != -length)
                            pillars.add(relative(i, j).toBlockPos().up(h).add(0, 0, -1));
                    }
        return pillars;
    }

    private @NotNull ArrayList<RelativePos> getDoors() {
        ArrayList<RelativePos> pillars = new ArrayList<>();
        for (int i : range)
            for (double j : halfRange)
                pillars.add(relative(i, j));
        for (double i : halfRange)
            for (int j : range)
                pillars.add(relative(i, j));
        return pillars;
    }

    private void placeDoors(HashMap<RelativePos, Boolean> isOpen) {
        for (RelativePos relative : getDoors()) {
            BlockPos pos = relative.toBlockPos();
            BlockState state = isOpen.get(relative) ? ModBlocks.VANISHING_DOOR.getDefaultState()
                    : ModBlocks.IMITATOR.getDefaultState();
            if (Math.abs(relative.x) == length || Math.abs(relative.z) == length)
                state = Blocks.OBSIDIAN.getDefaultState();
            if (Math.abs(relative.x) < 1 && Math.abs(relative.z) < 1) continue;
            for (int i = -1; i <= 1; i ++) {
                for (int h = 0; h < height; h++) {
                    if (isNotInt(relative.z)) {
                        setBlock(pos.add(i, h, 0), state);
                        if (world.getBlockEntity(pos.add(i, h, 0)) instanceof ImitatorBlockEntity imitator)
                            imitator.block = ModBlocks.VANISHING_DOOR;
                    }
                    if (isNotInt(relative.x)) {
                        setBlock(pos.add(0, h, i), state);
                        if (world.getBlockEntity(pos.add(0, h, i)) instanceof ImitatorBlockEntity imitator)
                            imitator.block = ModBlocks.VANISHING_DOOR;
                    }
                }
            }
        }
    }

    private @NotNull ArrayList<BlockPos> getRoomCenterBlocks() {
        ArrayList<BlockPos> centers = new ArrayList<>();
        for (double i : halfRange)
            for (double j : halfRange)
                if (Math.abs(i) > 1 && Math.abs(j) > 1
                        && (Math.abs(i) < length - 1 || Math.abs(j) < length - 1))
                    centers.add(relative(i, j).toBlockPos().up((height - 1) / 2));
        return centers;
    }

    private void placeRoomFeatures() {
        ArrayList<BlockPos> centers = getRoomCenterBlocks();
        ArrayList<BlockPos> empties = new ArrayList<>();
        while (!centers.isEmpty()) {
            BlockPos center = centers.get(world.random.nextInt(centers.size()));
            if (world.random.nextBoolean()) {
                setBlock(center.up(), Blocks.CHAIN.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y));
                setBlock(center.up(2), Blocks.CHAIN.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.Y));
            }
            else {
                setBlock(center.down(), Blocks.WARPED_FENCE.getDefaultState());
                setBlock(center.down(2), Blocks.WARPED_FENCE.getDefaultState());
            }
            int x = world.random.nextInt(20);
            if (x < 3) {
                setBlock(center, Blocks.SPAWNER.getDefaultState());
                if (world.getBlockEntity(center) instanceof Spawner spawner)
                    spawner.setEntityType(EntityType.BLAZE, world.random);
                if (world.random.nextInt(3) == 0)
                    setBlock(center.down(), ModBlocks.EXPERIENCE_COLLECTOR.getDefaultState());
            } //15%
            else if (x < 5) {
                setBlock(center, Blocks.SPAWNER.getDefaultState());
                if (world.getBlockEntity(center) instanceof Spawner spawner)
                    spawner.setEntityType(EntityType.BREEZE, world.random);
                if (world.random.nextInt(3) == 0)
                    setBlock(center.down(), ModBlocks.EXPERIENCE_COLLECTOR.getDefaultState());
            } //10%
            else if (x < 6) {
                setBlock(center, Blocks.SPAWNER.getDefaultState());
                if (world.getBlockEntity(center) instanceof Spawner spawner) {
                    spawner.setEntityType(EntityType.WITCH, world.random);
                    if (world.random.nextInt(3) == 0)
                        setBlock(center.down(), ModBlocks.EXPERIENCE_COLLECTOR.getDefaultState());
                }
            } //5%
            else if (x < 9) {
                setBlock(center, Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(center.up(), Blocks.RAW_GOLD_BLOCK.getDefaultState());
                setBlock(center.down(), Blocks.RAW_GOLD_BLOCK.getDefaultState());
                setBlock(center.north(), Blocks.RAW_GOLD_BLOCK.getDefaultState());
                setBlock(center.south(), Blocks.RAW_GOLD_BLOCK.getDefaultState());
                setBlock(center.west(), Blocks.RAW_GOLD_BLOCK.getDefaultState());
                setBlock(center.east(), Blocks.RAW_GOLD_BLOCK.getDefaultState());
            } //10%
            else if (x < 10) {
                setBlock(center, ModBlocks.COMPRESSOR.getDefaultState().with(CompressorBlock.POWERED, false));
                if (world.getBlockEntity(center) instanceof CompressorBlockEntity compressor)
                    for (int i = 0; i < compressor.size(); i++)
                        compressor.inventory.set(i, new ItemStack(Items.GOLD_NUGGET, 64));
                ArrayList<BlockPos> available = new ArrayList<>(centers);
                available.addAll(empties);
                List<BlockPos> nears = available.stream()
                        .filter(pos1 -> distance(pos1, center) <= 8)
                        .filter(pos1 -> !pos1.equals(center)).toList();
                if (!nears.isEmpty()) {
                    BlockPos near = nears.get(world.random.nextInt(nears.size()));
                    centers.remove(near);
                    setBlock(near, ModBlocks.ENDER_LINKER.getDefaultState());
                    if (world.getBlockEntity(near) instanceof EnderLinkerBlockEntity linker) {
                        linker.dimension = world.getRegistryKey();
                        linker.linked = center;
                    }
                }
            } //5%
            else if (x < 11) {
                setBlock(center.up(), Blocks.TNT.getDefaultState());
                setBlock(center.down(), Blocks.TNT.getDefaultState());
                setBlock(center.north(), Blocks.TNT.getDefaultState());
                setBlock(center.south(), Blocks.TNT.getDefaultState());
                setBlock(center.west(), Blocks.TNT.getDefaultState());
                setBlock(center.east(), Blocks.TNT.getDefaultState());
                setBlock(center, ModBlocks.BUD.getDefaultState());
            } //5%
            else if (x < 12) {
                setBlock(center, ModBlocks.EXPERIENCE_COLLECTOR.getDefaultState());
                setBlock(center.down(), ModBlocks.COMPRESSOR.getDefaultState());
                if (world.getBlockEntity(center) instanceof ExperienceCollectorBlockEntity collector)
                    collector.experience = ExperienceCollectorBlockEntity.max - 50;
                if (world.getBlockEntity(center.down()) instanceof CompressorBlockEntity compressor) {
                    compressor.inventory.set(0, new ItemStack(Items.BOOK,
                            world.random.nextInt(3) + 5));
                    compressor.inventory.set(1, new ItemStack(Items.EXPERIENCE_BOTTLE,
                            world.random.nextInt(6) + 9));
                }
            } //5%
            else if (x < 13) {
                world.spawnEntity(new EndCrystalEntity(world,
                        center.getX() + 0.5, center.getY() - 1, center.getZ() + 0.5));
            } //5%
            else {
                setBlock(center, Blocks.AIR.getDefaultState());
                setBlock(center.up(), Blocks.AIR.getDefaultState());
                setBlock(center.up(2), Blocks.AIR.getDefaultState());
                setBlock(center.down(), Blocks.AIR.getDefaultState());
                setBlock(center.down(2), Blocks.AIR.getDefaultState());
                empties.add(center);
            } //40%
            centers.remove(center);
        }
    }

    private int distance(@NotNull BlockPos pos1, @NotNull BlockPos pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getZ() - pos2.getZ());
    }

    private void placeCornerFeatures() {
        for (int li : new int[]{-1, 1})
            for (int lj : new int[]{-1, 1}) {
                BlockPos corner = relative(li * length, lj * length).toBlockPos();
                BlockPos mid = relative(li * (length - 0.5), lj * (length - 0.5)).toBlockPos();
                BlockPos far = relative(li * (length - 1), lj * (length - 1)).toBlockPos();

                for (int h = 0; h < 10; h++)
                    setBlock(corner.add(-li, height + h, -lj), Blocks.AIR.getDefaultState());
                setBlock(corner.add(-li, 0, -lj), Blocks.WATER.getDefaultState());
                setBlock(corner.add(-li, -1, -lj), Blocks.BLUE_STAINED_GLASS.getDefaultState());
                setBlock(corner.add(-li, -2, -lj), Blocks.BEACON.getDefaultState());
                setBlock(corner.add(-li, -3, -lj), Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(corner.add(-li, -3, -lj - 1), Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(corner.add(-li, -3, -lj + 1), Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(corner.add(-li - 1, -3, -lj), Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(corner.add(-li - 1, -3, -lj - 1), Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(corner.add(-li - 1, -3, -lj + 1), Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(corner.add(-li + 1, -3, -lj), Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(corner.add(-li + 1, -3, -lj - 1), Blocks.GOLD_BLOCK.getDefaultState());
                setBlock(corner.add(-li + 1, -3, -lj + 1), Blocks.GOLD_BLOCK.getDefaultState());

                setBlock(corner.add(-2 * li, 0, -lj),
                        Blocks.POLISHED_BLACKSTONE_BRICK_SLAB.getDefaultState());
                setBlock(corner.add(-li, 0, -2 * lj),
                        Blocks.POLISHED_BLACKSTONE_BRICK_SLAB.getDefaultState());
                setBlock(corner.add(-2 * li, 0, -2 * lj),
                        Blocks.POLISHED_BLACKSTONE_BRICK_SLAB.getDefaultState());
                setBlock(far.add(li, 0, lj), Blocks.BARREL.getDefaultState()
                        .with(BarrelBlock.FACING, Direction.UP));
                if (li * lj == 1) {
                    ArmorStandEntity armorStand = new ArmorStandEntity(world,
                            far.getX() + li + 0.5, far.getY() + 2, far.getZ() + lj + 0.5);
                    armorStand.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
                    armorStand.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
                    armorStand.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
                    armorStand.equipStack(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
                    lookAt(armorStand, far);
                    world.spawnEntity(armorStand);
                    if (world.getBlockEntity(far.add(li, 0, lj)) instanceof BarrelBlockEntity barrel) {
                        barrel.setStack(13, new ItemStack(ModItems.ENDER_LINKER_CONFIGURATOR, 3));
                        barrel.setStack(22, new ItemStack(Items.FLINT_AND_STEEL));
                    }
                    setBlock(mid, Blocks.NETHERRACK.getDefaultState());
                    setBlock(mid.up(), Blocks.FIRE.getDefaultState());
                }
                else if (li == 1) {
                    if (world.getBlockEntity(far.add(li, 0, lj)) instanceof BarrelBlockEntity barrel) {
                        barrel.setStack(4, new ItemStack(Blocks.COPPER_BLOCK, 16));
                        barrel.setStack(13, new ItemStack(ModBlocks.OXIDIZER));
                        barrel.setStack(22, new ItemStack(Items.WATER_BUCKET));
                    }
                    setBlock(mid.north(), Blocks.POLISHED_BLACKSTONE_SLAB.getDefaultState());
                    setBlock(mid.south(), Blocks.POLISHED_BLACKSTONE_SLAB.getDefaultState());
                    setBlock(mid.west(), Blocks.POLISHED_BLACKSTONE_SLAB.getDefaultState());
                    setBlock(mid.east(), Blocks.POLISHED_BLACKSTONE_SLAB.getDefaultState());
                    setBlock(mid.up(), ModBlocks.OXIDIZER.getDefaultState());
                }
                else {
                    if (world.getBlockEntity(far.add(li, 0, lj)) instanceof BarrelBlockEntity barrel) {
                        barrel.setStack(13, ExperienceCollectorBlock.enchantBook(world, 30));
                    }
                    world.setBlockState(far.add(li, 0, lj), Blocks.AIR.getDefaultState());
                    setBlock(mid, ModBlocks.DISENCHANTMENTOR.getDefaultState()
                            .with(DisenchantmentBlock.POWERED, true));
                    setBlock(mid.up(), ModBlocks.TRANSFER.getDefaultState());
                    setBlock(mid.up(2), ModBlocks.COMPRESSOR.getDefaultState());
                }
            }
    }

    private void lookAt(@NotNull Entity entity, @NotNull BlockPos pos) {
        double entityX = entity.getX();
        double entityY = entity.getY() + entity.getStandingEyeHeight();
        double entityZ = entity.getZ();
        double targetX = pos.getX() + 0.5;
        double targetY = pos.getY() + 0.5;
        double targetZ = pos.getZ() + 0.5;
        double deltaX = targetX - entityX;
        double deltaY = targetY - entityY;
        double deltaZ = targetZ - entityZ;
        double yaw = Math.atan2(deltaX, deltaZ) * 180.0 / Math.PI;
        double pitch = Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * 180.0 / Math.PI;
        entity.setYaw((float) yaw);
        entity.setPitch((float) pitch);
    }

    private void placeCenterFeatures() {
        setBlock(pos.up(), Blocks.SOUL_SAND.getDefaultState());
        setBlock(pos.north(), Blocks.CRYING_OBSIDIAN.getDefaultState());
        setBlock(pos.south(), Blocks.CRYING_OBSIDIAN.getDefaultState());
        setBlock(pos.west(), Blocks.CRYING_OBSIDIAN.getDefaultState());
        setBlock(pos.east(), Blocks.CRYING_OBSIDIAN.getDefaultState());
        setBlock(pos.north().up(), Blocks.SOUL_LANTERN.getDefaultState());
        setBlock(pos.south().up(), Blocks.SOUL_LANTERN.getDefaultState());
        setBlock(pos.west().up(), Blocks.SOUL_LANTERN.getDefaultState());
        setBlock(pos.east().up(), Blocks.SOUL_LANTERN.getDefaultState());
        setBlock(pos.up(2), Blocks.SOUL_FIRE.getDefaultState());

        placeAmethystFeature(pos.north(3));
        placeAmethystFeature(pos.south(3));
        placeAmethystFeature(pos.west(3));
        placeAmethystFeature(pos.east(3));
        placeAmethystFeature(pos.north(2).west(2));
        placeAmethystFeature(pos.north(2).east(2));
        placeAmethystFeature(pos.south(2).west(2));
        placeAmethystFeature(pos.south(2).east(2));

        placeCenterLoots(pos.north(5).west(5));
        placeCenterLoots(pos.north(5).east(5));
        placeCenterLoots(pos.south(5).west(5));
        placeCenterLoots(pos.south(5).east(5));
    }

    private void placeAmethystFeature(BlockPos pos) {
        setBlock(pos, Blocks.BUDDING_AMETHYST.getDefaultState());
        setBlock(pos.up(), Blocks.AMETHYST_CLUSTER.getDefaultState()
                .with(AmethystClusterBlock.FACING, Direction.UP));
        setBlock(pos.north(), Blocks.SPRUCE_TRAPDOOR.getDefaultState()
                .with(TrapdoorBlock.FACING, Direction.NORTH).with(TrapdoorBlock.OPEN, true));
        setBlock(pos.south(), Blocks.SPRUCE_TRAPDOOR.getDefaultState()
                .with(TrapdoorBlock.FACING, Direction.SOUTH).with(TrapdoorBlock.OPEN, true));
        setBlock(pos.west(), Blocks.SPRUCE_TRAPDOOR.getDefaultState()
                .with(TrapdoorBlock.FACING, Direction.WEST).with(TrapdoorBlock.OPEN, true));
        setBlock(pos.east(), Blocks.SPRUCE_TRAPDOOR.getDefaultState()
                .with(TrapdoorBlock.FACING, Direction.EAST).with(TrapdoorBlock.OPEN, true));
    }

    private void placeCenterLoots(@NotNull BlockPos pos) {
        setBlock(pos.up(), ModBlocks.COMPRESSOR.getDefaultState().with(CompressorBlock.POWERED, true));
        setBlock(pos, Blocks.REDSTONE_TORCH.getDefaultState());
        if (world.getBlockEntity(pos.up()) instanceof CompressorBlockEntity compressor) {
            compressor.inventory.set(4, new ItemStack(Items.BEACON));
            compressor.inventory.set(12, getCenterBedrockLoot());
            compressor.inventory.set(22, getCenterBedrockLoot());
            compressor.inventory.set(14, getCenterBedrockLoot());
            compressor.inventory.set(13, new ItemStack(ModItems.JETPACKS));
        }
    }

    @Contract(" -> new")
    private @NotNull ItemStack getCenterBedrockLoot() {
        return new ItemStack(world.random.nextBoolean() ? Blocks.BEDROCK : ModBlocks.BLACK_BEDROCK);
    }

    private void placeFloorAndCeiling() {
        BlockPos pos1 = relative(-length, -length).toBlockPos();
        BlockPos pos2 = relative(length, length).toBlockPos();
        for (int x = pos1.getX(); x <= pos2.getX(); x++)
            for (int z = pos1.getZ(); z <= pos2.getZ(); z++) {
                setBlock(new BlockPos(x, pos.getY() - 1, z),
                        ModBlocks.BLACK_BEDROCK.getDefaultState());
                setBlock(new BlockPos(x, pos.getY() + height, z),
                        ModBlocks.BLACK_BEDROCK.getDefaultState());
            }
    }

    @Contract(" -> new")
    public @NotNull Box getRangeBox() {
        final int dx = 15 + length * unit;
        final int dy = height * 2;
        final int dz = 15 + length * unit;
        return new Box(pos.getX() - dx, pos.getY() - dy, pos.getZ() - dz,
                pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
    }

    private boolean isNotInt(double x) {
        return !(Math.abs(Math.round(x) - x) >= 1e-2);
    }

    private class Structure {
        private final HashSet<RelativePos> blocks = new HashSet<>();
        private final HashMap<RelativePos, Boolean> posIsOpen = new HashMap<>();

        public Structure() {
            for (int i : range) for (int j : range) blocks.add(relative(i, j));
            for (int i : range) for (double j : halfRange) posIsOpen.put(relative(i, j), false);
            for (double i : halfRange) for (int j : range) posIsOpen.put(relative(i, j), false);
        }

        private boolean isOpen(@NotNull RelativePos pos1, @NotNull RelativePos pos2) {
            RelativePos mid = relative((pos1.x + pos2.x) / 2, (pos1.z + pos2.z) / 2);
            return posIsOpen.getOrDefault(mid, false);
        } //requires that they are near

        private @NotNull HashSet<RelativePos> getComponent(@NotNull HashSet<RelativePos> set, @NotNull RelativePos pos) {
            HashSet<RelativePos> component = new HashSet<>();
            component.add(pos);
            HashSet<RelativePos> nears = new HashSet<>();
            nears.add(pos.add(1, 0));
            nears.add(pos.add(-1, 0));
            nears.add(pos.add(0, 1));
            nears.add(pos.add(0, -1));
            nears = nears.stream().filter(pos1 -> isOpen(pos1, pos)).filter(set::contains)
                    .collect(Collectors.toCollection(HashSet::new));
            HashSet<RelativePos> excluded = set.stream().filter(pos1 -> !pos1.equals(pos))
                    .collect(Collectors.toCollection(HashSet::new));
            for (RelativePos near : nears) {
                var found = getComponent(excluded, near);
                component.addAll(found);
                excluded.removeAll(found);
            }
            return component;
        }

        private @NotNull HashSet<HashSet<RelativePos>> getComponents() {
            HashSet<HashSet<RelativePos>> ans = new HashSet<>();
            HashSet<RelativePos> remaining = new HashSet<>(blocks);
            while (!remaining.isEmpty()) {
                RelativePos pos1 = remaining.iterator().next();
                HashSet<RelativePos> component = getComponent(remaining, pos1);
                ans.add(component);
                remaining.removeAll(component);
            }
            return ans;
        }

        public HashMap<RelativePos, Boolean> generate() {
            RelativePos[] exclude = {
                    relative(1, 0.5), relative(1, -0.5),
                    relative(-1, 0.5), relative(-1, -0.5),
                    relative(0.5, 1), relative(-0.5, 1),
                    relative(0.5, -1), relative(-0.5, -1),
                    relative(length - 1, length - 0.5), relative(length - 0.5, length - 1),
                    relative(-length + 1, length - 0.5), relative(-length + 0.5, length - 1),
                    relative(length - 1, -length + 0.5), relative(length - 0.5, -length + 1),
                    relative(-length + 1, -length + 0.5), relative(-length + 0.5, -length + 1)
            };
            posIsOpen.put(exclude[world.random.nextInt(8)], true);
            posIsOpen.put(exclude[8 + world.random.nextInt(2)], true);
            posIsOpen.put(exclude[10 + world.random.nextInt(2)], true);
            posIsOpen.put(exclude[12 + world.random.nextInt(2)], true);
            posIsOpen.put(exclude[14 + world.random.nextInt(2)], true);
            int times = 0;
            do {
                times++;
                ArrayList<RelativePos> closed = posIsOpen.entrySet().stream()
                        .filter(entry -> !entry.getValue()).map(Map.Entry::getKey)
                        .collect(Collectors.toCollection(ArrayList::new));
                int count = (closed.size() + 1) / 4;
                for (int i = 0; i < count; i++) {
                    RelativePos open = closed.get(world.random.nextInt(closed.size()));
                    if (Arrays.asList(exclude).contains(open)) continue;
                    closed.remove(open);
                    posIsOpen.put(open, true);
                }
                if (times > 20) {
                    RandomToys.error("Maze Generation Failed: Might Not Be Solvable");
                    return posIsOpen;
                }
            } while (getComponents().size() > 1);
            for (int i : range)
                for (int j : range)
                    if (Math.abs(i) != length && Math.abs(j) != length) {
                        RelativePos[] nears = {
                                relative(i + 0.5, j), relative(i - 0.5, j),
                                relative(i, j + 0.5), relative(i, j - 0.5)
                        };
                        if (Arrays.stream(nears).allMatch(posIsOpen::get))
                            posIsOpen.put(nears[world.random.nextInt(nears.length)], false);
                    }
            return posIsOpen;
        }
    }
}
