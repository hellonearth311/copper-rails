package net.hellonearth311.copperrails.registries.custom.block;

import com.mojang.serialization.MapCodec;
import net.hellonearth311.copperrails.oxidize.OxidationLevel;
import net.hellonearth311.copperrails.oxidize.OxidizableRail;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PoweredCopperRail extends AbstractRailBlock implements OxidizableRail {
    public static final MapCodec<PoweredCopperRail> CODEC = createCodec(PoweredCopperRail::new);
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final OxidationLevel oxidationLevel;

    @Override
    public MapCodec<PoweredCopperRail> getCodec() {
        return CODEC;
    }

    public PoweredCopperRail(OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
        super(true, settings);
        this.oxidationLevel = oxidationLevel;
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH).with(POWERED, false).with(WATERLOGGED, false));
    }

    public PoweredCopperRail(AbstractBlock.Settings settings) {
        this(OxidationLevel.UNAFFECTED, settings);
    }

    @Override
    public OxidationLevel getOxidationLevel() {
        return this.oxidationLevel;
    }

    @Override
    public BlockState getDegradedState(BlockState state) {
        BlockState degradedState = getNextOxidationState(this.oxidationLevel, state);
        return degradedState != null ? degradedState : state;
    }

    private BlockState getNextOxidationState(OxidationLevel currentLevel, BlockState currentState) {
        return currentState;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !this.oxidationLevel.isFullyOxidized();
    }

    protected boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean bl, int distance) {
        if (distance >= 8) {
            return false;
        } else {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            boolean bl2 = true;
            RailShape railShape = state.get(SHAPE);
            switch (railShape) {
                case NORTH_SOUTH:
                    if (bl) {
                        k++;
                    } else {
                        k--;
                    }
                    break;
                case EAST_WEST:
                    if (bl) {
                        i++;
                    } else {
                        i--;
                    }
                    break;
                case ASCENDING_EAST:
                    if (bl) {
                        i++;
                    } else {
                        k++;
                        j--;
                        bl2 = false;
                    }
                    railShape = RailShape.EAST_WEST;
                    break;
                case ASCENDING_WEST:
                    if (bl) {
                        i--;
                    } else {
                        k++;
                        j--;
                        bl2 = false;
                    }
                    railShape = RailShape.EAST_WEST;
                    break;
                case ASCENDING_NORTH:
                    if (bl) {
                        k--;
                    } else {
                        i++;
                        j--;
                        bl2 = false;
                    }
                    railShape = RailShape.NORTH_SOUTH;
                    break;
                case ASCENDING_SOUTH:
                    if (bl) {
                        k++;
                    } else {
                        i++;
                        j--;
                        bl2 = false;
                    }
                    railShape = RailShape.NORTH_SOUTH;
            }

            if (this.findPoweredRailSignal(world, new BlockPos(i, j, k), bl2, distance, railShape) || this.findPoweredRailSignal(world, new BlockPos(i, j + 1, k), bl2, distance, railShape)) {
                return true;
            } else {
                return false;
            }
        }
    }

    protected boolean findPoweredRailSignal(World world, BlockPos pos, boolean bl, int distance, RailShape railShape) {
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isOf(this)) {
            return false;
        } else {
            RailShape railShape2 = blockState.get(SHAPE);
            if (railShape != RailShape.EAST_WEST || railShape2 != RailShape.NORTH_SOUTH && railShape2 != RailShape.ASCENDING_NORTH && railShape2 != RailShape.ASCENDING_SOUTH) {
                if (railShape != RailShape.NORTH_SOUTH || railShape2 != RailShape.EAST_WEST && railShape2 != RailShape.ASCENDING_EAST && railShape2 != RailShape.ASCENDING_WEST) {
                    if (blockState.get(POWERED)) {
                        return world.isReceivingRedstonePower(pos) || this.isPoweredByOtherRails(world, pos, blockState, true, distance + 1) || this.isPoweredByOtherRails(world, pos, blockState, false, distance + 1);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
        boolean bl = state.get(POWERED);
        boolean bl2 = world.isReceivingRedstonePower(pos) || this.isPoweredByOtherRails(world, pos, state, true, 0) || this.isPoweredByOtherRails(world, pos, state, false, 0);
        if (bl2 != bl) {
            world.setBlockState(pos, state.with(POWERED, bl2), Block.NOTIFY_ALL);
            world.updateNeighborsAlways(pos.down(), this);
            if (state.get(SHAPE).isAscending()) {
                world.updateNeighborsAlways(pos.up(), this);
            }
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> {
                yield switch (state.get(SHAPE)) {
                    case ASCENDING_EAST -> state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST -> state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH -> state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH -> state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case NORTH_SOUTH, EAST_WEST -> state;
                    default -> state;
                };
            }
            case COUNTERCLOCKWISE_90 -> {
                yield switch (state.get(SHAPE)) {
                    case NORTH_SOUTH -> state.with(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST -> state.with(SHAPE, RailShape.NORTH_SOUTH);
                    case ASCENDING_EAST -> state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_WEST -> state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_NORTH -> state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_SOUTH -> state.with(SHAPE, RailShape.ASCENDING_EAST);
                    default -> state;
                };
            }
            case CLOCKWISE_90 -> {
                yield switch (state.get(SHAPE)) {
                    case NORTH_SOUTH -> state.with(SHAPE, RailShape.EAST_WEST);
                    case EAST_WEST -> state.with(SHAPE, RailShape.NORTH_SOUTH);
                    case ASCENDING_EAST -> state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_WEST -> state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case ASCENDING_NORTH -> state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case ASCENDING_SOUTH -> state.with(SHAPE, RailShape.ASCENDING_WEST);
                    default -> state;
                };
            }
            default -> state;
        };
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        RailShape railShape = state.get(SHAPE);
        return switch (mirror) {
            case LEFT_RIGHT -> {
                yield switch (railShape) {
                    case ASCENDING_NORTH -> state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH -> state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    default -> super.mirror(state, mirror);
                };
            }
            case FRONT_BACK -> {
                yield switch (railShape) {
                    case ASCENDING_EAST -> state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST -> state.with(SHAPE, RailShape.ASCENDING_EAST);
                    default -> super.mirror(state, mirror);
                };
            }
            default -> super.mirror(state, mirror);
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED, WATERLOGGED);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, net.minecraft.entity.Entity entity) {
        if (!world.isClient && entity instanceof net.minecraft.entity.vehicle.AbstractMinecartEntity minecart) {
            if (state.get(POWERED)) {
                net.minecraft.util.math.Vec3d velocity = minecart.getVelocity();
                double speed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

                if (speed > 0.01) {
                    double multiplier = 1.06;
                    minecart.setVelocity(velocity.x * multiplier, velocity.y, velocity.z * multiplier);
                } else {
                    RailShape shape = state.get(SHAPE);
                    double pushForce = 0.02;

                    switch (shape) {
                        case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH ->
                            minecart.setVelocity(0, 0, pushForce);
                        case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST ->
                            minecart.setVelocity(pushForce, 0, 0);
                    }
                }
            }
        }
    }
}
