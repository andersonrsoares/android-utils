package br.com.andersonsoares.loadercalladapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * Created by andersonsoares on 01/02/2018.
 */


/**
 * A sample showing a custom {@link CallAdapter} which adapts the built-in {@link Call} to a custom
 * version whose callback has more granular methods.
 */
public final class LoaderCallAdapterFactory extends CallAdapter.Factory {
    LoaderUnauthorizedCallback loaderUnauthorizedCallback;
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations,
                                           Retrofit retrofit) {
        if (getRawType(returnType) != LoaderCall.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException(
                    "MyCall must have generic type (e.g., MyCall<ResponseBody>)");
        }
        Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Executor callbackExecutor = retrofit.callbackExecutor();
        return new ErrorHandlingCallAdapter(loaderUnauthorizedCallback,responseType, callbackExecutor);
    }

    private static final class ErrorHandlingCallAdapter<R> implements CallAdapter<R, LoaderCall<R>> {
        private final Type responseType;
        private final Executor callbackExecutor;
        LoaderUnauthorizedCallback loaderUnauthorizedCallback;
        ErrorHandlingCallAdapter(LoaderUnauthorizedCallback loaderUnauthorizedCallback,Type responseType, Executor callbackExecutor) {
            this.responseType = responseType;
            this.callbackExecutor = callbackExecutor;
            this.loaderUnauthorizedCallback = loaderUnauthorizedCallback;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override public LoaderCall<R> adapt(Call<R> call) {
            return new MyCallAdapter<>(loaderUnauthorizedCallback,responseType,call, callbackExecutor);
        }
    }


    public LoaderCallAdapterFactory(LoaderUnauthorizedCallback loaderUnauthorizedCallback) {
        this.loaderUnauthorizedCallback = loaderUnauthorizedCallback;
    }

    public static LoaderCallAdapterFactory create() {
        return create(null);
    }

    public static LoaderCallAdapterFactory create(LoaderUnauthorizedCallback loaderUnauthorizedCallback) {
        return new LoaderCallAdapterFactory(loaderUnauthorizedCallback);
    }



    /** Adapts a {@link Call} to {@link LoaderCall}. */
    static class MyCallAdapter<T> implements LoaderCall<T> {
        private final Call<T> call;
        private final Executor callbackExecutor;
        private final Type responseType;
        ProgressDialog dialog;
        Activity mContext;
        View mViewLayout;
        boolean mRetry = true;
        LoaderCallback<T> mCallback;
        String mMessange = "";
        LoaderUnauthorizedCallback loaderUnauthorizedCallback;
        MyCallAdapter(LoaderUnauthorizedCallback loaderUnauthorizedCallback,Type responseType,Call<T> call, Executor callbackExecutor) {
            this.call = call;
            this.callbackExecutor = callbackExecutor;
            this.responseType = responseType;
            this.loaderUnauthorizedCallback = loaderUnauthorizedCallback;
        }


        @Override
        public LoaderCall<T> message(String message) {
            this.mMessange = message;
            return this;
        }

        @Override
        public LoaderCall<T> retry(boolean retry) {
            this.mRetry = retry;
            return this;
        }

        @Override
        public LoaderCall<T> with(Activity activity) {
            this.mContext = activity;
            return this;
        }

        @Override
        public void cancel() {
            call.cancel();
        }
        int timesRetry;
        @Override
        public int retry(){
            timesRetry++;
            enqueue(mCallback);
            return timesRetry;
        }

        @Override
        public void enqueue(@NonNull LoaderCallback<T> callback) {
            this.mCallback = callback;
            try {
                if(mContext != null){
                    this.mViewLayout = mContext.getWindow().getDecorView().findViewById(android.R.id.content);

                    dialog = new ProgressDialog(mContext);
                    dialog.setCancelable(false);
                    if(mMessange != null && mMessange.length() > 0){
                        dialog.setMessage(mMessange);
                    }else{
                        dialog.setMessage( mContext.getResources().getString(R.string.geral_mensagem_buscandoDados));
                    }

                    dialog.show();
                }
            }catch (Throwable ex){
                Log.e("dialog", "CustomCallback: ",ex);
            }

            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    // TODO if 'callbackExecutor' is not null, the 'callback' methods should be executed
                    // on that executor by submitting a Runnable. This is left as an exercise for the reader.
                    if(dialog != null)
                        dialog.dismiss();

                    int code = response.code();
                    if (code >= 200 && code < 300) {
                        mCallback.onResponse(null,response.body());
                    } else if (code == 401) {
                        if(loaderUnauthorizedCallback != null){
                            loaderUnauthorizedCallback.callback(MyCallAdapter.this);
                        }else{
                            mCallback.onResponse(new ErrorLoaderCall(null,code),null);
                        }
                    } else if (code >= 400 && code < 500) {
                        mCallback.onResponse(new ErrorLoaderCall(null,code),null);
                    } else if (code >= 500 && code < 600) {
                        mCallback.onResponse(new ErrorLoaderCall(null,code),null);
                    } else {
                        mCallback.onResponse(new ErrorLoaderCall(null,code),null);
                    }

                }

                @Override
                public void onFailure(final Call<T> call, final Throwable t) {
                    // TODO if 'callbackExecutor' is not null, the 'callback' methods should be executed
                    // on that executor by submitting a Runnable. This is left as an exercise for the reader.
                    if(dialog != null)
                        dialog.dismiss();
//                    if (t instanceof IOException) {
//                        callback.networkError((IOException) t);
//                    } else {
//                        callback.unexpectedError(t);
//                    }

                    if(mViewLayout != null && mContext != null){
                        if(!mRetry){
                            mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                        }else
                        if(t instanceof TimeoutException || t instanceof SocketTimeoutException){
                            Snackbar snackbar = Snackbar
                                    .make(mViewLayout, mContext.getResources().getString(R.string.geral_mensagem_erroConexao), Snackbar.LENGTH_LONG)
                                    .setAction(mContext.getResources().getString(R.string.geral_mensagem_erroNovamente), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            MyCallAdapter.this.retry();
                                        }
                                    });
                            snackbar.show();
                           // onResponse.onFailure(new ErrorResponse(mContext.getResources().getString(R.string.geral_mensagem_erroProblema),999));
                        }
                        else
                        if(t instanceof UnknownHostException){
                            Snackbar snackbar = Snackbar
                                    .make(mViewLayout, mContext.getResources().getString(R.string.geral_mensagem_conexaoInternet), Snackbar.LENGTH_LONG);
                            snackbar.show();
                          //  onResponse.onFailure(new ErrorResponse(mContext.getResources().getString(R.string.geral_mensagem_erroProblema),999));
                        }
                        else
                        if(t instanceof ConnectException ){
                            Snackbar snackbar = Snackbar
                                    .make(mViewLayout, mContext.getResources().getString(R.string.geral_mensagem_conexaoInternet), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                        }
                        else{
                            mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                        }

                    }else
                     if(mContext != null){
                        if(!mRetry){
                            mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                        }
                        else
                        if(t instanceof TimeoutException || t instanceof SocketTimeoutException){
                            //Cria o gerador do AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            //define o titulo
                            builder.setTitle(mContext.getResources().getString(R.string.geral_mensagem_erroConexao));
                            //define a mensagem
                            builder.setMessage(mContext.getResources().getString(R.string.geral_mensagem_erroGostariaNovamente));
                            //define um botão como positivo
                            builder.setPositiveButton(mContext.getResources().getString(R.string.geral_button_sim), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    MyCallAdapter.this.retry();
                                }
                            });
                            //define um botão como negativo.
                            builder.setNegativeButton(mContext.getResources().getString(R.string.geral_button_nao), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                                }
                            });
                            //cria o AlertDialog
                            AlertDialog alerta = builder.create();
                            //Exibe
                            alerta.show();
                        }
                        else
                        if(t instanceof UnknownHostException ){//&& !NetworkUtil.isConnected(mContext)
                            //Cria o gerador do AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            //define o titulo
                            builder.setTitle(mContext.getResources().getString(R.string.geral_mensagem_erroConexao));
                            //define a mensagem
                            builder.setMessage(mContext.getResources().getString(R.string.geral_mensagem_conexaoInternet));
                            //define um botão como positivo
                            builder.setPositiveButton(mContext.getResources().getString(R.string.geral_button_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                    mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                                }
                            });
                            //cria o AlertDialog
                            AlertDialog alerta = builder.create();
                            //Exibe
                            alerta.show();

                        }
                        else
                        if(t instanceof ConnectException ){//&& !NetworkUtil.isConnected(mContext)
                            //Cria o gerador do AlertDialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            //define o titulo
                            builder.setTitle(mContext.getResources().getString(R.string.geral_mensagem_erroConexao));
                            //define a mensagem
                            builder.setMessage(mContext.getResources().getString(R.string.geral_mensagem_conexaoInternet));
                            //define um botão como positivo
                            builder.setPositiveButton(mContext.getResources().getString(R.string.geral_button_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                                }
                            });
                            //cria o AlertDialog
                            AlertDialog alerta = builder.create();
                            //Exibe
                            alerta.show();
                        }
                        else{
                            mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                        }
                    }else{
                         mCallback.onResponse(new ErrorLoaderCall(t,999),null);
                     }

                }
            });
        }

        @Override
        public LoaderCall<T> clone() {
            return new MyCallAdapter<>(loaderUnauthorizedCallback,responseType, call.clone(), callbackExecutor);
        }
    }
}