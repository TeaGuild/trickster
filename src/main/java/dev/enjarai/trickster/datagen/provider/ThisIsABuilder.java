package dev.enjarai.trickster.datagen.provider;

import dev.enjarai.trickster.data.conversion.Conversion;

import java.util.List;

public interface ThisIsABuilder<T extends Conversion> {
    List<T> build();
}
