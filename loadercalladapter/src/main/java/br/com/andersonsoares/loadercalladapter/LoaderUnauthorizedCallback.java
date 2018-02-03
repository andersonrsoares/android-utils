package br.com.andersonsoares.loadercalladapter;

/**
 * Created by andersonsoares on 02/02/2018.
 */

public interface LoaderUnauthorizedCallback<T> {
    public void callback(LoaderCall<T> call);
}
