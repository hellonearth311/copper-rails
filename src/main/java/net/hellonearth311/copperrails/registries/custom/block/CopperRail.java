package net.hellonearth311.copperrails.registries.custom.block;

import com.mojang.serialization.MapCodec;
import net.hellonearth311.copperrails.oxidize.OxidationLevel;
import net.hellonearth311.copperrails.oxidize.OxidizableRail;
import net.hellonearth311.copperrails.oxidize.Waxable;
import net.hellonearth311.copperrails.registries.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Optional;

public class CopperRail extends AbstractRailBlock implements OxidizableRail, Waxable {
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
            case OXIDIZED, WAXED_UNAFFECTED, WAXED_EXPOSED, WAXED_WEATHERED, WAXED_OXIDIZED -> state;
        };
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    @Deprecated
    public ActionResult use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        System.out.println("CopperRail.use (deprecated) called with item: " + player.getStackInHand(hand).getItem());
        return this.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public Optional<BlockState> getWaxedState(BlockState currentState) {
        if (this.oxidationLevel.isWaxed()) {
            return Optional.empty();
        }

        Block waxedBlock = switch (this.oxidationLevel) {
            case UNAFFECTED -> ModBlocks.WAXED_COPPER_RAIL;
            case EXPOSED -> ModBlocks.WAXED_EXPOSED_COPPER_RAIL;
            case WEATHERED -> ModBlocks.WAXED_WEATHERED_COPPER_RAIL;
            case OXIDIZED -> ModBlocks.WAXED_OXIDIZED_COPPER_RAIL;
            default -> null;
        };

        if (waxedBlock != null) {
            return Optional.of(waxedBlock.getDefaultState()
                    .with(SHAPE, currentState.get(SHAPE))
                    .with(WATERLOGGED, currentState.get(WATERLOGGED)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<BlockState> getUnwaxedState(BlockState currentState) {
        if (!this.oxidationLevel.isWaxed()) {
            return Optional.empty();
        }

        Block unwaxedBlock = switch (this.oxidationLevel) {
            case WAXED_UNAFFECTED -> ModBlocks.COPPER_RAIL;
            case WAXED_EXPOSED -> ModBlocks.EXPOSED_COPPER_RAIL;
            case WAXED_WEATHERED -> ModBlocks.WEATHERED_COPPER_RAIL;
            case WAXED_OXIDIZED -> ModBlocks.OXIDIZED_COPPER_RAIL;
            default -> null;
        };

        if (unwaxedBlock != null) {
            return Optional.of(unwaxedBlock.getDefaultState()
                    .with(SHAPE, currentState.get(SHAPE))
                    .with(WATERLOGGED, currentState.get(WATERLOGGED)));
        }
        return Optional.empty();
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !this.oxidationLevel.isFullyOxidized() && !this.oxidationLevel.isWaxed();
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

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = stack.getItem();

        // Handle waxing with honeycomb
        if (item instanceof HoneycombItem) {
            Optional<BlockState> waxedState = this.getWaxedState(state);
            if (waxedState.isPresent()) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                }

                if (!player.isCreative()) {
                    stack.decrement(1);
                }

                world.setBlockState(pos, waxedState.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.playSound(player, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }

        // Handle unwaxing with axe
        if (item instanceof AxeItem) {
            Optional<BlockState> unwaxedState = this.getUnwaxedState(state);
            if (unwaxedState.isPresent()) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
                }

                world.setBlockState(pos, unwaxedState.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
