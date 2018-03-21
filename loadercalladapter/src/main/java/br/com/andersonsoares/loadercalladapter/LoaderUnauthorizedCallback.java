package br.com.andersonsoares.loadercalladapter;

import retrofit2.Response;

/**
 * Created by andersonsoares on 02/02/2018.
 */

public interface LoaderUnauthorizedCallback {
     void callback(LoaderCall call, Response response);
}
