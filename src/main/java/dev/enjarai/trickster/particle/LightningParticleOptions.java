package dev.enjarai.trickster.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public record LightningParticleOptions(int color, double targetX, double targetY, double targetZ) implements ParticleEffect {

    public static final MapCodec<LightningParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.INT.optionalFieldOf("color", 0xffffffff).forGetter(LightningParticleOptions::color),
        Codec.DOUBLE.fieldOf("target_x").forGetter(LightningParticleOptions::targetX),
        Codec.DOUBLE.fieldOf("target_y").forGetter(LightningParticleOptions::targetY),
        Codec.DOUBLE.fieldOf("target_z").forGetter(LightningParticleOptions::targetZ)
    ).apply(instance, LightningParticleOptions::new));
    public static final PacketCodec<RegistryByteBuf, LightningParticleOptions> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, LightningParticleOptions::color,
        PacketCodecs.DOUBLE, LightningParticleOptions::targetX,
        PacketCodecs.DOUBLE, LightningParticleOptions::targetY,
        PacketCodecs.DOUBLE, LightningParticleOptions::targetZ,
        LightningParticleOptions::new
    );

    @Override
    public ParticleType<?> getType() {
        return ModParticles.LIGHTNING;
    }
}
