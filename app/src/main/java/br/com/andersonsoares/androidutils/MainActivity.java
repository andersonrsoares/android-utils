package br.com.andersonsoares.androidutils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.andersonsoares.activityutil.LocationActivity;
import br.com.andersonsoares.loadercalladapter.ErrorLoaderCall;
import br.com.andersonsoares.loadercalladapter.LoaderCall;
import br.com.andersonsoares.loadercalladapter.LoaderCallAdapterFactory;
import br.com.andersonsoares.loadercalladapter.LoaderCallback;
import br.com.andersonsoares.loadercalladapter.LoaderUnauthorizedCallback;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

        final OkHttpClient.Builder client = new OkHttpClient.Builder();

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        client.addInterceptor(logInterceptor);


        OkHttpClient okHttpClient = client
                .connectTimeout(5000*3, TimeUnit.MILLISECONDS)
                .readTimeout(7000*3, TimeUnit.MILLISECONDS)
                .writeTimeout(7000*3, TimeUnit.MILLISECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://hom-clickskin.devmakerdigital.com.br/api/")
                .addCallAdapterFactory(LoaderCallAdapterFactory.create(new LoaderUnauthorizedCallback() {
                    @Override
                    public void callback(LoaderCall call, Response response) {
                        call.retry();
                        System.out.println("UnauthorizedCallback ");
                    }
                }))
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HttpBinService service = retrofit.create(HttpBinService.class);

        Map<String,String> param = new HashMap<>();
        param.put("email","");
        param.put("senha","");


        LoaderCall<UserResponse> ip = service.getIp(param);
        ip.message("teste dialog").with(this).showProgress(false).enqueue(new LoaderCallback<UserResponse>() {
            @Override
            public void onResponse(ErrorLoaderCall errorResponse,UserResponse response) {
                Toast.makeText(MainActivity.this, "tessss", Toast.LENGTH_SHORT).show();

                try {
                    System.out.println("CLIENT " + response.data.name);
                    if(errorResponse != null){

                    }
                }catch (Exception ex){

                }

            }
        });

    }

    private void initView() {
        button = (Button) findViewById(R.id.button);
    }


    interface HttpBinService {
        @POST("medico/login")
        LoaderCall<UserResponse> getIp(@Body Map<String,String> body);

    }

    static class UserResponse {
        User data;
    }

    static class User {
        String name;
    }
}
