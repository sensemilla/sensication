package com.percolate.youtube.support;

import com.percolate.dagger.CaffeineApp;


/**
 * Created by LQG on 2014/12/4.
 */
public class GlobalApplication extends CaffeineApp {

    static GlobalApplication instance;

    public static GlobalApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }


}
