package com.percolate.dagger.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.percolate.dagger.CaffeineApp;
import com.percolate.dagger.component.CaffeineAppComponent;

public abstract class CaffeineBaseActivity extends AppCompatActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent((CaffeineAppComponent) CaffeineApp.get(this).caffeineAppComponent());

    }


    protected abstract void setupComponent(CaffeineAppComponent caffeineAppComponent);


   }


