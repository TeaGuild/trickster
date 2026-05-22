package dev.enjarai.trickster.spell.trick.projectile;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;

import java.util.Optional;

public class SummonWindChargeTrick extends Trick<SummonWindChargeTrick> {
    public SummonWindChargeTrick() {
        super(Pattern.of(0, 1, 2, 8, 4, 6, 0, 3, 6, 7, 8, 5, 2), Signature.of(FragmentType.VECTOR, FragmentType.SLOT.optionalOfArg(), SummonWindChargeTrick::run, FragmentType.ENTITY));
    }

    public EntityFragment run(SpellContext ctx, VectorFragment pos, Optional<SlotFragment> optionalSlot) {
        var stack = ctx.getStack(this, optionalSlot, s -> s.isOf(Items.WIND_CHARGE)).orElseThrow(() -> new MissingItemBlunder(this));
        var world = ctx.source().getWorld();

        try {
            ctx.useMana(this, cost(ctx.source().getPos().distance(pos.vector())));

            var projectile = EntityType.WIND_CHARGE.create(world);
            projectile.setPos(pos.x(), pos.y(), pos.z());
            ctx.source().getCaster().ifPresent(projectile::setOwner);

            world.spawnEntity(projectile);
            return EntityFragment.from(projectile);
        } catch (Throwable err) {
            ctx.source().offerOrDropItem(stack);
            throw err;
        }
    }

    private float cost(double dist) {
        return (float) (20 + Math.pow(dist, (dist / 3)));
    }
}
