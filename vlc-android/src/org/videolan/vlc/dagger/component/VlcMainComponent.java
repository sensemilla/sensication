package org.videolan.vlc.dagger.component;


import org.videolan.vlc.dagger.VlcActivityScope;
import org.videolan.vlc.dagger.modules.VlcMainModule;

import org.videolan.vlc.gui.AudioPlayerContainerActivity;

import dagger.Component;

@VlcActivityScope
@Component(
        dependencies = VlcAppComponent.class,
        modules = {VlcMainModule.class,

        }
)
public interface VlcMainComponent {
    void inject(AudioPlayerContainerActivity activity);

}