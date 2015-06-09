package com.percolate.dagger.modules;

import android.app.Activity;

import com.percolate.dagger.bus.BusProvider;
import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;

@Module
public class CaffeineMainModule {

    private final Activity activity;

    public CaffeineMainModule(Activity activity) {
    this.activity = activity;
    }


    @Provides
    public Bus provideBus() {
        return BusProvider.getInstance();
    }

}
