# android-utils

[![](https://jitpack.io/v/andersonrsoares/android-utils.svg)](https://jitpack.io/#andersonrsoares/android-utils)

### Installation

Add the following dependency to your module `build.gradle` file:
 You can use separate libraries
```gradle
dependencies {
            implementation 'com.github.andersonrsoares:android-utils:1.0.9'
            implementation 'com.github.andersonrsoares.android-utils:activityutil:1.0.9'
    }
```

Add this to your root `build.gradle` file (**not** your module `build.gradle` file) :
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

### loadercalladapter how to use
library that provides trial alert and progress on retrofit requests. Unauthorized callback can be useful to request new token 


Library dependences 
```
     'com.android.support:appcompat-v7:26.1.0'
     'com.android.support:support-v4:26.1.0'
     'com.android.support:design:26.1.0'
     'com.squareup.retrofit2:retrofit:2.3.0'
     'com.squareup.retrofit2:converter-gson:2.3.0'
     'com.squareup.okhttp3:logging-interceptor:3.3.1'
     'com.google.code.gson:gson:2.6.2'
```

Add addCallAdapterFactory to retrofit
```
 Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("url")
                .addCallAdapterFactory(LoaderCallAdapterFactory.create(new LoaderUnauthorizedCallback() {
                    @Override
                    public void callback(LoaderCall call) {
                        //requestNewToken();
                        //call.retry();
                    }
                }))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
```

Use LoaderCall 
```
 @GET("/object")
  LoaderCall<Object> getObject();
 ```
 
new "enqueue" provides onRetry action when request return network errors  
```
Service service = retrofit.create(Service.class);
        LoaderCall<Object> ip = service.getObject();
        ip.enqueue(viewtosnack,activity,new LoaderCallback<Object>() {
            @Override
            public void onResponse(Object response) {
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
```
put in strings.xml to change labels
```
 <string name="geral_mensagem_buscandoDados">Carregando</string>
  <string name="geral_mensagem_naoEncontrado">Não encontrado</string>
  <string name="geral_mensagem_erroServidor">Erro de Servidor</string>
  <string name="geral_mensagem_naoAutorizado">Não autorizado</string>
  <string name="geral_mensagem_erroProblema">Ocorreu um problema</string>
  <string name="geral_mensagem_erroConexao">Problema de Conexão</string>
  <string name="geral_mensagem_erroNovamente">Tentar Novamente</string>
  <string name="geral_mensagem_erroGostariaNovamente">Gostaria de tentar novamente?</string>
  <string name="geral_button_sim">Sim</string>
  <string name="geral_button_nao">Não</string>
  <string name="geral_mensagem_conexaoInternet">Verifique a conexão com internet</string>
  <string name="geral_button_ok">OK</string>
  <string name="geral_mensagem_sucesso">Operação realizada com sucesso</string>
  <string name="geral_mensagem_aguarde">Aguarde...</string>
  <string name="geral_mensagem_gps">Localização desabliitada, gostaria de habilitar</string>
  <string name="geral_mensagem_logado">Para continuar você deve estar logado.\nDeseja logar no aplicativo?</string>
```

### actovityutil how to use

Library dependences 
```
  'com.android.support:appcompat-v7:26.1.0'
  'com.google.android.gms:play-services-location:11.8.0'
```

BaseActivity provides a alert simplify.  
```
  public  class YouActivity extends BaseActivity

  showDialog("title","message");
        showDialog("title", "message","OK","CANCEL", new OnClickListener() {
            @Override
            public void onPositive(DialogInterface dialog) {
                
            }

            @Override
            public void onNegative(DialogInterface dialog) {

            }
        });
```

LocationActivity provide user location . 


```
  
```

Override onLocationChanged to get updated location.
```
  public class YouActivity extends LocationActivity

  @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
    }
    
    //user for get last location, can return null
    getCurrentLocation()
    
    //update parametrs
    
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setLocationInterval(5000) 
        setLocationFastestInterval(2000)
        setLocationNumUpdates(10)
        setLocationPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
    
```

put in strings.xml to change labels
```
 <string name="no_location_detected">
        No location detected. Make sure location is enabled on the device.
    </string>

    <string name="permission_rationale">Location permission is needed for core functionality</string>
    <string name="permission_denied_explanation">Permission was denied, but is needed for core
        functionality.</string>
    <string name="settings">Settings</string>

    <string name="location_not_satisfied">Location settings are not satisfied. Attempting to upgradelocation settings </string>
    <string name="location_inadequate">Location settings are inadequate, and cannot be fixed here. Fix in Settings.</string>
```

