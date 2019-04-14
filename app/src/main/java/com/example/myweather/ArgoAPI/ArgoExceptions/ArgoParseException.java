package com.example.myweather.ArgoAPI.ArgoExceptions;


/**
 * Thrown if the response from the API cannot be parsed
 */
public class ArgoParseException extends Exception {


    public ArgoParseException() {
    }

    public ArgoParseException(String message) {
        super(message);
    }

    public ArgoParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgoParseException(Throwable cause) {
        super(cause);
    }
}
