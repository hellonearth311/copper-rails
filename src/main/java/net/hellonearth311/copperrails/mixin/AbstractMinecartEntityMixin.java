package net.hellonearth311.copperrails.mixin;

import net.hellonearth311.copperrails.registries.ModBlocks;
import net.minecraft.block.Block;
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
    private void capSpeedOnCopperRails(CallbackInfo ci) {
        AbstractMinecartEntity minecart = (AbstractMinecartEntity) (Object) this;
        World world = minecart.getWorld();
        BlockPos pos = minecart.getBlockPos();

        Block blockBelow = world.getBlockState(pos).getBlock();
        if (blockBelow == ModBlocks.COPPER_RAIL) {
            Vec3d velocity = minecart.getVelocity();

            double maxSpeed = 0.2;

            double currentSpeed = velocity.length();
            if (currentSpeed > maxSpeed) {
                Vec3d cappedVelocity = velocity.normalize().multiply(maxSpeed);
                minecart.setVelocity(cappedVelocity);
            }
        }
    }
}
