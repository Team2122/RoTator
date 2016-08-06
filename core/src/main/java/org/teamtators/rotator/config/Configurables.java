package org.teamtators.rotator.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by alex on 8/5/16.
 */
public class Configurables {
    private static final Logger logger = LogManager.getLogger(Configurables.class);

    public static boolean configureObject(Object toConfigure, JsonNode config, ObjectMapper mapper) throws ConfigException {
        Preconditions.checkNotNull(toConfigure);
        Preconditions.checkNotNull(config);

        Class<?> clazz = toConfigure.getClass();
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type i : interfaces) {
            ParameterizedType type = (ParameterizedType) i;
            if (type.getRawType() == Configurable.class) {
                Class configClass = (Class) type.getActualTypeArguments()[0];
                try {
                    Object configObj = mapper.treeToValue(config, configClass);
                    Method configure = clazz.getMethod("configure", configClass);
                    configure.invoke(toConfigure, configObj);
                    return true;
                } catch (NoSuchMethodException e) {
                    logger.warn("Object of class " + clazz.getName() + " missing configure method");
                    return false;
                } catch (JsonProcessingException e) {
                    throw new ConfigException("Error reading config of " + configClass.toString() +
                            " for object of " + clazz.toString(), e);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new ConfigException("Error applying config on object of " + clazz.toString(), e);
                }
            }
        }
        logger.debug("Attempted to configure object of " + clazz.toString() + ", which is not Configurable");
        return false;
    }
}
