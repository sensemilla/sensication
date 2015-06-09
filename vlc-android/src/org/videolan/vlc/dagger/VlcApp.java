package org.videolan.vlc.dagger;

import android.content.Context;

import com.special.ResideMenu.ResideMenuApplication;

import org.sense.duckduckgo.DDGApplication;
import org.videolan.vlc.dagger.component.DaggerVlcAppComponent;
import org.videolan.vlc.dagger.component.VlcAppComponent;
import org.videolan.vlc.dagger.modules.VlcAppModule;

//import com.google.mail.GmailApp;

public class VlcApp extends ResideMenuApplication {

    private VlcAppComponent vlcAppComponent;
    static VlcApp vlcAppContext;


    @Override public void onCreate() {
        super.onCreate();
        vlcAppContext = this;
        setupGraph();

    }

    private void setupGraph() {
        vlcAppComponent = DaggerVlcAppComponent.builder()
                .vlcAppModule(new VlcAppModule(this))
                .build();
        vlcAppComponent.inject(this);
    }

    public VlcAppComponent vlcAppComponent() {
        return vlcAppComponent;
    }

    public static VlcApp get(Context context) {
        return vlcAppContext; // todo (App) context.getApplicationContext();
    }
    public static VlcApp getContext() {
        return vlcAppContext;
    }

}
