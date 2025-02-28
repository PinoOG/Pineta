package it.pino.pineta.helper.redis.exception;

public class RequestTimeoutException extends RuntimeException {
  public RequestTimeoutException(String message) {
    super(message);
  }
}
