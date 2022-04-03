package com.nulabinc.zxcvbn.matchers;

import java.io.InputStream;

class ResourceLoader {

    InputStream getInputStream(String path) {
        InputStream in = this.getResourceAsStreamWithFallback(path);
        if (in == null) {
            throw new IllegalStateException("Could not get resource as stream");
        }
        return in;
    }

    /**
     * This code base is spring-framework's ClassUtils#getDefaultClassLoader().
     * https://github.com/spring-projects/spring-framework/blob/dfb7ca733ad309b35040e0027fb7a2f10f3a196a/spring-core/src/main/java/org/springframework/util/ClassUtils.java#L173-L210
     *
     * First, return the InputStream to use: typically the thread context ClassLoader, if available;
     * Next, the ClassLoader that loaded the ResourceLoader class will be used as fallback.
     * Finally, if even the system ClassLoader could not access resource as stream, return null.
     */
    public InputStream getResourceAsStreamWithFallback(String path) {
        // 0. try loading the resource from the same artifact as this class
        {
            InputStream in = getClass().getResourceAsStream(path);
            if (in != null) {
                return in;
            }
        } // no exceptions thrown

        // 1. try to get resource with thread context ClassLoader
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream in = this.getResourceAsStream(cl, path);
            if (in != null) {
                return in;
            }
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }

        // 2. try to get resource with this class context ClassLoader
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream in = this.getResourceAsStream(cl, path);
            if (in != null) {
                return in;
            }
        }
        catch (Throwable ex) {
            // Cannot access this class context ClassLoader - falling back...
        }

        // 3. try to get resource with this class context ClassLoader
        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            InputStream in = this.getResourceAsStream(cl, path);
            if (in != null) {
                return in;
            }
        }
        catch (Throwable ex) {
            // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
        }

        return null;
    }

    private InputStream getResourceAsStream(ClassLoader cl, String path) {
        try {
            if (cl != null) {
                InputStream in = cl.getResourceAsStream(path);
                if (in != null) {
                    return in;
                }
            }
        }
        catch (Throwable ex) {
            // Cannot access resource as stream
        }
        return null;
    }
}
