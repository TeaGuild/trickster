package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.mana.storage.ManaVariant;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.*;
import java.util.function.Predicate;

public interface SpellSource {
    default Optional<ServerPlayerEntity> getPlayer() {
        return Optional.empty();
    }

    default Optional<Entity> getCaster() {
        return Optional.empty();
    }

    default Optional<Inventory> getInventory() {
        return Optional.empty();
    }

    default Optional<ItemStack> getOtherHandStack() {
        return getOtherHandStack(i -> true);
    }

    default Optional<ItemStack> getOtherHandStack(Predicate<ItemStack> filter) {
        return Optional.empty();
    }

    default Optional<SlotFragment> getOtherHandSlot() {
        return Optional.empty();
    }

    default Optional<SpellExecutionManager> getExecutionManager() {
        return Optional.empty();
    }

    default BlockPos getBlockPos() {
        var pos = getPos();
        return new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
    }

    default Optional<Vector3d> getFacing() {
        return Optional.empty();
    }

    default void showChatMessage(Text message) {
        showMessage(message);
    }

    default void showActionBarMessage(Text message) {
        showMessage(message);
    }

    default void showMessage(Text message) {

    }

    <T extends Component> Optional<T> getComponent(ComponentKey<T> key);

    float getHealth();

    float getMaxHealth();

    Storage<ManaVariant> getManaStorage();

    Vector3d getPos();

    ServerWorld getWorld();

    Fragment getCrowMind();

    void setCrowMind(Fragment fragment);

    void offerOrDropItem(ItemStack stack);

    default Vector3d getGlobalPos() {
        return SableCompanion.INSTANCE.projectOutOfSubLevel(getWorld(), getPos());
    }
}
