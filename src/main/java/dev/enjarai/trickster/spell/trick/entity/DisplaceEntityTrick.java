package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.EntityCannotBeDisplacedBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.joml.Vector3d;

import java.util.Set;

public class DisplaceEntityTrick extends Trick<DisplaceEntityTrick> {
    public DisplaceEntityTrick() {
        super(
                Pattern.of(1, 5, 7, 3, 1, 8, 3, 2, 7, 0, 5, 6, 1),
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

        // keep this: relative location calculation
        var distanceFromAnchor = (float) vector.length();
        var manaUse = 20 + (float) Math.pow(1.35, distanceFromAnchor);

        if (!ctx.source().getWorld().getRegistryKey().equals(target.sourceWorld())) {
            var scaledSource = entity.getPos().toVector3d().mul(entity.getWorld().getDimension().coordinateScale());
            var scaledDestination = ctx.source().getPos().mul(ctx.source().getWorld().getDimension().coordinateScale());
            manaUse += (float) (scaledSource.distance(scaledDestination) * 4);
        } else {
            manaUse += (float) (entity.getPos().toVector3d().distance(destination) * 4);
        }

        ctx.useMana(this, manaUse);

        entity.getWorld().playSoundFromEntity(null, entity, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        entity.teleport(ctx.source().getWorld(), destination.x(), destination.y(), destination.z(), Set.of(), entity.getHeadYaw(), entity.getPitch());
        entity.getWorld().playSoundFromEntity(null, entity, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        return VoidFragment.INSTANCE;
    }
}
