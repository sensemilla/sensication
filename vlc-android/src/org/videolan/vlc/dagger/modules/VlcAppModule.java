package org.videolan.vlc.dagger.modules;

import android.app.Application;
import android.content.Context;

import org.videolan.vlc.dagger.VlcApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class VlcAppModule {

    private VlcApp vlcApp;

    public VlcAppModule(VlcApp vlcApp) {
        this.vlcApp = vlcApp;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return vlcApp;
    }

    @Provides
    public Context provideContext() {
        return provideApplication().getApplicationContext();
    }
}
