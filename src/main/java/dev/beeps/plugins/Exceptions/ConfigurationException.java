package dev.beeps.plugins.Exceptions;

public class ConfigurationException extends Exception {

    public final String path;

    public ConfigurationException(String path, String message) {
        super(message);
        this.path = path;
    }

    public ConfigurationException(String path, String message, Throwable cause) {
        super(message, cause);
        this.path = path;
    }
}
