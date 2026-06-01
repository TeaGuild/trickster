package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.fleck.EntityHeadPosition;
import dev.enjarai.trickster.fleck.SpellFleck;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class EntityHeadSpellFleckTrick extends AbstractFleckTrick<EntityHeadSpellFleckTrick> {
    public EntityHeadSpellFleckTrick() {
        super(
            Pattern.of(4, 1, 2, 5, 8, 7, 6, 3, 0, 1),
            Signature.of(FragmentType.NUMBER, FragmentType.ENTITY, FragmentType.VECTOR, FragmentType.VECTOR, FragmentType.SPELL_PART,
                FragmentType.ENTITY.variadicOfArg().unpack().optionalOfArg(), EntityHeadSpellFleckTrick::run, FragmentType.NUMBER)
        );
    }

    public NumberFragment run(SpellContext ctx, NumberFragment id, EntityFragment entity, VectorFragment position, VectorFragment facing, SpellPart spell, Optional<List<EntityFragment>> targets) {
        return display(ctx, id, new SpellFleck(
            new EntityHeadPosition(
                entity.getEntity(ctx).orElseThrow(() -> new EntityInvalidBlunder(this)).getId(),
                position.vector().get(new Vector3f())
            ),
            facing.vector().get(new Vector3f()),
            spell,
            1f,
            0f
        ), targets);
    }
}
