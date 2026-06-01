package dev.enjarai.trickster.spell.fragment.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ZalgoFragment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import org.joml.Vector3dc;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public record SlotFragment(StorageSource.Slot slot, VariantType<?> variantType) implements StorageFragment {
    public static final StructEndec<SlotFragment> ENDEC = StructEndecBuilder.of(
        StorageSource.Slot.ENDEC.fieldOf("slot", SlotFragment::slot),
        VariantType.ENDEC.fieldOf("variant_type", SlotFragment::variantType),
        SlotFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.SLOT;
    }

    @Override
    public Text asText() {
        return slot.describe();
    }

    @Override
    public int getWeight() {
        return 32 + slot.getWeight();
    }

    @Override
    public Fragment applyEphemeral() {
        if (slot.isEphemeral()) {
            return new ZalgoFragment();
        }
        return this;
    }

    public static List<SlotFragment> getSlots(Trick<?> trick, SpellContext ctx, StorageSource source, VariantType<?> type) {
        var storage = source.getSlottedStorage(trick, ctx, type);
        var amount = storage.getSlotCount();

        var slots = new ArrayList<SlotFragment>();
        for (int i = 0; i < amount; i++) {
            slots.add(new SlotFragment(new StorageSource.Slot(i, source), type));
        }

        return slots;
    }

    public boolean applyItemModifier(Trick<?> trick, SpellContext ctx, Function<ItemStack, ItemStack> modifier) {
        assertVariantType(trick, VariantType.ITEM);

        var slot = getStorage(trick, ctx, VariantType.ITEM);
        var resource = slot.getResource();
        if (resource.isBlank()) {
            return false;
        }

        try (var trans = Transaction.openOuter()) {
            var total = slot.getAmount();
            var taken = slot.extract(resource, Long.MAX_VALUE, trans);

            if (total != taken) {
                return false;
            }

            var tempStack = resource.toStack(1);
            tempStack = modifier.apply(tempStack);

            var newResource = ItemVariant.of(tempStack);
            if (resource.equals(newResource)) {
                return false;
            }

            var given = slot.insert(newResource, taken, trans);

            if (given != taken) {
                return false;
            }
            trans.commit();
        }

        return true;
    }

    public <T> SingleSlotStorage<T> getStorage(Trick<?> trick, SpellContext ctx, VariantType<T> expectedVariantType) {
        assertVariantType(trick, expectedVariantType);
        return slot().getSelfSlot(trick, ctx, expectedVariantType);
    }

    public SingleSlotStorage<?> getStorage(Trick<?> trick, SpellContext ctx) {
        return slot().getSelfSlot(trick, ctx, variantType);
    }

    public <T> T getResource(Trick<?> trick, SpellContext ctx, VariantType<T> expectedVariantType) {
        assertVariantType(trick, expectedVariantType);
        return slot().getSelfSlot(trick, ctx, expectedVariantType).getResource();
    }

    public long getAmount(Trick<?> trick, SpellContext ctx) {
        return slot().getSelfSlot(trick, ctx, variantType).getAmount();
    }

    public Item getItem(Trick<?> trick, SpellContext ctx) throws BlunderException {
        assertVariantType(trick, VariantType.ITEM);
        return getResource(trick, ctx, VariantType.ITEM).getItem();
    }

    @Override
    public Optional<Vector3dc> getSourcePos(Trick<?> trick, SpellContext ctx) {
        if (slot().source() == StorageSource.Caster.INSTANCE) {
            return Optional.empty();
        } else {
            return Optional.of(slot().source().getPosition(trick, ctx));
        }
    }

    @Override
    public Vector3dc getSourceOrCasterPos(Trick<?> trick, SpellContext ctx) {
        return slot().source().getPosition(trick, ctx);
    }
}
