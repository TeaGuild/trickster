package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.block.LightBlockEntity;
import dev.enjarai.trickster.block.LightSconceBlock;
import dev.enjarai.trickster.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static dev.enjarai.trickster.block.LightBlock.LIGHT_LEVEL;
import static dev.enjarai.trickster.block.LightSconceBlock.HAS_LIGHT;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow
    protected ServerWorld world;

    @Definition(id = "world", field = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;world:Lnet/minecraft/server/world/ServerWorld;")
    @Definition(id = "removeBlock", method = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z")
    @Expression("this.world.removeBlock(?, false)")
    @WrapOperation(
        method = "tryBreakBlock", at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean replaceSconce(ServerWorld instance, BlockPos blockPos, boolean b, Operation<Boolean> original,
        @Local Block block, @Local(ordinal = 0) BlockState blockState) {
        if (block instanceof LightSconceBlock && blockState.get(HAS_LIGHT) && world.getBlockEntity(blockPos) instanceof LightBlockEntity oldEntity) {
            var colors = oldEntity.getColors();
            var result = original.call(instance, blockPos, b);

            world.setBlockState(blockPos, ModBlocks.LIGHT.getDefaultState().with(LIGHT_LEVEL, blockState.get(LIGHT_LEVEL)), 0);
            if (world.getBlockEntity(blockPos) instanceof LightBlockEntity newEntity) {
                newEntity.setColors(colors);
            }

            return result;
        }

        return original.call(instance, blockPos, b);
    }
}
