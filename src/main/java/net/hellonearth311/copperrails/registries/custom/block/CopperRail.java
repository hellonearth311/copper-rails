package net.hellonearth311.copperrails.registries.custom.block;

import com.mojang.serialization.MapCodec;
import net.hellonearth311.copperrails.registries.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CopperRail extends AbstractRailBlock implements OxidizableRail {
    public static final MapCodec<CopperRail> CODEC = createCodec(CopperRail::new);
    public static final EnumProperty<RailShape> SHAPE = Properties.RAIL_SHAPE;
    private final OxidationLevel oxidationLevel;

    @Override
    public MapCodec<CopperRail> getCodec() {
        return CODEC;
    }

    public CopperRail(OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
        super(false, settings);
        this.oxidationLevel = oxidationLevel;
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH).with(WATERLOGGED, false));
    }

    public CopperRail(AbstractBlock.Settings settings) {
        this(OxidationLevel.UNAFFECTED, settings);
    }

    @Override
    public OxidationLevel getOxidationLevel() {
        return this.oxidationLevel;
    }

    @Override
    public BlockState getDegradedState(BlockState state) {
        return switch (this.oxidationLevel) {
            case UNAFFECTED -> ModBlocks.EXPOSED_COPPER_RAIL.getDefaultState()
                    .with(SHAPE, state.get(SHAPE))
                    .with(WATERLOGGED, state.get(WATERLOGGED));
            case EXPOSED -> ModBlocks.WEATHERED_COPPER_RAIL.getDefaultState()
                    .with(SHAPE, state.get(SHAPE))
                    .with(WATERLOGGED, state.get(WATERLOGGED));
            case WEATHERED -> ModBlocks.OXIDIZED_COPPER_RAIL.getDefaultState()
                    .with(SHAPE, state.get(SHAPE))
                    .with(WATERLOGGED, state.get(WATERLOGGED));
            case OXIDIZED -> state;
        };
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !this.oxidationLevel.isFullyOxidized();
    }

    @Override
    protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
        if (neighbor.getDefaultState().emitsRedstonePower() && new RailPlacementHelper(world, pos, state).getNeighbors().size() == 3) {
            this.updateBlockState(world, pos, state, false);
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        RailShape railShape = state.get(SHAPE);

        return state.with(SHAPE, switch (rotation) {
            case CLOCKWISE_180 -> {
                switch (railShape) {
                    case NORTH_SOUTH:
                        yield RailShape.NORTH_SOUTH;
                    case EAST_WEST:
                        yield RailShape.EAST_WEST;
                    case ASCENDING_EAST:
                        yield RailShape.ASCENDING_WEST;
                    case ASCENDING_WEST:
                        yield RailShape.ASCENDING_EAST;
                    case ASCENDING_NORTH:
                        yield RailShape.ASCENDING_SOUTH;
                    case ASCENDING_SOUTH:
                        yield RailShape.ASCENDING_NORTH;
                    case SOUTH_EAST:
                        yield RailShape.NORTH_WEST;
                    case SOUTH_WEST:
                        yield RailShape.NORTH_EAST;
                    case NORTH_WEST:
                        yield RailShape.SOUTH_EAST;
                    case NORTH_EAST:
                        yield RailShape.SOUTH_WEST;
                    default:
                        throw new MatchException(null, null);
                }
            }
            case COUNTERCLOCKWISE_90 -> {
                switch (railShape) {
                    case NORTH_SOUTH:
                        yield RailShape.EAST_WEST;
                    case EAST_WEST:
                        yield RailShape.NORTH_SOUTH;
                    case ASCENDING_EAST:
                        yield RailShape.ASCENDING_NORTH;
                    case ASCENDING_WEST:
                        yield RailShape.ASCENDING_SOUTH;
                    case ASCENDING_NORTH:
                        yield RailShape.ASCENDING_WEST;
                    case ASCENDING_SOUTH:
                        yield RailShape.ASCENDING_EAST;
                    case SOUTH_EAST:
                        yield RailShape.NORTH_EAST;
                    case SOUTH_WEST:
                        yield RailShape.SOUTH_EAST;
                    case NORTH_WEST:
                        yield RailShape.SOUTH_WEST;
                    case NORTH_EAST:
                        yield RailShape.NORTH_WEST;
                    default:
                        throw new MatchException(null, null);
                }
            }
            case CLOCKWISE_90 -> {
                switch (railShape) {
                    case NORTH_SOUTH:
                        yield RailShape.EAST_WEST;
                    case EAST_WEST:
                        yield RailShape.NORTH_SOUTH;
                    case ASCENDING_EAST:
                        yield RailShape.ASCENDING_SOUTH;
                    case ASCENDING_WEST:
                        yield RailShape.ASCENDING_NORTH;
                    case ASCENDING_NORTH:
                        yield RailShape.ASCENDING_EAST;
                    case ASCENDING_SOUTH:
                        yield RailShape.ASCENDING_WEST;
                    case SOUTH_EAST:
                        yield RailShape.SOUTH_WEST;
                    case SOUTH_WEST:
                        yield RailShape.NORTH_WEST;
                    case NORTH_WEST:
                        yield RailShape.NORTH_EAST;
                    case NORTH_EAST:
                        yield RailShape.SOUTH_EAST;
                    default:
                        throw new MatchException(null, null);
                }
            }
            default -> railShape;
        });
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        RailShape railShape = state.get(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                switch (railShape) {
                    case ASCENDING_NORTH:
                        return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.with(SHAPE, RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    default:
                        return super.mirror(state, mirror);
                }
            case FRONT_BACK:
                switch (railShape) {
                    case ASCENDING_EAST:
                        return state.with(SHAPE, RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.with(SHAPE, RailShape.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return state.with(SHAPE, RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.with(SHAPE, RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.with(SHAPE, RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.with(SHAPE, RailShape.NORTH_WEST);
                    default:
                        break;
                }
        }
        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED);
    }
}
