package dev.enjarai.trickster.item;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class EsotericManaItem extends BottledItem {
    public EsotericManaItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            ModCriteria.DRINK_MANA.trigger(player);
        }

        return super.finishUsing(stack, world, user);
    }
}
