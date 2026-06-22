package dev.enjarai.trickster.item;

import dev.enjarai.trickster.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TomeItem extends Item {
    public TomeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(ModSounds.THE_FUNNY);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
