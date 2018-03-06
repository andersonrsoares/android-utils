package br.com.andersonsoares.loadercalladapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by andersonsoares on 02/02/2018.
 */

public interface LoaderCall<T> {
    public int retry();
    public void cancel();
    public void enqueue(@Nullable Activity activity, LoaderCallback<T> callback);
    public void enqueue(boolean retry,@Nullable Activity context, @NonNull LoaderCallback<T> callback);
    public void enqueue(LoaderCallback<T> callback);
    public LoaderCall<T> clone();
    // Left as an exercise for the reader...
    // TODO MyResponse<T> execute() throws MyHttpException;
}