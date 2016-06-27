package com.dmitry.drop.project.application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

/**
 * Created by Dmitry on 20/06/2016.
 *
 * Application class extends ORM and initialises database
 */
public class Application extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("drop_project.db").create();
        ActiveAndroid.initialize(dbConfiguration);
    }
}
