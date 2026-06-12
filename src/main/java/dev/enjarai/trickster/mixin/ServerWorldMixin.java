package dev.enjarai.trickster.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.storage.ManaStorage;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import dev.enjarai.trickster.spell.mana.type.component.StabilityComponent;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(
        method = "spawnEntity", at = @At("RETURN")
    )
    private void meowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LightningEntity) {
            var entityWorld = entity.getWorld();
            var lightningCharge = ManaVariant.of(Manae.STORM, ComponentChanges.builder().add(ManaComponents.STABILITY, new StabilityComponent(entityWorld.getTime())).build());

            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        var pos = entity.getBlockPos().add(x, y, z);

                        if (entityWorld.getBlockState(pos).isIn(ModBlocks.EXPOSED_KNOT_HOLDERS)) {
                            if (ItemStorage.SIDED.find(entityWorld, pos, null) instanceof SlottedStorage<ItemVariant> itemStorage) {
                                for (var slot : itemStorage.getSlots()) {
                                    var manaStorage = ManaStorage.ITEM.find(
                                        slot.getResource().toStack(),
                                        new ManaStorage.LookupContext(ContainerItemContext.ofSingleSlot(slot), entityWorld)
                                    );

                                    if (manaStorage == null) continue;

                                    for (var view : manaStorage.nonEmptyViews()) {
                                        var variant = view.getResource();
                                        if (variant.isOf(Manae.STORM)) {
                                            try (var trans = Transaction.openOuter()) {
                                                var extracted = view.extract(variant, Long.MAX_VALUE, trans);
                                                var newVariant = Manae.STORM.mix(entityWorld, lightningCharge, 5000 * ManaPool.MANA_SCALE, variant, extracted);

                                                if (newVariant.isPresent()) {
                                                    var inserted = manaStorage.insert(newVariant.get(), extracted, trans);
                                                    if (inserted == extracted) {
                                                        trans.commit();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
