package dev.enjarai.trickster.mixin.client.connector.present;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.block.LightBlockEntity;
import dev.enjarai.trickster.block.LightSconceBlock;
import dev.enjarai.trickster.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import static dev.enjarai.trickster.block.LightBlock.LIGHT_LEVEL;
import static dev.enjarai.trickster.block.LightSconceBlock.HAS_LIGHT;

@Mixin(ClientPlayerInteractionManager.class)
@Pseudo
public class ClientPlayerInteractionManagerMixin {
    @WrapOperation(
        method = "destroyBlock", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onDestroyedByPlayer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;ZLnet/minecraft/world/level/material/FluidState;)Z"
        )
    )
    public boolean replaceSconce(
        BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, boolean b, FluidState fluidState, Operation<Boolean> original, @Local Block block
    ) {
        if (block instanceof LightSconceBlock && blockState.get(HAS_LIGHT) && world.getBlockEntity(blockPos) instanceof LightBlockEntity oldEntity) {
            var colors = oldEntity.getColors();
            var result = original.call(blockState, world, blockPos, player, b, fluidState);

            world.setBlockState(blockPos, ModBlocks.LIGHT.getDefaultState().with(LIGHT_LEVEL, blockState.get(LIGHT_LEVEL)), 0);
            if (world.getBlockEntity(blockPos) instanceof LightBlockEntity newEntity) {
                newEntity.setColors(colors);
            }

            return result;
        }

        return original.call(blockState, world, blockPos, player, b, fluidState);
    }
}
