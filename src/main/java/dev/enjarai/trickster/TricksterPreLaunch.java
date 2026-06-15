package dev.enjarai.trickster;

import dev.enjarai.trickster.util.Hacks;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class TricksterPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        Hacks.addStructPoolEnums();
    }
}
