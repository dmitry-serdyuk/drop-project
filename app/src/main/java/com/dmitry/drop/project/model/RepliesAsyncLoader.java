package com.dmitry.drop.project.model;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Laptop on 15/06/2016.
 */
public class RepliesAsyncLoader extends AsyncTask<Void, Void, Void> {


    public interface RepliesLoaderListener {
        public void onSuccess();

        public void onError(Exception e);
    }

    private boolean shouldFail;
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
}
