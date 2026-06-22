package dev.enjarai.trickster.spell.mana.type;

import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import net.minecraft.component.ComponentMap;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class AstralMana extends Mana {
    public AstralMana() {
        super(ComponentMap.EMPTY);
    }

    @Override
    public boolean permitsTransfer(ManaVariant variant) {
        return false;
    }

    @Override
    public boolean permitsPositiveInterest(ManaVariant variant) {
        return true;
    }

    @Override
    public boolean permitsNegativeInterest(ManaVariant variant) {
        return true;
    }

    @Override
    public int getColor(ManaVariant variant) {
        return 0xfdffa8;
    }

    public MutableText getName(ManaVariant variant) {
        var name = variant.getComponentMap().get(ManaComponents.NAME);

        if (name == null) {
            return Text.translatable("trickster.mana.trickster.astral.default").withColor(getColor(variant));
        } else {
            return Text.translatable("trickster.mana.trickster.astral.named", name).withColor(getColor(variant));
        }
    }
}
