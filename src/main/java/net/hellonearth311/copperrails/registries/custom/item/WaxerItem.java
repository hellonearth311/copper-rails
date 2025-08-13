package net.hellonearth311.copperrails.registries.custom.item;

import net.hellonearth311.copperrails.oxidize.Waxable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class WaxerItem extends Item {
    public WaxerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();

        if (state.getBlock() instanceof Waxable waxableBlock) {
            Optional<BlockState> waxedState = waxableBlock.getWaxedState(state);
            if (waxedState.isPresent()) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, context.getStack());
                }

                if (!player.isCreative()) {
                    context.getStack().damage(1, player);
                }

                world.setBlockState(pos, waxedState.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, waxedState.get()));
                world.playSound(player, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        // Remove the old locate command functionality
        return ActionResult.PASS;
    }
}