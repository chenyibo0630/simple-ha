package org.buenos.common.ha.utils;

import org.buenos.common.ha.HighAvailabilityServices;

public class HAServiceUtils {

    public static HighAvailabilityServices loadCustomHighAvailabilityServices(String className) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        HighAvailabilityServices highAvailabilityServices = null;
        try {
            highAvailabilityServices = instantiate(
                className,
                HighAvailabilityServices.class,
                classLoader);
        } catch (Exception e) {
            throw new IllegalStateException("load HA services fail, class name: " + className, e);
        }
        return highAvailabilityServices;
    }

    public static <T> T instantiate(
        final String className, final Class<T> targetType, final ClassLoader classLoader) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Class<? extends T> clazz = Class.forName(className, false, classLoader).asSubclass(targetType);
        return clazz.newInstance();
    }
}
