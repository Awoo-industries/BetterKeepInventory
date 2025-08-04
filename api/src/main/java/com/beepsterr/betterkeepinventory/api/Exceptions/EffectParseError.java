package com.beepsterr.betterkeepinventory.api.Exceptions;

public class EffectParseError extends ParseError {
    public EffectParseError(String path, String message) {
        super(path, message);
    }
}
