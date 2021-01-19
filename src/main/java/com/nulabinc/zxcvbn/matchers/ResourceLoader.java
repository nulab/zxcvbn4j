package com.nulabinc.zxcvbn.matchers;

import java.io.InputStream;

class ResourceLoader {

    InputStream getInputStream(String path) {
        ClassLoader cl = getDefaultClassLoader();
        if (cl == null) {
            throw new IllegalStateException("Cannot determine classloader");
        }
        return cl.getResourceAsStream(path);
    }

    /**
     * This code is a copy of spring-framework's ClassUtils#getDefaultClassLoader().
     * https://github.com/spring-projects/spring-framework/blob/dfb7ca733ad309b35040e0027fb7a2f10f3a196a/spring-core/src/main/java/org/springframework/util/ClassUtils.java#L173-L210
     *
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you clearly prefer a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     * @return the default ClassLoader (only {@code null} if even the system
     * ClassLoader isn't accessible)
     * @see Thread#getContextClassLoader()
     * @see ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ResourceLoader.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }
}