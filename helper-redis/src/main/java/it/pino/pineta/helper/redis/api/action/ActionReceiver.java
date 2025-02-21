package it.pino.pineta.helper.redis.api.action;


public interface ActionReceiver{

    void subscribe();

    void unsubscribe();
}
