package com.beepsterr.betterkeepinventory.api.Exceptions;

public class ConditionParseError extends ParseError {
    public ConditionParseError(String path, String message) {
        super(path, message);
    }
}
