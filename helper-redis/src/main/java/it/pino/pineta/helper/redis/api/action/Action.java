package it.pino.pineta.helper.redis.api.action;


import com.alibaba.fastjson2.JSONObject;

public abstract class Action<T> {

    public abstract JSONObject serialize(T t);

    public abstract T deserialize(final JSONObject jsonObject);
}
