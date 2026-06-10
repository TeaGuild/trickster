package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Mana {
    private final double conversionMultiplier;
    private final int color;

    public Mana(double conversionMultiplier, int color) {
        this.conversionMultiplier = conversionMultiplier;
        this.color = color;
    }

    public boolean isEmpty() {
        return this == Manae.EMPTY;
    }

    public double getConversionMultiplier() {
        return conversionMultiplier;
    }

    public int getColor() {
        return this.color;
    }

    public MutableText getName() {
        var id = Manae.REGISTRY.getId(this);
        if (id == null) {
            return Text.literal("Unregistered");
        }
        return Text.translatable(Trickster.MOD_ID + ".mana." + id.getNamespace() + "." + id.getPath()).withColor(color);
    }
}
