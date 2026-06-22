package dev.enjarai.trickster.spell.mana.type;

import org.joml.Vector3dc;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import net.minecraft.component.ComponentMap;

public class AntiMana extends Mana {
    public AntiMana() {
        super(ComponentMap.EMPTY);
    }

    @Override
    public boolean permitsTransfer(ManaVariant variant) {
        return true;
    }

    @Override
    public boolean permitsPositiveInterest(ManaVariant variant) {
        return true;
    }

    @Override
    public boolean permitsNegativeInterest(ManaVariant variant) {
        return false;
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
