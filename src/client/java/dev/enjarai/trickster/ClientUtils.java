package dev.enjarai.trickster;

import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SubscribeToPoolPacket;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import io.wispforest.lavender.mixin.access.ClientAdvancementManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ClientUtils {
    public static void trySubscribe(ManaComponent manaComponent) {
        if (manaComponent.pool() instanceof SharedManaPool sharedPool &&
            MinecraftClient.getInstance().world != null &&
            ModGlobalComponents.SHARED_MANA.get(MinecraftClient.getInstance().world.getScoreboard()).get(sharedPool.uuid()).isEmpty()) {
            ModNetworking.CHANNEL.clientHandle().send(new SubscribeToPoolPacket(sharedPool.uuid()));
        }
    }

    public static boolean hasScryglass() {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return false;

        var capability = player.accessoriesCapability();
        if (capability == null) return false;

        return capability.isEquipped(i -> i.isIn(ModItems.SCRYGLASS));
    }

    public static boolean hasAdvancement(Identifier id) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return false;

        var advancementHandler = player.networkHandler.getAdvancementHandler();
        var advancement = advancementHandler.get(id);
        if (advancement == null) return false;

        // We can steal this accessor from lavender since we depend on it anyway
        var progress = ((ClientAdvancementManagerAccessor) advancementHandler).lavender$getAdvancementProgresses().get(advancement);
        return progress != null && progress.isDone();
    }
}
