package net.hellonearth311.copperrails.mixin;

import net.hellonearth311.copperrails.registries.custom.block.OxidizableRail;
import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void applyOxidationSpeedEffect(CallbackInfo ci) {
        AbstractMinecartEntity minecart = (AbstractMinecartEntity) (Object) this;
        World world = minecart.getWorld();
        BlockPos pos = minecart.getBlockPos();

        // Check the block the minecart is on
        BlockState blockState = world.getBlockState(pos);

        // If it's not an oxidizable rail, check one block down
        if (!(blockState.getBlock() instanceof OxidizableRail)) {
            blockState = world.getBlockState(pos.down());
        }

        // Apply speed reduction based on oxidation level
        if (blockState.getBlock() instanceof OxidizableRail oxidizableRail) {
            float speedMultiplier = oxidizableRail.getOxidationLevel().getSpeedMultiplier();

            // Only apply if the multiplier is less than 1.0 (i.e., oxidized)
            if (speedMultiplier < 1.0f) {
                Vec3d velocity = minecart.getVelocity();

                // Apply the speed reduction
                Vec3d newVelocity = velocity.multiply(speedMultiplier, 1.0, speedMultiplier);
                minecart.setVelocity(newVelocity);
            }
        }
    }
}
