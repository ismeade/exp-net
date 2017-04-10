package com.ade.exp.network.tomcat.embed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by liyang on 2017/3/28.
 */
public class MethodManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, MethodHandler> map = new HashMap<>();

    public MethodManager(Class... clazzs) {
        if (null != clazzs) {
            for (Class clazz : clazzs) {
                add(clazz);
            }
        }
    }

    private void add(Class clazz) {
        Mapping classMap = (Mapping) clazz.getAnnotation(Mapping.class);
        if (null != classMap) {
            Method[] methods = HomePage.class.getDeclaredMethods();
            if (null != methods) {
                for (Method method : methods) {
                    Mapping mapping = method.getAnnotation(Mapping.class);
                    if (null != mapping) {
                        try {
                            map.put("/" + classMap.name() + "/" + mapping.name(), new MethodHandler(clazz.newInstance(), method));
                        } catch (InstantiationException | IllegalAccessException e) {
                            logger.error(e.getLocalizedMessage(), e);
                        }
                    }
                }
            }
        }
    }

    public String run(String uri, Object... paras) {
        MethodHandler methodHandler = map.get(uri);
        if (null != methodHandler) {
            String msg = (String) methodHandler.run(paras);
            if (null != msg)
                return html(msg);
        }
        return null;
    }

    private String html(String msg) {
        return "<html><head></head><body>" + msg + "</body></html>";
    }

}
