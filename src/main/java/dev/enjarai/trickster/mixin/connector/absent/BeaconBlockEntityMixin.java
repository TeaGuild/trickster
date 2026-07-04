package dev.enjarai.trickster.mixin.connector.absent;

import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.block.LightBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @ModifyVariable(method = "tick", at = @At("STORE"), index = 14)
    private static int colorBlockColor(int n, @Local(argsOnly = true) World world, @Local(ordinal = 1) BlockPos blockPos) {
        if (world.getBlockEntity(blockPos) instanceof LightBlockEntity color) {
            return color.colors[0];
        }
        return n;
    }
}
