package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.EntityCannotBeDisplacedBlunder;
import dev.enjarai.trickster.spell.blunder.TooManyDisplacementsBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.DisplaceFragment;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class PrepareDisplaceEntityTrick extends Trick<PrepareDisplaceEntityTrick> {
    public PrepareDisplaceEntityTrick() {
        super(Pattern.of(), Signature.of(FragmentType.ENTITY.wardOf(), PrepareDisplaceEntityTrick::run, FragmentType.DISPLACE));
    }

    public DisplaceFragment run(SpellContext ctx, EntityFragment target) {
        var entity = target
            .getEntity(ctx)
            .orElseThrow(() -> new UnknownEntityBlunder(this));

        if (entity.getType().isIn(ModEntities.CANNOT_DISPLACE)) {
            throw new EntityCannotBeDisplacedBlunder(this, entity);
        }

        ctx.useMana(this, 128);
        return DisplaceFragment.fromEntity(entity)
            .orElseThrow(() -> new TooManyDisplacementsBlunder(this, entity));
    }
}
