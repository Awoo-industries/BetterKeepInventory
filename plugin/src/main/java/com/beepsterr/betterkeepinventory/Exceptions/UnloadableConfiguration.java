package com.beepsterr.betterkeepinventory.Exceptions;

public class UnloadableConfiguration extends RuntimeException {
    public UnloadableConfiguration(String message) {
        super(message);
    }
}
