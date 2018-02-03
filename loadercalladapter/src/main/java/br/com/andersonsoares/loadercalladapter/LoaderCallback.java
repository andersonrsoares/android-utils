package br.com.andersonsoares.loadercalladapter;

/**
 * Created by andersonsoares on 02/02/2018.
 */

public interface LoaderCallback<T> {

    public void onResponse(T response);
    public void onFailure(ErrorLoaderCall errorResponse);
    public void onRetry();
}
