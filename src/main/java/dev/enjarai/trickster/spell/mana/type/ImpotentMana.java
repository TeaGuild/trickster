package dev.enjarai.trickster.spell.mana.type;

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

    // TODO invert transfer tax
}
