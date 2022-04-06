package com.nulabinc.zxcvbn.matchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class ResourceLoader {

    public List<String> getEntriesFromFile(String filePath, boolean fileIsOptional) {
        List<String> entries = new ArrayList<>();
        try (final InputStream input = getResourceAsStreamWithFallback(filePath)) {
            if (input == null && fileIsOptional) {
                return null;
            }

            // Reasons for not using StandardCharsets
            // refs: https://github.com/nulab/zxcvbn4j/issues/62
            final BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            String str;
            while ((str = reader.readLine()) != null) {
                entries.add(str);
            }
            return entries;
        } catch (final RuntimeException | IOException e) {
            throw new IllegalArgumentException("File :" + filePath + " could not be loaded");
        }
    }

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