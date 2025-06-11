package masi.s2.graph;

public class PathNotFoundException extends Exception {
    public PathNotFoundException(String message) {
        super(message);
    }

    public PathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 