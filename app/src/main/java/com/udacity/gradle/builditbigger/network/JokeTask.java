package com.udacity.gradle.builditbigger.network;

import android.os.AsyncTask;
import android.util.Log;

import com.example.lars.myapplication.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by lars on 22.01.17.
 */

public class JokeTask extends AsyncTask<JokeTask.JokeListener, Void, String> {

    private static MyApi jokeApi = null;

    private JokeListener mJokeListener;

    @Override
    protected String doInBackground(JokeListener... listeners) {
        if(jokeApi == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            jokeApi = builder.build();
        }
        if (listeners != null && listeners.length > 0) {
            mJokeListener = listeners[0];
        }
        try {
            return jokeApi.loadJoke().execute().getJoke();
        } catch (IOException e) {
            Log.e("JokeTask", e.getMessage());
            return "All joke tellers probably sleeping";
        }
    }

    @Override
    protected void onPostExecute(String joke) {
        if (mJokeListener != null) {
            mJokeListener.jokeLoaded(joke);
            mJokeListener = null;
        }
    }

    public interface JokeListener {
        void jokeLoaded(String joke);
    }
}
