package dev.enjarai.trickster.item;

import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.mana.type.Mana;
import net.minecraft.item.ItemStack;

public interface ConverterItem {
    @Nullable
    Mana convert(SpellContext ctx, ItemStack stack);
}
