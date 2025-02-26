package it.pino.pineta.helper.redis.exception;

import org.jetbrains.annotations.NotNull;

public class IncomingActionException extends RuntimeException {

    public IncomingActionException(final @NotNull Exception ex) {
        super("Failed to handle incoming action message: " + ex);
    }
}
