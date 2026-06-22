package dev.enjarai.trickster.spell.mana.type;

import org.joml.Vector3dc;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import net.minecraft.component.ComponentMap;

public class ImpotentMana extends Mana {
    public ImpotentMana() {
        super(ComponentMap.EMPTY);
    }

    @Override
    public boolean permitsTransfer(ManaVariant variant) {
        return true;
    }

    @Override
    public float modifyInterest(ManaVariant variant, float interest) {
        if (interest >= 0) {
            return 0;
        } else {
            return -interest;
        }
    }

    @Override
    public int getColor(ManaVariant variant) {
        return 0x330088;
    }

    @Override
    public double getManaTransferTaxMultiplier(ManaVariant variant, SpellContext ctx, Vector3dc pos) {
        return 2 - super.getManaTransferTaxMultiplier(variant, ctx, pos);
    }
}
