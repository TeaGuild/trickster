package dev.enjarai.trickster.util;

import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.item.ItemStack;

public class SlotReferenceStorage extends SingleStackStorage {
    private final SlotReference slotReference;

    public SlotReferenceStorage(SlotReference slotReference) {
        this.slotReference = slotReference;
    }

    @Override
    protected ItemStack getStack() {
        return slotReference.getStack();
    }

    @Override
    protected void setStack(ItemStack stack) {
        slotReference.setStack(stack);
    }
}
