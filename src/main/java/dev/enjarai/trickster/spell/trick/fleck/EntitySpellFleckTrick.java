package dev.enjarai.trickster.spell.trick.fleck;

import dev.enjarai.trickster.fleck.EntityPosition;
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

public class EntitySpellFleckTrick extends AbstractFleckTrick<EntitySpellFleckTrick> {
    public EntitySpellFleckTrick() {
        super(
            Pattern.of(4, 7, 8, 5, 2, 1, 0, 3, 6, 7),
            Signature.of(FragmentType.NUMBER, FragmentType.ENTITY, FragmentType.VECTOR, FragmentType.VECTOR, FragmentType.SPELL_PART,
                FragmentType.ENTITY.variadicOfArg().unpack().optionalOfArg(), EntitySpellFleckTrick::run, FragmentType.NUMBER)
        );
    }

    public NumberFragment run(SpellContext ctx, NumberFragment id, EntityFragment entity, VectorFragment position, VectorFragment facing, SpellPart spell, Optional<List<EntityFragment>> targets) {
        return display(ctx, id, new SpellFleck(
            new EntityPosition(
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
