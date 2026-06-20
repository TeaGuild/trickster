package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import net.minecraft.component.ComponentMap;

public class AntiMana extends Mana {
    public AntiMana() {
        super(ComponentMap.EMPTY);
    }

    @Override
    public int getColor(ManaVariant variant) {
        return 0x330088;
    }

    // TODO invert transfer tax
}
