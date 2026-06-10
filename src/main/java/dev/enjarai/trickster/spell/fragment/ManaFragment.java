package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.mana.type.Mana;
import dev.enjarai.trickster.spell.mana.type.Manae;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.text.Text;

public record ManaFragment(Mana mana) implements Fragment {
    public static final StructEndec<ManaFragment> ENDEC = StructEndecBuilder.of(
        MinecraftEndecs.ofRegistry(Manae.REGISTRY).fieldOf("mana", ManaFragment::mana),
        ManaFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.MANA;
    }

    @Override
    public Text asText() {
        return mana.getName();
    }

    @Override
    public int getWeight() {
        return 16;
    }
}
