package cn.appscomm.netlib.util;

import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/2 12:24
 */
public abstract class JsonHelper<T> {
    private Class<T> entityClass;

    public JsonHelper() {
        Type genType = this.getClass().getGenericSuperclass();
        entityClass = (Class<T>) ((ParameterizedType) genType).getActualTypeArguments()[0];
    }

    public T prase(String objectString) {
        return new GsonBuilder().create().fromJson(objectString, entityClass);
    }
}
