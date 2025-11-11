package net.nathcat.marcus_tts.exceptions;

public class APIException extends Exception {
    private final String error;
    
    public APIException(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "API Error! \"" + error + "\"";
    }
}
