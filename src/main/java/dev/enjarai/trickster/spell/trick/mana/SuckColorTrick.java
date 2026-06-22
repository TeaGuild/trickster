package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.data.DataLoader;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.enjarai.trickster.spell.mana.type.Manae;
import dev.enjarai.trickster.spell.mana.type.component.ColorComponent;
import dev.enjarai.trickster.spell.mana.type.component.ManaComponents;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.component.ComponentChanges;

public class SuckColorTrick extends Trick<SuckColorTrick> {
    public SuckColorTrick() {
        super(
            Pattern.of(0, 4, 6, 3, 0, 1, 2, 4, 8, 5, 2, 7, 0),
            Signature.of(FragmentType.VECTOR, SuckColorTrick::suck, FragmentType.NUMBER)
        );
    }

    public NumberFragment suck(SpellContext ctx, VectorFragment pos) {
        var world = ctx.source().getWorld();
        var blockPos = pos.toBlockPos();
        var state = world.getBlockState(blockPos);
        var conversion = DataLoader
            .getColorSuckingLoader()
            .convert(state.getBlock(), world, blockPos, ctx.source().getPlayer());

        if (conversion == null) {
            throw new BlockInvalidBlunder(this, state.getBlock());
        }

        var particlePos = blockPos.toCenterPos();
        world.spawnParticles(
            ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
            1, 0, 0, 0, 0);

        var insertingVariant = ManaVariant.of(Manae.COLOR).withComponentChanges(
            ComponentChanges.builder().add(ManaComponents.COLOR, new ColorComponent(conversion.color())).build());
        long insertingAmount = (long) (conversion.manaAmount() * ManaPool.MANA_SCALE);
        double taxedMult = insertingVariant.getManaTransferTaxMultiplier(ctx, blockPos.toCenterPos().toVector3d());
        long inserted = 0;

        try (var trans = Transaction.openOuter()) {
            inserted = insertMixing(ctx, ctx.getManaStorage(), insertingVariant, (long) (insertingAmount * taxedMult), trans);

            if (inserted > 0) {
                trans.commit();
            }
        }

        return NumberFragment.ofMana((long) (insertingAmount - inserted / taxedMult));
    }
}
