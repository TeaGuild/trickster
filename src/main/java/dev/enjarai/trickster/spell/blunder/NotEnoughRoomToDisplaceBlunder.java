package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;

public class NotEnoughRoomToDisplaceBlunder extends TrickBlunderException {
    private final Entity entity;
    private final VectorFragment pos;

    public NotEnoughRoomToDisplaceBlunder(Trick<?> source, Entity entity, VectorFragment pos) {
        super(source);
        this.entity = entity;
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("There is not enough room for ").append(entity.getType().getName()).append(" at ").append(pos.asFormattedText());
    }
}
