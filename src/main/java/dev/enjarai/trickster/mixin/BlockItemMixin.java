package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.enjarai.trickster.block.LightBlock;
import dev.enjarai.trickster.block.LightBlockEntity;
import dev.enjarai.trickster.block.LightSconceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static dev.enjarai.trickster.block.LightBlock.LIGHT_LEVEL;
import static dev.enjarai.trickster.block.LightSconceBlock.HAS_LIGHT;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @WrapOperation(
        method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z")
    )
    private boolean replaceLight(BlockItem instance, ItemPlacementContext context, BlockState state, Operation<Boolean> original) {
        if (instance.getBlock() instanceof LightSconceBlock) {
            var pos = context.getBlockPos();
            var world = context.getWorld();
            var oldState = world.getBlockState(pos);

            if (oldState.getBlock() instanceof LightBlock && world.getBlockEntity(pos) instanceof LightBlockEntity oldEntity) {
                var colors = oldEntity.getColors();
                var result = original.call(instance, context, state);

                world.setBlockState(pos, state
                    .with(LIGHT_LEVEL, oldState.get(LIGHT_LEVEL))
                    .with(HAS_LIGHT, true), 0);
                if (world.getBlockEntity(pos) instanceof LightBlockEntity newEntity) {
                    newEntity.setColors(colors);
                }

                return result;
            }
        }

        return original.call(instance, context, state);
    }
}
