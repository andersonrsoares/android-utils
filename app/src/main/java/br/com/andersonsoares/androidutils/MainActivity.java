package br.com.andersonsoares.androidutils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import br.com.andersonsoares.activityutil.LocationActivity;
import br.com.andersonsoares.loadercalladapter.ErrorLoaderCall;
import br.com.andersonsoares.loadercalladapter.LoaderCall;
import br.com.andersonsoares.loadercalladapter.LoaderCallAdapterFactory;
import br.com.andersonsoares.loadercalladapter.LoaderCallback;
import br.com.andersonsoares.loadercalladapter.LoaderUnauthorizedCallback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends LocationActivity {

    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teste();
            }
        });
    }


    public void teste() {
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
        ip.enqueue(this,new LoaderCallback<Ip>() {
            @Override
            public void onResponse(ErrorLoaderCall errorResponse,Ip response) {
                System.out.println("CLIENT " + response.origin);
                if(errorResponse != null){

                }
            }
        });

    }

    private void initView() {
        button = (Button) findViewById(R.id.button);
    }


    interface HttpBinService {
        @GET("/ip")
        LoaderCall<Ip> getIp();
    }

    static class Ip {
        String origin;
    }
}
