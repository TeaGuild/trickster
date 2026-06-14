package dev.enjarai.trickster.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {
    @Unique
    private boolean destabilizationApplied;

    protected LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
        method = "tick", at = @At("HEAD")
    )
    private void destabilizeMana(CallbackInfo ci) {
        if (!destabilizationApplied) {
            var lightningCharge = ManaVariant.of(Manae.STORM, ComponentChanges.builder().add(ManaComponents.STABILITY, new StabilityComponent(getWorld().getTime())).build());

            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        var pos = getBlockPos().add(x, y, z);
                        var state = getWorld().getBlockState(pos);

                        if (state.isIn(ModBlocks.EXPOSED_KNOT_HOLDERS) && getWorld().getBlockEntity(pos) instanceof Inventory inventory) {
                            for (int i = 0; i < inventory.size(); i++) {
                                var stack = inventory.getStack(i);

                                if (stack.get(ModComponents.MANA) instanceof ManaComponent comp && comp.pool().getVariant(getWorld()).isOf(Manae.STORM)) {
                                    var pool = comp.pool().makeClone(getWorld());
                                    Manae.STORM.mix(getWorld(), lightningCharge, 5000 * ManaPool.MANA_SCALE, pool.getVariant(getWorld()), pool.get(getWorld())).ifPresent(newVariant -> {
                                        pool.setVariant(newVariant, getWorld());
                                        stack.set(ModComponents.MANA, comp.with(pool));
                                    });
                                }
                            }
                        }
                    }
                }
            }

            destabilizationApplied = true;
        }
    }
}
