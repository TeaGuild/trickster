package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Mana;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

public record ManaFragment(Mana mana) implements Fragment {
    public static final StructEndec<ManaFragment> ENDEC = StructEndecBuilder.of(
        Mana.ENDEC.fieldOf("mana", ManaFragment::mana),
        ManaFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.MANA;
    }

    @Override
    public Text asText() {
        return mana.getName(ManaVariant.of(mana));
    }

    @Override
    public int getWeight() {
        return 16;
    }
}
