package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.util.Unit;
import net.minecraft.text.MutableText;

public class NotEnoughManaBlunder extends TrickBlunderException {
    private final long required;

    public NotEnoughManaBlunder(Trick<?> source, long required) {
        super(source);
        this.required = required;
    }

    @Override
    public MutableText createMessage() {
        var unit = Unit.getGandalfUnit(required);

        return super.createMessage()
            .append("Not enough mana, at least ")
            .append(formatFloat((float) unit.correct(required)))
            .append(" ")
            .append(unit.shortName())
            .append(" are required.");
    }
}
