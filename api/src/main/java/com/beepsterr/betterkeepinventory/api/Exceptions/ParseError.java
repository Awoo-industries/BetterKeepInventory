package com.beepsterr.betterkeepinventory.api.Exceptions;

public class ParseError extends RuntimeException {

    protected String path;
    public ParseError(String path, String message) {
        super(message);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
