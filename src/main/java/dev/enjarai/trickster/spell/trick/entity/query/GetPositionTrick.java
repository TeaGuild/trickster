package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.DisplaceFragment;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3d;

public class GetPositionTrick extends Trick<GetPositionTrick> {
    public GetPositionTrick() {
        super(
            Pattern.of(1, 4, 7, 3, 1, 5, 7),
            Signature.of(FragmentType.ENTITY, GetPositionTrick::fromEntity, FragmentType.VECTOR)
        );
        overload(Signature.of(FragmentType.DISPLACE, GetPositionTrick::fromDisplacement, FragmentType.VECTOR));
    }

    public VectorFragment fromEntity(SpellContext ctx, EntityFragment entity) {
        var pos = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getPos();
        return new VectorFragment(new Vector3d(pos.x, pos.y, pos.z));
    }

    public VectorFragment fromDisplacement(SpellContext ctx, DisplaceFragment displacement) {
        var pos = displacement.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)).getPos();
        return new VectorFragment(new Vector3d(pos.x, pos.y, pos.z));
    }
}
