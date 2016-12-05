package com.rohithavatapally.weatherapp.network;

/**
 * Created by RohithAvatapally on 12/4/16.
 */

public class WeatherException extends Exception {

    public enum ErrorType {
        JSON_PARSE,
        NETWORK_ERROR;
    }

    public final ErrorType errorType;

    public WeatherException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public WeatherException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }


}
