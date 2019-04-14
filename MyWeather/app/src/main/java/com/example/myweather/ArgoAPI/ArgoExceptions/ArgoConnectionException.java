package com.example.myweather.ArgoAPI.ArgoExceptions;

/**
 * Thrown if the connection to the Agro API fails
 */
public class ArgoConnectionException extends Exception {

    public ArgoConnectionException() {
    }

    public ArgoConnectionException(String message) {
        super(message);
    }

    public ArgoConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgoConnectionException(Throwable cause) {
        super(cause);
    }
}
