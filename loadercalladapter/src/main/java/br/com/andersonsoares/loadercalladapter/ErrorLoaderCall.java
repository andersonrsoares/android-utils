package br.com.andersonsoares.loadercalladapter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andersonsoares on 02/02/2018.
 */

public class ErrorLoaderCall extends Throwable {
    public int errorcode;
    public Object objectError;

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

    public ErrorLoaderCall(Throwable cause, int errorcode, Object objectError) {
        super(cause);
        this.errorcode = errorcode;
        this.objectError = objectError;
    }

    public ErrorLoaderCall(String message, int errorcode, Object objectError) {
        super(message);
        this.errorcode = errorcode;
        this.objectError = objectError;
    }
}
