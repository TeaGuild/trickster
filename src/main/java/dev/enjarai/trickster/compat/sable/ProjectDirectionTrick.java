package dev.enjarai.trickster.compat.sable;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import org.joml.Vector3d;

public class ProjectDirectionTrick extends Trick<ProjectDirectionTrick> {
    public ProjectDirectionTrick() {
        super(
            Pattern.of(4, 7, 5, 4, 0, 2, 8, 6, 0),
            Signature.of(FragmentType.VECTOR, FragmentType.VECTOR, ProjectDirectionTrick::run, FragmentType.VECTOR)
        );
    }

    private VectorFragment run(SpellContext ctx, VectorFragment pos, VectorFragment dir) {
        var world = ctx.source().getWorld();
        var vec = pos.vector();
        SubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContaining(world, vec);
        if (subLevelAccess == null) {
            return dir;
        }
        var res = subLevelAccess.logicalPose().transformNormal(dir.vector(), new Vector3d());
        return new VectorFragment(res);
    }
}
