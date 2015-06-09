package org.videolan.vlc.dagger.component;

import org.videolan.vlc.dagger.VlcApp;
import org.videolan.vlc.dagger.modules.VlcAppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                VlcAppModule.class,
        }
)
public interface VlcAppComponent {
    void inject(VlcApp app);
}