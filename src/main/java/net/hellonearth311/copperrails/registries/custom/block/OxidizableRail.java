package net.hellonearth311.copperrails.registries.custom.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public interface OxidizableRail {
    OxidationLevel getOxidationLevel();

    default void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    default void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        float degradationChance = this.getDegradationChance();
        if (degradationChance > 0.0f && random.nextFloat() < degradationChance) {
            this.tryDegrade(state, world, pos, random);
        }
    }

    default void tryDegrade(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int affectedBlocks = this.getDegradationResult(world, pos, random);
        if (affectedBlocks > 0) {
            BlockState degradedState = this.getDegradedState(state);
            if (degradedState != state) {
                world.setBlockState(pos, degradedState);
            }
        }
    }

    default int getDegradationResult(ServerWorld world, BlockPos pos, Random random) {
        int affectedBlocks = 0;
        int airBlocks = 0;

        for (BlockPos blockPos : BlockPos.iterateOutwards(pos, 4, 4, 4)) {
            int distance = blockPos.getManhattanDistance(pos);
            if (distance > 4) continue;

            if (world.getBlockState(blockPos).isAir()) {
                airBlocks++;
            }
        }

        float degradationMultiplier = (float)airBlocks / 125.0f;
        if (random.nextFloat() < degradationMultiplier) {
            affectedBlocks++;
        }

        return affectedBlocks;
    }

    default float getDegradationChance() {
        return this.getOxidationLevel() == OxidationLevel.OXIDIZED ? 0.0f : 0.05688889f;
    }

    BlockState getDegradedState(BlockState state);
}
