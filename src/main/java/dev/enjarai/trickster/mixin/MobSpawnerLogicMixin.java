package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerLogic.class)
public class MobSpawnerLogicMixin {
    @Unique
    private final static int RANGE = 8;

    @Inject(method = "serverTick", at = @At(value = "HEAD"))
    private void playerSpawnerAdvancement(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (EntityPredicates.EXCEPT_SPECTATOR.test(player) && EntityPredicates.VALID_LIVING_ENTITY.test(player)) {
                double d = player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ());
                if (RANGE < 0.0 || d < RANGE * RANGE) {
                    ModCriteria.ACTIVATE_SPAWNER.trigger(player);
                }
            }
        }
    }
}
