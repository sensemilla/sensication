package com.percolate.dagger.component;


import com.percolate.dagger.CaffeineActivityScope;
import com.percolate.dagger.bus.BusProvider;
import com.percolate.dagger.modules.CaffeineMainModule;
import com.percolate.youtube.ui.search.YouTubeActivity;

import dagger.Component;

@CaffeineActivityScope
@Component(
        dependencies = CaffeineAppComponent.class,
        modules = {CaffeineMainModule.class,

        }
)
public interface CaffeineMainComponent {
    void inject(YouTubeActivity activity);
    void inject(BusProvider busProvider);
}