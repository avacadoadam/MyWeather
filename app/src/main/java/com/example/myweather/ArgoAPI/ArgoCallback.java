package com.example.myweather.ArgoAPI;

/**
 * A interface for the Argo API class
 */
public interface ArgoCallback {

    void success(Weather weather);

    /**
     * @param e may be of instance ArgoConnectionException or ArgoParseException
     */
    void unsuccessful(Exception e);

}
