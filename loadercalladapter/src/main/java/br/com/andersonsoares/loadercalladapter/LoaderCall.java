package br.com.andersonsoares.loadercalladapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by andersonsoares on 02/02/2018.
 */

public interface LoaderCall<T> {
    int retry();
    void cancel();
    LoaderCall<T> message(String message);
    LoaderCall<T> retry(boolean retry);
    LoaderCall<T> with(Activity activity);
    void enqueue(LoaderCallback<T> callback);
    LoaderCall<T> clone();
    // Left as an exercise for the reader...
    // TODO MyResponse<T> execute() throws MyHttpException;
}