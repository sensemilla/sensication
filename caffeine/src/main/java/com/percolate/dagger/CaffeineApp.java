package com.percolate.dagger;

import android.app.Application;
import android.content.Context;

import com.percolate.dagger.component.CaffeineAppComponent;
import com.percolate.dagger.component.DaggerCaffeineAppComponent;
import com.percolate.dagger.modules.CaffeineAppModule;

//import com.google.mail.GmailApp;

public class CaffeineApp extends Application {

    private CaffeineAppComponent caffeineAppComponent;
    static CaffeineApp caffeineAppContext;


    @Override public void onCreate() {
        super.onCreate();
        caffeineAppContext = this;
        setupGraph();

    }

    private void setupGraph() {
        caffeineAppComponent = DaggerCaffeineAppComponent.builder()
                .caffeineAppModule(new CaffeineAppModule(this))
                .build();
        caffeineAppComponent.inject(this);
    }

    public CaffeineAppComponent caffeineAppComponent() {
        return caffeineAppComponent;
    }

    public static CaffeineApp get(Context context) {
        return caffeineAppContext; // todo (App) context.getApplicationContext();
    }
    public static CaffeineApp getContext() {
        return caffeineAppContext;
    }

}
