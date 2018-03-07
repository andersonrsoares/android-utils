package br.com.andersonsoares.loadercalladapter;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by andersonsoares on 02/02/2018.
 */

public class ErrorLoaderCall extends Throwable {
    public int errorcode;
    public JSONObject objectError;


    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public ErrorLoaderCall(Throwable cause, int errorcode) {
        super(cause);
        this.errorcode = errorcode;

    }

    public ErrorLoaderCall(Throwable cause, int errorcode, JSONObject objectError) {
        super(cause);
        this.errorcode = errorcode;
        this.objectError = objectError;
    }

    public ErrorLoaderCall(String message, int errorcode, JSONObject objectError) {
        super(message);
        this.errorcode = errorcode;
        this.objectError = objectError;
    }

    public Object getObjectError() {
        return objectError;
    }

    public void setObjectError(JSONObject objectError) {
        this.objectError = objectError;
    }

}
