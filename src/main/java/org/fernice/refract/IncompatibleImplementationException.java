package org.fernice.refract;

public class IncompatibleImplementationException extends RuntimeException {

    public IncompatibleImplementationException() {
        super();
    }

    public IncompatibleImplementationException(String message) {
        super(message);
    }

    public IncompatibleImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompatibleImplementationException(Throwable cause) {
        super(cause);
    }
}
