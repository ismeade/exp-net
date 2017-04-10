package com.ade.exp.network.tomcat.embed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 * Created by liyang on 2017/3/28.
 */
public class MethodHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Object object;

    private Method method;

    MethodHandler(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    Object run(Object... obj) {
        if (null != obj && null != method) {
            try {
                return method.invoke(object, Arrays.copyOf(obj, method.getParameterCount()));
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
        return null;
    }

}
