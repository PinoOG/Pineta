package it.pino.pineta.helper.redis.exception;

import it.pino.pineta.helper.redis.action.RedisAction;
import org.jetbrains.annotations.NotNull;

public class PublishFailureException extends RuntimeException {

    public PublishFailureException(final @NotNull Exception ex, final @NotNull RedisAction action) {
        super("Failed to publish " +action.getClass().getSimpleName()+ " redis action..." + ex.getMessage());
    }

    public PublishFailureException(final @NotNull Exception ex) {
        super("Failed to publish redis action..." + ex.getMessage());
    }
}
