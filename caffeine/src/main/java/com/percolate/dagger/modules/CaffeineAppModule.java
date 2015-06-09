package com.percolate.dagger.modules;

import android.app.Application;
import android.content.Context;

import com.percolate.dagger.CaffeineApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CaffeineAppModule {

    private CaffeineApp caffeineApp;

    public CaffeineAppModule(CaffeineApp caffeineApp) {
        this.caffeineApp = caffeineApp;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return caffeineApp;
    }

    @Provides
    public Context provideContext() {
        return provideApplication().getApplicationContext();
    }
}
