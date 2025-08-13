package net.hellonearth311.copperrails.oxidize;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public interface Waxable {

    default ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        System.out.println("Waxable.onUse called with item: " + itemStack.getItem());

        if (itemStack.isOf(Items.HONEYCOMB)) {
            System.out.println("Item is honeycomb, checking for waxed state...");
            Optional<BlockState> waxedState = this.getWaxedState(state);
            System.out.println("Waxed state present: " + waxedState.isPresent());
            if (waxedState.isPresent()) {
                System.out.println("Applying wax to block...");
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemStack);
                }
                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                world.setBlockState(pos, waxedState.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, waxedState.get()));
                world.playSound(player, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }

        if (HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().containsKey(state.getBlock())) {
            System.out.println("Block can be unwaxed, checking for unwaxed state...");
            Optional<BlockState> unwaxedState = this.getUnwaxedState(state);
            System.out.println("Unwaxed state present: " + unwaxedState.isPresent());
            if (unwaxedState.isPresent()) {
                System.out.println("Removing wax from block...");
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemStack);
                }
                world.setBlockState(pos, unwaxedState.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, unwaxedState.get()));
                world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }

        System.out.println("No waxing action taken, returning PASS");
        return ActionResult.PASS;
    }

    Optional<BlockState> getWaxedState(BlockState currentState);
    Optional<BlockState> getUnwaxedState(BlockState currentState);
}
