package dev.enjarai.trickster.screen.owo;

import dev.enjarai.trickster.ClientUtils;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Mana;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ManaCostComponent extends FlowLayout {

    public ManaCostComponent(String costCalculation, Mana mana, Identifier bookTexture) {
        super(Sizing.fill(100), Sizing.fixed(3), io.wispforest.owo.ui.container.FlowLayout.Algorithm.VERTICAL);

        var tooltip = Text.translatable("trickster.mana_cost.costs_mana")
            .append(Text.literal("\n"))
            .append(Text.literal(costCalculation)
                .styled(s -> s.withFormatting(Formatting.GRAY)));
        if (ClientUtils.hasAdvancement(Trickster.id("unusual_mana"))) {
            tooltip
                .append(Text.literal("\n\n"))
                .append(Text.translatable("trickster.mana_cost.type", mana.getName(ManaVariant.of(mana))));
        }

        child(Components.texture(bookTexture, 54, 240, 109, 5, 512, 256)
            .blend(true)
            .id("cost-texture")
            .positioning(Positioning.absolute(0, -1))
            .tooltip(tooltip))
            .allowOverflow(true)
            .horizontalAlignment(HorizontalAlignment.CENTER);
    }
}
