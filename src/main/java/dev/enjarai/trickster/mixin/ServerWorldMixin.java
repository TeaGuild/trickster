package dev.enjarai.trickster.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import dev.enjarai.trickster.spell.mana.type.component.StabilityComponent;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(
        method = "spawnEntity", at = @At("RETURN")
    )
    private void destabilizeMana(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LightningEntity) {
            var world = entity.getWorld();
            var lightningCharge = ManaVariant.of(Manae.STORM, ComponentChanges.builder().add(ManaComponents.STABILITY, new StabilityComponent(world.getTime())).build());

            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        var pos = entity.getBlockPos().add(x, y, z);
                        var state = world.getBlockState(pos);

                        if (state.isIn(ModBlocks.EXPOSED_KNOT_HOLDERS) && world.getBlockEntity(pos) instanceof Inventory inventory) {
                            for (int i = 0; i < inventory.size(); i++) {
                                var stack = inventory.getStack(i);

                                if (stack.get(ModComponents.MANA) instanceof ManaComponent comp && comp.pool().getVariant(world).isOf(Manae.STORM)) {
                                    var pool = comp.pool().makeClone(world);
                                    Manae.STORM.mix(world, lightningCharge, 5000 * ManaPool.MANA_SCALE, pool.getVariant(world), pool.get(world)).ifPresent(newVariant -> {
                                        pool.setVariant(newVariant, world);
                                        stack.set(ModComponents.MANA, comp.with(pool));
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
