package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import net.minecraft.component.ComponentMap;

public class SimpleMana extends Mana {
    public final int color;

    public SimpleMana(ComponentMap defaultComponents, int color) {
        super(defaultComponents);
        this.color = color;
    }

    @Override
    public boolean permitsPositiveInterest() {
        return false;
    }

    @Override
    public boolean permitsNegativeInterest() {
        return true;
    }

    @Override
    public int getColor(ManaVariant variant) {
        return color;
    }
}
