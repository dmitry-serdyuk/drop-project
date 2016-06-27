package com.dmitry.drop.project.model;

import android.os.AsyncTask;

/**
 * Created by Dmitry on 15/06/2016.
 *
 * This class simulates the back end call to an online storage
 */
public class RepliesAsyncLoader extends AsyncTask<Void, Void, Void> {

    private RepliesLoaderListener listener;
    public RepliesAsyncLoader(RepliesLoaderListener listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Simulating backend call
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onSuccess();
    }

    public interface RepliesLoaderListener {
        void onSuccess();

        void onError(Exception e);
    }
}
