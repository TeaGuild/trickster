package dev.enjarai.trickster.item;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class SpellAccessory extends AccessoryItem {
    public SpellAccessory() {
        super(new Settings().maxCount(1));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        if (reference.entity() instanceof PlayerEntity player && !player.getWorld().isClient()) {
            var caster = ModEntityComponents.CASTER.get(player);
            var fragment = stack.get(ModComponents.FRAGMENT);

            if (fragment != null && fragment.value() instanceof SpellPart spell) {
                caster.setTormentSpell(spell);
                ModCriteria.USE_TORMENT_ON_A_CHAIN.trigger((ServerPlayerEntity) player);
            }
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        var capability = reference.capability();

        if (capability == null) {
            return false;
        }

        int amount = capability.getEquipped(s -> s.getItem() instanceof SpellAccessory).size();

        if (amount == 0) {
            return true;
        }

        if (amount == 1 && reference.getStack().getItem() instanceof SpellAccessory) {
            return true;
        }

        return false;
    }
}
