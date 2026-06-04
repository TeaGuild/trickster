package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.EntityCannotBeDisplacedBlunder;
import dev.enjarai.trickster.spell.blunder.InvalidDisplacementBlunder;
import dev.enjarai.trickster.spell.blunder.NotEnoughRoomToDisplaceBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import org.joml.Vector3d;

public class DisplaceEntityTrick extends Trick<DisplaceEntityTrick> {
    public DisplaceEntityTrick() {
        super(
            Pattern.of(2, 5, 8).add(Pattern.of(1, 5, 7)).add(Pattern.of(0, 5, 6)),
            Signature.of(FragmentType.DISPLACE, FragmentType.VECTOR, DisplaceEntityTrick::run, FragmentType.VOID)
        );
    }

    public VoidFragment run(SpellContext ctx, DisplaceFragment target, VectorFragment v) {
        var entity = target
            .getEntity(ctx)
            .orElseThrow(() -> new UnknownEntityBlunder(this));
        var vector = v.vector();
        var destination = ctx.source().getPos().add(vector, new Vector3d());

        if (entity.getType().isIn(ModEntities.CANNOT_DISPLACE)) {
            throw new EntityCannotBeDisplacedBlunder(this, entity);
        }

        if (!target.isValid(ctx)) {
            throw new InvalidDisplacementBlunder(this);
        }

        var destinationWorld = ctx.source().getWorld();
        var inVoid = destination.y < destinationWorld.getDimension().minY();
        if (inVoid || !destinationWorld.isSpaceEmpty(entity,
            entity.getDimensions(entity.getPose()).getBoxAt(destination.x, destination.y, destination.z))) {

            throw new NotEnoughRoomToDisplaceBlunder(this, entity, new VectorFragment(destination));
        }

        // keep this: relative location calculation
        var distanceFromAnchor = (float) vector.length();
        var manaUse = 20 + (float) Math.pow(1.35, distanceFromAnchor);

        if (!destinationWorld.getRegistryKey().equals(target.sourceWorld())) {
            var scaledSource = entity.getPos().toVector3d().mul(entity.getWorld().getDimension().coordinateScale());
            var scaledDestination = ctx.source().getPos().mul(destinationWorld.getDimension().coordinateScale());
            manaUse += 1000;
            manaUse += (float) (scaledSource.distance(scaledDestination) * 3);
        } else {
            manaUse += (float) (entity.getPos().toVector3d().distance(ctx.source().getPos()) * 3);
        }

        ctx.useScaledMana(this, manaUse);

        var instant = ctx.source().getCaster()
            .map(c -> c.getUuid().equals(entity.getUuid()))
            .orElse(false);
        target.consume(ctx, destinationWorld, destination, instant);

        return VoidFragment.INSTANCE;
    }
}
