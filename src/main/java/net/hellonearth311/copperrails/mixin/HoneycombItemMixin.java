package net.hellonearth311.copperrails.mixin;

import net.hellonearth311.copperrails.oxidize.Waxable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(HoneycombItem.class)
public class HoneycombItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        // Check if the block implements our Waxable interface
        if (state.getBlock() instanceof Waxable waxableBlock) {
            System.out.println("HoneycombItem used on Waxable block: " + state.getBlock());

            Optional<BlockState> waxedState = waxableBlock.getWaxedState(state);
            if (waxedState.isPresent()) {
                System.out.println("Applying wax to copper rail...");

                if (context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, context.getStack());
                }

                if (!context.getPlayer().isCreative()) {
                    context.getStack().decrement(1);
                }

                world.setBlockState(pos, waxedState.get(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(context.getPlayer(), waxedState.get()));
                world.playSound(context.getPlayer(), pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);

                cir.setReturnValue(ActionResult.SUCCESS);
                return;
            }
        }
    }
}
