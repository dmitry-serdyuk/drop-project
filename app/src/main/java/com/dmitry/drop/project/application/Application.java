package com.dmitry.drop.project.application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

/**
 * Created by Laptop on 20/06/2016.
 */
public class Application extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("drop_project.db").create();
        ActiveAndroid.initialize(dbConfiguration);
    }
}
