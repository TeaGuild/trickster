package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Mana {
    public static final Endec<Mana> ENDEC = MinecraftEndecs.ofRegistry(Manae.REGISTRY);

    private final int color;

    public Mana(int color) {
        this.color = color;
    }

    public boolean isEmpty() {
        return this == Manae.EMPTY;
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
