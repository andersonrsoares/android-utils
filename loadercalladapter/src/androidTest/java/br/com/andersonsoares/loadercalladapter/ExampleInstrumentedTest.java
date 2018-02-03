package br.com.andersonsoares.loadercalladapter;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("br.com.andersonsoares.loadercalladapter.test", appContext.getPackageName());
    }

    @Test
    public void teste(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://httpbin.org")
                .addCallAdapterFactory(LoaderCallAdapterFactory.create(new LoaderUnauthorizedCallback() {
                    @Override
                    public void callback(LoaderCall call) {
                        call.retry();
                        System.out.println("UnauthorizedCallback ");
                    }
                }))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HttpBinService service = retrofit.create(HttpBinService.class);
        LoaderCall<Ip> ip = service.getIp();
        ip.enqueue(new LoaderCallback<Ip>() {
            @Override
            public void onResponse(Ip response) {
                System.out.println("CLIENT " + response.origin);
            }

            @Override
            public void onFailure(ErrorLoaderCall errorResponse) {
                System.out.println("CLIENT ERROR " + errorResponse.getErrorcode() + " " + errorResponse.getMessage());
            }

            @Override
            public void onRetry() {
                System.out.println("onRetry! ");
            }
        });

    }


    interface HttpBinService {
        @GET("/ip")
        LoaderCall<Ip> getIp();
    }

    static class Ip {
        String origin;
    }
}
