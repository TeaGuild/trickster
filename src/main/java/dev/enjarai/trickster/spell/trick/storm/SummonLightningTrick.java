package dev.enjarai.trickster.spell.trick.storm;

import dev.enjarai.trickster.mixin.accessor.ServerWorldAccessor;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.EntityType;

public class SummonLightningTrick extends Trick<SummonLightningTrick> {
    public SummonLightningTrick() {
        super(Pattern.of(3, 7, 5, 8, 7, 6, 3, 0, 7, 2, 5), Signature.of(FragmentType.VECTOR, SummonLightningTrick::summon, FragmentType.ENTITY));
    }

    public EntityFragment summon(SpellContext ctx, VectorFragment pos) {
        ctx.useScaledMana(this, variant -> {
            return variant.getMana().equals(Manae.STORM)
                && variant.getComponentMap().get(ManaComponents.STABILITY).getStability(ctx.source().getWorld()) < 0.5;
        }, 5000 + Math.pow(1.1, ctx.source().getPos().distance(pos.vector())));

        var lightning = EntityType.LIGHTNING_BOLT.create(ctx.source().getWorld());
        var finalPos = ((ServerWorldAccessor) ctx.source().getWorld()).callGetLightningPos(pos.toBlockPos());

        lightning.setPosition(finalPos.toBottomCenterPos());
        ctx.source().getWorld().spawnEntity(lightning);

        return EntityFragment.from(lightning);
    }
}
