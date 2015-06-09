package com.percolate.dagger.component;

import com.percolate.dagger.CaffeineApp;
import com.percolate.dagger.modules.CaffeineAppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                CaffeineAppModule.class,
        }
)
public interface CaffeineAppComponent {
    void inject(CaffeineApp app);
}