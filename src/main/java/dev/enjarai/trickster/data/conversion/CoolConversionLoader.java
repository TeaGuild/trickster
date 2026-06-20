package dev.enjarai.trickster.data.conversion;

import net.minecraft.registry.RegistryWrapper;

public class CoolConversionLoader extends SimpleBlockConversionLoader {
    public CoolConversionLoader(RegistryWrapper.WrapperLookup registryLookup) {
        super("cool", registryLookup);
    }
}
