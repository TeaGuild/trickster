package dev.enjarai.trickster.mixin.client.connector.absent;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.block.LightBlockEntity;
import dev.enjarai.trickster.block.LightSconceBlock;
import dev.enjarai.trickster.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static dev.enjarai.trickster.block.LightBlock.LIGHT_LEVEL;
import static dev.enjarai.trickster.block.LightSconceBlock.HAS_LIGHT;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @WrapOperation(
        method = "breakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z")
    )
    private boolean replaceSconce(World instance, BlockPos blockPos, BlockState state, int flags,
        Operation<Boolean> original, @Local World world,
        @Local Block block, @Local(ordinal = 0) BlockState blockState) {
        if (block instanceof LightSconceBlock && blockState.get(HAS_LIGHT) && world.getBlockEntity(blockPos) instanceof LightBlockEntity oldEntity) {
            var colors = oldEntity.getColors();
            var result = original.call(instance, blockPos, state, flags);

            world.setBlockState(blockPos, ModBlocks.LIGHT.getDefaultState().with(LIGHT_LEVEL, blockState.get(LIGHT_LEVEL)), 0);
            if (world.getBlockEntity(blockPos) instanceof LightBlockEntity newEntity) {
                newEntity.setColors(colors);
            }

            return result;
        }

        return original.call(instance, blockPos, state, flags);
    }
}
