package dev.enjarai.trickster.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Vec3d;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class ServerPlayerVelocityComponent implements ServerTickingComponent {
    private final PlayerEntity player;
    private Vec3d lastPosition;
    private Vec3d velocity;

    public ServerPlayerVelocityComponent(PlayerEntity player) {
        this.player = player;
        lastPosition = player.getPos();
    }

    @Override
    public void serverTick() {
        var pos = player.getPos();
        velocity = pos.subtract(lastPosition);
        lastPosition = pos;
    }

    public Vec3d getVelocity() {
        return velocity;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {

    }
}
