package br.com.andersonsoares.loadercalladapter;

import android.support.annotation.Nullable;

/**
 * Created by andersonsoares on 02/02/2018.
 */

public interface LoaderCallback<T> {
    void onResponse(@Nullable ErrorLoaderCall error,@Nullable T response);
}
