package dev.enjarai.trickster.data.conversion;

import com.mojang.datafixers.util.Either;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import nl.enjarai.cicada.api.util.random.Weighted;

import java.util.List;
import java.util.Optional;

public interface Conversion extends Weighted {
    BlockState state();

    Optional<Either<List<String>, Boolean>> keepProperties();

    Optional<NbtCompound> nbt();
}
