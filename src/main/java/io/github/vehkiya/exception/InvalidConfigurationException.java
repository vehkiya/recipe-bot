package io.github.vehkiya.exception;

public class InvalidConfigurationException extends ApplicationException {
    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
