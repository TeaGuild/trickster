package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.player.PlayerEntity;

public class GetVelocityTrick extends Trick<GetVelocityTrick> {
    public GetVelocityTrick() {
        super(Pattern.of(1, 8, 3), Signature.of(FragmentType.ENTITY, GetVelocityTrick::run, FragmentType.VECTOR));
    }

    public VectorFragment run(SpellContext ctx, EntityFragment entity) {
        var realEntity = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (realEntity instanceof PlayerEntity) {
            var velocity = ModEntityComponents.SERVER_PLAYER_VELOCITY.get(realEntity).getVelocity();
            return VectorFragment.of(velocity);
        }

        return VectorFragment.of(realEntity.getVelocity());
    }
}
