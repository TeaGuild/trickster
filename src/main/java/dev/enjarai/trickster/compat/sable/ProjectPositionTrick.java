package dev.enjarai.trickster.compat.sable;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.ryanhcode.sable.companion.SableCompanion;
import org.joml.Vector3d;

public class ProjectPositionTrick extends Trick<ProjectPositionTrick> {
    public ProjectPositionTrick() {
        super(
            Pattern.of(4, 6, 8, 4, 1, 5, 7, 3, 1),
            Signature.of(FragmentType.VECTOR, ProjectPositionTrick::run, FragmentType.VECTOR)
        );
    }

    private VectorFragment run(SpellContext ctx, VectorFragment vectorFragment) {
        var world = ctx.source().getWorld();
        var pos = vectorFragment.vector();
        var res = SableCompanion.INSTANCE.projectOutOfSubLevel(world, pos, new Vector3d());
        return new VectorFragment(res);
    }
}
